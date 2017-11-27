package org.openmrs.module.atomfeed.client.impl;

import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedWorker implements EventWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedWorker.class);

    @Override
    public void process(Event event) {
        LOGGER.info("Started feed event processing (id: {})", event.getId());
    }

    @Override
    public void cleanUp(Event event) {
        LOGGER.info("Started feed cleanUp processing (id: {})", event.getId());
    }
}