/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.writers.impl;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("atomfeed.DefaultFeedWriter")
public class DefaultFeedWriter extends FeedWriterBase {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeedWriter.class);
	
	private static final String UUID_PATTERN = "{uuid}";
	
	@Override
	public void writeFeed(OpenmrsObject openmrsObject, EventAction eventAction, FeedConfiguration feedConfiguration) {
		if (!feedConfiguration.isEnabled()) {
			LOGGER.debug("Skipped writing '{}' to AtomFeed because "
					+ "the synchronization for this object is disabled in the configuration",
				openmrsObject.getClass().getName());
			return;
		}
		
		final Event event = new Event(
				UUID.randomUUID().toString(),
				feedConfiguration.getTitle(),
				DateTime.now(),
				(URI) null,
				getEventContent(openmrsObject, feedConfiguration),
				feedConfiguration.getCategory(),
				eventAction.name()
		);
		debugEvent(event);
		saveEvent(event);
		LOGGER.info("A feed for {} has been saved in AtomFeed", openmrsObject.getClass().getName());
	}
	
	private String getEventContent(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
		Map<String, String> links = getLinks(openmrsObject, feedConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(links);
		} catch (IOException e) {
			throw new AtomfeedException("There is a problem with serialize resource links to AtomFeed content");
		}
	}

	private Map<String, String> getLinks(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
		String uuid = openmrsObject.getUuid();
		Map<String, String> urls = new HashMap<>();
		feedConfiguration.getLinkTemplates().forEach((key, value) -> urls.put(key, value.replace(UUID_PATTERN, uuid)));
		return urls;
	}
}
