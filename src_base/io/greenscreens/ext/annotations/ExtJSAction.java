/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Used to define ExtJS.Direct controller and it's namespace.
 * Each controller to be accessible from ExtJS.Direct must have this qualifier.
 * Engine uses this to match incoming request on rest service to defined actions.
 * Used mostly for security control.
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface ExtJSAction {

    String namespace();

    String action();
}
