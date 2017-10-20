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
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

public final class AtomfeedUtils {
	
	public static String readResourceFile(String file) throws AtomfeedIoException {
        try (InputStream in = AtomfeedUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new AtomfeedIoException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new AtomfeedIoException(e);
        }
    }
	
	public static FeedConfiguration[] parseFileToJsonConfigurationResource(String resourcePath) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(readResourceFile(resourcePath), FeedConfiguration[].class);
		}
		catch (IOException e) {
			throw new AtomfeedIoException(e);
		}
	}
	
	public static FeedConfiguration[] parseStringToJsonConfigurationResource(String value) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(value, FeedConfiguration[].class);
		}
		catch (IOException e) {
			throw new AtomfeedIoException(e);
		}
	}
	
	public static boolean isValidateJson(String json) throws AtomfeedIoException {
		try {
			final JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(json);
			while (parser.nextToken() != null) {}
		}
		catch (JsonParseException jpe) {
			return false;
		}
		catch (IOException e) {
			throw new AtomfeedIoException(e);
		}
		return true;
	}
	
	public static void parseJsonConfigurationResourceToFile(FeedConfiguration[] feedConfigurations, String file)
	        throws AtomfeedIoException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			File resultFile = new File(AtomfeedUtils.class.getClassLoader().getResource("").getPath() + file);
			writer.writeValue(resultFile, feedConfigurations);
		}
		catch (IOException e) {
			throw new AtomfeedIoException(e);
		}
	}
}
