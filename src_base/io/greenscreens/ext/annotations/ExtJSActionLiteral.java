/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext.annotations;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;

@SuppressWarnings("all")
public final class ExtJSActionLiteral extends AnnotationLiteral<ExtJSAction> implements ExtJSAction {

    private static final long serialVersionUID = 1L;

    private final String namespace;
    private final String action;

    public ExtJSActionLiteral(final String namespace, final String action) {
        super();
        this.action = action;
        this.namespace = namespace;
    }

    @Override
    @Nonbinding
    public String action() {
        return action;
    }

    @Override
    @Nonbinding
    public String namespace() {
        return namespace;
    }

}
