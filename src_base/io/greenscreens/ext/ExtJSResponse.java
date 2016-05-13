/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.ext;

import java.io.Serializable;

/**
 ExtJs standard response structure used by other extended response classes

 { "success": false,
   "msg": "",
   "error": "",
   "stack": ""
  }
 */
public class ExtJSResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String msg;
    private String code;
    private Throwable exception;

    public ExtJSResponse(final boolean success, final String message) {
        super();
        this.success = success;
        this.msg = message;
    }

    public ExtJSResponse(final Throwable exception, final String message) {
        setError(exception, message);
    }

    public ExtJSResponse() {
        super();
        this.success = false;
    }

    public final boolean isSuccess() {
        return success;
    }

    public final void setSuccess(final boolean success) {
        this.success = success;
    }

    public final String getMsg() {
        return msg;
    }

    public final void setMsg(final String msg) {
        this.msg = msg;
    }

    public final Throwable getException() {
        return exception;
    }

    public final void setException(final Throwable exception) {
        this.exception = exception;

        if (exception == null) {
            return;
        }

        if (exception instanceof RuntimeException && exception.getCause() != null) {
            this.exception = exception.getCause();
        } else {
            this.exception = exception;
        }

    }

    public final void setError(final Throwable exception, final String message) {
        success = false;
        msg = message;
        setException(exception);
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
