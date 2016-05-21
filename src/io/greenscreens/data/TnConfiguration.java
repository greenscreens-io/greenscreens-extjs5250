/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Properties parser
 */
public class TnConfiguration extends Properties {

	private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(TnConfiguration.class);
    
	// 15 seconds time difference for timestamp 
	private final static long TIMEDIFF = 15000;
	
    // SUFFIXES FOR REMOTE 5250 HOST
    private static final String HOST_PREFIX = "prefixes";

    // address of 5250 server
    private static final String HOST_IP = "ip";

    // port of 5250 server
    private static final String HOST_PORT = "port";

    // virtual name used from frontend
    private static final String HOST_NAME = "name";

    // supported code page
    private static final String CODE_PAGE = "codepage";
    
    // default false, bypass message screen of another user job
    private static final String MSG_BYPASS = "msgbypass";
    
    // display name prefix, if given name GT 7, rest will be cutoff
    private static final String DISPLAY_PREFIX = "display_prefix";
    
    // trigger for message from as400 used to close session on web
    private static final String CLOSE_MSG = "close_msg";
    
    private static final String SHARED_PWD = "shared_pwd";
    private static final String SHARED_IV = "shared_iv";
    private static final String SHARED_TIME = "shared_time";

    
    public final String[] getPrefixes() {
    	
        final String prefixes = (String) this.get(HOST_PREFIX);
        
        if (prefixes == null) {
            throw new RuntimeException("5250 hosts not set in configuration file!");
        }
        return prefixes.split(",");
    }
    
    public final TnHost getHost(String prefix) throws RuntimeException {    	
        String tmp = prefix + ".";
        TnHost rh = new TnHost();
        rh.setIpAddress(this.getProperty(tmp + HOST_IP));
        rh.setPort(this.getProperty(tmp + HOST_PORT));
        rh.setName(this.getProperty(tmp + HOST_NAME));
        rh.setCodePage(this.getProperty(tmp + CODE_PAGE));
        rh.setBypassMsg(this.getProperty(tmp + MSG_BYPASS));
        rh.setDisplayPrefix(this.getProperty(tmp + DISPLAY_PREFIX));
        rh.setCloseMessage(this.getProperty(tmp + CLOSE_MSG));
        return rh;
    }
    
    public final Map<String, TnHost> getHosts() throws RuntimeException {
    	
        final HashMap<String, TnHost> hosts = new HashMap<String, TnHost>();
        // load 5250 hosts
        String[] prefs = getPrefixes();
        for (String pref : prefs) {
            TnHost rh = this.getHost(pref);
            if (rh.isValid()) {
                hosts.put(rh.getName(), rh);
            } else {
                LOGGER.warn("Host config not valid for {}", rh.getName());
            }
        }
        return Collections.unmodifiableMap(hosts);    	    	
    }
    
    public long getSharedTime() {
    	
    	long time = TIMEDIFF;
    	
        final String shared_time = (String) this.get(SHARED_TIME);
        if (shared_time != null) {
        	try {
        		time = Long.parseLong(shared_time);
        	} catch(Exception e) {
        		LOGGER.error(e.getMessage(), e);
        	}
        }
        
        return time;    	
    }
    
    public String getSharedIV() {
    	return (String) this.get(SHARED_IV);
    }
    
    public String getSharedPassword() {
    	return (String) this.get(SHARED_PWD);
    }    
            
}
