/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.web;

import javax.websocket.server.ServerEndpoint;

import io.greenscreens.websocket.WebSocketConfigurator;
import io.greenscreens.websocket.WebSocketService;
import io.greenscreens.websocket.WebsocketDecoder;
import io.greenscreens.websocket.WebsocketEncoder;

@ServerEndpoint(value = "/socket", configurator = WebSocketConfigurator.class,
               decoders = { WebsocketDecoder.class }, 
               encoders = { WebsocketEncoder.class }, 
               subprotocols = { "ws4is" })
public class TnWebSocketService extends WebSocketService {
    // this is main initialization websocket service
}
