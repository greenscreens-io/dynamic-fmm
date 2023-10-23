/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Objects;

/**
 * Internal generic data converters
 */
enum Converters {
    ;

    /**
     * Foreign functions can receive either a primitive types or 
     * a "pointer" represented by Java MemoryAddress class 
     * @param clazz Java type to be converted
     * @return Foreign type
     */
    static MemoryLayout toLayout(final Class<?> clazz) {
        MemoryLayout layout = null;
        if (clazz.isPrimitive()) {
            if (byte.class.equals(clazz)) {
                layout = ValueLayout.JAVA_BYTE;
            } else if (boolean.class.equals(clazz)) {
                layout = ValueLayout.JAVA_BOOLEAN;
            } else if (char.class.equals(clazz)) {
                layout = ValueLayout.JAVA_CHAR;
            } else if (double.class.equals(clazz)) {
                layout = ValueLayout.JAVA_DOUBLE;
            } else if (float.class.equals(clazz)) {
                layout = ValueLayout.JAVA_FLOAT;
            } else if (int.class.equals(clazz)) {
                layout = ValueLayout.JAVA_INT;
            } else if (long.class.equals(clazz)) {
                layout = ValueLayout.JAVA_LONG;
            } else if (short.class.equals(clazz)) {
                layout = ValueLayout.JAVA_SHORT;
            }
        } else {
            layout = ValueLayout.ADDRESS;
        }
        return layout;
    }

    /**
     * Convert method argument from java to format for foreign function call
     *
     * @param param
     * @param data
     * @param arena
     * @return
     */
    static Object toExternal(final Parameter param, final Object data, final Arena arena) {
        return toExternal(param.getType(), data, arena);
    }

    /**
     * Convert method argument from java to format for foreign function call
     *
     * @param klass
     * @param data
     * @param arena
     * @return
     */
    static Object toExternal(final Class<?> klass, final Object data, final Arena arena) {

        final boolean isArray = klass.isArray();
        final Class<?> type = Helpers.toType(klass);
        if (!isArray && type.isPrimitive()) {
            return data;
        }

        if (byte[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_BYTE, Objects.isNull(data) ? new byte[0] : (byte[]) data);
        } else if (boolean[].class.equals(klass)) {
            final byte[] raw = Helpers.toBytes(Objects.isNull(data) ? new boolean[0] : (boolean[]) data);
            return arena.allocateArray(ValueLayout.JAVA_BYTE, raw);
        } else if (char[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_CHAR, Objects.isNull(data) ? new char[0] : (char[]) data);
        } else if (double[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_DOUBLE, Objects.isNull(data) ? new double[0] : (double[]) data);
        } else if (float[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_FLOAT, Objects.isNull(data) ? new float[0] : (float[]) data);
        } else if (int[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_INT, Objects.isNull(data) ? new int[0] : (int[]) data);
        } else if (long[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_LONG, Objects.isNull(data) ? new long[0] : (long[]) data);
        } else if (short[].class.equals(klass)) {
            return arena.allocateArray(ValueLayout.JAVA_SHORT, Objects.isNull(data) ? new short[0] : (short[]) data);

        } else if (Boolean.class.equals(type)) {
            return ((Boolean) data).booleanValue();
        } else if (Byte.class.isAssignableFrom(type)) {
            return ((Byte) data).byteValue();
        } else if (Character.class.equals(type)) {
            return ((Character) data).charValue();
        } else if (Double.class.isAssignableFrom(type)) {
            return ((Double) data).doubleValue();
        } else if (Float.class.isAssignableFrom(type)) {
            return ((Float) data).floatValue();
        } else if (Integer.class.isAssignableFrom(type)) {
            return ((Integer) data).intValue();
        } else if (Long.class.isAssignableFrom(type)) {
            return ((Long) data).longValue();
        } else if (Short.class.isAssignableFrom(type)) {
            return ((Short) data).shortValue();

        } else if (MemorySegment.class.equals(type)) {
            return (MemorySegment) data;
        } else if (MethodHandle.class.equals(type)) {
            return ForeignGenerator.toPointer((MethodHandle) data, arena);
        } else if (String.class.equals(type)) {
            return arena.allocateUtf8String(Helpers.normalize((String) data));

        } else if (ByteBuffer.class.isAssignableFrom(type)) {
            final ByteBuffer buffer = (ByteBuffer) data;
            final byte[] raw = new byte[buffer.remaining()];
            buffer.get(raw);
            return toExternal(byte[].class, raw, arena);
        } else if (CharBuffer.class.isAssignableFrom(type)) {
            final CharBuffer buffer = (CharBuffer) data;
            final char[] raw = new char[buffer.remaining()];
            buffer.get(raw);
            return toExternal(char[].class, raw, arena);
        }

        throw new RuntimeException("Unsupported data type");
    }

    /**
     * Convert data received from foreign function call to Java data type
     *
     * @param klass
     * @param data
     * @param arena
     * @return
     */
    static Object fromExternal(final Class<?> klass, final Object data, final Arena arena) {
        if (Objects.isNull(data)) {
            return null;
        }
        final boolean isPointer = data instanceof MemorySegment;
        return isPointer ? fromExternal(klass, (MemorySegment) data, arena) : data;
    }

    /**
     * Convert "pointer" received from foreign function call to Java data type
     *
     * @param klass
     * @param data
     * @param arena
     * @return
     */
    static Object fromExternal(final Class<?> klass, final MemorySegment data, final Arena arena) {

        if (Objects.isNull(data)) {
            return null;
        }

        final Class<?> type = Helpers.toType(klass);
        if (Helpers.isVoid(type)) {
            return data;
        }

        if (byte[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_BYTE);
        } else if (boolean[].class.equals(klass)) {
            final byte[] raw = data.toArray(ValueLayout.JAVA_BYTE);
            return Helpers.toBoolean(raw);
        } else if (char[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_CHAR);
        } else if (double[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_DOUBLE);
        } else if (float[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_FLOAT);
        } else if (int[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_INT);
        } else if (long[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_LONG);
        } else if (short[].class.equals(klass)) {
            return data.toArray(ValueLayout.JAVA_SHORT);

        } else if (byte.class.equals(type)) {
            return data.get(ValueLayout.JAVA_BYTE, 0);
        } else if (boolean.class.equals(type)) {
            return data.get(ValueLayout.JAVA_BOOLEAN, 0);
        } else if (char.class.equals(type)) {
            return data.get(ValueLayout.JAVA_CHAR, 0);
        } else if (double.class.equals(type)) {
            return data.get(ValueLayout.JAVA_DOUBLE, 0);
        } else if (float.class.equals(type)) {
            return data.get(ValueLayout.JAVA_FLOAT, 0);
        } else if (int.class.equals(type)) {
            return data.get(ValueLayout.JAVA_INT, 0);
        } else if (long.class.equals(type)) {
            return data.get(ValueLayout.JAVA_LONG, 0);
        } else if (short.class.equals(type)) {
            return data.get(ValueLayout.JAVA_SHORT, 0);

        } else if (Boolean.class.equals(type)) {
            return Boolean.valueOf(data.get(ValueLayout.JAVA_BOOLEAN, 0));
        } else if (Byte.class.equals(type)) {
            return Byte.valueOf(data.get(ValueLayout.JAVA_BYTE, 0));
        } else if (Double.class.isAssignableFrom(type)) {
            return Double.valueOf(data.get(ValueLayout.JAVA_DOUBLE, 0));
        } else if (Float.class.isAssignableFrom(type)) {
            return Float.valueOf(data.get(ValueLayout.JAVA_FLOAT, 0));
        } else if (Integer.class.isAssignableFrom(type)) {
            return Integer.valueOf(data.get(ValueLayout.JAVA_INT, 0));
        } else if (Long.class.isAssignableFrom(type)) {
            return Long.valueOf(data.get(ValueLayout.JAVA_LONG, 0));
        } else if (Short.class.isAssignableFrom(type)) {
            return Short.valueOf(data.get(ValueLayout.JAVA_SHORT, 0));

        } else if (Character.class.equals(type)) {
            return Character.valueOf(data.get(ValueLayout.JAVA_CHAR, 0));
        } else if (String.class.equals(type)) {
            return data.reinterpret(Integer.MAX_VALUE, arena, null).getUtf8String(0);

        } else if (ByteBuffer.class.isAssignableFrom(type)) {
            final byte[] raw = (byte[]) fromExternal(byte[].class, data, arena);
            return ByteBuffer.wrap(raw);
        } else if (CharBuffer.class.isAssignableFrom(type)) {
            final char[] raw = (char[]) fromExternal(char[].class, data, arena);
            return CharBuffer.wrap(raw);
        }

        return data;
    }

}
