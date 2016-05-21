/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.greenscreens.security.AesCrypt;
import io.greenscreens.security.Security;

/**
 * Load properties file with list of all available 5250 server connections and
 * WebSocket setup properties. Initialized from WebSocketLoader class. Self
 * register to ServletContext with Constants.HOST_5250_CONFIG.
 */
public enum TnConfigLoader {
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(TnConfigLoader.class);

    // suffix for name of properties file
    private static final String PROP_ID = "io.greenscreens.properties";

    private static final String prefix;

    private static TnConfiguration configuration;

    static {
        prefix = System.getProperty("user.home").concat(System.getProperty("file.separator"));
        reload();
    }

    /**
     * Return loaded configuration
     * @return Properties loaded from text file
     */
    public static final TnConfiguration getConfiguration() {
    	return configuration;
    }

	/**
     * Reload configuration from file
     * @return
     * @throws RuntimeException
     */
    public static final Map<String, TnHost> reload() throws RuntimeException {

    	if (configuration!=null) {
    		configuration.clear();
    	}
        configuration = loadConfig();

        final String shared_pwd = configuration.getSharedPassword();
        if (shared_pwd != null) {
        	AesCrypt.setSecretKey(shared_pwd);
        }

        final String shared_iv = configuration.getSharedIV();
        if (shared_iv != null) {
        	AesCrypt.setIv(shared_iv);
        }

        Security.setTime(configuration.getSharedTime());
       	Security.setCrypt(new AesCrypt());

        return configuration.getHosts();
    }

    /**
     * Load servers configuration from user home folder
     * @return
     */
    private static TnConfiguration loadConfig() {

        final File file = new File(prefix + PROP_ID);

        LOGGER.info("CONFIG LOADED FROM >>> :" + file.getAbsolutePath());

        final TnConfiguration prop = new TnConfiguration();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            prop.load(fis);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(fis);
        }
        return prop;
    }

    /**
     * Close stream
     * @param closeable
     */
    private static void close(InputStream closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
