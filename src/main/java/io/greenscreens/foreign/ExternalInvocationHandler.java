/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.annotation.Annotation;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
		String lib = Converters.normalize(annotation.name());
		if (lib.length() == 0) lib = Converters.normalize(System.getProperties().getProperty(annotation.property()));
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
		final Object ret = Objects.isNull(args) ? handle.invoke() : handle.invoke(arguments);
		return unwrap(method, ret);
	}
	
	/**
	 * Unwrap result with support for array of primitive types or string 
	 * Converts MemorySegment to actual type if supported. 
	 * @param method 
	 * @param ret Unwrapped data type
	 * @return
	 */
	private Object unwrap(final Method method, final Object ret) {
		if (Objects.isNull(ret)) return ret;
		Class<?> type = method.getReturnType();
		final boolean isArray = type.isArray();
		final boolean isPrimitive = isArray ? type.arrayType().isPrimitive() : type.isPrimitive();
		type = isArray && isPrimitive ? type.arrayType() : type;
		if (!isPrimitive) {
			final boolean isString = String.class.equals(type);	
			return isString ? ((MemorySegment) ret).reinterpret(Integer.MAX_VALUE, arena, null).getUtf8String(0) : ret;			
		}
		if (!isArray) return ret;
		return Converters.reformat(type, (MemorySegment) ret, arena);
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
			if (klazz.isArray()) {
					if (Objects.isNull(gcarena)) gcarena = Arena.ofAuto();
					arguments[i] = Converters.reformat(klazz, args[i], gcarena);
			} else if (String.class.equals(klazz)) {
				if (Objects.isNull(gcarena)) gcarena = Arena.ofAuto();
				arguments[i] = gcarena.allocateUtf8String(Converters.normalize((String)args[i]));
			} else if (MethodHandle.class.equals(klazz)) {
				arguments[i] = Converters.toPointer((MethodHandle) args[i], arena);
			} else if (MemorySegment.class.equals(klazz)) {
				arguments[i] = args[i];
			} else {
				final Callback cb = Converters.toCallback(annotations[i]);
				if (Objects.nonNull(cb)) {
					final MethodHandle handle = initCallback(klazz, cb.name());
					arguments[i] = Objects.isNull(handle) ? handle : Converters.toPointer(handle.bindTo(args[i]), arena);
				} else {
					arguments[i] = Converters.reformat(args[i]);
				}
			}
			i++;
		}
		return arguments;
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
	private  MethodHandle build(final Method method) {
		final MemorySegment segment = stdlib.find(method.getName()).orElseThrow();
		final FunctionDescriptor descriptor = Converters.buildDescriptor(method); 
		final MethodHandle handle = Linker.nativeLinker().downcallHandle(segment, descriptor);
		final int count = method.getParameterCount();
		return count == 0 ?  handle : handle.asSpreader(Object[].class, count);
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
		if (Objects.isNull(list)) list = Converters.getCallbacks(clazz);
		return list;
	}

}
