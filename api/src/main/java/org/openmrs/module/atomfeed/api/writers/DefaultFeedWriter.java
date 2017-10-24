/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.writers;

import java.net.URI;
import java.util.UUID;

import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.springframework.stereotype.Component;

@Component("atomfeed.DefaultFeedWriter")
public class DefaultFeedWriter extends FeedWriterBase {
	
	private static final String UUID_PATTERN = "{uuid}";
	
	@Override
	public void writeFeed(OpenmrsObject openmrsObject, EventAction eventAction, FeedConfiguration feedConfiguration) {
		final Event event = new Event(
				UUID.randomUUID().toString(),
				feedConfiguration.getTitle(),
				DateTime.now(),
				(URI) null,
				getEventContent(openmrsObject, feedConfiguration),
				eventAction.name()
		);
		debugEvent(event);
		saveEvent(event);
	}
	
	private String getEventContent(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
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
}
