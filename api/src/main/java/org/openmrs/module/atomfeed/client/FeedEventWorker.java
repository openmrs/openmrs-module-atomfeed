package org.openmrs.module.atomfeed.client;

import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

public interface FeedEventWorker extends EventWorker {

    void process(Event event);

    void cleanUp(Event event);
}
