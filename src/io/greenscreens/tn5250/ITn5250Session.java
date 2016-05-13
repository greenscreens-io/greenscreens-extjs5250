/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.tn5250;

import io.greenscreens.data.tn5250.Tn5250ScreenElement;
import io.greenscreens.data.tn5250.Tn5250ScreenRequest;
import io.greenscreens.data.tn5250.Tn5250ScreenResponse;
import io.greenscreens.ext.ExtJSResponse;
import io.greenscreens.websocket.WebSocketSession;

public interface ITn5250Session {

    /* process key request */
    void process(Tn5250ScreenRequest request, Tn5250ScreenElement[] fields);

    /* resends last screen */
    Tn5250ScreenResponse refresh();

    String getDisplayId();

    void setDisplayId(String displayId);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getHostName();

    void setHostName(String hostName);

    void disconnect();

    boolean isConnected();

    String toString();

    void updateWebSocketSession(WebSocketSession wsSession);
    
    void sendResponse(ExtJSResponse response);
    
    String getWSocketId();
    
    void log(boolean sts);

}