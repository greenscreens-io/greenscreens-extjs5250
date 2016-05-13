/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext;

import java.util.Set;

/**
 * ExtJS array response structure.
 */
public class ExtJSResponseList<T> extends ExtJSResponse {

    private static final long serialVersionUID = 1L;

    private Set<T> data;

    public ExtJSResponseList() {
        super();
    }

    public ExtJSResponseList(final boolean success, final String message) {
        super(success, message);
    }

    public ExtJSResponseList(final Throwable exception, final String message) {
        super(exception, message);
    }

    public final Set<T> getData() {
        return data;
    }

    public final void setData(final Set<T> data) {
        this.data = data;
    }

}
