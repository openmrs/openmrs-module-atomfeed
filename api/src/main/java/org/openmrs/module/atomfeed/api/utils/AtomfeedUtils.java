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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public final class AtomfeedUtils {
    
    public static void disableMaxFailedEventCondition(AtomFeedClient atomFeedClient) {
        atomFeedClient.setMaxFailedEvents(Integer.MAX_VALUE);
    }
    
    public static String readResourceFile(String file) throws AtomfeedException {
        try (InputStream in = AtomfeedUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new AtomfeedException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new AtomfeedException(e);
        }
    }
    
    public static List<FeedConfiguration> parseJsonFileToFeedConfiguration(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            FeedConfiguration[] array = mapper.readValue(readResourceFile(resourcePath), FeedConfiguration[].class);
            return Arrays.asList(array);
        } catch (IOException e) {
            throw new AtomfeedException(e);
        }
    }
    
    public static List<FeedConfiguration> parseJsonStringToFeedConfiguration(String value) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            FeedConfiguration[] array = mapper.readValue(value, FeedConfiguration[].class);
            return Arrays.asList(array);
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
            throw new AtomfeedException(e);
        }
        return true;
    }
    
    public static String writeFeedConfigurationToJsonString(Collection<FeedConfiguration> feedConfigurations)
            throws AtomfeedException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            return writer.writeValueAsString(feedConfigurations);
        } catch (IOException e) {
            throw new AtomfeedException(e);
        }
    }
    
    public static void writeFeedConfigurationToJsonFile(List<FeedConfiguration> feedConfigurations, String file)
            throws AtomfeedException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            File resultFile = new File(AtomfeedUtils.class.getClassLoader().getResource("").getPath() + file);
            writer.writeValue(resultFile, feedConfigurations);
        } catch (IOException e) {
            throw new AtomfeedException(e);
        }
    }
    
    public static boolean resourceFileExists(String path) {
        InputStream in = AtomfeedUtils.class.getClassLoader().getResourceAsStream(path);
        return in != null;
    }
    
    public static PlatformTransactionManager getSpringPlatformTransactionManager() {
        return Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
    }
    
    public static AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
        return new AtomFeedSpringTransactionManager(AtomfeedUtils.getSpringPlatformTransactionManager());
    }
}
