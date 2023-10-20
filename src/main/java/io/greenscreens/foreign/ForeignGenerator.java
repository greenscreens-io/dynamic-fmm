/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generate foreign function MethodHandle signatures
 */
enum ForeignGenerator {
    ;

	/**
	 * List of allowed method parameter and return types
	 * NOTE: Any class or interface annotated with @Callback is also allowed.
	 */
	private final static Class<?>[] ALLOWED_TYPES = {
        byte.class, boolean.class, char.class, int.class, long.class, float.class, double.class, short.class,
        Byte.class, Boolean.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Short.class,
        String.class, MethodHandle.class, MemorySegment.class, void.class, Void.class,
        ByteBuffer.class, CharBuffer.class
    };

    /**
     * Generate MetohdHandles from provided Interface, used for foreign
     * functions call
     *
     * @param symbolLookup
     * @param type
     * @return
     */
    static Map<Method, MethodHandle> generate(final SymbolLookup symbolLookup, final Class<?> type) {
        final Map<Method, MethodHandle> cache = new ConcurrentHashMap<>();
        allowed(type).stream().forEach(m -> cache.put(m, ForeignGenerator.build(symbolLookup, m)));
        return cache;
    }

    /**
     * Build MethodHandler signature for foreign library function
     *
     * @param method
     * @return
     */
    static MethodHandle build(final SymbolLookup lookup, final Method method) {
        final MemorySegment segment = lookup.find(method.getName()).orElseThrow();
        final FunctionDescriptor descriptor = ForeignGenerator.buildDescriptor(method);
        final MethodHandle handle = Linker.nativeLinker().downcallHandle(segment, descriptor);
        final int count = method.getParameterCount();
        return count == 0 ? handle : handle.asSpreader(Object[].class, count);
    }

    /**
     * Filter out all allowed methods for foreign functions based on
     * ALLOWED_TYPES
     *
     * @param type
     * @return
     */
    static Collection<Method> allowed(final Class<?> type) {
        return Stream.of(type.getMethods()).filter(m -> isAllowed(m)).collect(Collectors.toList());
    }

    static boolean isAllowed(final Method method) {
        final Class<?> type = Helpers.toType(method.getReturnType());
        return isAllowed(type)
                && Stream.of(method.getParameters())
                        .map(c -> isAllowed(Helpers.toType(c.getType())) || c.isAnnotationPresent(Callback.class))
                        .count() == method.getParameterCount();
    }

    /**
     * Check if
     *
     * @param type
     * @return
     */
    static boolean isAllowed(final Class<?> type) {
        return Stream.of(ALLOWED_TYPES).anyMatch(type::equals);
    }

    /**
     * Build a descriptor from an Interface method, required to create a foreign
     * call
     *
     * @param method
     * @return
     */
    static FunctionDescriptor buildDescriptor(final Method method) {
        final boolean isVoid = void.class.equals(method.getReturnType());
        return isVoid ? buildVoidDescriptor(method) : buildReturnDescriptor(method);
    }

    static FunctionDescriptor buildVoidDescriptor(final Method method) {
        final MemoryLayout[] args = tolayouts(method);
        return args.length == 0 ? FunctionDescriptor.ofVoid() : FunctionDescriptor.ofVoid(args);
    }

    static FunctionDescriptor buildReturnDescriptor(final Method method) {
        final Class<?> clazz = method.getReturnType();
        final MemoryLayout[] args = tolayouts(method);
        return args.length == 0 ? FunctionDescriptor.of(toLayout(clazz)) : FunctionDescriptor.of(toLayout(clazz), args);
    }

    /**
     * Convert Interface method arguments into a signature for foreign library
     * function
     *
     * @param method A method which arguments are to be converted
     * @return
     */
    static MemoryLayout[] tolayouts(final Method method) {
        final Parameter[] params = method.getParameters();
        final MemoryLayout[] args = new MemoryLayout[params.length];
        int i = 0;
        while (i < params.length) {
            args[i] = toLayout(params[0].getType());
            i++;
        }
        return args;
    }

    /**
     * Foreign functions can receive either a primitive types or a "pointer"
     * represented by Java MemoryAddress class
     *
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

    /**
     * Convert callback method arguments into a signature for foreign library
     * function
     *
     * @param handle
     * @return
     */
    static MemoryLayout[] tolayouts(final MethodHandle handle) {
        final Class<?>[] params = handle.type().parameterArray();
        final MemoryLayout[] args = new MemoryLayout[params.length];
        int i = 0;
        while (i < params.length) {
            args[i] = toLayout(params[i]);
            i++;
        }
        return args;
    }

    /**
     * Convert callback method to a "pointer"
     *
     * @param handle
     * @return
     */
    static MemorySegment toPointer(final MethodHandle handle, final Arena arena) {
        if (Objects.isNull(handle)) {
            return null;
        }
        final FunctionDescriptor descriptor = buildDescriptor(handle);
        return Linker.nativeLinker().upcallStub(handle, descriptor, arena);
    }

    /**
     * Build a descriptor from a callback method, required to create a callback
     * pointer
     *
     * @param handle
     * @return
     */
    static FunctionDescriptor buildDescriptor(final MethodHandle handle) {
        if (Objects.isNull(handle)) {
            return null;
        }
        final boolean isVoid = void.class.equals(handle.type().returnType());
        return isVoid ? buildVoidDescriptor(handle) : buildReturnDescriptor(handle);
    }

    static FunctionDescriptor buildVoidDescriptor(final MethodHandle handle) {
        final MemoryLayout[] args = tolayouts(handle);
        return FunctionDescriptor.ofVoid(args);
    }

    static FunctionDescriptor buildReturnDescriptor(final MethodHandle handle) {
        final Class<?> clazz = handle.type().returnType();
        final MemoryLayout[] args = tolayouts(handle);
        return FunctionDescriptor.of(toLayout(clazz), args);
    }

}