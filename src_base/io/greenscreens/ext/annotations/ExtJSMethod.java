/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext.annotations;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

/**
 * Used to define remotely available action method.
 * Name must match name from incoming ExtJS.Direct call
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface ExtJSMethod {

    String value();

    @Nonbinding
    boolean formHandler() default false;
}
