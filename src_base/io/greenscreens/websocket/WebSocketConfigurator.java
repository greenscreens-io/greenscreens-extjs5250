/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import io.greenscreens.WS4ISConstants;

/**
 * Config object for @ServerEndpoint annotation used to intercept
 * WebSocket initialization for custom setup.
 *
 */
@Vetoed
public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public final String getNegotiatedSubprotocol(final List<String> supported, final List<String> requested) {
        return WS4ISConstants.WEBSOCKET_SUBPROTOCOL;
    }

    // modifyHandshake() is called before getEndpointInstance()!
    @Override
    public final void modifyHandshake(final ServerEndpointConfig sec, final HandshakeRequest request, final HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);
        final HttpSession httpSession = (HttpSession) request.getHttpSession();
        final Map<String, Object> map = sec.getUserProperties();
        map.put(HttpSession.class.getCanonicalName(), httpSession);
        map.put(Locale.class.getCanonicalName(), getLocale(request));
        map.put(WS4ISConstants.WEBSOCKET_PATH, sec.getPath());
    }

    /**
     * Get locale lanuage requested by browser
     * @param  HandshakeRequest [description]
     * @return                  [description]
     */
    private Locale getLocale(final HandshakeRequest request) {
        // Accept-Language:hr,en-US;q=0.8,en;q=0.6
        final Map<String, List<String>> map = request.getHeaders();
        final List<String> params = map.get("Accept-Language");

        Locale locale = Locale.ENGLISH;
        if (params != null && params.size() > 0) {
            String data = params.get(0);
            data = data.split(";")[0];
            data = data.split(",")[0];
            locale = new Locale(data);
        }
        return locale;
    }

}
