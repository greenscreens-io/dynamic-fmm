/*
 * Copyright (C) 2015, 2023 Green Screens Ltd.
 */
package io.greenscreens.foreign.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify array type size to be returned
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface Size {

    /**
     * Fixed data return size
     * @return
     */
    int value() default 0;
    
    /**
     * Parameter index containing return data size
     * @return
     */
    int index() default -1;
}
