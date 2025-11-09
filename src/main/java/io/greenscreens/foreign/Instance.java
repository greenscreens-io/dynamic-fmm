/*
* Copyright (C) 2015, 2025 Green Screens Ltd.
*/
package io.greenscreens.foreign;

/**
 * Alternative version allowing to reload DDL ??!
 * @param <T>
 */
public class Instance<T> implements AutoCloseable {
    
    final T proxy;
    final ExternalInvocationHandler handler;
    
    Instance(final T proxy, final ExternalInvocationHandler handler) {
        super();
        this.proxy = proxy;
        this.handler = handler;
    }
    
    public T get() {
        return proxy;
    }

    @Override
    public void close() throws Exception {
        handler.close();
    }
    
}
