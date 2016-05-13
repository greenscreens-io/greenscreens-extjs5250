/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens;

import javax.servlet.ServletContext;

/**
 * Enumeration containing some internally used constants
 */
public enum WS4ISConstants {

    ;

    public static final String HTTP_SEESION_STATUS   = "ws4is.session.status";
    public static final String HTTP_SEESION_REQUIRED = "Websocket requires valid http session";

    public static final String WEBSOCKET_PATH        = "ws4is.websocket.path";
    public static final String WEBSOCKET_SUBPROTOCOL = "ws4is";
    public static final String WEBSOCKET_TYPE        = "ws";

    public static final String DIRECT_SERVICE_NOT_FOUND = "Requested ExtDirect Service not found";

    public static final String DIRECT_SERVICE_NOT_FOUND_CODE = "E0000";
    public static final String TEMP              = System.getProperty("java.io.tmpdir");
    private static String SUB_TEMP = "";

	/**
	 * This is pah of current web application
	 * @return [description]
	 */
	public final static String getSUB_TEMP() {
		return SUB_TEMP;
	}

	/**
	 * Pah of current web application is set on app startup
	 * @param context [description]
	 */
	public final static void initialize(ServletContext context) {
		SUB_TEMP = context.getContextPath();
	}

}
