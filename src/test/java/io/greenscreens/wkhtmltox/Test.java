/*
 * Copyright (C) 2015, 2025 Green Screens Ltd.
 */
package io.greenscreens.wkhtmltox;

import java.lang.foreign.MemorySegment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        WKHtmlToX.initialize();
    }

    public static void main(String[] args) throws Throwable {

        boolean sts = false;
        // final WKHtmlToX WKHtmlToX = ExternalFactory.create(WKHtmlToX.class);

        System.out.println(WKHtmlToX.wkhtmltopdf_version());

        WKHtmlToX.wkhtmltoimage_init(0);
        WKHtmlToX.wkhtmltopdf_init(0);

        sts = makePDF();
        System.out.println(String.format("PDF rendering: %s", sts));

        sts = makeImage();
        System.out.println(String.format("Image rendering: %s", sts));

        WKHtmlToX.wkhtmltoimage_deinit();
        WKHtmlToX.wkhtmltopdf_deinit();

    }

    static boolean makePDF() throws Throwable {

        boolean sts = false;

        final MemorySegment gs = WKHtmlToX.wkhtmltopdf_create_global_settings();
        final MemorySegment os = WKHtmlToX.wkhtmltopdf_create_object_settings();

        WKHtmlToX.wkhtmltopdf_set_global_setting(gs, "out", pdfFile);
        //WKHtmlToX.wkhtmltopdf_set_global_setting(gs, "size.pageSize ", "A3");		
        //WKHtmlToX.wkhtmltopdf_set_global_setting(gs, "dpi", "300");

        //WKHtmlToX.wkhtmltopdf_set_object_setting(os, "dpi", "300");
        WKHtmlToX.wkhtmltopdf_set_object_setting(os, "page", htmlFile);
        WKHtmlToX.wkhtmltopdf_set_object_setting(os, "load.printMediaType", "true");
        WKHtmlToX.wkhtmltopdf_set_object_setting(os, "web.enableJavascript", "true");
        WKHtmlToX.wkhtmltopdf_set_object_setting(os, "web.loadImages", "true");
        WKHtmlToX.wkhtmltopdf_set_object_setting(os, "web.background", Boolean.toString(true));

        final MemorySegment c = WKHtmlToX.wkhtmltopdf_create_converter(gs);

        try {

            WKHtmlToX.wkhtmltopdf_add_object(c, os, null);

            WKHtmlToX.wkhtmltopdf_set_error_callback(c, cb1);
            WKHtmlToX.wkhtmltopdf_set_warning_callback(c, cb2);

            final int val = WKHtmlToX.wkhtmltopdf_convert(c);
            sts = val == 1;
        } finally {
            WKHtmlToX.wkhtmltopdf_destroy_object_settings(os);
            WKHtmlToX.wkhtmltopdf_destroy_global_settings(gs);
            WKHtmlToX.wkhtmltopdf_destroy_converter(c);
        }

        return sts;
    }

    static boolean makeImage() throws Throwable {

        boolean sts = false;
        final MemorySegment gs = WKHtmlToX.wkhtmltoimage_create_global_settings();

        WKHtmlToX.wkhtmltoimage_set_global_setting(gs, "in", htmlFile);
        WKHtmlToX.wkhtmltoimage_set_global_setting(gs, "out", pngFile);
        WKHtmlToX.wkhtmltoimage_set_global_setting(gs, "fmt", "png");

        WKHtmlToX.wkhtmltoimage_set_global_setting(gs, "smartWidth", "true");
        WKHtmlToX.wkhtmltoimage_set_global_setting(gs, "transparent", "true");
        WKHtmlToX.wkhtmltoimage_set_global_setting(gs, "quality", "50");

        final MemorySegment c = WKHtmlToX.wkhtmltoimage_create_converter(gs, null);

        try {

            WKHtmlToX.wkhtmltoimage_set_error_callback(c, cb1);
            WKHtmlToX.wkhtmltoimage_set_warning_callback(c, cb2);

            // if value == 1 then success; here only to test callback
            WKHtmlToX.wkhtmltoimage_set_finished_callback(c, cb);

            final int val = WKHtmlToX.wkhtmltoimage_convert(c);
            sts = val == 1;
        } finally {
            // cause JVM crash; either bug in wkhtmltox or 
            // cleared automatically when converter destroyed
            // WKHtmlToX.wkhtmltoimage_destroy_global_settings(gs);            
            WKHtmlToX.wkhtmltoimage_destroy_converter(c);
        }

        return sts;

    }

}
