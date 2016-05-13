/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.web.controllers;

import io.greenscreens.data.TnConstants;
import io.greenscreens.data.TnHost;
import io.greenscreens.data.tn5250.Tn5250ScreenElement;
import io.greenscreens.data.tn5250.Tn5250ScreenRequest;
import io.greenscreens.data.tn5250.Tn5250ScreenResponse;
import io.greenscreens.ext.ExtJSResponse;
import io.greenscreens.ext.ExtJSResponseList;
import io.greenscreens.ext.annotations.ExtJSAction;
import io.greenscreens.ext.annotations.ExtJSDirect;
import io.greenscreens.ext.annotations.ExtJSMethod;
import io.greenscreens.security.Security;
import io.greenscreens.tn5250.ITn5250Session;
import io.greenscreens.tn5250.Tn5250SessionFactory;
import io.greenscreens.web.TnWebHelper;
import io.greenscreens.websocket.WebSocketSession;
import io.greenscreens.websocket.data.WebSocketInstruction;
import io.greenscreens.websocket.data.WebSocketResponse;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Controller for 5250 telnet that will be invoked from browser through websocket.
 * Used for managing 5250 telnet connections, data retrieval and
 * keyboard commands processing
 */
@ExtJSDirect(paths = { "socket" })
@ExtJSAction(namespace = "io.greenscreens", action = "Tn5250Controller")
public class Tn5250Controller {

    @Inject
    private WebSocketSession session;

    /**
     * Close all 5250 sessions
     */
    @ExtJSMethod("closeSessions")
    public final ExtJSResponse closeSessions() {

        final ExtJSResponse response = new ExtJSResponse();
        try {
            final Map<String, ITn5250Session> sessions = TnWebHelper.getTn5250Sessions(session);
            final Collection<ITn5250Session> list = sessions.values();
            for (final ITn5250Session tnSession : list) {
            	if (tnSession.isConnected()) {
            		tnSession.disconnect();
            	}
            }
            response.setSuccess(true);
        } catch (Exception exception) {
            response.setError(exception, exception.getMessage());
        }
        return response;
    }

    /**
     * List all active 5250 sessions for current web session
     */
    @ExtJSMethod("listSessions")
    public final ExtJSResponseList<String> list5250Sessions() {

        final ExtJSResponseList<String> response = new ExtJSResponseList<>();
        try {
            final Map<String, ITn5250Session> sessions = TnWebHelper.getTn5250Sessions(session)
            		.entrySet().stream()
            		.filter(s-> s.getValue().isConnected())
            		.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
            response.setSuccess(true);
            response.setData(sessions.keySet());
        } catch (Exception exception) {
            response.setError(exception, exception.getMessage());
        }
        return response;
    }

    /*---------------------------------------------------------------------------------------------------*/

    /**
     * Start session in thread
     *
     * little delay for WebSocket to send display id response.
     * after successful connection to tn5250, listener will send
     * first screen data with displayID so front end can find
     * proper display to render image on it
     */
    private final void startSession(final TnHost host,
    		                        final String displayId,
    								final String dispalyName,
						            final String username,
						            final String password,
						            final String program,
						            final String menu,
						            final String lib
						    		) {

		    final Runnable rn = new Runnable() {

		        @Override
		        public void run() {
		            try {
		                Thread.sleep(100);
		                final String pwd = Security.decode(username, password);
		                final ITn5250Session tnSession = Tn5250SessionFactory.create(session, host, displayId, dispalyName,
                                                                                     username, pwd, program, menu, lib);
		                TnWebHelper.getTn5250Sessions(session).put(tnSession.getDisplayId(), tnSession);
		            } catch (Exception e) {
		                e.printStackTrace();
		                final Tn5250ScreenResponse response = TnWebHelper.get5250ErrorResponse(e);
		    	        final WebSocketResponse wsResponse = new WebSocketResponse(WebSocketInstruction.ERR);
		    	        wsResponse.setData(response);
		    	        session.sendResponse(wsResponse, true);
		            }
		        }
		    };
		    final Thread t = new Thread(rn);
		    t.start();
    }

    /**
     * Open new session with auto sign-on.
     * Bypass Sign-on screen
     */
    @ExtJSMethod("openSessionAuto")
    public final Tn5250ScreenResponse open5250SessionAuto(final String hostName,
    		                                              final String dispalyName,
    		                                              final String username,
    		                                              final String password,
    		                                              final String program,
    		                                              final String menu,
    		                                              final String lib
    		                                              ) {
        Tn5250ScreenResponse response = null;

        try {

            final TnHost host = TnWebHelper.findTnHost(session, hostName);

            if (host == null) {
                response = new Tn5250ScreenResponse(false, TnConstants.HOST_NOT_FOUND);
            } else {

                final String displayId = Long.toString(System.nanoTime());
                response = new Tn5250ScreenResponse(true, null);
                response.setLocked(true);
                response.setClearScr(true);
                response.setDisplayID(displayId);
                startSession(host, displayId, dispalyName, username, password, program, menu, lib);

            }

        } catch (Exception e) {
            response = TnWebHelper.get5250ErrorResponse(e);
        }
        return response;
    }

    /**
     * Open new session for available AS/400 connection
     */
    @ExtJSMethod("openSession")
    public final Tn5250ScreenResponse open5250Session(final String hostName, final String displayName) {

        Tn5250ScreenResponse response = null;

        try {

            final TnHost host = TnWebHelper.findTnHost(session, hostName);

            if (host == null) {
                response = new Tn5250ScreenResponse(false, TnConstants.HOST_NOT_FOUND);
            } else {
                final String displayId = Long.toString(System.nanoTime());
                response = new Tn5250ScreenResponse(true, null);
                response.setLocked(true);
                response.setClearScr(true);
                response.setDisplayID(displayId);
                startSession(host, displayId, displayName, null, null, null, null, null);
            }

        } catch (Exception e) {
            response = TnWebHelper.get5250ErrorResponse(e);
        }
        return response;
    }

    /**
     * Close active 5250 session.
     */
    @ExtJSMethod("closeSession")
    public final Tn5250ScreenResponse close5250Session(final String data) {

        Tn5250ScreenResponse response = null;

        try {

            final Map<String, ITn5250Session> sessions = TnWebHelper.getTn5250Sessions(session);
            final ITn5250Session tnSession = sessions.remove(data);

            if (tnSession == null) {
                response = TnWebHelper.get5250SessionNotFoundResponse();
            } else {
                response = new Tn5250ScreenResponse(true, null);
                response.setLocked(true);
                response.setClearScr(true);
                response.setMsg(TnConstants.NOT_CONNECTED);
                response.setCode(TnConstants.NOT_CONNECTED_CODE);
                tnSession.disconnect();
            }

        } catch (Exception e) {
            response = TnWebHelper.get5250ErrorResponse(e);
        }
        response.setDisplayID(data);
        return response;
    }

    /**
     * Receive command for active 5250 session
     */
    @ExtJSMethod("refreshSession")
    public final Tn5250ScreenResponse refresh5250Session(final String displayID) {

        Tn5250ScreenResponse response = null;

        try {

            final ITn5250Session tnSession = TnWebHelper.findTn5250Session(session, displayID);

            if (tnSession == null) {
                response = TnWebHelper.get5250SessionNotFoundResponse();
            } else {
                response = tnSession.refresh();
            }

        } catch (Exception e) {
            response = TnWebHelper.get5250ErrorResponse(e);
        }

        response.setDisplayID(displayID);
        return response;
    }

    /**
     * Receive command for active 5250 session
     */
    @ExtJSMethod("requestSession")
    public final Tn5250ScreenResponse request5250Session(final Tn5250ScreenRequest data, final Tn5250ScreenElement[] fields) {

        Tn5250ScreenResponse response = null;

        try {

            final ITn5250Session tnSession = TnWebHelper.findTn5250Session(session, data.getDisplayID());

            if (tnSession == null) {
                response = TnWebHelper.get5250SessionNotFoundResponse();
            } else {
                tnSession.process(data, fields);
                if ("RESET".equals(data.getKeyRequest())) {
                	refresh5250Session(data.getDisplayID());
                }
                response = new Tn5250ScreenResponse(true, null);
            }

        } catch (Exception e) {
            response = TnWebHelper.get5250ErrorResponse(e);
        }

        response.setDisplayID(data.getDisplayID());
        return response;
    }

}
