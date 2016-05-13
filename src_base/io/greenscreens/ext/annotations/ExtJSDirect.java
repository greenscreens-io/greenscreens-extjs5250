/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Used to link rest / websocket service to Controller
 * Also used by descriptor service to ba able to generate ExtJS dynamic code.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface ExtJSDirect {

    /**
     * if * then all matched;
     * if not set then ignored and not generally available
     * if set, must match corresponding UriInfo from rest service
     * @return
     */
    @Nonbinding
    String[] paths();
}
