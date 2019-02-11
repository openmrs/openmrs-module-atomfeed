package org.openmrs.module.atomfeed.client;

import org.ict4h.atomfeed.client.service.EventWorker;

public interface AtomFeedClientHelper {

	AtomFeedClient getAtomFeedClient(EventWorker eventWorker, int connectTimeout, int readTimeout);
}
