/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.web.controllers;

import io.greenscreens.data.TnHost;
import io.greenscreens.ext.ExtJSResponse;
import io.greenscreens.ext.ExtJSResponseList;
import io.greenscreens.ext.annotations.ExtJSAction;
import io.greenscreens.ext.annotations.ExtJSDirect;
import io.greenscreens.ext.annotations.ExtJSMethod;
import io.greenscreens.web.TnWebHelper;
import io.greenscreens.websocket.WebSocketSession;

import java.util.Map;
import javax.inject.Inject;

/**
 * Hosts controller that will be invoked from browser. Used for managing hosts
 * configurations.
 */
@ExtJSDirect(paths = { "socket" })
@ExtJSAction(namespace = "io.greenscreens", action = "HostsController")
public class TnHostsController {

    @Inject
    private WebSocketSession session;

    /**
     * Reload AS/400 server configurations
     */
    @ExtJSMethod("reloadDefinitions")
    public final ExtJSResponse reload5250Definitions() {

        final ExtJSResponse response = new ExtJSResponse();
        try {
            TnWebHelper.reloadConfiguration(session);
            response.setSuccess(true);
        } catch (Exception exception) {
            response.setError(exception, exception.getMessage());
        }
        return response;
    }

    /**
     * List all available AS/400 servers for connections
     */
    @ExtJSMethod("listDefinitions")
    public final ExtJSResponseList<String> list5250Definitions() {

        final ExtJSResponseList<String> response = new ExtJSResponseList<>();
        try {
            final Map<String, TnHost> hosts = TnWebHelper.getTnHosts(session);
            response.setSuccess(true);
            response.setData(hosts.keySet());
        } catch (Exception exception) {
            response.setError(exception, exception.getMessage());
        }

        return response;
    }

}
