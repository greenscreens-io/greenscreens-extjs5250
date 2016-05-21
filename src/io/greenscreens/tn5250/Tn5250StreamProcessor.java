/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.tn5250;

import java.util.List;
import java.util.regex.Pattern;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang.StringUtils;
import org.tn5250j.Session5250;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.tn5250.Screen5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;
import org.tn5250j.framework.tn5250.ScreenOIA;

import io.greenscreens.data.tn5250.Tn5250ScreenElement;
import io.greenscreens.data.tn5250.Tn5250ScreenRequest;
import io.greenscreens.data.tn5250.Tn5250ScreenResponse;

/**
 * Main 5250 stream processor which converts 5250 screen data into web data.
 */
@Vetoed
enum Tn5250StreamProcessor {
    ;

    private static final Pattern LTRIM = Pattern.compile("^\\s+");
    private static final Pattern RTRIM = Pattern.compile("\\s+$");

    /**
     * Left trim text
     * @param  String [description]
     * @return        [description]
     */
    public static String ltrim(final String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    /**
     * Right trim text
     * @param  String [description]
     * @return        [description]
     */
    public static String rtrim(final String s) {
        return RTRIM.matcher(s).replaceAll("");
    }

    /**
     * Refresh current screen.
     */
    public static void refresh(final Session5250 session, final Tn5250ScreenResponse response) {
        final Screen5250 screen = session.getScreen();
        final ScreenOIA oia = screen.getOIA();
        final Tn5250ScreenData screenData  = getResponse(session);
        List<Tn5250ScreenElement> elements = screenData.getScreenElements();
        response.setData(elements);
        response.setSize(screen.getColumns());
        response.setLocked(oia.isKeyBoardLocked());
        response.setMsgw(oia.isMessageWait());
    }

    /**
     * forward request from web to host and waits for return after response,
     * screen will be reloaded
     * TODO - handle session hang & timeout so not to continue
     */
    public static boolean process(final Session5250 session, final Tn5250ScreenRequest request, final Tn5250ScreenElement[] fields) {

        boolean sendIt = false;

        // Parameter PF set in javascript by key press
        String aidS = request.getKeyRequest();
        if (aidS != null && aidS.length() > 0) {
            sendIt = true;
        }

        if (aidS != null && aidS.length() > 0) {
            aidS = "[" + aidS.toLowerCase() + "]";
        } else {
            aidS = TN5250jConstants.MNEMONIC_ENTER;
        }

        if (aidS.equals(TN5250jConstants.MNEMONIC_SYSREQ)) {
            if (request.getData() == null) {
            	session.getVT().systemRequest(" ");
            } else {
            	session.getVT().systemRequest(request.getData());
            }
            return true;
        }

        final Screen5250 screen = session.getScreen();
        // fill screen fields from web data
        if (updateFieldValues(screen, fields)) {
            sendIt = true;
        }

        // get currently cursor positioned field
        if (request.isBlank()) {
        	screen.setCursor(request.getCursorRow(), request.getCursorCol());
        } else {
        	processCursor(screen, request.getCursorField());
        }

        sendKeyRequest(screen, sendIt, aidS);

        return true;
    }

    /**
     *  sends request from web to host
     */
    private static void sendKeyRequest(final Screen5250 screen, final boolean sendIt, final String aidS) {
        if (sendIt || screen.getScreenFields().getFieldCount() == 0) {
            screen.sendKeys(aidS);
        }
    }

    /**
     * detect cursor position and update position to 5250 screen
     */
    private static void processCursor(final Screen5250 screen, final int field) {

        int fld = field;
        boolean isField = fld > 0;

        if (isField) {
        	
          if (fld > 1000) {
            fld = fld / 1000;
          }

          ScreenField sf = screen.getScreenFields().getField(fld - 1);
          if (sf != null) {
        	  isField = sf.isBypassField();
          }
          
        } else {
          fld = screen.getScreenFields().getSize();
        }

        screen.gotoField(fld);

    }

    /**
     *  update values from web field to 5250 screen fields
     */
    private static boolean updateFieldValues(final Screen5250 screen, final Tn5250ScreenElement[] webFields) {

        boolean sendIt = false;
        final ScreenFields fields = screen.getScreenFields();

        int pad = 0;
        String fieldValue = null;

        for (final Tn5250ScreenElement webField : webFields) {

            fieldValue = webField.getValue();
            final ScreenField screenField = fields.getField(webField.getFieldId() - 1);

            if (screenField.getFieldLength() < screen.getColumns()) {
                if (fieldValue == null ) continue;
                if (!webField.isChanged()) continue;
            }

            if(screenField.isToUpper()) {
            	fieldValue = fieldValue.toUpperCase();
            }

            if (fieldValue.length() > 0) {
            //if (screenField.getString().length()>0) {
                sendIt = true;
                screen.setCursor(screenField.startRow(), screenField.startCol());

                if (screenField.isRightToLeft() || screenField.isNumeric() || screenField.isSignedNumeric()) {
                	pad = screenField.getLength() - fieldValue.length();
                	fieldValue = StringUtils.repeat(" ", pad) + fieldValue;
                }

                screenField.setString(fieldValue);

                // System.out.println("FLD" + (x + 1) + "-> " + field + " -> " +
                // sf.getString());
            } else {
            	if (screenField.getString().trim().length() > 0) {
            		fieldValue = StringUtils.repeat(" ", screenField.getLength());
                	screenField.setString(fieldValue);
                }
            }

        }
        return sendIt;
    }

    /**
     * Process response screen and fills up list of screen elements ready to be sent to the browser
     */
    private static Tn5250ScreenData getResponse(final Session5250 session) {

        final Screen5250 screen = session.getScreen();
        final Tn5250ScreenData screenData = new Tn5250ScreenData(1, 1, screen);
        processScreenData(screenData);
        return screenData;
    }

    /**
     * Process parsed telnet screen into data for web
     */
    private static void processScreenData(final Tn5250ScreenData screenRect) {

    	int pos = 0;

    	while (pos < screenRect.getLenScreen()) {

            //screenRect.updateIfColorChanged(pos);
            screenRect.updateIfAttributeChanged(pos);

        	// added screen fields check for debug stream
            if (screenRect.getScreenFields() != null && screenRect.isField(pos)) {
                processField(screenRect, pos);
            } else {
                if (screenRect.isText(pos)) {
                    screenRect.addText(pos);
                } else {
                    screenRect.addSpace();
                }
            }

            screenRect.updateIfNewLine();

            pos++;
        }
        screenRect.updateScreenElements();
    }

    /**
     * Find field by position and start processing it
     */
    private static void processField(final Tn5250ScreenData screenRect, final int pos) {

        final ScreenField screenField = screenRect.findScreenField(pos);
        final boolean process = screenField != null && screenField.startPos() == pos;

        if (process) {
            processField(screenRect, pos, screenField);
        }

    }

    /**
     * Process single field data
     */
    private static void processField(final Tn5250ScreenData screenRect, final int pos, final ScreenField screenField) {
        screenRect.initScreenElement();
        screenRect.updateIfHiddenField(pos);

        if (screenField.isBypassField()) {
            screenRect.updateIfHiddenField(pos);
        }

        // if the field will extend past the screen column size
        // we will just truncate it to be the size of the rest
        // of the screen.
        int len = screenField.getLength();
        if (screenRect.getCol() + len > screenRect.getNumCols()) {
            len = screenRect.getNumCols() - screenRect.getCol();
        }

        // get the field contents and only trim the non numeric
        // fields so that the numeric fields show up with
        // the correct alignment within the field.
        final String value = getFieldValue(screenField);
        final int focusfield = getFocusField(screenRect.getScreenFields());
        final long fieldMask = getFieldMask(screenField, focusfield);
        final boolean rightAdjust = getFieldAdjustment(screenField);

        screenRect.getElement().setRightAdjustment(rightAdjust);
        screenRect.getElement().setFieldType(fieldMask);
        screenRect.getElement().setFieldId(screenField.getFieldId());
        screenRect.getElement().setLength(len);
        screenRect.getElement().setMaxLength(len);
        screenRect.getElement().setValue(value);

        /*
        screenRect.getElement().setFFW1(screenField.getFFW1());
        screenRect.getElement().setFFW2(screenField.getFFW2());

        screenRect.getElement().setFCW1(screenField.getFCW1());
        screenRect.getElement().setFCW2(screenField.getFCW2());
        */

        if (len < screenField.getLength()) {
            processMultiLineField(screenRect, screenField, len, fieldMask);
        }

    }

    /**
     *  process 5250 multiline screen fields to web fields
     */
    private static void processMultiLineField(final Tn5250ScreenData screenRect, final ScreenField screenField, final int len, final long fieldMask) {
        int row = screenRect.getRow();
        final int numCols = screenRect.getNumCols();

        final int alen = (screenField.getLength() - len);
        final int al = alen / (screenRect.getNumCols() - 1);
        int ai, astart, astop = 0;

        for (ai = 1; ai <= al; ai++) {
            row++;

            astart = len + (ai - 1) * (numCols - 1);
            astop = astart + (numCols - 1);
            if (astop > alen) {
                astop = alen;
            }
            String aval = screenField.getString().substring(astart, astop);
            if (screenField.isNumeric() || screenField.isSignedNumeric()) {

            } else {
                aval = rtrim(aval);
            }

            int flen = (numCols - 1) * ai;
            if (flen > screenField.getLength()) {
                flen = screenField.getLength() - flen;
            } else {
                flen = numCols - 1;
            }

            screenRect.initScreenElement();
            Tn5250ScreenElement element = screenRect.getElement();
            //element.setAttributeId(screenField.getAttr());
            element.setFieldType(fieldMask);
            element.setFieldId(screenField.getFieldId() * 1000 + ai);
            element.setLength(flen);
            element.setMaxLength(flen);
            element.setValue(aval);
            element.setRow(row);
        }
    }

    /**
     * is value right adjusted
     */
    private static boolean getFieldAdjustment(final ScreenField screenField) {
    	int  adj = screenField.getAdjustment();
    	return adj == 5 || adj == 6;
    }

    /**
     *  get 5250 field value
     */
    private static String getFieldValue(final ScreenField screenField) {
    	/*
        String value = null;
        if (screenField.isNumeric() || screenField.isSignedNumeric()) {
            value = screenField.getString().trim();
        } else {
            value = rtrim(screenField.getString());
        }
        */
        return screenField.getString();
    }

    /**
     *  convert 5250 field statuses to web field mask
     *  new fields should be added in front of old ones
     */
    private static long getFieldMask(final ScreenField screenField, final int focusfield) {

    	int sts = 0;
        long mask = 0;

        sts = (screenField.isSelectionField() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isRightToLeft() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = ((focusfield == screenField.getFieldId()) ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isAutoEnter() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isBypassField() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isContinued() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isContinuedFirst() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isContinuedLast() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isContinuedMiddle() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isDupEnabled() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isFER() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isHiglightedEntry() ? 1 : 0);;
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isMandatoryEnter() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isNumeric() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isSignedNumeric() ? 1 : 0);
        mask = mask | sts;
        mask = mask << 1;

        sts = (screenField.isToUpper() ? 1 : 0);
        mask = mask | sts;

        return mask;
    }

    /**
     *  find focused field on 5250 screen
     */
    private static int getFocusField(final ScreenFields screenFields) {

        ScreenField focusField = screenFields.getCurrentField();
        if (focusField == null) {
            focusField = screenFields.getFirstInputField();
        }

        int fieldID = -1;
        if (focusField != null) {
            fieldID = focusField.getFieldId();
        }
        return fieldID;

    }
}
