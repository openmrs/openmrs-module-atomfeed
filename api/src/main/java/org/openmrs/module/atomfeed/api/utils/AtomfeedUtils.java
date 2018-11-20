/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.GeneralConfiguration;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.util.OpenmrsClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public final class AtomfeedUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtomfeedUtils.class);

    public static void disableMaxFailedEventCondition(AtomFeedClient atomFeedClient) {
        atomFeedClient.setMaxFailedEvents(Integer.MAX_VALUE);
    }
    
    public static String readResourceFile(String file) throws AtomfeedException {
        try (InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(file)) {
            if (in == null) {
                throw new AtomfeedException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new AtomfeedException(e);
        }
    }
    
    public static GeneralConfiguration parseJsonFileToFeedConfiguration(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GeneralConfiguration generalConfiguration = mapper.readValue(readResourceFile(resourcePath), GeneralConfiguration.class);
            return generalConfiguration;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new AtomfeedException(e);
        }
    }
    
    public static GeneralConfiguration parseJsonStringToFeedConfiguration(String value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GeneralConfiguration generalConfiguration = mapper.readValue(value, GeneralConfiguration.class);
            return generalConfiguration;
        } catch (IOException e) {
            throw new AtomfeedException(e);
        }
    }
    
    public static boolean isValidateJson(String json) throws AtomfeedException {
        try {
            final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
            while (parser.nextToken() != null) {
            }
        } catch (JsonParseException jpe) {
            return false;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new AtomfeedException(e);
        }
        return true;
    }
    
    public static String writeFeedConfigurationToJsonString(GeneralConfiguration generalConfiguration)
            throws AtomfeedException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            return writer.writeValueAsString(generalConfiguration);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new AtomfeedException(e);
        }
    }
    
    public static void writeFeedConfigurationToJsonFile(GeneralConfiguration generalConfiguration, String file)
            throws AtomfeedException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            File resultFile = new File(AtomfeedUtils.class.getClassLoader().getResource("").getPath() + file);
            writer.writeValue(resultFile, generalConfiguration);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new AtomfeedException(e);
        }
    }
    
    public static boolean resourceFileExists(String path) {
        InputStream in = OpenmrsClassLoader.getInstance().getResourceAsStream(path);
        return in != null;
    }
    
    public static PlatformTransactionManager getSpringPlatformTransactionManager() {
        return Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
    }
    
    public static AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
        return new AtomFeedSpringTransactionManager(AtomfeedUtils.getSpringPlatformTransactionManager());
    }
}
