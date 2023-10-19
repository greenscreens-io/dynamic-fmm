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
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Internal generic data converters
 */
enum Converters {
;

	/**
	 * UTF16 support
	 * 
	 * @param wide
	 * @return
	 */
	public static String fromWideString(final MemorySegment wide) {
		final CharBuffer cb = wide.asByteBuffer().order(ByteOrder.nativeOrder()).asCharBuffer();
		while (cb.hasRemaining())
			if (cb.get() == 0)
				break;
		return cb.limit(cb.position()).rewind().toString();
	}

	/**
	 * UTF16 support
	 * 
	 * @param s
	 * @param allocator
	 * @return
	 */
	public static MemorySegment toWideString(final String s, final SegmentAllocator allocator) {
		final MemorySegment ms = allocator.allocateArray(ValueLayout.JAVA_CHAR, s.length() + 1);
		ms.asByteBuffer().order(ByteOrder.nativeOrder()).asCharBuffer().put(s).put('\0');
		return ms;
	}

	/**
	 * Boolean array support
	 * @param bytes
	 * @return
	 */
	public static boolean[] toBoolean(final byte[] bytes) {
		final boolean[] bits = new boolean[bytes.length];
		int i = bytes.length;
		while (--i >= 0) {
			bits[i] = (bytes[i] & 0x01) == 1;
		}
		return bits;
	}
	
	/**
	 * Boolean array support
	 * @param bits
	 * @return
	 */
	public static byte[] toBytes(final boolean[] bits) {
		final byte[] bytes = new byte[bits.length];
		int i = bits.length;
		while (--i >= 0) {
			bytes[i] = (byte) (bits[i] ? 1 : 0);
		}
		return bytes;
	}
	
	/**
	 * Foreign functions can receive either a primitive types or 
	 * a "pointer" represented by Java MemoryAddress class 
	 * @param clazz Java type to be converted
	 * @return Foreign type
	 */
	static MemoryLayout toLayout(final Class<?> clazz) {
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
	
	static MemorySegment reformat(final Class<?> type, final Object data, final Arena arena) {
		if (byte.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_BYTE, Objects.isNull(data) ? new byte[0] : (byte[])data);
		} else if (boolean.class.equals(type)) {
			final byte[] raw = toBytes(Objects.isNull(data) ? new boolean[0] : (boolean[])data);
			return arena.allocateArray(ValueLayout.JAVA_BYTE, raw);						
		} else if (char.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_CHAR, Objects.isNull(data) ? new char[0] : (char[])data);
		} else if (double.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_DOUBLE, Objects.isNull(data) ? new double[0] : (double[])data);
		} else if (float.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_FLOAT, Objects.isNull(data) ? new float[0] : (float[])data);
		} else if (int.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_INT, Objects.isNull(data) ? new int[0] : (int[])data);
		} else if (long.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_LONG, Objects.isNull(data) ? new long[0] : (long[])data);
		} else if (short.class.equals(type)) {
			return arena.allocateArray(ValueLayout.JAVA_SHORT, Objects.isNull(data) ? new short[0] : (short[])data);
		} else {
			throw new RuntimeException("Unsupported data type");
		}		
	}

	static Object reformat(final Class<?> type, final MemorySegment data, final Arena arena) {
		
		if (byte.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_BYTE);
		} else if (boolean.class.equals(type)) {
			final byte[] raw = data.toArray(ValueLayout.JAVA_BYTE);
			return toBoolean(raw);
		} else if (char.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_CHAR);
		} else if (double.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_DOUBLE);
		} else if (float.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_FLOAT);
		} else if (int.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_INT);
		} else if (long.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_LONG);
		} else if (short.class.equals(type)) {
			return data.toArray(ValueLayout.JAVA_SHORT);
		} 
		return data;
	}
	
	static Object reformat(final Object obj) {
		if (Objects.isNull(obj)) return null;
	    return switch (obj) {
	    	case Byte b -> b.byteValue();
	    	case Boolean o -> o.booleanValue();
	    	case Character c -> c.charValue();
	    	case Double d  -> d.doubleValue();
	    	case Float f  -> f.floatValue();
	    	case Integer i -> i.intValue();
	        case Long l    -> l.longValue();
	        case Short s    -> s.shortValue();	        
	        default        -> obj;
	    };
	}
	
	/**
	 * Normalize string, preventing null
	 * @param val
	 * @return
	 */
	static String normalize(final String val) {
		return Objects.isNull(val) ? "" : val.trim();
	}

	/**
	 * Detect if argument is a callback
	 * @param annotations
	 * @return
	 */
	static Callback toCallback(final Annotation[] annotations) {
		for (Annotation ann : annotations) {
			if (ann.annotationType().equals(Callback.class)) {
				return (Callback) ann;
			}
		}
		return null;
	}
	
	/**
	 * Find all methods marked as callbacks
	 * @param clazz
	 * @return
	 */
	static Collection<Method> getCallbacks(final Class<?> clazz) {
		return Arrays.asList(clazz.getMethods()).stream()
			.filter(m -> Objects.nonNull(m.getAnnotation(Callback.class)))
			.collect(Collectors.toList());
	}
	
	/**
	 * Convert callback method to a "pointer" 
	 * @param handle
	 * @return
	 */
	static MemorySegment toPointer(final MethodHandle handle, final Arena arena) {
		if (Objects.isNull(handle)) return null;
		final FunctionDescriptor descriptor = buildDescriptor(handle);
		return Linker.nativeLinker().upcallStub(handle, descriptor, arena);
	}

	/**
	 * Build a descriptor from a callback method, required to create a callback pointer
	 * @param handle
	 * @return
	 */
	static FunctionDescriptor buildDescriptor(final MethodHandle handle) {
		if (Objects.isNull(handle)) return null;
		final boolean isVoid = void.class.equals(handle.type().returnType());
		return isVoid ? buildVoidDescriptor(handle) : buildReturnDescriptor(handle);
	}
	
	static FunctionDescriptor buildVoidDescriptor(final MethodHandle handle) {
		final MemoryLayout [] args = tolayouts(handle);
		return FunctionDescriptor.ofVoid(args);
	}

	static FunctionDescriptor buildReturnDescriptor(final MethodHandle handle) {
		final Class<?> clazz = handle.type().returnType();
		final MemoryLayout [] args = tolayouts(handle);
		return FunctionDescriptor.of(Converters.toLayout(clazz), args);
	}

	/**
	 * Convert callback method arguments into a signature for foreign library function 
	 * @param handle
	 * @return
	 */
	static MemoryLayout[] tolayouts(final MethodHandle handle) {
		final Class<?> [] params =  handle.type().parameterArray(); 
		final MemoryLayout [] args = new MemoryLayout[params.length];
		int i = 0;
		while (i<params.length) {
			args[i] = Converters.toLayout(params[i]);
			i++;
		}
		return args;
	}

	/**
	 * Build a descriptor from an Interface method, required to create a foreign call
	 * @param method
	 * @return
	 */
	static  FunctionDescriptor buildDescriptor(final Method method) {
		final boolean isVoid = void.class.equals(method.getReturnType());
		return isVoid ? buildVoidDescriptor(method) : buildReturnDescriptor(method); 		
	}
	
	static FunctionDescriptor buildVoidDescriptor(final Method method) {
		final MemoryLayout [] args = tolayouts(method);
		return args.length == 0 ?  FunctionDescriptor.ofVoid() : FunctionDescriptor.ofVoid(args);
	}
	
	static FunctionDescriptor buildReturnDescriptor(final Method method) {
		final Class<?> clazz = method.getReturnType();
		final MemoryLayout [] args = tolayouts(method);
		return args.length == 0 ?  FunctionDescriptor.of(Converters.toLayout(clazz)) :FunctionDescriptor.of(Converters.toLayout(clazz), args);
	}
	
	/**
	 * Convert Interface method arguments into a signature for foreign library function
	 * @param method A method which arguments are to be converted
	 * @return
	 */
	static MemoryLayout[] tolayouts(final Method method) {
		final Parameter [] params =  method.getParameters();
		final MemoryLayout [] args = new MemoryLayout[params.length];
		int i = 0;
		while (i<params.length) {
			args[i] = Converters.toLayout(params[0].getType());
			i++;
		}
		return args;
	}		
}
