/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import io.greenscreens.WS4ISConstants;

/**
 * Set session status flags for WebSOcket service to be able to detect if session is destroyed.
 * For example, in SPA apps, user can logout, but on second browser tab websocket is still active, 
 * so WS has to know session is ended to prevent invalid calls. 
 */
@WebListener
public final class WebSocketHttpSessionListener implements HttpSessionListener, ServletContextListener {

    @Override
    public void sessionCreated(final HttpSessionEvent arg0) {
        final HttpSession httpSession = arg0.getSession();
        httpSession.setAttribute(WS4ISConstants.HTTP_SEESION_STATUS, Boolean.TRUE.toString());
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent arg0) {
        final HttpSession httpSession = arg0.getSession();
        httpSession.setAttribute(WS4ISConstants.HTTP_SEESION_STATUS, Boolean.FALSE.toString());
    }

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		WS4ISConstants.initialize(arg0.getServletContext());
	}

}
