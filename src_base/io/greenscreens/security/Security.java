/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.security;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for handling encryption
 * One can use http://travistidwell.com/jsencrypt/demo/ to generate keys.
 * 
 * Must be initialized with ICrypt module.
 * Create class that implements ICrypt and implement encryption / decryption code.
 * 
 */
public enum Security {
	;

	private static final Logger LOGGER = LoggerFactory.getLogger(Security.class);
	
	// encryption engine used AES / RSA
	private static ICrypt crypt;
	
	// timeout value from config file
	private static long time;  
	
	/**
	 * Get password timeout value in seconds 	
	 * @return
	 */
	public static long getTime() {
		return time;
	}
	
	/**
	 * Get password timeout value in miliseconds
	 * @return
	 */
	public static long getTimeMilis() {
		return time * 1000;
	}

	/**
	 * Set password timeout value in seconds
	 * @param time
	 */
	public static void setTime(long time) {
		Security.time = time;
	}

	/**
	 * Set encrypt / decrypt module
	 * @param crypt
	 */
	public static void setCrypt(ICrypt crypt) {
		Security.crypt = crypt;
	}

	/**
	 * Encrypt data with registered ICrypt module
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data) throws Exception {
		return crypt.decrypt(data);
	}
	
	/**
	 * Decrypt data with registered ICrypt module
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data) throws Exception {
		return crypt.encrypt(data);
	}	

	/**
	 * Calculate difference between current time and given timestamp
	 * Timestamp can be in UNIX format (PHP) or Java
	 *   
	 * @param time
	 * @return
	 */
	public static final long timediff(final String time) {
		long stime = System.currentTimeMillis();
		long ptime = Long.parseLong(time.trim());
		
		if (ptime < 0 ) return 0;
		
		if (time.length()== 10) {
			stime = stime / 1000;
		}
		
		long dtime =  Math.abs(stime - ptime);
		return dtime;
	}
	
    /**
     * Password can be in plain text or escaped / base64 / rsa encrypted / aes encrypted
     * Password can be combined with timestamp separated by new line 
     * @param encryptedPassword
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static final String decode(final String user, final String encryptedPassword) throws Exception {

    	if (encryptedPassword == null) return null;
    	if (encryptedPassword.length() <= 10 ) return encryptedPassword;

    	String encpwd = Security.decrypt(encryptedPassword);
    	//long stime = System.currentTimeMillis();

    	// format = pwd, user, timestamp
    	String [] params = encpwd.split("\n");

    	// check for password timestamp  
    	if (params.length == 3) {    		
    		if (!params[1].equals(user)) {
    			LOGGER.error(">>Invalid credentials<<");
    			LOGGER.error("> decripted: {}", params[1]);
    			LOGGER.error("> requested: {}", user);    			
    			throw new Exception("Invalid credentials.");
    		}
    		String pwd = params[2].trim();
    		long ctime = Security.getTime();    		
    		long dtime = timediff(pwd);
    		if (pwd.length()>10) {
    			ctime = Security.getTimeMilis();
    		}
    		
    		// is difference GT sec, do not login
    		if (dtime < 0 || dtime > ctime) {
    			LOGGER.error(">>Password timeout<<");    			
    			LOGGER.error("> USER: {}", user);
    			LOGGER.error("> HASH : {}", encryptedPassword);
    			LOGGER.error("> Server time : {}", ctime);
    			LOGGER.error("> Difference  : {}", dtime);
                throw new Exception("Password timeout.");		    		                
    		}
    		encpwd = params[0];
    		LOGGER.info(">>Success hash decryption for user : {}, hash: {}", user, encryptedPassword);
    	} else {
    		LOGGER.error(">>Invalid hash<<");
			LOGGER.error("> USER: {}", user);
			LOGGER.error("> HASH : {}", encryptedPassword);
    		throw new Exception("Invalid hash!");
    	}
    	return encpwd;
    }		
}
