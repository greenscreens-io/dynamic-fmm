/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox;

import java.lang.foreign.MemorySegment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.greenscreens.foreign.ExternalFactory;
import io.greenscreens.wkhtmltox.callback.IIntCallback;
import io.greenscreens.wkhtmltox.callback.IStringCallback;

/**
 * Example usage for WKHTMLTOX library to generate PDF and IMAGE from provided
 * HTML file or URL
 */
public class Test {

    private static final Logger LOG = LoggerFactory.getLogger(Test.class);

    private static final String htmlFile = "https://en.wikipedia.org/wiki/Java_(programming_language)";
    private static final String pngFile = "rendered.png";
    private static final String pdfFile = "rendered.pdf";

    private static final IStringCallback cb1 = IStringCallback.asError(LOG);
    private static final IStringCallback cb2 = IStringCallback.asWarn(LOG);
    private static final IIntCallback cb = IIntCallback.instance();

    static {
        System.getProperties().setProperty("wkhtmltopdf.library.path", "libs/wkhtmltox");
    }

    public static void main(String[] args) throws Throwable {

        boolean sts = false;
        final WKHtmlToX instance = ExternalFactory.create(WKHtmlToX.class);

        System.out.println(instance.wkhtmltopdf_version());

        instance.wkhtmltoimage_init(0);
        instance.wkhtmltopdf_init(0);

        sts = makePDF(instance);
        System.out.println(String.format("PDF rendering: %s", sts));

        sts = makeImage(instance);
        System.out.println(String.format("Image rendering: %s", sts));

        instance.wkhtmltoimage_deinit();
        instance.wkhtmltopdf_deinit();

    }

    static boolean makePDF(final WKHtmlToX instance) throws Throwable {

        boolean sts = false;

        final MemorySegment gs = instance.wkhtmltopdf_create_global_settings();
        final MemorySegment os = instance.wkhtmltopdf_create_object_settings();

        instance.wkhtmltopdf_set_global_setting(gs, "out", pdfFile);
        //instance.wkhtmltopdf_set_global_setting(gs, "size.pageSize ", "A3");		
        //instance.wkhtmltopdf_set_global_setting(gs, "dpi", "300");

        //instance.wkhtmltopdf_set_object_setting(os, "dpi", "300");
        instance.wkhtmltopdf_set_object_setting(os, "page", htmlFile);
        instance.wkhtmltopdf_set_object_setting(os, "load.printMediaType", "true");
        instance.wkhtmltopdf_set_object_setting(os, "web.enableJavascript", "true");
        instance.wkhtmltopdf_set_object_setting(os, "web.loadImages", "true");
        instance.wkhtmltopdf_set_object_setting(os, "web.background", Boolean.toString(true));

        final MemorySegment c = instance.wkhtmltopdf_create_converter(gs);

        try {

            instance.wkhtmltopdf_add_object(c, os, null);

            instance.wkhtmltopdf_set_error_callback(c, cb1);
            instance.wkhtmltopdf_set_warning_callback(c, cb2);

            final int val = instance.wkhtmltopdf_convert(c);
            sts = val == 1;
        } finally {
            instance.wkhtmltopdf_destroy_object_settings(os);
            instance.wkhtmltopdf_destroy_global_settings(gs);
            instance.wkhtmltopdf_destroy_converter(c);
        }

        return sts;
    }

    static boolean makeImage(final WKHtmlToX instance) throws Throwable {

        boolean sts = false;
        final MemorySegment gs = instance.wkhtmltoimage_create_global_settings();

        instance.wkhtmltoimage_set_global_setting(gs, "in", htmlFile);
        instance.wkhtmltoimage_set_global_setting(gs, "out", pngFile);
        instance.wkhtmltoimage_set_global_setting(gs, "fmt", "png");

        instance.wkhtmltoimage_set_global_setting(gs, "smartWidth", "true");
        instance.wkhtmltoimage_set_global_setting(gs, "transparent", "true");
        instance.wkhtmltoimage_set_global_setting(gs, "quality", "50");

        final MemorySegment c = instance.wkhtmltoimage_create_converter(gs, null);

        try {

            instance.wkhtmltoimage_set_error_callback(c, cb1);
            instance.wkhtmltoimage_set_warning_callback(c, cb2);

            // if value == 1 then success; here only to test callback
            instance.wkhtmltoimage_set_finished_callback(c, cb);

            final int val = instance.wkhtmltoimage_convert(c);
            sts = val == 1;
        } finally {
            // cause JVM crash; either bug in wkhtmltox or 
            // cleared automatically when converter destroyed
            // instance.wkhtmltoimage_destroy_global_settings(gs);            
            instance.wkhtmltoimage_destroy_converter(c);
        }

        return sts;

    }

}
