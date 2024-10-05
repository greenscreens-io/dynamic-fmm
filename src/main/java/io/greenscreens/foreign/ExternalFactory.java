/*
* Copyright (C) 2015, 2024 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.reflect.Proxy;

/**
 * Main dynamic foreign function engine.
 */
public enum ExternalFactory {
    ;

    /**
     * Pass an Interface for it's methods to map to the foreign library.
     * 
     * @param <T>
     * @param caller
     * @return
     */
    public static <T> T create(final Class<T> caller) {
        return createClosable(caller).get();
    }

    /**
     * Pass an Interface for it's methods to map to the foreign library.
     * This method create reloadable version.  
     * @param <T>
     * @param caller
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Instance<T> createClosable(final Class<T> caller) {
        final ExternalInvocationHandler handler = new ExternalInvocationHandler(caller);
        final T t = (T) Proxy.newProxyInstance(caller.getClassLoader(), new Class<?>[] { caller }, handler);
        return new Instance<T>(t, handler);
    }
}
