/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.web;

import io.greenscreens.data.TnConfigLoader;
import io.greenscreens.data.TnConstants;
import io.greenscreens.data.TnHost;
import io.greenscreens.tn5250.ITn5250Session;
import io.greenscreens.web.gzip.GZipCache;
import io.greenscreens.websocket.WebSocketEventStatus;
import io.greenscreens.websocket.WebSocketSession;
import io.greenscreens.websocket.WebsocketEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.tn5250j.tools.LangTool;

/**
 * HttpSession and Servlet context listener to handle tn5250 sessions on httpsession close.
 */
@WebListener
public final class TnWebListener implements HttpSessionListener, ServletContextListener {

    private ServletContext servletContext;
    private boolean removed = false;

    @SuppressWarnings("unchecked")
    @Produces
    Map<String, TnHost> getTnHostList() {
        return (Map<String, TnHost>) servletContext.getAttribute(TnHost.class.getCanonicalName());
    }

    /*
     * Closes on websocket disconnect ?? Maybe not... close only if http
     * sessions are closed?! because multiple browser windows might be opened
     * maybe to identify 5250 sessions opened websocket id ? protected void
     * onWSClose(@Observes WebsocketEvent wsEvent){
     * closeTnSession(wsEvent.getWebSocketSession().getHttpSession()); }
     */

    /**
     * reassign WebSocket session to active telnet sessions;
     * this is in a case when browser is reloaded
     */
    protected void onWebSocketEvent(@Observes final WebsocketEvent wsEvent) {

    	if (removed) {
    		return;
    	}

    	final WebSocketEventStatus status = wsEvent.getEventStatus();

    	if (status == WebSocketEventStatus.START || status == WebSocketEventStatus.CLOSE) {
            final WebSocketSession wsSession = wsEvent.getWebSocketSession();

            if (status == WebSocketEventStatus.START) {
            	processStartEvent(wsSession);
            }

        	if (status == WebSocketEventStatus.CLOSE) {
        		TnWebHelper.closeTn5250Sessions(wsSession);
        	}
        }
    }

    /**
     * Support for session reattach. When SPA web apps are used.
     * For example ExtJS based app with multiple tabs
     * Generaly not needed any more, as only one WebSocket is used per terminal session
     * In SPA apps, every tab will have it's own IFRAM element for new temrinal
     *
     * @param WebSocketSession [description]
     */
    private void processStartEvent(final WebSocketSession wsSession) {

    	final Map<String, ITn5250Session> tnSessions = TnWebHelper.getTn5250Sessions(wsSession);
    	Iterator<Entry<String, ITn5250Session>> it = tnSessions.entrySet().iterator();

        while (it.hasNext()) {
        	final ITn5250Session session = it.next().getValue();
            if (session.isConnected()) {
            	session.updateWebSocketSession(wsSession);
            }
    	}
    }

    /**
     * Close terminal sessions linked to given web session
     * @param HttpSession [description]
     */
    private void closeTn5250Session(final HttpSession webSession) {
    	System.out.println("Closing Tn5250 sessions for Web session :" + webSession.getId());
    	TnWebHelper.closeTn5250Sessions(webSession);
        webSession.removeAttribute(ITn5250Session.class.getCanonicalName());
    }


    /**
     * When new web sesision is created set some intial attributes
     */
    @Override
    public void sessionCreated(final HttpSessionEvent arg0) {
        final HttpSession webSession = arg0.getSession();
        webSession.setAttribute(ITn5250Session.class.getCanonicalName(), new ConcurrentHashMap<String, ITn5250Session>());
        webSession.setAttribute(TnConstants.SESSION_COUNTER, new AtomicInteger());
    }

    /**
     * Clean all 5250 sessions for current session
     */
    @Override
    public void sessionDestroyed(final HttpSessionEvent arg0) {
    	removed = true;
        final HttpSession webSession = arg0.getSession();
        webSession.removeAttribute(TnConstants.SESSION_COUNTER);
        closeTn5250Session(webSession);
    }

    /**
     * Remove 5250 server configurations from memory
     */
    @Override
    public void contextDestroyed(final ServletContextEvent arg0) {
    	removed = true;
        final ServletContext servletContext = arg0.getServletContext();
        servletContext.removeAttribute(TnHost.class.getCanonicalName());
        GZipCache.release(servletContext);
    }

    /**
     * Load 5250 server configurations
     */
    @Override
    public void contextInitialized(final ServletContextEvent arg0) {
    	removed = false;
        LangTool.init();
        final Map<String, TnHost> hosts = TnConfigLoader.reload();
        final ServletContext servletContext = arg0.getServletContext();
        servletContext.setAttribute(TnHost.class.getCanonicalName(), hosts);
    }

}
