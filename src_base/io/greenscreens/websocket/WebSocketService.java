/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import io.greenscreens.websocket.data.WebSocketRequest;

/**
 * Base WebSocket endpoint with ExtJS support. Should not be used directly.
 * Create new class extending this one and annotate new class with @ServerEndpoint
 */
public class WebSocketService {

    @Inject
    private WebSocketEndpoint endpoint;

    @OnMessage
    public final void onMessage(final WebSocketRequest message, final Session session) {
        endpoint.onMessage(message, session);
    }

    @OnOpen
    public final void onOpen(final Session session, final EndpointConfig config) {
    	if (endpoint == null) {
    		endpoint = CDI.current().select(WebSocketEndpoint.class).get();    		
    	}
        endpoint.onOpen(session, config);
    }

    @OnClose
    public final void onClose(final Session session, final CloseReason reason) {
        endpoint.onClose(session, reason);
    }

    @OnError
    public final void onError(final Session session, final Throwable t) {
        endpoint.onError(session, t);
    }

}
