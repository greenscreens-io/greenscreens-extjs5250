/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.tn5250;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;

import io.greenscreens.WS4ISConstants;
import io.greenscreens.data.tn5250.Tn5250ScreenElement;

/**
 * Class representing 5250 screen data.
 * It is used to prepare 5250 screen data for web
*
 * Called from parser to create screen elements
 * that will be rendered on web page.
 *
 * Screen elements can e fields or ready only text
 */
@Vetoed
final class Tn5250ScreenData implements TN5250jConstants {

	private final char[] text;
	private final char[] attr;
	private final char[] isAttr;
	private final char[] color;
	private final char[] extended;
	private final char[] graphic;
	private final char[] field;

	private final ScreenFields screenFields;
	private final int numRows;
	private final int numCols;
	private final int lenScreen;
	private final List<Tn5250ScreenElement> screenElements;

	private int row = 1;
	private int col = 0;
	private int lastAttr = 32;
	private boolean newLine = true;
	private Tn5250ScreenElement element = null;

	/**
	 * Main constructor to initialize screen grid based on screen size
	 * @param   [description]
	 * @param   [description]
	 * @param   [description]
	 * @return  [description]
	 */
	public Tn5250ScreenData(final int startRow, final int startCol, final Screen5250 screen) {

		this.screenElements = new ArrayList<Tn5250ScreenElement>();
		this.screenFields = screen.getScreenFields();
		this.numCols = screen.getColumns();
		this.numRows = screen.getRows();
		this.lenScreen = screen.getScreenLength();

		final int size = numCols * numRows;

		text = new char[size];
		attr = new char[size];
		isAttr = new char[size];
		color = new char[size];
		extended = new char[size];
		graphic = new char[size];
		field = new char[size];

		init(startRow, startCol, screen);
	}

	/**
	 * Copy creen data into char arrays
	 * Underlaying planes can be updated by remote terminal service
	 * Here we are keeping last screen image for front
	 * @param  [description]
	 * @param  [description]
	 * @param  [description]
	 */
	private void init(final int startRow, final int startCol, final Screen5250 screen) {

		final int size = numCols * numRows;
		final int endRow = numRows;
		final int endCol = numCols;

		//initScreenElement(0);

		if (size == screen.getScreenLength()) {
			screen.GetScreen(text, size, PLANE_TEXT);
			screen.GetScreen(attr, size, PLANE_ATTR);
			screen.GetScreen(isAttr, size, PLANE_IS_ATTR_PLACE);
			screen.GetScreen(color, size, PLANE_COLOR);
			screen.GetScreen(extended, size, PLANE_EXTENDED);
			screen.GetScreen(graphic, size, PLANE_EXTENDED_GRAPHIC);
			screen.GetScreen(field, size, PLANE_FIELD);
		} else {
			screen.GetScreenRect(text, size, startRow, startCol, endRow, endCol, PLANE_TEXT);
			screen.GetScreenRect(attr, size, startRow, startCol, endRow, endCol, PLANE_ATTR);
			screen.GetScreenRect(isAttr, size, startRow, startCol, endRow, endCol, PLANE_IS_ATTR_PLACE);
			screen.GetScreenRect(color, size, startRow, startCol, endRow, endCol, PLANE_COLOR);
			screen.GetScreenRect(extended, size, startRow, startCol, endRow, endCol, PLANE_EXTENDED);
			screen.GetScreenRect(graphic, size, startRow, startCol, endRow, endCol, PLANE_EXTENDED_GRAPHIC);
			screen.GetScreenRect(field, size, startRow, startCol, endRow, endCol, PLANE_FIELD);
		}
	}

	/**
	 * Does screen contains any data
	 * @return [description]
	 */
	public boolean isEmpty() {
		return (new String(text)).trim().length() == 0;
	}

	/**
	 * Get current screen element
	 * @return [description]
	 */
	public Tn5250ScreenElement getElement() {
		return element;
	}

	/**
	 * Create ne screen element with last detected screen attribute
	 * @return [description]
	 */
	public Tn5250ScreenElement initScreenElement() {
		return initScreenElement(row, lastAttr);
	}

	/**
	 * Create ne screen element with override screen attribute
	 * @param  int [description]
	 * @return     [description]
	 */
	public Tn5250ScreenElement initScreenElement(final int lastAttr) {
		return initScreenElement(row, lastAttr);
	}

	private Tn5250ScreenElement initScreenElement(final int row, final int lastAttr) {
		element = new Tn5250ScreenElement();
		element.setFieldId(-1); // not a field
		element.setAttributeId(lastAttr);
		element.setRow(row);
		element.setValue("");
		screenElements.add(element);
		return this.element;
	}

	/**
	 * Copy char from screen position into current screen element
	 * @param int [description]
	 */
	public void addText(final int pos) {
		char chr = text[pos];
		//if ((element.getAttributeId() & 0x7) == 0x4)

		element.addToValue(chr);
	}

	/**
	 * Add space to screen last active element.
	 */
	public void addSpace() {
		initScreenElement(39);
		element.setAttributeId(39);
		element.addToValue(' ');
		//setChangeAttr(true);
	}

	/**
	 * Check to see if plane position contains field
	 * @param  int [description]
	 * @return     [description]
	 */
	public boolean isField(final int pos) {
		return field[pos] != 0;
	}

	/**
	 * Check to see if lane position is empty or have visile text
	 * @param  int [description]
	 * @return     [description]
	 */
	public boolean isText(final int pos) {
		return isAttr[pos] == 0;
	}

	/**
	 * Check character to see is it in unicode range of RTL languages
	 * If so, mark field as RTL
	 * @param  c Character code
	 * @return   Return true / false
	 */
	int isRtl(int c) {
	  	  if (
	  	    (c==0x05BE)||(c==0x05C0)||(c==0x05C3)||(c==0x05C6)||
	  	    ((c>=0x05D0)&&(c<=0x05F4))||
	  	    (c==0x0608)||(c==0x060B)||(c==0x060D)||
	  	    ((c>=0x061B)&&(c<=0x064A))||
	  	    ((c>=0x066D)&&(c<=0x066F))||
	  	    ((c>=0x0671)&&(c<=0x06D5))||
	  	    ((c>=0x06E5)&&(c<=0x06E6))||
	  	    ((c>=0x06EE)&&(c<=0x06EF))||
	  	    ((c>=0x06FA)&&(c<=0x0710))||
	  	    ((c>=0x0712)&&(c<=0x072F))||
	  	    ((c>=0x074D)&&(c<=0x07A5))||
	  	    ((c>=0x07B1)&&(c<=0x07EA))||
	  	    ((c>=0x07F4)&&(c<=0x07F5))||
	  	    ((c>=0x07FA)&&(c<=0x0815))||
	  	    (c==0x081A)||(c==0x0824)||(c==0x0828)||
	  	    ((c>=0x0830)&&(c<=0x0858))||
	  	    ((c>=0x085E)&&(c<=0x08AC))||
	  	    (c==0x200F)||(c==0xFB1D)||
	  	    ((c>=0xFB1F)&&(c<=0xFB28))||
	  	    ((c>=0xFB2A)&&(c<=0xFD3D))||
	  	    ((c>=0xFD50)&&(c<=0xFDFC))||
	  	    ((c>=0xFE70)&&(c<=0xFEFC))||
	  	    ((c>=0x10800)&&(c<=0x1091B))||
	  	    ((c>=0x10920)&&(c<=0x10A00))||
	  	    ((c>=0x10A10)&&(c<=0x10A33))||
	  	    ((c>=0x10A40)&&(c<=0x10B35))||
	  	    ((c>=0x10B40)&&(c<=0x10C48))||
	  	    ((c>=0x1EE00)&&(c<=0x1EEBB))
	  	  ) return 1;
	  	  return 0;
	  	}

	/**
	 * If attribute changed compared to previous positio,
	 * then it is time to create new screen element
	 * @param int [description]
	 */
	public void updateIfAttributeChanged(final int pos) {

		//boolean isRtl = isRtl(text[pos])==1;
		setLastAttr(pos);
		if (newLine) {
			newLine = false;
			initScreenElement();
			return;
		}

		if (attr[pos-1] != attr[pos] || field[pos-1] != field[pos] || color[pos-1] != color[pos] || isAttr[pos-1] != isAttr[pos]) {
			initScreenElement();
		} else {

			if (element.isRtl()) {
				initScreenElement();
			}

			// fix for new renderer - clickable buttons must be separated to be recognized
			if(text[pos-1] != text[pos] && text[pos] == ' ' ) {
				initScreenElement();
			}
		}

	}

	/**
	 * Check if we mowed to new line.
	 */
	public void updateIfNewLine() {
		++col;
		if (col == numCols) {
			col = 0;
			row++;
			newLine = true;
		}
	}

	public void updateIfHiddenField(final int pos) {
		char chr = extended[pos];
		if ((chr & TN5250jConstants.EXTENDED_5250_NON_DSP) != 0) {
			element.setHidden(1);
		}
	}

	public void updateIfBypassField(final int pos) {
		if ((int) attr[pos] == 39) {
			element.setHidden(1);
		}
	}

	public ScreenField findScreenField(final int pos) {
		return screenFields.findByPosition(pos);
	}

	public int getLenScreen() {
		return lenScreen;
	}

	public int getLastAttr() {
		return lastAttr;
	}

	public void setLastAttr(final int pos) {
		this.lastAttr = attr[pos];
	}

	public int getRow() {
		return row;
	}

	public void setRow(final int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(final int col) {
		this.col = col;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public ScreenFields getScreenFields() {
		return screenFields;
	}

	public List<Tn5250ScreenElement> getScreenElements() {
		return screenElements;
	}

	public void updateScreenElements() {
		for (final Tn5250ScreenElement element : screenElements) {
			element.update();
		}
	}

}
