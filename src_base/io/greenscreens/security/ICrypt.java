/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.security;

/**
 * Interface for encryption module used by Security class
 */
public interface ICrypt {

	/**
	 * Decrypt from hex encoded string to string 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	String decrypt(String data) throws Exception;
	
	/**
	 * Encrypt string to hex encoded string
	 * @param data
	 * @return
	 * @throws Exception
	 */
	String encrypt(String data) throws Exception;

}
