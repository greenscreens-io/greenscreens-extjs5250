/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.websocket;

import javax.enterprise.inject.Vetoed;

/**
 * Class holding event data  
 */
@Vetoed
public class WebsocketEvent {

    private final WebSocketSession session;
    private final WebSocketEventStatus eventStatus;
    private final Throwable throwable;

    public WebsocketEvent(final WebSocketSession session, final WebSocketEventStatus eventStatus) {
        super();
        this.session = session;
        this.eventStatus = eventStatus;
        this.throwable = null;
    }

    public WebsocketEvent(final WebSocketSession session, final WebSocketEventStatus eventStatus, final Throwable throwable) {
        super();
        this.session = session;
        this.eventStatus = eventStatus;
        this.throwable = throwable;
    }

    public final WebSocketSession getWebSocketSession() {
        return session;
    }

    public final WebSocketEventStatus getEventStatus() {
        return eventStatus;
    }

    public final Throwable getThrowable() {
        return throwable;
    }

}
