/*
 * Copyright (C) 2015, 2024 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox;

import java.lang.foreign.MemorySegment;

import io.greenscreens.foreign.annotations.Callback;
import io.greenscreens.foreign.annotations.External;
import io.greenscreens.foreign.annotations.Trivial;
import io.greenscreens.wkhtmltox.callback.IIntCallback;
import io.greenscreens.wkhtmltox.callback.IStringCallback;

/**
 * WKHTMLTOX external native library functions mapping for Java
 */
@External(name = "libs/wkhtmltox", property = "wkhtmltopdf.library.path")
public interface WKHtmlToX {

    ;

    //IMAGE ENGINE

    @Trivial
    public int wkhtmltoimage_init(final int use_graphics) throws Throwable;

    @Trivial
    public int wkhtmltoimage_deinit() throws Throwable;

    @Trivial
    public MemorySegment wkhtmltoimage_create_global_settings() throws Throwable;

    @Trivial
    public void wkhtmltoimage_destroy_global_settings(final MemorySegment settings) throws Throwable;

    @Trivial
    public void wkhtmltoimage_set_global_setting(final MemorySegment settings, final String name, final String value) throws Throwable;

    @Trivial
    public MemorySegment wkhtmltoimage_create_converter(final MemorySegment settings, final String data) throws Throwable;

    @Trivial
    public void wkhtmltoimage_destroy_converter(final MemorySegment converter) throws Throwable;

    @Trivial
    public int wkhtmltoimage_http_error_code(final MemorySegment converter) throws Throwable;

    public int wkhtmltoimage_convert(final MemorySegment converter) throws Throwable;

    public String wkhtmltoimage_progress_string(final MemorySegment converter) throws Throwable;

    public void wkhtmltoimage_set_warning_callback(final MemorySegment converter, @Callback final IStringCallback callback) throws Throwable;

    public void wkhtmltoimage_set_error_callback(final MemorySegment converter, @Callback final IStringCallback callback) throws Throwable;

    public void wkhtmltoimage_set_progress_changed_callback(final MemorySegment converter, @Callback("callback") final IIntCallback callback) throws Throwable;

    public void wkhtmltoimage_set_finished_callback(final MemorySegment converter, @Callback("callback") final IIntCallback callback) throws Throwable;

    // PDF ENGINE
    @Trivial
    public String wkhtmltopdf_version() throws Throwable;

    @Trivial
    public int wkhtmltopdf_init(final int use_graphics) throws Throwable;

    @Trivial
    public int wkhtmltopdf_deinit() throws Throwable;

    @Trivial
    public MemorySegment wkhtmltopdf_create_global_settings() throws Throwable;

    @Trivial
    public MemorySegment wkhtmltopdf_create_object_settings() throws Throwable;

    @Trivial
    public void wkhtmltopdf_destroy_global_settings(final MemorySegment settings) throws Throwable;

    @Trivial
    public void wkhtmltopdf_destroy_object_settings(final MemorySegment os) throws Throwable;

    @Trivial
    public void wkhtmltopdf_set_global_setting(final MemorySegment settings, final String name, final String value) throws Throwable;

    @Trivial
    public void wkhtmltopdf_set_object_setting(final MemorySegment os, final String name, final String value) throws Throwable;

    @Trivial
    public MemorySegment wkhtmltopdf_create_converter(final MemorySegment settings) throws Throwable;

    @Trivial
    public void wkhtmltopdf_destroy_converter(final MemorySegment converter) throws Throwable;

    @Trivial
    public int wkhtmltopdf_http_error_code(final MemorySegment converter) throws Throwable;

    public void wkhtmltopdf_add_object(final MemorySegment converter, final MemorySegment objectSetting, final String data) throws Throwable;

    public int wkhtmltopdf_convert(final MemorySegment converter) throws Throwable;

    public String wkhtmltopdf_progress_string(final MemorySegment converter) throws Throwable;

    public void wkhtmltopdf_set_warning_callback(final MemorySegment converter, @Callback final IStringCallback callback) throws Throwable;

    public void wkhtmltopdf_set_error_callback(final MemorySegment converter, @Callback final IStringCallback callback) throws Throwable;

    public void wkhtmltopdf_set_progress_changed_callback(final MemorySegment converter, @Callback("callback") final IIntCallback callback) throws Throwable;

    public void wkhtmltopdf_set_finished_callback(final MemorySegment converter, @Callback("callback") final IIntCallback callback) throws Throwable;

}
