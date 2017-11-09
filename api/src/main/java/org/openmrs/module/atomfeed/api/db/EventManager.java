/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.db;

import static org.openmrs.module.atomfeed.AtomfeedConstants.DEFAULT_FEED_WRITER;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.writers.FeedWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
	
	@Autowired
	FeedConfigurationService feedConfigurationService;
	
	public void serveEvent(OpenmrsObject openmrsObject, EventAction eventAction) {
		LOGGER.info("Called serveEvent method. Parameters: openmrsObject={}, eventAction={}",
				openmrsObject.getClass().getName(),
				eventAction.name()
		);
		FeedConfiguration feedConfiguration =
				feedConfigurationService.getFeedConfigurationByOpenMrsClass(openmrsObject.getClass().getName());
		
		if (feedConfiguration != null) {
			getFeedWriter(feedConfiguration).writeFeed(openmrsObject, eventAction, feedConfiguration);
		} else {
			LOGGER.debug("Skipped serving hibernate operation on '{}' because "
					+ "object AtomFeed configuration has not been found", openmrsObject.getClass().getName());
		}
	}
	
	private FeedWriter getFeedWriter(FeedConfiguration feedConfiguration) {
		String feedWriterBeanId;
		if (StringUtils.isBlank(feedConfiguration.getFeedWriter())) {
			feedWriterBeanId = DEFAULT_FEED_WRITER;
		} else {
			feedWriterBeanId = feedConfiguration.getFeedWriter();
		}
		
		try {
			return Context.getRegisteredComponent(feedWriterBeanId, FeedWriter.class);
		} catch (Exception e) {
			throw new AtomfeedException("`" + feedWriterBeanId + "` bean for FeedWriter has not been found", e);
		}
	}
}
