/*
 * Copyright (C) 2015, 2025 Green Screens Ltd.
 */
package io.greenscreens.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * Helper class to work with byte arrays and ByteBuffers
 */
public enum ByteUtil {
;
	protected static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	/**
	 * Convert ByteBuffer to HEX string
	 * @param buffer
	 * @return
	 */
	public static String bufferToHex(final ByteBuffer buffer) {

		if (Objects.nonNull(buffer)) {
			final int pos = buffer.position();
			final byte[] b = new byte[buffer.limit()];
			buffer.rewind();
			buffer.get(b);
			buffer.position(pos);
			return ByteUtil.bytesToHex(b);
		}

		return null;
	}
	
	/**
	 * Converts byte array to hex string
	 *
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(final byte[] bytes) {

		final char[] hexChars = new char[bytes.length * 2];
		int v = 0;

		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		return new String(hexChars);
	}

	/**
	 * Convert char array to hex string
	 *
	 * @param bytes
	 * @return
	 */
	public static String charsToHex(final char[] bytes) {

		final char[] hexChars = new char[bytes.length * 2];
		int v = 0;

		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j];
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		return new String(hexChars);
	}

	/**
	 * Converts hex string into byte array
	 *
	 * @param s
	 * @return
	 */
	public static byte[] fromHexAsBytes(final String s) {

		final int len = s.length();
		final byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Clone ByteBuffer
	 *
	 * @param original
	 * @return
	 */
	public static ByteBuffer clone(final ByteBuffer original) {
		final ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		original.rewind();// copy from the beginning
		clone.put(original);
		original.rewind();
		clone.flip();
		return clone;
	}

    public static byte[] toBytes(final ByteBuffer buffer) {
        return getBytesFrom(buffer, 0, buffer.limit());
    }
    
    public static byte[] getBytesFrom(final ByteBuffer buffer, final int position, final int len) {
        final byte[] data = new byte[len];
        buffer.rewind();
        buffer.position(position);
        buffer.get(data);
        return data;
    }
    
    public static byte[] fromBase64(final String data) {
        return Base64.getDecoder().decode(data);
    }

    public static String toBase64(final byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] fromBase64Url(final String data) {
        return Base64.getUrlDecoder().decode(data);
    }

    public static String toBase64Url(final byte[] data) {       
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static byte[] digest(final String mode, final byte[] bytes) {
        byte[] result = null;
        try {
            final MessageDigest md = MessageDigest.getInstance(mode);
            result = md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            result = new byte[0];
        }

        return result;

    }    
}
