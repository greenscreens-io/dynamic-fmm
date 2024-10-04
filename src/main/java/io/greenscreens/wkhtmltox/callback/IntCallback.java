/*
* Copyright (C) 2015, 2024 Green Screens Ltd.
*/
package io.greenscreens.wkhtmltox.callback;

import java.lang.foreign.MemorySegment;

/**
 * Simple processing callback - undocumented
 */
final class IntCallback implements IIntCallback {

    @Override
    public void callback(MemorySegment converter, int value) {
        System.out.println(value);
    }

}
