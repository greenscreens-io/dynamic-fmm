/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.annotation.Annotation;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Dynamic engine to intercept interface calls and map them to the external library methods.
 */
final class ExternalInvocationHandler implements InvocationHandler {

	private final Thread hook; 
	private final Class<?> caller;
	private final Map<Method, MethodHandle> cache;
	private final Map<Class<?>, Collection<Method>> callbacks;

	private final Arena arena;
	private final Lookup lookup;
	private final SymbolLookup stdlib;
	
	/**
	 * Main constructor, initialize Interface wrapper for remote library 
	 * @param caller
	 */
	public ExternalInvocationHandler(final Class<?> caller) {
		super();
		this.caller = caller;
		this.arena = Arena.ofShared();
		this.lookup = MethodHandles.lookup();
		this.cache = new ConcurrentHashMap<>();
		this.callbacks = new ConcurrentHashMap<>();
		this.hook =  new Thread(() -> ExternalInvocationHandler.this.close());
		this.stdlib = SymbolLookup.libraryLookup(findLib(), arena);
		Runtime.getRuntime().addShutdownHook(hook);
	}
	
	/**
	 * Auto release all resources. 
	 * Automatically called when JVM exits. 
	 */
	private void close() {
		if (Objects.nonNull(arena)) arena.close();
	}

	/**
	 * Normalize external library name. If extension is not specified, 
	 * proper one will be set based on currently used OS. 
	 * @return
	 */
	private String findLib() {
		final External annotation =  caller.getAnnotation(External.class);
		String lib = normalize(annotation.name());
		if (lib.length() == 0) lib = normalize(System.getProperties().getProperty(annotation.property()));
		if (isWin()) {
			return lib.endsWith(".dll") ? lib : lib + ".dll";
		} else {
			return lib.endsWith(".so") ? lib : lib + ".so";
		}		
	}
	
	/**
	 * Detect if current OS i Windows
	 * @return
	 */
	private boolean isWin() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	/**
	 * Main Interface interceptor method where all magic happens. 
	 */
	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final MethodHandle handle = get(method);
		final Object[] arguments = wrap(method, args);	
		final Object obj = Objects.isNull(args) ? handle.invoke() : handle.invoke(arguments);
		final boolean isString = String.class.equals(method.getReturnType());		
		return isString ? ((MemorySegment) obj).reinterpret(Integer.MAX_VALUE, arena, null).getUtf8String(0) : obj;
	}
	
	/**
	 * Wrap arguments into a proper types for foreign function calls
	 * @param method
	 * @param args
	 * @return
	 * @throws IllegalAccessException
	 */
	private Object[] wrap(final Method method, final Object[] args) throws IllegalAccessException {
		
		if (Objects.isNull(args)) return args;
		
		final Class<?>[] types = method.getParameterTypes();
		final Annotation[][] annotations = method.getParameterAnnotations();
		final Object[] arguments = new Object[args.length];
		
		Arena gcarena = null;
		int i = 0;		
		
		while (i<args.length) {
			final Class<?>  klazz = types[i];
			if (klazz.isArray() && klazz.arrayType().isPrimitive()) {
					if (Objects.isNull(gcarena)) gcarena = Arena.ofAuto();
					if (byte.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_BYTE, (byte[])args[i]);
					} else if (boolean.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_BYTE, (byte[])args[i]);
					} else if (char.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_CHAR, (char[])args[i]);
					} else if (double.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_DOUBLE, (double[])args[i]);
					} else if (float.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_FLOAT, (float[])args[i]);
					} else if (int.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_INT, (int[])args[i]);
					} else if (long.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_LONG, (long[])args[i]);
					} else if (short.class.equals(klazz)) {
						arguments[i] = gcarena.allocateArray(ValueLayout.JAVA_SHORT, (short[])args[i]);
					} else {
						throw new RuntimeException("Unsupported data type");
					}
			} else if (String.class.equals(klazz)) {
				if (Objects.isNull(gcarena)) gcarena = Arena.ofAuto();
				arguments[i] = gcarena.allocateUtf8String(normalize((String)args[i]));
			} else if (MethodHandle.class.equals(klazz)) {
				arguments[i] = toPointer((MethodHandle) args[i]);
			} else {
				final Callback cb = toCallback(annotations[i]);
				if (Objects.nonNull(cb)) {
					final MethodHandle handle = initCallback(klazz, cb.name());
					arguments[i] = Objects.isNull(handle) ? handle : toPointer(handle.bindTo(args[i]));
				} else {
					arguments[i] = args[i];
				}
			}
			i++;
		}
		return arguments;
	}

	/**
	 * Detect if argument is a callback
	 * @param annotations
	 * @return
	 */
	private Callback toCallback(final Annotation[] annotations) {
		for (Annotation ann : annotations) {
			if (ann.annotationType().equals(Callback.class)) {
				return (Callback) ann;
			}
		}
		return null;
	}
	
	/**
	 * Convert callback method to a "pointer" 
	 * @param handle
	 * @return
	 */
	private MemorySegment toPointer(final MethodHandle handle) {
		final FunctionDescriptor descriptor = buildDescriptor(handle);
		return Linker.nativeLinker().upcallStub(handle, descriptor, arena);
	}

	/**
	 * Build a descriptor from a callback method, required to create a callback pointer
	 * @param handle
	 * @return
	 */
	private FunctionDescriptor buildDescriptor(final MethodHandle handle) {
		final boolean isVoid = void.class.equals(handle.type().returnType());
		return isVoid ? buildVoidDescriptor(handle) : buildReturnDescriptor(handle);
	}
	
	private FunctionDescriptor buildVoidDescriptor(final MethodHandle handle) {
		final MemoryLayout [] args = tolayouts(handle);
		return FunctionDescriptor.ofVoid(args);
	}

	private FunctionDescriptor buildReturnDescriptor(final MethodHandle handle) {
		final Class<?> clazz = handle.type().returnType();
		final MemoryLayout [] args = tolayouts(handle);
		return FunctionDescriptor.of(toLayout(clazz), args);
	}

	/**
	 * Convert callback method arguments into a signature for foreign library function 
	 * @param handle
	 * @return
	 */
	private MemoryLayout[] tolayouts(final MethodHandle handle) {
		final Class<?> [] params =  handle.type().parameterArray(); 
		final MemoryLayout [] args = new MemoryLayout[params.length];
		int i = 0;
		while (i<params.length) {
			args[i] = toLayout(params[i]);
			i++;
		}
		return args;
	}
	
	
	/**
	 * Retrieve or build MethodHandler signature for foreign library function
	 * @param method
	 * @return
	 */
	private MethodHandle get(final Method method) {
		if (!cache.containsKey(method)) {
			cache.put(method, build(method));
		}
		return cache.get(method);
	} 

	/**
	 * Build MethodHandler signature for foreign library function
	 * @param method
	 * @return
	 */
	private MethodHandle build(final Method method) {
		final MemorySegment segment = stdlib.find(method.getName()).orElseThrow();
		final FunctionDescriptor descriptor = buildDescriptor(method); 
		final MethodHandle handle = Linker.nativeLinker().downcallHandle(segment, descriptor);
		final int count = method.getParameterCount();
		return count == 0 ?  handle : handle.asSpreader(Object[].class, count);
	}
	
	/**
	 * Build a descriptor from an Interface method, required to create a foreign call
	 * @param method
	 * @return
	 */
	private FunctionDescriptor buildDescriptor(final Method method) {
		final boolean isVoid = void.class.equals(method.getReturnType());
		return isVoid ? buildVoidDescriptor(method) : buildReturnDescriptor(method); 		
	}
	
	private FunctionDescriptor buildVoidDescriptor(final Method method) {
		final MemoryLayout [] args = tolayouts(method);
		return args.length == 0 ?  FunctionDescriptor.ofVoid() : FunctionDescriptor.ofVoid(args);
	}
	
	private FunctionDescriptor buildReturnDescriptor(final Method method) {
		final Class<?> clazz = method.getReturnType();
		final MemoryLayout [] args = tolayouts(method);
		return args.length == 0 ?  FunctionDescriptor.of(toLayout(clazz)) :FunctionDescriptor.of(toLayout(clazz), args);
	}
	
	/**
	 * Convert Interface method arguments into a signature for foreign library function
	 * @param method A method which arguments are to be converted
	 * @return
	 */
	private MemoryLayout[] tolayouts(final Method method) {
		final Parameter [] params =  method.getParameters();
		final MemoryLayout [] args = new MemoryLayout[params.length];
		int i = 0;
		while (i<params.length) {
			args[i] = toLayout(params[0].getType());
			i++;
		}
		return args;
	}
			
	/**
	 * Foreign functions can receive either a primitive types or 
	 * a "pointer" represented by Java MemoryAddress class 
	 * @param clazz Java type to be converted
	 * @return Foreign type
	 */
	private MemoryLayout toLayout(final Class<?> clazz) {
		MemoryLayout layout = null;
		if (clazz.isPrimitive()) {
			if (byte.class.equals(clazz)) {
				layout = ValueLayout.JAVA_BYTE;
			} else if (boolean.class.equals(clazz)) {
				layout = ValueLayout.JAVA_BOOLEAN;				
			} else if (char.class.equals(clazz)) {
				layout = ValueLayout.JAVA_CHAR;
			} else if (double.class.equals(clazz)) {
				layout = ValueLayout.JAVA_DOUBLE;
			} else if (float.class.equals(clazz)) {
				layout = ValueLayout.JAVA_FLOAT;
			} else if (int.class.equals(clazz)) {
				layout = ValueLayout.JAVA_INT;			
			} else if (long.class.equals(clazz)) {
				layout = ValueLayout.JAVA_LONG;
			} else if (short.class.equals(clazz)) {
				layout = ValueLayout.JAVA_SHORT;
			}
		} else {
			layout = ValueLayout.ADDRESS;			
		}
		return layout;
	}
	
	/**
	 * Normalize string, preventing null
	 * @param val
	 * @return
	 */
	private String normalize(final String val) {
		return Objects.isNull(val) ? "" : val.trim();
	}

	/**
	 * Find a proper method used as a callback and convert that method 
	 * into a "pointer" - a MethodHandle
	 * @param clazz Interface containing a callback method
	 * @param name Name of the 
	 * @return
	 * @throws IllegalAccessException
	 */
	private MethodHandle initCallback(final Class<?> clazz, final String name) throws IllegalAccessException {
		final Collection<Method> list = getCallbacks(clazz);
		final Optional<Method> method = list.stream() 				
				.filter(m -> m.getAnnotation(Callback.class).name().equals(name))
				.findFirst().or(() -> list.stream().findFirst());
		return method.isPresent() ? lookup.unreflect(method.get()) : null;
	}
	
	/**
	 * Find all Interface @Callback annotated methods and cache them per class
	 * @param clazz
	 * @return
	 */
	private Collection<Method> getCallbacks(final Class<?> clazz) {
		Collection<Method> list = callbacks.get(clazz);
		if (Objects.isNull(list)) {
			list = Arrays.asList(clazz.getMethods()).stream()
			.filter(m -> Objects.nonNull(m.getAnnotation(Callback.class)))
			.collect(Collectors.toList());
			callbacks.put(clazz, list);
		}
		return list;
	}
	
}
