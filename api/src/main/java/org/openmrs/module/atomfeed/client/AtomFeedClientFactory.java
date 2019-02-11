package org.openmrs.module.atomfeed.client;

import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.module.atomfeed.api.utils.ContextUtils;

public class AtomFeedClientFactory {

    private final static int SECOND = 1000;
    private final static int CONNECT_TIMEOUT = 15 * SECOND;
    private final static int READ_TIMEOUT = 30 * SECOND;

    private AtomFeedClientFactory() { }

    public static AtomFeedClient createClient(EventWorker eventWorker) {
        AtomFeedClientHelper atomFeedClientHelper = ContextUtils.getAtomFeedClientHelper();
        AtomFeedClient atomFeedClient = atomFeedClientHelper.getAtomFeedClient(eventWorker, CONNECT_TIMEOUT, READ_TIMEOUT);
        return atomFeedClient;
    }

}
