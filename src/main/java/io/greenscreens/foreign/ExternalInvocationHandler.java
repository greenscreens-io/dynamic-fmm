/*
* Copyright (C) 2015, 2024 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import io.greenscreens.foreign.annotations.Callback;
import io.greenscreens.foreign.annotations.External;
import io.greenscreens.foreign.annotations.GarbageCollector;
import io.greenscreens.foreign.annotations.Size;

/**
 * Dynamic engine to intercept interface calls and map them to the external
 * library methods.
 */
final class ExternalInvocationHandler implements InvocationHandler, AutoCloseable {

    private final Thread hook;
    private final Class<?> caller;
    private final Map<Method, MethodHandle> cache;
    private final Map<String, MethodHandle> collectors;

    private final Arena arena;
    private final SymbolLookup symbolLookup;
    private final CallbackGenerator callbacks;
    private final AtomicBoolean closed = new AtomicBoolean();

    /**
     * Main constructor, initialize Interface wrapper for remote library
     *
     * @param caller
     */
    ExternalInvocationHandler(final Class<?> caller) {
        super();
        this.caller = caller;
        this.arena = Arena.ofShared();
        this.callbacks = CallbackGenerator.instance();
        this.symbolLookup = SymbolLookup.libraryLookup(findLib(), arena);
        this.cache = ForeignGenerator.generate(symbolLookup, caller);
        this.collectors = filterGC();
        this.hook = new Thread(() -> ExternalInvocationHandler.this.release());
        Runtime.getRuntime().addShutdownHook(hook);
    }

    private Map<String, MethodHandle> filterGC() {
        return cache.entrySet().stream()
                .filter(e -> e.getKey().isAnnotationPresent(GarbageCollector.class))
                .collect(Collectors.toMap(k -> k.getKey().getAnnotation(GarbageCollector.class).value(), v -> v.getValue()));
    }

    /**
     * Main Interface interceptor method where all magic happens.
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (!cache.containsKey(method)) {
            throw UnavailableException.create();
        }
        final MethodHandle handle = cache.get(method);
        final Object[] arguments = wrap(method, args);
        final Object ret = Objects.isNull(args) ? handle.invoke() : handle.invoke(arguments);
        return unwrap(proxy, method, ret, args);
    }

    /**
     * Unwrap result with support for array of primitive types or string
     * Converts MemorySegment to actual type if supported.
     *
     * @param method
     * @param ret Unwrapped data type
     * @return
     * @throws Throwable
     */
    private Object unwrap(final Object owner, final Method method, final Object ret, final Object[] args) throws Throwable {
        final int retLen = length(method, args);
        final Object o = Converters.fromExternal(method.getReturnType(), ret, retLen, arena);
        if (ret instanceof MemorySegment && !(o instanceof MemorySegment)) {
            final GarbageCollector gc = method.getAnnotation(GarbageCollector.class);
            final String key = Objects.nonNull(gc) ? gc.value() : "";
            final MethodHandle gcHandle = collectors.get(key);
            if (Objects.nonNull(gcHandle)) {
                gcHandle.invoke(new Object[]{ret});
            } else {
                System.err.println("!! Warning !! memory leak might happen in foreign functions when mapping a pointer to the Java type and not releaseing the remote pointer!");
            }
        }
        return o;
    }

    /**
     * Wrap arguments into a proper types for foreign function calls
     *
     * @param method
     * @param args
     * @return
     * @throws IllegalAccessException
     */
    private Object[] wrap(final Method method, final Object[] args) throws IllegalAccessException {

        if (Objects.isNull(args)) {
            return args;
        }

        final Parameter[] params = method.getParameters();
        final Object[] arguments = new Object[args.length];
        final Arena gcarena = Arena.ofAuto();

        int i = 0;

        do {

            final Object argument = args[i];
            final Parameter param = params[i];
            final boolean isCallback = param.isAnnotationPresent(Callback.class);

            if (isCallback) {
                arguments[i] = callbacks.initCallback(param, argument, arena);
            } else {
                arguments[i] = Converters.toExternal(param, argument, gcarena);
            }

            i++;
        } while (i < args.length && i < params.length);

        return arguments;
    }

    /**
     * Return data type length of specified
     *
     * @param method
     * @param args
     * @return
     */
    private int length(final Method method, final Object[] args) {
        final Size size = method.getAnnotation(Size.class);
        if (Objects.isNull(size)) {
            return 0;
        }
        return (size.index() > -1) ? (int) args[size.index()] : size.value();
    }

    /**
     * Normalize external library name. If extension is not specified, proper
     * one will be set based on currently used OS.
     *
     * @return
     */
    private String findLib() {
        final External annotation = caller.getAnnotation(External.class);
        String lib = Helpers.normalize(annotation.name());
        if (lib.length() == 0) {
            lib = Helpers.normalize(System.getProperties().getProperty(annotation.property()));
        }
        if (Helpers.isWin()) {
            return lib.endsWith(".dll") ? lib : lib + ".dll";
        } else {
            return lib.endsWith(".so") ? lib : lib + ".so";
        }
    }

    private void release() {
        if (!closed.getAndSet(true)) arena.close();
    }
    
    /**
     * Auto release all resources. Automatically called when JVM exits.
     */
    public void close() {
        if (!closed.get()) Runtime.getRuntime().removeShutdownHook(hook);
        release();
    }

}
