/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket;

import java.io.IOException;

//import javax.enterprise.inject.Vetoed;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.greenscreens.JsonDecoder;
import io.greenscreens.websocket.data.WebSocketResponse;

/**
 * Internal encoder for WebSocket ExtJS response
 *
 */
//@Vetoed
public class WebsocketEncoder implements Encoder.Text<WebSocketResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketEncoder.class);

    private ObjectMapper mapper = null;

    @Override
    public final void destroy() {

    }

    @Override
    public final void init(final EndpointConfig arg0) {
        mapper = JsonDecoder.getJSONEngine();
    }

    @Override
    public final String encode(final WebSocketResponse data) throws EncodeException {
        String response = null;
        try {
            if (mapper != null) {
                response = mapper.writeValueAsString(data);
            }
        } catch (IOException exception) {
            LOGGER.error(exception.getMessage(), exception);
            throw new EncodeException(data, exception.getMessage(), exception);
        }
        if (response == null) {
            response = "";
        }
        return response;
    }

}
