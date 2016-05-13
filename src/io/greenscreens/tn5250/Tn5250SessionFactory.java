/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.tn5250;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.inject.Vetoed;
import javax.servlet.http.HttpSession;

import org.tn5250j.Session5250;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.common.SessionManager;

import io.greenscreens.data.TnConstants;
import io.greenscreens.data.TnHost;
import io.greenscreens.websocket.WebSocketSession;

/**
 * tn5250 connection factory
 */
@Vetoed
public enum Tn5250SessionFactory {
    ;

	//private static final Logger LOGGER = LoggerFactory.getLogger(Tn5250SessionFactory.class);


	/**
	 * Create auto sign-on session with screen bypass
	 *
	 * @param wsSession
	 * @param host
	 * @param displayId
	 * @param displayName
	 * @param username
	 * @param password
	 * @param program
	 * @param menu
	 * @param lib
	 * @return
	 */
    public static final ITn5250Session create(final WebSocketSession wsSession,
    										  final TnHost host,
    										  final String displayId,
    										  final String displayName,
    								          final String username,
    								          final String password,
    								          final String program,
    								          final String menu,
    								          final String lib
    										  ) {
    	final String _displayName = getDisplayName(wsSession, host, displayName);
        final Session5250 session = createSession(wsSession, host, _displayName, displayId, username, password, program, menu, lib);
        return new Tn5250Session(wsSession, displayId, _displayName, session, host);
    }


    /**
     * Create standard session with sign-on screen
     *
     * @param wsSession
     * @param host
     * @param displayId
     * @param displayName
     * @return
     */
    public static final ITn5250Session create(final WebSocketSession wsSession, final TnHost host, final String displayId, final String displayName) {

    	final String _displayName = getDisplayName(wsSession, host, displayName);
        final Session5250 session = createSession(wsSession, host, _displayName, displayId, null ,null, null, null, null);
        return new Tn5250Session(wsSession, displayId, _displayName, session, host);
    }

    /**
     * Display names must be unique so we can refer to them from web
     * One session can have multiple terminal screen open in different tabs.
     * DisplayID is needed to know for which tab we will retrieve wich terminal data.
     *
     * @param  WebSocketSession [description]
     * @return                  [description]
     */
    private static final String getDisplayName(final WebSocketSession wsSession) {
        final HttpSession session = wsSession.getHttpSession();
        final AtomicInteger counter = (AtomicInteger) session.getAttribute(TnConstants.SESSION_COUNTER);
        return Integer.toString(counter.incrementAndGet());
    }

    /**
     * Generate display name for terminal based on prefix from configuration
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @return  [description]
     */
	private static final String getDisplayName(final WebSocketSession wsSession, final TnHost host, final String displayName) {

        String _displayName = null;

        if (displayName != null && displayName.length()>0) {

        	if (host.getDisplayPrefix() != null) {
    			_displayName = host.getDisplayPrefix().concat(displayName);
    		} else {
    			_displayName = displayName;
    		}

    	} else {

            if (host.getDisplayPrefix() != null) {
    			_displayName = host.getDisplayPrefix().concat(getDisplayName(wsSession));
    		} else {
    			_displayName = "DSP_".concat(getDisplayName(wsSession));
    		}

    	}

    	if (_displayName.length() > 10) {
    		return _displayName.substring(0, 10);
    	} else {
    		return _displayName.substring(0, _displayName.length());
    	}
	}

    /**
     * Create terminal sssion, hasa support for bypass signon
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @param   [description]
     * @return  [description]
     */
    private static final Session5250 createSession(final WebSocketSession wsSession,
    											   final TnHost host,
    											   final String displayName,
    											   final String displayId,
    	    								          final String username,
    	    								          final String password,
    	    								          final String program,
    	    								          final String menu,
    	    								          final String lib
    		) {

        final Properties sesProps = new Properties();

        sesProps.put(TN5250jConstants.SESSION_HOST, host.getIpAddress());
        sesProps.put(TN5250jConstants.SESSION_HOST_PORT, host.getPort());

        // sesProps.put(TN5250jConstants.SESSION_CODE_PAGE ,"");

        sesProps.put(TN5250jConstants.SESSION_TN_ENHANCED, "1");
        sesProps.put(TN5250jConstants.SESSION_USE_GUI, "1");
        sesProps.put(TN5250jConstants.SESSION_TERM_NAME_SYSTEM, "1");
        sesProps.put(TN5250jConstants.SESSION_TN_ENHANCED, "1");
        sesProps.put(TN5250jConstants.SESSION_SCREEN_SIZE, TN5250jConstants.SCREEN_SIZE_27X132_STR);
        sesProps.put(TN5250jConstants.SESSION_CODE_PAGE, host.getCodePage());

        // bypass signon data
        if (username != null) {
        	sesProps.put("SESSION_CONNECT_USER", username);
        	if (password != null) {
        		sesProps.put("SESSION_CONNECT_PASSWORD", password);
        	}
        }

    	if (program != null) {
    		sesProps.put("SESSION_CONNECT_PROGRAM", program);
    	}

    	if (menu != null) {
    		sesProps.put("SESSION_CONNECT_MENU", menu);
    	}

    	if (lib != null) {
    		sesProps.put("SESSION_CONNECT_LIBRARY", lib);
    	}


        if (displayName!=null) {
         sesProps.put(TN5250jConstants.SESSION_DEVICE_NAME,displayName.toUpperCase());
        }

        final Tn5250SessionListener listener = new Tn5250SessionListener(wsSession, displayId, host);
        final SessionManager manager = SessionManager.instance();
        final Session5250 hostSession = manager.openSession(sesProps, "", displayName);
        hostSession.addSessionListener(listener);
        hostSession.connect();

        return hostSession;
    }

}
