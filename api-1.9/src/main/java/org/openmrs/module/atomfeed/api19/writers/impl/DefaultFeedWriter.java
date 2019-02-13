/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api19.writers.impl;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.ict4h.atomfeed.server.service.Event;
import org.joda.time.DateTime;
import org.openmrs.OpenmrsObject;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.converter.FeedBuilder;
import org.openmrs.module.atomfeed.api.db.EventAction;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.filter.GenericFeedFilterStrategy;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.utils.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component(AtomfeedConstants.DEFAULT_FEED_WRITER_1_9)
@OpenmrsProfile(openmrsPlatformVersion = "1.9.* - 2.0.0")
public class DefaultFeedWriter extends FeedWriterBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFeedWriter.class);

	@Autowired
	private FeedConfigurationService feedConfigurationService;

	@Override
	public void writeFeed(OpenmrsObject openmrsObject, EventAction eventAction, FeedConfiguration feedConfiguration) {
		if (!feedConfiguration.isEnabled()) {
			LOGGER.debug("Skipped writing '{}' to AtomFeed because "
							+ "the synchronization for this object is disabled in the configuration",
					openmrsObject.getClass().getName());
			return;
		}

		StringBuilder tags = new StringBuilder(eventAction.name());

		for (String beanName : feedConfigurationService.getFeedFilterBeans()) {
			GenericFeedFilterStrategy feedFilter = Context.getRegisteredComponent(beanName, GenericFeedFilterStrategy.class);
			String tag = StringUtils.normalizeSpace(feedFilter.createFilterFeed(openmrsObject));
			if (tag != null && !tag.isEmpty()) {
				tags.append(",").append(tag);
			}
		}

		final Event event = new Event(
				UUID.randomUUID().toString(),
				feedConfiguration.getTitle(),
				DateTime.now(),
				null,
				getEventContent(openmrsObject, feedConfiguration),
				feedConfiguration.getCategory(),
				tags.toString()
		);
		debugEvent(event);
		saveEvent(event);
		LOGGER.info("A feed for {} has been saved in AtomFeed", openmrsObject.getClass().getName());
	}

	private String getEventContent(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
		FeedBuilder builder = ContextUtils.getFeedBuilder(openmrsObject);
		Map<String, String> links = builder.getLinks(openmrsObject, feedConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(links);
		} catch (IOException e) {
			throw new AtomfeedException("There is a problem with serialize resource links to AtomFeed content");
		}
	}
}
