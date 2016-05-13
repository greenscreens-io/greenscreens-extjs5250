/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.web;

import io.greenscreens.data.TnConfigLoader;
import io.greenscreens.data.TnConstants;
import io.greenscreens.data.TnHost;
import io.greenscreens.data.tn5250.Tn5250ScreenResponse;
import io.greenscreens.tn5250.ITn5250Session;
import io.greenscreens.websocket.WebSocketSession;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.Vetoed;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Helper methods used from web controllers.
 */
@Vetoed
public enum TnWebHelper {
    ;

    /**
     * Generate SESSION_NOT_FOUND response
     * @return [description]
     */
    public static Tn5250ScreenResponse get5250SessionNotFoundResponse() {
        final Tn5250ScreenResponse response = new Tn5250ScreenResponse(false, TnConstants.SESSION_NOT_FOUND);
        response.setConerr(TnConstants.SESSION_NOT_FOUND);
        response.setCode(TnConstants.SESSION_NOT_FOUND_CODE);
        response.setLocked(false);
        response.setClearScr(false);
        return response;
    }

    /**
     * Generate response based on Java Exception
     * @param  Exception [description]
     * @return           [description]
     */
    public static Tn5250ScreenResponse get5250ErrorResponse(final Exception exception) {
        final Tn5250ScreenResponse response = new Tn5250ScreenResponse(exception, exception.getMessage());
        response.setConerr(TnConstants.REQUEST_ERROR);
        response.setCode(TnConstants.REQUEST_ERROR_CODE);
        response.setLocked(false);
        response.setClearScr(false);
        return response;
    }


    /* * * * * * * * TN 5250 sessions * * * * * * */

    /**
     * Find terminal session based on displayID
     * @param  session   [description]
     * @param  dispalyID [description]
     * @return           [description]
     */
    public static ITn5250Session findTn5250Session(final WebSocketSession session, final String dispalyID) {
        final Map<String, ITn5250Session> map = getTn5250Sessions(session);
        return map.get(dispalyID);
    }

    /**
     * Get all live terminal sessions
     * @param  HttpSession [description]
     * @return             [description]
     */
    @SuppressWarnings("unchecked")
    public static Map<String, ITn5250Session> getTn5250Sessions(final HttpSession httpSession) {
        final Map<String, ITn5250Session> sessions = (Map<String, ITn5250Session>) httpSession.getAttribute(ITn5250Session.class.getCanonicalName());
        return sessions;
    }

    /**
     * Get all live terminal sessions
     * @param  WebSocketSession [description]
     * @return                  [description]
     */
    public static Map<String, ITn5250Session> getTn5250Sessions(final WebSocketSession session) {
        final HttpSession httpSession = session.getHttpSession();
        return getTn5250Sessions(httpSession);
    }

    /**
     * Close terminal session when WebSocket closes
     * @param  [description]
     * @param  [description]
     * @param  [description]
     * @param  [description]
     * @param  [description]
     */
    private static void closeTn5250Session(final WebSocketSession wsSession, Map<String, ITn5250Session> sessions, Entry<String, ITn5250Session> entry) {
    	final ITn5250Session tnSession = entry.getValue();
    	if (tnSession.isConnected()) {
    		if (wsSession.getId().equals(tnSession.getWSocketId())) {
    			sessions.remove(entry.getKey());
        		tnSession.disconnect();
    		}
    	}
    }

    /**
     * Close all terminal session for given web session
     * @param HttpSession [description]
     */
    public static void closeTn5250Sessions(final HttpSession httpSession) {
    	final Map<String, ITn5250Session> sessions = getTn5250Sessions(httpSession);
        final Iterator<Entry<String, ITn5250Session>> tsessions = sessions.entrySet().iterator();
        while(tsessions.hasNext()) {
        	ITn5250Session tnSession = tsessions.next().getValue();
        	if (tnSession.isConnected()) {
        		System.out.println("   Closing Tn5250 session :" + tnSession.getDisplayId());
        		tnSession.disconnect();
        	}
        }
        sessions.clear();
    }

    /**
     * Close all terminal sessions fr given WebSoket session
     * @param WebSocketSession [description]
     */
    public static void closeTn5250Sessions(final WebSocketSession wsSession) {
    	final Map<String, ITn5250Session> sessions = getTn5250Sessions(wsSession);
        final Iterator<Entry<String, ITn5250Session>> tsessions = sessions.entrySet().iterator();
        while(tsessions.hasNext()) {
        	closeTn5250Session(wsSession, sessions, tsessions.next());
        }
    }


    /* * * * * * * * TN HOSTS * * * * * * */

    /**
     * Find host configuration based on virtual name
     * @param  session [description]
     * @param  name    [description]
     * @return         [description]
     */
    public static TnHost findTnHost(final WebSocketSession session, final String name) {
        Map<String, TnHost> map = getTnHosts(session);
        return map.get(name);
    }

    /**
     * Get all host configurations
     * @param  WebSocketSession [description]
     * @return                  [description]
     */
    @SuppressWarnings("unchecked")
    public static Map<String, TnHost> getTnHosts(final WebSocketSession session) {
        final HttpSession httpSession = session.getHttpSession();
        final ServletContext servletContext = httpSession.getServletContext();
        final Map<String, TnHost> hosts = (Map<String, TnHost>) servletContext.getAttribute(TnHost.class.getCanonicalName());
        return hosts;
    }

    /**
     * Reload all host configurations from confg file
     * @param WebSocketSession [description]
     */
    public static void reloadConfiguration(final WebSocketSession session) {
        final HttpSession httpSession = session.getHttpSession();
        final ServletContext servletContext = httpSession.getServletContext();
        servletContext.setAttribute(TnHost.class.getCanonicalName(), TnConfigLoader.reload());
    }
}
