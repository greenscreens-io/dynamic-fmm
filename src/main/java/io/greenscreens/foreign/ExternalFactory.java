/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;
import java.lang.reflect.Proxy;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

/**
 * Main dynamic foreign function engine.  
 */
public enum ExternalFactory {
;

	/**
	 * Pass an Interface for its methods to map to the foreign library.
	 * @param <T>
	 * @param caller
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T create(final Class<T> caller) {

		final ExternalInvocationHandler handler = new ExternalInvocationHandler(caller);

		final Object instance = Proxy.newProxyInstance(
				  caller.getClassLoader(),
				  new Class<?> [] {caller},
				  handler);

		return (T) instance;

	}

	/**
	 * UTF16 support
	 * @param wide
	 * @return
	 */
	public static String fromWideString(final MemorySegment wide) {
		final CharBuffer cb = wide.asByteBuffer().order(ByteOrder.nativeOrder()).asCharBuffer();
		while (cb.hasRemaining()) if (cb.get() == 0) break;
		return cb.limit(cb.position()).rewind().toString();
	}

	/**
	 * UTF16 support
	 * @param s
	 * @param allocator
	 * @return
	 */
	public static MemorySegment toWideString(final String s, final SegmentAllocator allocator) {
		final MemorySegment ms = allocator.allocateArray(ValueLayout.JAVA_CHAR, s.length() + 1);
		ms.asByteBuffer().order(ByteOrder.nativeOrder()).asCharBuffer().put(s).put('\0');
		return ms;
	}
}
