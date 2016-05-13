/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.tn5250;

import javax.enterprise.inject.Vetoed;

import io.greenscreens.data.TnConstants;
import io.greenscreens.data.TnHost;
import io.greenscreens.data.tn5250.Tn5250ScreenResponse;
import io.greenscreens.websocket.WebSocketSession;
import io.greenscreens.websocket.data.WebSocketInstruction;
import io.greenscreens.websocket.data.WebSocketResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tn5250j.Session5250;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.event.ScreenListener;
import org.tn5250j.event.ScreenOIAListener;
import org.tn5250j.event.SessionChangeEvent;
import org.tn5250j.event.SessionListener;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenOIA;

/**
 * Session5250 screen change listener. Whenever screen changes, new websocket
 * event is sent to the browser for screen refresh with new data
 */
@Vetoed
class Tn5250SessionListener implements SessionListener, ScreenListener, ScreenOIAListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(Tn5250SessionListener.class);

	/* used for data sending to websocket */
	private final String displayID;
	private WebSocketSession wsSession;

	/* internal properties */
	private Session5250 session;
	private Screen5250 screen;
	private ScreenOIA oia;

	private int screenSize;
	private boolean connected;
	private boolean isBypassMsg;
	private String bypassMsg;

	/**
	 * Main constructor
	 * @param   [description]
	 * @param   [description]
	 * @param   [description]
	 * @return  [description]
	 */
	public Tn5250SessionListener(final WebSocketSession wsSession, final String displayId, final TnHost host) {
		this.wsSession = wsSession;
		this.displayID = displayId;
		this.screenSize = 80;
		this.bypassMsg = host.getCloseMsg();
		this.isBypassMsg = host.isBypassMsg();
	}

	public void onSessionChanged(final SessionChangeEvent arg0) {
		// connect/disconnect

		boolean process = wsSession != null && wsSession.isOpen() && !connected;
		process = process && arg0.getState() == TN5250jConstants.STATE_CONNECTED;

		// System.out.println("SESSION_CHANGED :" + arg0.getState());
		if (process) {
			connected = true;
			session = (Session5250) arg0.getSource();
			screen = session.getScreen();
			screen.addScreenListener(this);
			oia = screen.getOIA();
			oia.addOIAListener(this);
			sendData(1);
		}
	}

	public void onScreenSizeChanged(final int rows, final int cols) {
		// System.out.println("SCREEN_SIZE_CHANGED" + rows + "/" + cols);
		this.screenSize = cols;
	}

	public void onScreenChanged(final int inUpdate, final int startRow, final int startCol, final int endRow, final int endCol) {
		// System.out.println("SCREEN_CHANGED " + inUpdate);
		sendData(inUpdate);
	}

	public void onOIAChanged(final ScreenOIA oia, final int OIAchange) {
		sendData(0);
	}

	/**
	 * Get screen from terminal session and convert it to web screen elements
	 * @param  inUpdate [description]
	 * @return          [description]
	 */
	private Tn5250ScreenResponse getResponseScreen(int inUpdate) {

		final Tn5250ScreenResponse response = new Tn5250ScreenResponse(true, null);
		final boolean locked = oia.isKeyBoardLocked();
		final boolean msgw = oia.isMessageWait();
		final boolean clrscr = oia.getLevel() == ScreenOIA.OIA_LEVEL_CLEAR_SCREEN;

		response.setClearScr(clrscr);
		response.setSize(screenSize);
		response.setLocked(locked);
		response.setMsgw(msgw);
		response.setInhibited(oia.getInputInhibited());
		response.setLevel(oia.getLevel());
		response.setComm(oia.getCommCheckCode());
		response.setMach(oia.getMachineCheckCode());
		response.setDisplayID(displayID);

		int row = session.getScreen().getCurrentRow() - 1;
		int col = session.getScreen().getCurrentCol() - 1;
		response.setCol(col);
		response.setRow(row);

		//System.out.println("Col :" +  col + " -  row: " + row);

		if (!session.isConnected()) {
			response.setMsg(TnConstants.NOT_CONNECTED);
			response.setCode(TnConstants.NOT_CONNECTED_CODE);
			response.setClearScr(true);
			response.setLocked(true);
		} else if (inUpdate == 1) {
			response.setClearScr(true);
			Tn5250StreamProcessor.refresh(session, response);
		}

		return response;
	}

	private final static String A = "Display Program Messages";
	private final static String B = "is allocated to another job.";
	private final static String C = "Press Enter to continue.";
	private final static String D = "F3=Exit";
	private final static String E = "F12=Cancel";
	private int msgpass = 0;

	/**
	 * Detect first post signon screen for auto bypass
	 * @param  val [description]
	 * @return     [description]
	 */
	private boolean msgExists(String val) {
		boolean sts = false;

		if (val==null) return sts;

		if (val.contains(A) && msgpass == 0) {
			msgpass++;
			sts = true;
		}
		if (val.contains(B) && msgpass == 1) {
			msgpass++;
			sts = true;
		}
		if (val.contains(C) && msgpass == 2) {
			msgpass++;
			sts = true;
		}
		if (val.contains(D) && msgpass == 3) {
			msgpass++;
			sts = true;
		}
		if (val.contains(E) && msgpass == 4) {
			msgpass++;
			sts = true;
		}
		return sts;
	}

	/**
	 * Check for dup session message screen for same user session
	 * @return
	 */
	private boolean isUserJobScreen(String data) {

		if (data.length() == 0) {
			return false;
		}

		boolean sts = false;
		if (msgpass == 5) {
			msgpass = 0;
			msgExists(data);
		} else {
			msgExists(data);
			if (msgpass == 5) {
				sts = true;
			}
		}

		return sts;
	}

	/**
	 * If detected message on terminal screen, auto close connection
	 *
	 * @param  data [description]
	 * @return      [description]
	 */
	private boolean isCloseSignal(String data) {

		if (bypassMsg == null || data == null) {
			return false;
		}
		if (data.length() == 0 || bypassMsg.length() == 0) {
			return false;
		}
		return data.equals(bypassMsg);
	}

	/**
	 * Check for another job message screen bypass
	 *
	 * @param responseScreen
	 * @return
	 */
	private boolean checkForBypass(final Tn5250ScreenResponse responseScreen, final String data) {

		if (isUserJobScreen(data)) {
			session.getScreen().sendKeys(TN5250jConstants.MNEMONIC_ENTER);
			return true;
		}

		return false;
	}

	/**
	 * Send generated screen to web
	 * @param inUpdate [description]
	 */
	private void sendData(int inUpdate) {

		boolean isClose = false;
		try {
			if (wsSession != null && wsSession.isOpen()) {
				final Tn5250ScreenResponse responseScreen = getResponseScreen(inUpdate);

				String data = new String(screen.getScreenAsChars()).trim();
				if (isBypassMsg && checkForBypass(responseScreen, data)) {
					return;
				}

				isClose = isCloseSignal(data);
				if (isClose) {
					responseScreen.setSize(0);
				}

				final WebSocketResponse wsResponse = new WebSocketResponse(WebSocketInstruction.DATA);
				wsResponse.setData(responseScreen);
				wsSession.sendResponse(wsResponse, true);
			}

			if (isClose) {
				session.disconnect();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
