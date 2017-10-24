/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.db;

import static org.openmrs.module.atomfeed.api.utils.AtomfeedUtils.readResourceFile;

import java.io.IOException;
import java.util.Arrays;

import org.codehaus.jackson.map.ObjectMapper;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class EventManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
	
	public void serveEvent(OpenmrsObject openmrsObject, EventAction eventAction) {
		LOGGER.info("Called serveEvent method. Parameters: openmrsObject={},"
			+ " eventAction={}", openmrsObject.getClass().getName(), eventAction.name());
		
		FeedConfiguration feedConfiguration = getFeedConfiguration(openmrsObject.getClass().getName());
	}
	
	private FeedConfiguration getFeedConfiguration(String openmrsClass) {
		// TODO: to replaced by FeedConfiguration's manager methods
		ObjectMapper mapper = new ObjectMapper();
		FeedConfiguration[] array;
		try {
			array = mapper.readValue(readResourceFile("defaultFeedConfiguration.json"), FeedConfiguration[].class);
		} catch (IOException e) {
			throw new AtomfeedIoException(e);
		}
		
		for (FeedConfiguration feedConfiguration : Arrays.asList(array)) {
			if (feedConfiguration.getOpenMrsClass().equals(openmrsClass)) {
				return feedConfiguration;
			}
		}
		throw new AtomfeedIoException("Atomfeed configuration for '" + openmrsClass + "' has not been found");
	}
}
