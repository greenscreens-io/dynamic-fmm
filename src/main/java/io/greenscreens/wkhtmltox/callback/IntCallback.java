/*
* Copyright (C) 2015, 2025 Green Screens Ltd.
*/
package io.greenscreens.wkhtmltox.callback;

import java.lang.foreign.MemorySegment;

/**
 * Simple processing callback - undocumented
 */
final class IntCallback implements IIntCallback {

    @Override
    public void callback(final MemorySegment converter, final int value) {
        System.out.println(value);
    }

}
