/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.data;

/**
 * Constants used by tn5250 websocket service.
 */
public enum TnConstants {
    ;
    
    //public static final String HOST_5250_CONFIG = TnConstants.class.getCanonicalName() + "_HOST_5250_CONFIG";
	
	public static final String VERSION     =  "2.7";
    
    //public static final String DISPLAY_ID        =  "Display name not set";
    public static final String NOT_CONNECTED     =  "Session not connected";
    public static final String UNABLE_TO_CONNECT =  "Unable to connect";
    public static final String NO_DATA           =  "Screen data not received";
    public static final String INVALID_DISPLAY   =  "Invalid display name";
    public static final String NO_HOST_CONFIG    =  "Hosts configurations is missing";
    public static final String HOST_NOT_FOUND    =  "Host not found";
    public static final String SESSION_NOT_FOUND =  "Session not found";
    public static final String REQUEST_ERROR     =  "Request error";
    
    public static final String DISPLAY_ID_CODE        =  "E0001";
    public static final String NOT_CONNECTED_CODE     =  "E0002";
    public static final String UNABLE_TO_CONNECT_CODE =  "E0003";
    public static final String NO_DATA_CODE           =  "E0004";
    public static final String INVALID_DISPLAY_CODE   =  "E0005";
    public static final String NO_HOST_CONFIG_CODE    =  "E0006";
    public static final String HOST_NOT_FOUND_CODE    =  "E0007";
    public static final String SESSION_NOT_FOUND_CODE =  "E0008";
    public static final String REQUEST_ERROR_CODE     =  "E0009";    
    
    public static final String SESSION_STORE     = "tn5250";
    public static final String CURRENT_SESSION   = "tn5250_display";
    public static final String SESSION_COUNTER   = "tn5250_counter";
    
}
