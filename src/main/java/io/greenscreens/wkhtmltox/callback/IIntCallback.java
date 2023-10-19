/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox.callback;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.Callback;

/**
 * Processing callback implementation
 */
public interface IIntCallback {
	
	@Callback
	void callback(final MemorySegment converter, final int value);

	public static IIntCallback instance() {
		return new IntCallback();
	}
}
