/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox.callback;

import java.lang.foreign.MemorySegment;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import io.greenscreens.foreign.Callback;

/**
 * Callback function definition
 */
public interface IStringCallback {

    @Callback
    void callback(final MemorySegment converter, final MemorySegment value);

    void result(final String value);

    public static IStringCallback create(final Logger logger, final Level level) {
        return new StringCallback(logger, level);
    }

    public static IStringCallback asInfo(final Logger logger) {
        return new StringCallback(logger, Level.INFO);
    }

    public static IStringCallback asWarn(final Logger logger) {
        return new StringCallback(logger, Level.WARN);
    }

    public static IStringCallback asError(final Logger logger) {
        return new StringCallback(logger, Level.ERROR);
    }

    public static IStringCallback asDebug(final Logger logger) {
        return new StringCallback(logger, Level.DEBUG);
    }

    public static IStringCallback asTrace(final Logger logger) {
        return new StringCallback(logger, Level.TRACE);
    }
}