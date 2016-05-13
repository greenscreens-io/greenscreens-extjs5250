/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*
 * This class is used to cast to Screenelement as
 *  helper to map to shortened method names
 */
package io.greenscreens.data.tn5250;

import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Screen element is mapped to this to minimize 
 * communication between browser and client.
 */
public class Tn5250ScreenElement {

    public long[] d = { 0, 0, 0, 0, 0, 0, 0, 0};
    public String t;
    
    public boolean c; // changed
    
    private transient boolean isRtl = false;
    public transient StringBuffer text = new StringBuffer();

    @JsonIgnore
	@Transient
    public final long getHidden() {
        return d[0];
    }

    public final void setHidden(final long isHidden) {
        d[0] = isHidden;
    }

    @JsonIgnore
    @Transient
    public final long getFieldType() {
        return d[1];
    }

    public final void setFieldType(final long fieldType) {
        d[1] = fieldType;
    }

    @JsonIgnore
    @Transient
    public final int getFieldId() {
        return (int) d[2];
    } 

    public final void setFieldId(final long fieldId) {
        d[2] = fieldId;
    }

    @JsonIgnore
    @Transient
    public final long getAttributeId() {
        return d[3];
    }

    public final void setAttributeId(final long attributeId) {
        d[3] = attributeId;
    }

    @JsonIgnore
    @Transient
    public final long getLength() {
        return d[4];
    }

    public final void setLength(final long length) {
        d[4] = length;
    }

    @JsonIgnore
    @Transient
    public final long getMaxLength() {
        return d[5];
    }

    public final void setMaxLength(final long maxLength) {
        d[5] = maxLength;
    }

    @JsonIgnore
    @Transient
    public final long getRow() {
        return d[6];
    }

    public final void setRow(final long row) {
        d[6] = row;
    }

    /*
    public final void setFCW1(final long row) {
    	d[7] = row;
    }

    public final void setFCW2(final long row) {
    	d[8] = row;
    }

    public final void setFFW1(final long row) {
    	d[9] = row;
    }

    public final void setFFW2(final long row) {
    	d[10] = row;
    }
    */
    
    @JsonIgnore
    @Transient
    public final String getValue() {
        return t;
    }

    public final void setValue(final String value) {
        t = value;
    }

    public final void addToValue(final char value) {
        if (value == 0) {
            text.append(" ");
        } else {
        	text.append(value);
        }
    }
    
    public final void update() {
        if (d[2] > 0) {
            return;
        }
        t = text.toString();
        if (isRtl) {
        	t = (new StringBuilder(t)).reverse().toString();
        }
    }

	public void setRightAdjustment(boolean rightAdjust) {
		if(rightAdjust) {
		  d[7] = 1;	
		}
	}

    @JsonIgnore
	@Transient	
    public boolean isRtl() {
		return isRtl;
	}
    
	public void setRtl(boolean isRtl) {
		this.isRtl = isRtl;
	}

    @JsonIgnore
	@Transient
	public boolean isChanged() {
		return c;
	}

	public void setChanged(boolean c) {
		this.c = c;
	}
	
}
