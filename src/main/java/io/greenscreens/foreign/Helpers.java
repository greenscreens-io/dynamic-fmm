/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.greenscreens.foreign.annotations.Callback;

/**
 * Internal generic data converters
 */
enum Helpers {
    ;

    /**
     * Detect if current OS is Windows
     *
     * @return
     */
    static boolean isWin() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Check if class is void (method return type)
     *
     * @param type
     * @return
     */
    static boolean isVoid(final Class<?> type) {
        return void.class.equals(type) || Void.class.equals(type);
    }

    /**
     * Convert class to base type. If it is array, return array type.
     *
     * @param type
     * @return
     */
    static Class<?> toType(final Class<?> type) {
        return type.isArray() ? type.arrayType().componentType() : type;
    }

    /**
     * UTF16 support
     *
     * @param wide
     * @return
     */
    public static String fromWideString(final MemorySegment wide) {
        final CharBuffer cb = wide.asByteBuffer().order(ByteOrder.nativeOrder()).asCharBuffer();
        while (cb.hasRemaining()) {
            if (cb.get() == 0) {
                break;
            }
        }
        return cb.limit(cb.position()).rewind().toString();
    }

    /**
     * UTF16 support
     *
     * @param s
     * @param allocator
     * @return
     */
    public static MemorySegment toWideString(final String s, final SegmentAllocator allocator) {
        final MemorySegment ms = allocator.allocateArray(ValueLayout.JAVA_CHAR, s.length() + 1);
        ms.asByteBuffer().order(ByteOrder.nativeOrder()).asCharBuffer().put(s).put('\0');
        return ms;
    }

    /**
     * Boolean array support
     *
     * @param bytes
     * @return
     */
    public static boolean[] toBoolean(final byte[] bytes) {
        final boolean[] bits = new boolean[bytes.length];
        int i = bytes.length;
        while (--i >= 0) {
            bits[i] = (bytes[i] & 0x01) == 1;
        }
        return bits;
    }

    /**
     * Boolean array support
     *
     * @param bits
     * @return
     */
    public static byte[] toBytes(final boolean[] bits) {
        final byte[] bytes = new byte[bits.length];
        int i = bits.length;
        while (--i >= 0) {
            bytes[i] = (byte) (bits[i] ? 1 : 0);
        }
        return bytes;
    }

    /**
     * Convert primitive wrapper class instance to a primitive value
     *
     * @param obj
     * @return
     */
    static Object toPrimitive(final Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return switch (obj) {
            case Byte b ->
                b.byteValue();
            case Boolean o ->
                o.booleanValue();
            case Character c ->
                c.charValue();
            case Double d ->
                d.doubleValue();
            case Float f ->
                f.floatValue();
            case Integer i ->
                i.intValue();
            case Long l ->
                l.longValue();
            case Short s ->
                s.shortValue();
            default ->
                obj;
        };
    }

    /**
     * Normalize string, preventing null
     *
     * @param val
     * @return
     */
    static String normalize(final String val) {
        return Objects.isNull(val) ? "" : val.trim();
    }

    /**
     * Find all methods marked as callbacks
     *
     * @param clazz
     * @return
     */
    static Collection<Method> getCallbacks(final Class<?> clazz) {
        return Stream.of(clazz.getMethods()).filter(m -> Objects.nonNull(m.getAnnotation(Callback.class))).collect(Collectors.toList());
    }

    /**
     * Find index of a first variadic argument; -1 if none
     *
     * @param method
     * @return
     */
    static int variadic(final Method method) {
        final AtomicInteger id = new AtomicInteger(-1);
        return Stream.of(method.getParameters()).map(p -> p.isVarArgs() ? id.incrementAndGet() : -1).findFirst().orElse(-1);
    }

}
