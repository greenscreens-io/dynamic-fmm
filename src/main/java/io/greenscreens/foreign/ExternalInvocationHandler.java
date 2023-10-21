/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Objects;

/**
 * Dynamic engine to intercept interface calls and map them to the external
 * library methods.
 */
final class ExternalInvocationHandler implements InvocationHandler {

    private final Thread hook;
    private final Class<?> caller;
    private final Map<Method, MethodHandle> cache;

    private final Arena arena;
    private final SymbolLookup symbolLookup;
    private final CallbackGenerator callbacks;

    /**
     * Main constructor, initialize Interface wrapper for remote library
     *
     * @param caller
     */
    public ExternalInvocationHandler(final Class<?> caller) {
        super();
        this.caller = caller;
        this.arena = Arena.ofShared();
        this.callbacks = CallbackGenerator.instance();
        this.symbolLookup = SymbolLookup.libraryLookup(findLib(), arena);
        this.cache = ForeignGenerator.generate(symbolLookup, caller);
        this.hook = new Thread(() -> ExternalInvocationHandler.this.close());
        Runtime.getRuntime().addShutdownHook(hook);
    }

    /**
     * Auto release all resources. Automatically called when JVM exits.
     */
    private void close() {
        if (Objects.nonNull(arena)) {
            arena.close();
        }
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
        if (isWin()) {
            return lib.endsWith(".dll") ? lib : lib + ".dll";
        } else {
            return lib.endsWith(".so") ? lib : lib + ".so";
        }
    }

    /**
     * Detect if current OS i Windows
     *
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
        if (!cache.containsKey(method)) {
            throw UnavalableException.create();
        }
        final MethodHandle handle = cache.get(method);
        final Object[] arguments = wrap(method, args);
        final Object ret = Objects.isNull(args) ? handle.invoke() : handle.invoke(arguments);
        return unwrap(method, ret);
    }

    /**
     * Unwrap result with support for array of primitive types or string
     * Converts MemorySegment to actual type if supported.
     *
     * @param method
     * @param ret Unwrapped data type
     * @return
     */
    private Object unwrap(final Method method, final Object ret) {
        return Converters.fromExternal(method.getReturnType(), ret, arena);
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

}
