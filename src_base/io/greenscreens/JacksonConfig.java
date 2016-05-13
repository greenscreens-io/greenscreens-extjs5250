/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */
package io.greenscreens;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Class for preparing custom JSON serializer / deserializer to Java CDI 
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    final private ObjectMapper apiMapper;

    public JacksonConfig() {
        super();
        apiMapper = JsonDecoder.getJSONEngine();
        apiMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public final ObjectMapper getContext(final Class<?> type) {
        return apiMapper;
    }

}
