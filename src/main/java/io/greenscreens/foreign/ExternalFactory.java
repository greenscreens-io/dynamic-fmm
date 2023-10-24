/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.foreign;

import java.lang.reflect.Proxy;

/**
 * Main dynamic foreign function engine.
 */
public enum ExternalFactory {
    ;

    /**
     * Pass an Interface for its methods to map to the foreign library.
     * 
     * @param <T>
     * @param caller
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(final Class<T> caller) {
        final ExternalInvocationHandler handler = new ExternalInvocationHandler(caller);
        return (T) Proxy.newProxyInstance(caller.getClassLoader(), new Class<?>[] { caller }, handler);
    }

}
