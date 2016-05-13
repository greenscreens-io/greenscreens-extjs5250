/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket;

//import javax.enterprise.inject.Vetoed;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.greenscreens.JsonDecoder;
import io.greenscreens.websocket.data.WebSocketRequest;

/**
 * Internal JSON decoder for WebSocket ExtJS request
 */
//@Vetoed
public class WebsocketDecoder implements Decoder.Text<WebSocketRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketDecoder.class);

    @Override
    public final WebSocketRequest decode(final String message) throws DecodeException {

        WebSocketRequest wsMessage = null;

        try {
            final JsonDecoder<WebSocketRequest> jd = new JsonDecoder<>(WebSocketRequest.class, message);
            wsMessage = jd.getObject();
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new DecodeException(message, exception.getMessage(), exception);
        }
        return wsMessage;
    }

    @Override
    public final boolean willDecode(final String message) {
        boolean decode = false;
        if (message != null) {
            decode = message.trim().startsWith("{") && message.trim().endsWith("}");
        }
        return decode;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(final EndpointConfig arg0) {

    }

}
