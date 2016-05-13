/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket.data;

import io.greenscreens.WS4ISConstants;

/**
 * Object to be converted into JSON structure. 
 * {type :'ws' , sid : session_id , tid : transaction_id, timeout : 0 , ....}
 */
public class WebSocketResponse {
    
    private String type = WS4ISConstants.WEBSOCKET_TYPE;

    private final WebSocketInstruction cmd;

    private String errMsg;
    private int errId;
    private Object data;

    public WebSocketResponse(final WebSocketInstruction cmd) {
        this.cmd = cmd;
    }

    public final String getType() {
        return type;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final String getErrMsg() {
        return errMsg;
    }

    public final void setErrMsg(final String errMsg) {
        this.errMsg = errMsg;
    }

    public final int getErrId() {
        return errId;
    }

    public final void setErrId(final int errId) {
        this.errId = errId;
    }

    public final Object getData() {
        return data;
    }

    public final void setData(final Object data) {
        this.data = data;
    }

    public final WebSocketInstruction getCmd() {
        return cmd;
    }

}
