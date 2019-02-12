package org.openmrs.module.atomfeed.client;

import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public interface AtomFeedClientHelper {

	AtomFeedClient getAtomFeedClient(EventWorker eventWorker, int connectTimeout, int readTimeout);

	AFTransactionManager createAtomFeedSpringTransactionManager(PlatformTransactionManager transactionManager);
}
