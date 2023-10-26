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

import io.greenscreens.foreign.annotations.Callback;
import io.greenscreens.foreign.annotations.Trivial;

/**
 * Generate foreign function MethodHandle signatures
 */
enum ForeignGenerator {
    ;

    final static Linker linker = Linker.nativeLinker();

    /**
     * List of allowed method parameter and return types NOTE: Any class or
     * interface annotated with @Callback is also allowed.
     */
    private final static Class<?>[] ALLOWED_TYPES = {
        byte.class, boolean.class, char.class, int.class, long.class, float.class, double.class, short.class,
        Byte.class, Boolean.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Short.class,
        String.class, MethodHandle.class, MemorySegment.class, void.class, Void.class, ByteBuffer.class, CharBuffer.class,
        byte[].class, boolean[].class, char[].class, int[].class, long[].class, float[].class, double[].class, short[].class,};

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
        allowed(type).stream()
                .forEach(m -> store(cache, m, symbolLookup));
        return cache;
    }

    private static void store(final Map<Method, MethodHandle> cache, final Method method, final SymbolLookup symbolLookup) {

        try {
            final MethodHandle handle = ForeignGenerator.build(symbolLookup, method);
            cache.put(method, handle);
        } catch (Exception e) {
            throw new UnavailableException(method.toString(), e);
        }
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
        final Linker.Option[] opts = options(method);
        final MethodHandle handle = linker.downcallHandle(segment, descriptor, opts);
        final int count = method.getParameterCount();
        return count == 0 ? handle : handle.asSpreader(Object[].class, count);
    }

    /**
     * Generate foreign function calling options; support for variadic arguments
     * and performance optimizations
     *
     * @param method
     * @return
     */
    static Linker.Option[] options(final Method method) {
        final int id = Helpers.variadic(method);
        final boolean isTrivial = method.isAnnotationPresent(Trivial.class);
        int size = (isTrivial ? 1 : 0) + (id < 0 ? 0 : 1);
        final Linker.Option[] options = new Linker.Option[size];
        if (id > -1) {
            options[--size] = Linker.Option.firstVariadicArg(id);
        }
        if (isTrivial) {
            options[--size] = Linker.Option.isTrivial();
        }
        return options;
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

    /**
     * Verify if method is allowed for foreign function mapping. Method return
     * type and arguments must match one of allowed classes
     *
     * @param method
     * @return
     */
    static boolean isAllowed(final Method method) {
        final Class<?> type = Helpers.toType(method.getReturnType());
        return isAllowed(type)
                && Stream.of(method.getParameters())
                        .map(p -> isAllowed(p))
                        .count() == method.getParameterCount();
    }

    /**
     * Check if Method parameter is allowed
     *
     * @param parameter
     * @return
     */
    static boolean isAllowed(final Parameter parameter) {
        return isAllowed(Helpers.toType(parameter.getType())) || parameter.isAnnotationPresent(Callback.class);
    }

    /**
     * Check if class is one of allowed types
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
        final MemoryLayout[] args = toLayouts(method);
        return args.length == 0 ? FunctionDescriptor.ofVoid() : FunctionDescriptor.ofVoid(args);
    }

    static FunctionDescriptor buildReturnDescriptor(final Method method) {
        final Class<?> clazz = method.getReturnType();
        final MemoryLayout[] args = toLayouts(method);
        return args.length == 0 ? FunctionDescriptor.of(Converters.toLayout(clazz)) : FunctionDescriptor.of(Converters.toLayout(clazz), args);
    }

    /**
     * Convert Interface method arguments into a signature for foreign library
     * function
     *
     * @param method A method which arguments are to be converted
     * @return
     */
    static MemoryLayout[] toLayouts(final Method method) {
        final Parameter[] params = method.getParameters();
        final MemoryLayout[] args = new MemoryLayout[params.length];
        int i = -1;
        while (++i < params.length) {
            args[i] = Converters.toLayout(params[i].getType());
        }
        return args;
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
        do {
            args[i] = Converters.toLayout(params[i]);
        } while (++i < params.length);
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
        return FunctionDescriptor.of(Converters.toLayout(clazz), args);
    }

}
