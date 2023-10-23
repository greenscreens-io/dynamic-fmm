/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generate MethodHandle for callback annotated interfaces/class
 */
final class CallbackGenerator {

    private final Lookup lookup;
    private final Map<Class<?>, Collection<Method>> callbacks;

    public CallbackGenerator() {
        super();
        this.lookup = MethodHandles.lookup();
        this.callbacks = new ConcurrentHashMap<>();
    }

    /**
     * Generate MethodHandle from Method argument descriptor Parameter and bind
     * it to the owner instance
     *
     * @param param
     * @param owner
     * @param arena Temporary Arena.ofAuto allocation provided from above
     * @return
     * @throws IllegalAccessException
     */
    MemorySegment initCallback(final Parameter param, final Object owner, final Arena arena) throws IllegalAccessException {
        final MethodHandle handle = initCallback(param, owner);
        return ForeignGenerator.toPointer(handle, arena);
    }

    /**
     * Generate MethodHandle from Method argument descriptor Parameter and bind
     * it to the owner instance
     *
     * @param param
     * @param owner
     * @return
     * @throws IllegalAccessException
     */
    MethodHandle initCallback(final Parameter param, final Object owner) throws IllegalAccessException {
        final MethodHandle handle = initCallback(param);
        return Objects.nonNull(handle) ? handle.bindTo(owner) : handle;
    }

    /**
     * Generate MethodHandle from Method argument descriptor Parameter
     *
     * @param param
     * @return
     * @throws IllegalAccessException
     */
    MethodHandle initCallback(final Parameter param) throws IllegalAccessException {
        return param.isAnnotationPresent(Callback.class) ? initCallback(param.getType(), param.getAnnotation(Callback.class)) : null;
    }

    /**
     * Find Class Method that matches Callback annotation , and generate
     * MethodHandle
     *
     * @param clazz
     * @param callback
     * @return
     * @throws IllegalAccessException
     */
    MethodHandle initCallback(final Class<?> clazz, final Callback callback) throws IllegalAccessException {
        if (Objects.isNull(callback)) return null;
        return initCallback(clazz, callback.name());
    }

    /**
     * Find a proper method used as a callback and convert that method into a
     * "pointer" - a MethodHandle
     *
     * @param clazz Interface containing a callback method
     * @param name Name of the
     * @return
     * @throws IllegalAccessException
     */
    MethodHandle initCallback(final Class<?> clazz, final String name) throws IllegalAccessException {
        final Collection<Method> list = getCallbacks(clazz);
        final Optional<Method> method = list.stream()
                .filter(m -> m.getAnnotation(Callback.class).name().equals(name))
                .findFirst().or(() -> list.stream().findFirst());
        return method.isPresent() ? lookup.unreflect(method.get()) : null;
    }

    /**
     * Find all Interface @Callback annotated methods and cache them per class
     *
     * @param clazz
     * @return
     */
    Collection<Method> getCallbacks(final Class<?> clazz) {
        final Class<?> type = Helpers.toType(clazz);
        Collection<Method> list = callbacks.get(type);
        if (Objects.isNull(list)) {
            list = Helpers.getCallbacks(type);
            callbacks.put(type, list);
        }
        return list;
    }

    public static CallbackGenerator instance() {
        return new CallbackGenerator();
    }
}
