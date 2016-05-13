/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket.data;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import io.greenscreens.WS4ISConstants;
import io.greenscreens.ext.ExtJSDirectRequest;

/**
 * Class used to map JSON structure describing ExtJS websocket request.
 */
public class WebSocketRequest {

    public final String type = WS4ISConstants.WEBSOCKET_TYPE;

    private WebSocketInstruction cmd; // 'welcome , bye, data' ,
    private int timeout; // set only when cmd=welcome

    private String errMsg;
    private int errId;

    // list of commands - batch
    private ArrayList<ExtJSDirectRequest<JsonNode>> data;

    public final WebSocketInstruction getCmd() {
        return cmd;
    }

    public final void setCmd(final WebSocketInstruction cmd) {
        this.cmd = cmd;
    }

    public final int getTimeout() {
        return timeout;
    }

    public final void setTimeout(final int timeout) {
        this.timeout = timeout;
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

    public final String getType() {
        return type;
    }

    public final ArrayList<ExtJSDirectRequest<JsonNode>> getData() {
        return data;
    }

    public final void setData(final ArrayList<ExtJSDirectRequest<JsonNode>> data) {
        this.data = data;
    }

}
