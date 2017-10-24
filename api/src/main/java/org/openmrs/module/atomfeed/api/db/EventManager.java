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
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.ict4h.atomfeed.server.service.Event;

import org.joda.time.DateTime;
import org.openmrs.OpenmrsObject;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class EventManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
	
	private static final String UUID_PATTERN = "{uuid}";
	
	public void serveEvent(OpenmrsObject openmrsObject, EventAction eventAction) {
		LOGGER.info("Called serveEvent method. Parameters: openmrsObject={},"
			+ " eventAction={}", openmrsObject.getClass().getName(), eventAction.name());
		
		FeedConfiguration feedConfiguration = getFeedConfiguration(openmrsObject.getClass().getName());
		
		final Event event = new Event(
			UUID.randomUUID().toString(),
			feedConfiguration.getTitle(),
			DateTime.now(),
			(URI) null,
			getEventObject(openmrsObject, feedConfiguration),
			eventAction.name()
		);
		debugEvent(event);
	}
	
	private String getEventObject(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
		final String urlTemplate = getPreferredTemplate(feedConfiguration);
		String uuid = openmrsObject.getUuid();
		return urlTemplate.replace(UUID_PATTERN, uuid);
	}
	
	private String getPreferredTemplate(FeedConfiguration feedConfiguration) {
		String endpoint;
		final String fhir = "fhir";
		final String rest = "rest";
		if (feedConfiguration.getLinkTemplates().containsKey(fhir)) {
			endpoint = feedConfiguration.getLinkTemplates().get(fhir);
		} else if (feedConfiguration.getLinkTemplates().containsKey(rest)) {
			endpoint = feedConfiguration.getLinkTemplates().get(rest);
		} else {
			throw new AtomfeedIoException("Not exists appropriate object endpoint template");
		}
		return endpoint;
	}
	
	private FeedConfiguration getFeedConfiguration(String openmrsClass) {
		// TODO: to replaced by FeedConfiguration's manager methods
		ObjectMapper mapper = new ObjectMapper();
		FeedConfiguration[] array = null;
		try {
			mapper.readValue(readResourceFile("defaultFeedConfiguration.json"), FeedConfiguration[].class);
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
	
	private void debugEvent(Event event) {
		final ObjectMapper objectMapper = new ObjectMapper();
		if (LOGGER.isDebugEnabled()) {
			try {
				String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
				LOGGER.debug("{} AtomFeed event created, event body:\n {}", event.getUuid(), json);
			} catch (IOException e) {
				throw new AtomfeedIoException(e);
			}
		} else {
			LOGGER.info("Created AtomFeed event with {} UUID", event.getUuid());
		}
	}
}
