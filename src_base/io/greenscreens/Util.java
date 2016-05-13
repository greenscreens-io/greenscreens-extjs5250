/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens;

/**
 * Simple util class for string handling
 *
 */
public enum Util {
	;

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	/**
	 * Converts byte array to hex string
	 * @param  bytes [description]
	 * @return       [description]
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	/**
	 * Converts hex string into byte array
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

}
