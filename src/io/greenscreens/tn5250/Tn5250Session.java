/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.tn5250;

import javax.enterprise.inject.Vetoed;

import io.greenscreens.data.TnHost;
import io.greenscreens.data.tn5250.Tn5250ScreenElement;
import io.greenscreens.data.tn5250.Tn5250ScreenRequest;
import io.greenscreens.data.tn5250.Tn5250ScreenResponse;
import io.greenscreens.ext.ExtJSResponse;
import io.greenscreens.websocket.WebSocketSession;
import io.greenscreens.websocket.data.WebSocketInstruction;
import io.greenscreens.websocket.data.WebSocketResponse;

import org.tn5250j.Session5250;
import org.tn5250j.TN5250jConstants;

/**
 * Represents user single telnet connection property for web client. Web client
 * can have multiple 5250 connections so multiple displays will be used.
 */
@Vetoed
final class Tn5250Session implements ITn5250Session {

    // unique display id per user session
    // (needed because displayName can be null and it's impossible to know host
    // generated display name)
    private String displayId;

    // display name of 5250 connection (can be null)
    private String displayName;

    // virtual host name to which real configuration is mapped
    private String hostName;

    // tn5250j active connection
    private Session5250 session;

    // browser to server session
    private WebSocketSession wsSession;

    // remote terminal server configuration
    private TnHost host;

    public Tn5250Session() {}

    /**
     * Main constructor to initialize and linc Webocket session with terminal session
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @return  [description]
     */
    public Tn5250Session(final WebSocketSession wsSession, final String displayId, final String displayName,
                         final Session5250 session, final TnHost host) {

        this.session = session;
        this.displayId = displayId;
        this.displayName = displayName;
        this.wsSession = wsSession;
        this.hostName = host.getName();
        session.getConfiguration().setProperty("displayID", displayId);
    }

    /* process key request */
    public void process(final Tn5250ScreenRequest request, final Tn5250ScreenElement[] fields) {
        Tn5250StreamProcessor.process(session, request, fields);
    }

    /* resends last screen */
    public Tn5250ScreenResponse refresh() {
        final Tn5250ScreenResponse response = new Tn5250ScreenResponse(true, null);
        response.setDisplayID(this.displayId);
        Tn5250StreamProcessor.refresh(session, response);
        return response;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(final String displayId) {
        this.displayId = displayId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }

    public synchronized void disconnect() {
        if (!session.isConnected()) {
            return;
        }
        session.disconnect();
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    public WebSocketSession getWsSession() {
		return wsSession;
	}

	public String toString() {
        return displayId + ":" + displayName + ":" + hostName;
    }

    /**
     * This is a hack as tn5250j is not intended to be used in server environment
     *
     * @param WebSocketSession [description]
     */
    public void updateWebSocketSession(final WebSocketSession wsSession) {
    	this.wsSession = wsSession;
        final Tn5250SessionListener listener = new Tn5250SessionListener(wsSession, getDisplayId(), host);
        session.addSessionListener(listener);
        session.fireSessionChanged(TN5250jConstants.STATE_CONNECTED);
    }

    /**
     *  Send data to connected WebSocket session
     */
	@Override
	public void sendResponse(ExtJSResponse response) {
		if (wsSession != null && wsSession.isOpen()) {
	        final WebSocketResponse wsResponse = new WebSocketResponse(WebSocketInstruction.DATA);
	        wsResponse.setData(response);
	        wsSession.sendResponse(wsResponse, true);
		}
	}

	@Override
	public String getWSocketId() {
		if (wsSession != null) {
			return wsSession.getId();
		}
		return null;
	}

	@Override
	public void log(boolean sts) {
		session.getConfiguration().setProperty("lograw", Boolean.toString(sts));
		// tn5250j does not support logging multiple screens
		// TODO - 5250 logging
        //session.getVT().toggleDebug();
	}
}
