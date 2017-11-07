/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.writers.impl;

import java.net.URI;
import java.util.UUID;

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
		final String urlTemplate = getPreferredTemplate(feedConfiguration);
		String uuid = openmrsObject.getUuid();
		return urlTemplate.replace(UUID_PATTERN, uuid);
	}
	
	private String getPreferredTemplate(FeedConfiguration feedConfiguration) {
		// TODO: to change to more generic version
		String endpoint;
		final String fhir = "fhir";
		final String rest = "rest";
		if (feedConfiguration.getLinkTemplates().containsKey(fhir)) {
			endpoint = feedConfiguration.getLinkTemplates().get(fhir);
		} else if (feedConfiguration.getLinkTemplates().containsKey(rest)) {
			endpoint = feedConfiguration.getLinkTemplates().get(rest);
		} else {
			throw new AtomfeedException("Not exists appropriate object endpoint template");
		}
		return endpoint;
	}
}
