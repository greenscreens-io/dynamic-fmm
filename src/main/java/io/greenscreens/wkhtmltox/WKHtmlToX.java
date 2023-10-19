/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.Callback;
import io.greenscreens.foreign.External;
import io.greenscreens.wkhtmltox.callback.IIntCallback;
import io.greenscreens.wkhtmltox.callback.IStringCallback;

/**
 * WKHTMLTOX external native library functions mapping for Java
 */
@External(name="libs/wkhtmltox", property = "wkhtmltopdf.library.path")
public interface WKHtmlToX {
	
	int wkhtmltoimage_init(final int use_graphics) throws Throwable;

	int wkhtmltoimage_deinit() throws Throwable;
	
	MemorySegment wkhtmltoimage_create_global_settings() throws Throwable;

	void wkhtmltoimage_destroy_global_settings(final MemorySegment settings) throws Throwable;
	
	void wkhtmltoimage_set_global_setting(final MemorySegment settings, final String name, final String value) throws Throwable;
	
	MemorySegment wkhtmltoimage_create_converter(final MemorySegment settings, final String data) throws Throwable;

	void wkhtmltoimage_destroy_converter(final MemorySegment converter) throws Throwable;
	
	int wkhtmltoimage_convert(final MemorySegment converter) throws Throwable;

	String wkhtmltoimage_progress_string(final MemorySegment converter) throws Throwable;

	void wkhtmltoimage_set_warning_callback(final MemorySegment converter, @Callback(name = "callback") final IStringCallback callback) throws Throwable;

	void wkhtmltoimage_set_error_callback(final MemorySegment converter, @Callback(name = "callback") final IStringCallback callback) throws Throwable;
	
	void wkhtmltoimage_set_progress_changed_callback(final MemorySegment converter, @Callback(name = "callback") final IIntCallback callback) throws Throwable;
	
	void wkhtmltoimage_set_finished_callback(final MemorySegment converter, @Callback(name = "callback") final IIntCallback callback) throws Throwable;
	
	int wkhtmltoimage_http_error_code(final MemorySegment converter) throws Throwable;
	
	int wkhtmltopdf_init(final int use_graphics) throws Throwable;
	
	MemorySegment wkhtmltopdf_create_global_settings() throws Throwable;

	void wkhtmltopdf_destroy_global_settings(final MemorySegment settings) throws Throwable;

	MemorySegment wkhtmltopdf_create_object_settings() throws Throwable;
	
	void wkhtmltopdf_destroy_object_settings(final MemorySegment os) throws Throwable;

	void wkhtmltopdf_set_global_setting(final MemorySegment settings, final String name, final String value) throws Throwable;
	
	void wkhtmltopdf_set_object_setting(final MemorySegment os, final String name, final String value) throws Throwable;

	MemorySegment wkhtmltopdf_create_converter(final MemorySegment settings) throws Throwable;

	void wkhtmltopdf_destroy_converter(final MemorySegment converter) throws Throwable;

	int wkhtmltopdf_convert(final MemorySegment converter) throws Throwable;

	String wkhtmltopdf_progress_string(final MemorySegment converter) throws Throwable;

	void wkhtmltopdf_set_progress_changed_callback(final MemorySegment converter, @Callback(name = "callback") final IIntCallback callback) throws Throwable;
	
	void wkhtmltopdf_set_warning_callback(final MemorySegment converter, @Callback(name = "callback") final IStringCallback callback) throws Throwable;
	
	void wkhtmltopdf_set_error_callback(final MemorySegment converter, @Callback(name = "callback") final IStringCallback callback) throws Throwable;
	
	void wkhtmltopdf_set_finished_callback(final MemorySegment converter, @Callback(name = "callback") final IIntCallback callback) throws Throwable;
	
	int wkhtmltopdf_http_error_code(final MemorySegment converter) throws Throwable;
	
	void wkhtmltopdf_add_object(final MemorySegment converter, final MemorySegment objectSetting, final String data) throws Throwable;
	
	String wkhtmltopdf_version() throws Throwable;

	int wkhtmltopdf_deinit() throws Throwable;
	
}