/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket.data;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * WebSocket return structure {type:'ws' , cmd : * , data : *}
 */
public enum WebSocketInstruction {

    WELCO("welco"), 
    BYE("bye"), 
    ERR("err"), 
    DATA("data"), 
    ECHO("echo")
    ;

    private final String text;

    private WebSocketInstruction(final String text) {
        this.text = text;
    }

    @JsonValue
    public String getText() {
        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
