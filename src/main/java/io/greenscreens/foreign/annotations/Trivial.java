/*
* Copyright (C) 2015, 2024 Green Screens Ltd.
 */
package io.greenscreens.foreign.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify foreign method is a trivial method, allowing JVM
 * internal optimizations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Trivial {

}
