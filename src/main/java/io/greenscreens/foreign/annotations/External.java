/*
* Copyright (C) 2015, 2023 Green Screens Ltd.
*/
package io.greenscreens.foreign.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to describe Java Interface as linked to the external library
 * ("dll" on Windows , "so" on Linux, Unix, Apple)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface External {

    /**
     * A path to the external library (without extension) Example "libs/wkhtmltox"
     * will be constructed into "libs/wkhtmltox.dll" or "libs/wkhtmltox.so"
     * 
     * @return
     */
    String name() default "";

    /**
     * System property name containing a path to the external library
     * 
     * @return
     */
    String property() default "";
}
