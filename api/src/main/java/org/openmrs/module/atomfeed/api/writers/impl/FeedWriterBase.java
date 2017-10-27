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
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventService;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;

import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.writers.FeedWriter;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class FeedWriterBase implements FeedWriter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedWriterBase.class);
	
	private AtomFeedSpringTransactionManager atomFeedSpringTransactionManager;
	
	private EventService eventService;
	
	public FeedWriterBase() {
		atomFeedSpringTransactionManager = new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
		AllEventRecordsQueue allEventRecordsQueue = new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager);
		
		this.eventService = new EventServiceImpl(allEventRecordsQueue);
	}
	
	protected void saveEvent(Event event) {
		atomFeedSpringTransactionManager.executeWithTransaction(
			new AFTransactionWorkWithoutResult() {
				@Override
				protected void doInTransaction() {
					eventService.notify(event);
				}
				
				@Override
				public PropagationDefinition getTxPropagationDefinition() {
					return PropagationDefinition.PROPAGATION_REQUIRED;
				}
			}
		);
	}
	
	private PlatformTransactionManager getSpringPlatformTransactionManager() {
		List<PlatformTransactionManager> platformTransactionManagers =
				Context.getRegisteredComponents(PlatformTransactionManager.class);
		return platformTransactionManagers.get(0);
	}
	
	protected void debugEvent(Event event) {
		final ObjectMapper objectMapper = new ObjectMapper();
		if (LOGGER.isDebugEnabled()) {
			try {
				String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(event);
				LOGGER.debug("{} AtomFeed event created, event body:\n {}", event.getUuid(), json);
			} catch (IOException e) {
				throw new AtomfeedException(e);
			}
		} else {
			LOGGER.info("Created AtomFeed event with {} UUID", event.getUuid());
		}
	}
}
