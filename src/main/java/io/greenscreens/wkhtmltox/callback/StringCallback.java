/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox.callback;

import java.lang.foreign.MemorySegment;

import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * Simple logging callback
 */
final class StringCallback implements IStringCallback {

    private final Logger logger;
    private final Level level;

    StringCallback(final Logger logger, final Level level) {
        super();
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void callback(MemorySegment converter, MemorySegment value) {
        try {
            final String msg = value.reinterpret(Integer.MAX_VALUE).getUtf8String(0);
            result(msg);
        } catch (Exception e) {
            final String msg = e.getMessage();
            logger.error(msg);
            logger.debug(msg, e);
        }
    }

    @Override
    public void result(String value) {
        switch (level) {
            case INFO: {
                logger.info(value);
                break;
            }
            case WARN: {
                logger.warn(value);
                break;
            }
            case ERROR: {
                logger.error(value);
                break;
            }
            case DEBUG: {
                logger.debug(value);
                break;
            }
            default:
                throw new IllegalArgumentException("Unexpected value: " + level);
        }
    }

}
