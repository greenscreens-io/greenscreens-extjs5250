/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext;

/**
 * Class to be converted to JSON data, specific format for ExtJS response.
 * For Ext.Direct to recognize response, action, method, type and tid must be set
 * as ExtJS keeps signature on send and expects the same signature in response to match queued event
 *
 * @param <T>
 */
public class ExtJSDirectResponse<T> {

    // {"action":"DemoForm","method":"submit","data":[{"id":"0","username":"asfsa","password":"asdfv","email":"sadfv","rank":"345"}],"type":"rpc","tid":1}
    private String action;
    private String method;
    private String type;
    private String tid = "-1";
    private boolean keepTransaction;
    private Object result;

    public ExtJSDirectResponse(final ExtJSDirectRequest<T> request, final Object response) {
        super();

        this.result = response;
        if (request != null) {
            this.action = request.getAction();
            this.method = request.getMethod();
            this.tid = request.getTid();
            this.type = request.getType();
        }
    }

    public final String getAction() {
        return action;
    }

    public final void setAction(final String action) {
        this.action = action;
    }

    public final String getMethod() {
        return method;
    }

    public final void setMethod(final String method) {
        this.method = method;
    }

    public final String getType() {
        return type;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final String getTid() {
        return tid;
    }

    public final void setTid(final String tid) {
        this.tid = tid;
    }

    public final Object getResult() {
        return result;
    }

    public final void setResult(final Object result) {
        this.result = result;
    }

    public final boolean isKeepTransaction() {
        return keepTransaction;
    }

    public final void setKeepTransaction(final boolean keepTransaction) {
        this.keepTransaction = keepTransaction;
    }

}
