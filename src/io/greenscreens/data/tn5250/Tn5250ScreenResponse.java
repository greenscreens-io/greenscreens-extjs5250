/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.data.tn5250;

import java.util.List;

import io.greenscreens.ext.ExtJSResponse;

/**
 * Response structure with screen information.
 */
public class Tn5250ScreenResponse extends ExtJSResponse {

    private static final long serialVersionUID = 1L;

    public String devName;
    public String displayID;
    public boolean locked;          // sends lock screen request
    public boolean clearScr;        // sends clear screen request
    public int size = 80;           // sends screen size 80/132
    public boolean msgw;            // sends message wait signal
    public String conerr;           // host error code
    
    public int inhibited;
    public int level;
    public int comm;
    public int mach;

    public int row;
    public int col;
    
    public List<Tn5250ScreenElement> data;

    public Tn5250ScreenResponse() {
        super();
    }

    public Tn5250ScreenResponse(final boolean success, final String message) {
        super(success, message);
    }

    public Tn5250ScreenResponse(final Throwable exception, final String message) {
        super(exception, message);
    }

    public final String getDevName() {
        return devName;
    }

    public final void setDevName(final String devName) {
        this.devName = devName;
    }

    public final boolean isLocked() {
        return locked;
    }

    public final void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public final boolean isClearScr() {
        return clearScr;
    }

    public final void setClearScr(final boolean clearScr) {
        this.clearScr = clearScr;
    }

    public final int getSize() {
        return size;
    }

    public final void setSize(final int size) {
        this.size = size;
    }

    public final boolean isMsgw() {
        return msgw;
    }

    public final void setMsgw(final boolean msgw) {
        this.msgw = msgw;
    }

    public final String getConerr() {
        return conerr;
    }

    public final void setConerr(final String conerr) {
        this.conerr = conerr;
    }

    public final List<Tn5250ScreenElement> getData() {
        return data;
    }

    public final void setData(final List<Tn5250ScreenElement> data) {
        this.data = data;
    }

    public final String getDisplayID() {
        return displayID;
    }

    public final void setDisplayID(final String displayID) {
        this.displayID = displayID;
    }

	public int getInhibited() {
		return inhibited;
	}

	public void setInhibited(int inhibited) {
		this.inhibited = inhibited;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getComm() {
		return comm;
	}

	public void setComm(int comm) {
		this.comm = comm;
	}

	public int getMach() {
		return mach;
	}

	public void setMach(int mach) {
		this.mach = mach;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
}
