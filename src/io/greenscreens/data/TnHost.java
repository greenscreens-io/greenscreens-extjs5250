/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.data;

import java.net.InetSocketAddress;
import javax.enterprise.inject.Vetoed;

/**
 * Used by Property loader to prepare
 * list of all available host configurations.
 */
@Vetoed
public final class TnHost {

	private String ipAddress;
	private int port;
	private String name;
	private boolean bypassMsg = false;
	private String codePage = "Cp870";
	private String displayPrefix;
	private String closeMsg;

	public InetSocketAddress getLocation() {
		InetSocketAddress inetAddress = new InetSocketAddress(ipAddress, port);
		if (inetAddress.isUnresolved()) {
			inetAddress = null;
		}
		return inetAddress;
	}

	public boolean isValid() {
		return (name != null) && (getLocation() != null);
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPortNumber() {
		return port;
	}

	public String getPort() {
		return Integer.toString(port);
	}

	public void setPort(final String port) {
		this.port = Integer.parseInt(port);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getCodePage() {
		return codePage;
	}

	public void setCodePage(final String codePage) {
		if (codePage == null) {
			return;
		}
		this.codePage = codePage;
	}

	private boolean checkBoolean(String value) {

		if (value == null) {
			return false;
		}

		if (Boolean.TRUE.toString().equals(value.toLowerCase())) {
			return true;
		}

		if (Boolean.FALSE.toString().equals(value.toLowerCase())) {
			return false;
		}

		return false;
	}

	public boolean isBypassMsg() {
		return bypassMsg;
	}

	public void setBypassMsg(String bypassMsg) {
		this.bypassMsg = checkBoolean(bypassMsg);
	}

	public void setDisplayPrefix(String displayPrefix) {
		this.displayPrefix = displayPrefix;
	}

	public String getDisplayPrefix() {
		return displayPrefix;
	}

	public void setCloseMessage(String closeMsg) {
		this.closeMsg = closeMsg;
	}

	public String getCloseMsg() {
		return closeMsg;
	}

}
