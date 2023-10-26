/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox.callback;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.annotations.mvn cleanCallback;

/**
 * Processing callback implementation
 */
public interface IIntCallback {

    // named callback, not require in a single method callback
    // can be used when multiple callbacks used
    @Callback("callback")
    void callback(final MemorySegment converter, final int value);

    public static IIntCallback instance() {
        return new IntCallback();
    }
}
