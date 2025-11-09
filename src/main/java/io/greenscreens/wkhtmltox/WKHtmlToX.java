/*
* Copyright (C) 2015, 2025 Green Screens Ltd.
*/
package io.greenscreens.wkhtmltox;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import io.greenscreens.wkhtmltox.callback.IIntCallback;
import io.greenscreens.wkhtmltox.callback.IStringCallback;

public enum WKHtmlToX {
    ;

    private final static Lookup lookup;
    static private Arena arena = null;
    static private SymbolLookup stdlib = null;

    static private MethodHandle wkhtmltoimage_init = null;
    static private MethodHandle wkhtmltoimage_deinit = null;
    static private MethodHandle wkhtmltoimage_create_global_settings = null;
    static private MethodHandle wkhtmltoimage_destroy_global_settings = null;
    static private MethodHandle wkhtmltoimage_set_global_setting = null;
    static private MethodHandle wkhtmltoimage_create_converter = null;
    static private MethodHandle wkhtmltoimage_destroy_converter = null;
    static private MethodHandle wkhtmltoimage_convert = null;
    static private MethodHandle wkhtmltoimage_progress_string = null;

    static private MethodHandle wkhtmltoimage_set_progress_changed_callback = null;
    static private MethodHandle wkhtmltoimage_set_finished_callback = null;
    static private MethodHandle wkhtmltoimage_set_warning_callback = null;
    static private MethodHandle wkhtmltoimage_set_error_callback = null;
    static private MethodHandle wkhtmltoimage_http_error_code = null;

    static private MethodHandle wkhtmltopdf_init = null;
    static private MethodHandle wkhtmltopdf_create_global_settings = null;
    static private MethodHandle wkhtmltopdf_destroy_global_settings = null;
    static private MethodHandle wkhtmltopdf_create_object_settings = null;
    static private MethodHandle wkhtmltopdf_destroy_object_settings = null;
    static private MethodHandle wkhtmltopdf_set_global_setting = null;
    static private MethodHandle wkhtmltopdf_set_object_setting = null;
    static private MethodHandle wkhtmltopdf_create_converter = null;
    static private MethodHandle wkhtmltopdf_destroy_converter = null;
    static private MethodHandle wkhtmltopdf_convert = null;
    static private MethodHandle wkhtmltopdf_progress_string = null;
    static private MethodHandle wkhtmltopdf_add_object = null;
    static private MethodHandle wkhtmltopdf_version = null;
    static private MethodHandle wkhtmltopdf_deinit = null;

    static private MethodHandle wkhtmltopdf_set_progress_changed_callback = null;
    static private MethodHandle wkhtmltopdf_set_finished_callback = null;
    static private MethodHandle wkhtmltopdf_set_warning_callback = null;
    static private MethodHandle wkhtmltopdf_set_error_callback = null;
    static private MethodHandle wkhtmltopdf_http_error_code = null;

    static private MethodHandle stringCallback = null;
    static private MethodHandle intCallback = null;
    static private FunctionDescriptor strCallbackDescriptor;
    static private FunctionDescriptor intCallbackDescriptor;

    static {
        lookup = MethodHandles.lookup();
        try {
            intCallback = initCallback(IIntCallback.class);
            stringCallback = initCallback(IStringCallback.class);
            strCallbackDescriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            intCallbackDescriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_INT);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static final void clear() {
        wkhtmltoimage_init = null;
        wkhtmltoimage_deinit = null;
        wkhtmltoimage_create_global_settings = null;
        wkhtmltoimage_destroy_global_settings = null;
        wkhtmltoimage_set_global_setting = null;
        wkhtmltoimage_create_converter = null;
        wkhtmltoimage_destroy_converter = null;
        wkhtmltoimage_convert = null;
        wkhtmltoimage_progress_string = null;
        wkhtmltoimage_set_progress_changed_callback = null;
        wkhtmltoimage_set_finished_callback = null;
        wkhtmltoimage_set_warning_callback = null;
        wkhtmltoimage_set_error_callback = null;
        wkhtmltoimage_http_error_code = null;

        wkhtmltopdf_init = null;
        wkhtmltopdf_create_global_settings = null;
        wkhtmltopdf_destroy_global_settings = null;
        wkhtmltopdf_create_object_settings = null;
        wkhtmltopdf_destroy_object_settings = null;
        wkhtmltopdf_set_global_setting = null;
        wkhtmltopdf_set_object_setting = null;
        wkhtmltopdf_create_converter = null;
        wkhtmltopdf_destroy_converter = null;
        wkhtmltopdf_convert = null;
        wkhtmltopdf_progress_string = null;
        wkhtmltopdf_add_object = null;
        wkhtmltopdf_version = null;
        wkhtmltopdf_deinit = null;
        wkhtmltopdf_set_progress_changed_callback = null;
        wkhtmltopdf_set_finished_callback = null;
        wkhtmltopdf_set_warning_callback = null;
        wkhtmltopdf_set_error_callback = null;
        wkhtmltopdf_http_error_code = null;
    }

    static void initialize() {
        clear();
        final String lib = System.getProperties().getProperty("wkhtmltopdf.library.path");
        if (Objects.nonNull(arena)) {
            arena.close();
            arena = null;
        }
        arena = Arena.ofShared();
        stdlib = SymbolLookup.libraryLookup(lib, arena);

    }

    public static int wkhtmltoimage_init(final int use_graphics) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_init)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_init").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
            wkhtmltoimage_init = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltoimage_init.invokeExact(use_graphics);
    }

    public static int wkhtmltoimage_deinit() throws Throwable {
        if (Objects.isNull(wkhtmltoimage_deinit)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_deinit").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT);
            wkhtmltoimage_deinit = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltoimage_deinit.invokeExact();
    }

    public static MemorySegment wkhtmltoimage_create_global_settings() throws Throwable {
        if (Objects.isNull(wkhtmltoimage_create_global_settings)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_create_global_settings").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS);
            wkhtmltoimage_create_global_settings = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (MemorySegment) wkhtmltoimage_create_global_settings.invokeExact();
    }

    public static void wkhtmltoimage_destroy_global_settings(final MemorySegment settings) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_destroy_global_settings)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_destroy_global_settings").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
            wkhtmltoimage_destroy_global_settings = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        wkhtmltoimage_destroy_global_settings.invokeExact(settings);
    }

    public static void wkhtmltoimage_set_global_setting(final MemorySegment settings, final String name, final String value) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_set_global_setting)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_set_global_setting").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_set_global_setting = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final Arena gcarena = Arena.ofAuto();
        final MemorySegment a1 = gcarena.allocateFrom(name);
        final MemorySegment a2 = gcarena.allocateFrom(value);
        wkhtmltoimage_set_global_setting.invokeExact(settings, a1, a2);
    }

    public static MemorySegment wkhtmltoimage_create_converter(final MemorySegment settings, final String data) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_create_converter)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_create_converter").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_create_converter = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final Arena gcarena = Arena.ofAuto();
        final MemorySegment a1 = gcarena.allocateFrom(normalize(data));
        return (MemorySegment) wkhtmltoimage_create_converter.invokeExact(settings, a1);
    }

    public static void wkhtmltoimage_destroy_converter(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_destroy_converter)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_destroy_converter").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
            wkhtmltoimage_destroy_converter = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        wkhtmltoimage_destroy_converter.invoke(converter);
    }

    public static int wkhtmltoimage_convert(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_convert)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_convert").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
            wkhtmltoimage_convert = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltoimage_convert.invokeExact(converter);
    }

    public static String wkhtmltoimage_progress_string(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_progress_string)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_progress_string").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_progress_string = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        final MemorySegment ret = (MemorySegment) wkhtmltoimage_progress_string.invokeExact(converter);
        final MemorySegment ret2 = ret.reinterpret(Integer.MAX_VALUE, arena, null);
        return ret2.getString(0);
    }

    public static void wkhtmltoimage_set_progress_changed_callback(final MemorySegment converter, final IIntCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_set_progress_changed_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_set_progress_changed_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_set_progress_changed_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = intCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, intCallbackDescriptor, arena);
        wkhtmltoimage_set_progress_changed_callback.invokeExact(converter, callback);
    }

    public static void wkhtmltoimage_set_warning_callback(final MemorySegment converter, final IStringCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_set_warning_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_set_warning_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_set_warning_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = stringCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, strCallbackDescriptor, arena);
        wkhtmltoimage_set_warning_callback.invoke(converter, callback);
    }

    public static void wkhtmltoimage_set_error_callback(final MemorySegment converter, final IStringCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_set_error_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_set_error_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_set_error_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = stringCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, strCallbackDescriptor, arena);
        wkhtmltoimage_set_error_callback.invoke(converter, callback);
    }

    public static void wkhtmltoimage_set_finished_callback(final MemorySegment converter, final IIntCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_set_finished_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_set_finished_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltoimage_set_finished_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = intCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, intCallbackDescriptor, arena);
        wkhtmltoimage_set_finished_callback.invoke(converter, callback);
    }

    public static int wkhtmltoimage_http_error_code(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltoimage_http_error_code)) {
            final MemorySegment segment = stdlib.find("wkhtmltoimage_http_error_code").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
            wkhtmltoimage_http_error_code = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltoimage_http_error_code.invokeExact(converter);
    }

    public static int wkhtmltopdf_init(final int use_graphics) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_init)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_init").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
            wkhtmltopdf_init = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltopdf_init.invokeExact(use_graphics);
    }

    public static MemorySegment wkhtmltopdf_create_global_settings() throws Throwable {
        if (Objects.isNull(wkhtmltopdf_create_global_settings)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_create_global_settings").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS);
            wkhtmltopdf_create_global_settings = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (MemorySegment) wkhtmltopdf_create_global_settings.invokeExact();
    }

    public static void wkhtmltopdf_destroy_global_settings(final MemorySegment settings) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_destroy_global_settings)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_destroy_global_settings").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
            wkhtmltopdf_destroy_global_settings = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        wkhtmltopdf_destroy_global_settings.invokeExact(settings);
    }

    public static MemorySegment wkhtmltopdf_create_object_settings() throws Throwable {
        if (Objects.isNull(wkhtmltopdf_create_object_settings)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_create_object_settings").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS);
            wkhtmltopdf_create_object_settings = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (MemorySegment) wkhtmltopdf_create_object_settings.invokeExact();
    }

    public static void wkhtmltopdf_destroy_object_settings(final MemorySegment os) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_destroy_object_settings)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_destroy_object_settings").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
            wkhtmltopdf_destroy_object_settings = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        wkhtmltopdf_destroy_object_settings.invokeExact(os);
    }

    public static void wkhtmltopdf_set_global_setting(final MemorySegment settings, final String name, final String value) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_set_global_setting)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_set_global_setting").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_set_global_setting = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final Arena gcarena = Arena.ofAuto();
        final MemorySegment a1 = gcarena.allocateFrom(name);
        final MemorySegment a2 = gcarena.allocateFrom(value);
        wkhtmltopdf_set_global_setting.invokeExact(settings, a1, a2);
    }

    public static void wkhtmltopdf_set_object_setting(final MemorySegment os, final String name, final String value) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_set_object_setting)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_set_object_setting").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_set_object_setting = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final Arena gcarena = Arena.ofAuto();
        final MemorySegment a1 = gcarena.allocateFrom(name);
        final MemorySegment a2 = gcarena.allocateFrom(value);
        wkhtmltopdf_set_object_setting.invokeExact(os, a1, a2);
    }

    public static MemorySegment wkhtmltopdf_create_converter(final MemorySegment settings) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_create_converter)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_create_converter").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_create_converter = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (MemorySegment) wkhtmltopdf_create_converter.invoke(settings);
    }

    public static void wkhtmltopdf_destroy_converter(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_destroy_converter)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_destroy_converter").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
            wkhtmltopdf_destroy_converter = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        wkhtmltopdf_destroy_converter.invokeExact(converter);
    }

    public static int wkhtmltopdf_convert(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_convert)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_convert").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
            wkhtmltopdf_convert = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltopdf_convert.invokeExact(converter);
    }

    public static String wkhtmltopdf_progress_string(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_progress_string)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_progress_string").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_progress_string = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        final MemorySegment ret = (MemorySegment) wkhtmltopdf_progress_string.invokeExact(converter);
        final MemorySegment ret2 = ret.reinterpret(Integer.MAX_VALUE, arena, null);
        return ret2.getString(0);
    }

    public static void wkhtmltopdf_set_progress_changed_callback(final MemorySegment converter, final IIntCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_set_progress_changed_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_set_progress_changed_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_set_progress_changed_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = intCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, intCallbackDescriptor, arena);
        wkhtmltopdf_convert.invoke(converter, callback);
    }

    public static void wkhtmltopdf_set_warning_callback(final MemorySegment converter, final IStringCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_set_warning_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_set_warning_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_set_warning_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = stringCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, strCallbackDescriptor, arena);
        wkhtmltopdf_set_warning_callback.invoke(converter, callback);
    }

    public static void wkhtmltopdf_set_error_callback(final MemorySegment converter, final IStringCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_set_error_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_set_error_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_set_error_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = stringCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, strCallbackDescriptor, arena);
        wkhtmltopdf_set_error_callback.invoke(converter, callback);
    }

    public static int wkhtmltopdf_http_error_code(final MemorySegment converter) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_http_error_code)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_http_error_code").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
            wkhtmltopdf_http_error_code = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltopdf_http_error_code.invokeExact(converter);
    }

    public static void wkhtmltopdf_set_finished_callback(final MemorySegment converter, final IIntCallback cb) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_set_finished_callback)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_set_finished_callback").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_set_finished_callback = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MethodHandle cbBind = intCallback.bindTo(cb);
        final MemorySegment callback = Linker.nativeLinker().upcallStub(cbBind, intCallbackDescriptor, arena);
        wkhtmltopdf_set_finished_callback.invoke(converter, callback);
    }

    private static MethodHandle initCallback(final Class<?> clazz) throws IllegalAccessException {
        final Method method = Arrays.asList(clazz.getMethods()).stream().filter(m -> m.getName().equals("callback")).findFirst().get();
        return lookup.unreflect(method);
    }

    public static void wkhtmltopdf_add_object(final MemorySegment converter, final MemorySegment objectSetting, final String data) throws Throwable {
        if (Objects.isNull(wkhtmltopdf_add_object)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_add_object").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS);
            wkhtmltopdf_add_object = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final Arena gcarena = Arena.ofAuto();
        final MemorySegment a1 = gcarena.allocateFrom(normalize(data));
        wkhtmltopdf_add_object.invokeExact(converter, objectSetting, a1);
    }

    public static String wkhtmltopdf_version() throws Throwable {
        if (Objects.isNull(wkhtmltopdf_version)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_version").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.ADDRESS);
            wkhtmltopdf_version = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }

        final MemorySegment ret = (MemorySegment) wkhtmltopdf_version.invokeExact();
        final MemorySegment ret2 = ret.reinterpret(Integer.MAX_VALUE, arena, null);
        return ret2.getString(0);
    }

    public static int wkhtmltopdf_deinit() throws Throwable {
        if (Objects.isNull(wkhtmltopdf_deinit)) {
            final MemorySegment segment = stdlib.find("wkhtmltopdf_deinit").orElseThrow();
            final FunctionDescriptor descriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT);
            wkhtmltopdf_deinit = Linker.nativeLinker().downcallHandle(segment, descriptor);
        }
        return (int) wkhtmltopdf_deinit.invokeExact();
    }

    private static String normalize(final String val) {
        return Objects.isNull(val) ? "" : val.trim();
    }

}