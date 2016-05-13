/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.data.tn5250;

/**
 * Request sent from web 5250 to process screen.
 * Contains device name, current cursor field &
 * position and pressed keyboard.
 */
public class Tn5250ScreenRequest {

    private String keyRequest;
    private int cursorField;
    private int cursorCol;
    private int cursorRow;
    private String displayID;
    private String data;
    private boolean blank = false;  // for cursor positioning only

    public final String getKeyRequest() {
        return keyRequest;
    }

    public final void setKeyRequest(final String keyRequest) {
        this.keyRequest = keyRequest;
    }

    public final int getCursorField() {
        return cursorField;
    }

    public final void setCursorField(final int cursorField) {
        this.cursorField = cursorField;
    }

    public final int getCursorRow() {
        return cursorRow;
    }

    public final void setCursorRow(final int cursorRow) {
        this.cursorRow = cursorRow;
    }

    public int getCursorCol() {
		return cursorCol;
	}

	public void setCursorCol(int cursorCol) {
		this.cursorCol = cursorCol;
	}

	public final String getData() {
        return data;
    }

    public final void setData(final String data) {
        this.data = data;
    }

    public final String getDisplayID() {
        return displayID;
    }

    public final void setDisplayID(final String displayID) {
        this.displayID = displayID;
    }

	public boolean isBlank() {
		return blank;
	}

	public void setBlank(boolean blank) {
		this.blank = blank;
	}

}
