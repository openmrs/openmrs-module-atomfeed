package org.openmrs.module.atomfeed.client20.impl;

import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientHelper;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component(value = "atomfeed.ClientHelper2_0")
@OpenmrsProfile(openmrsPlatformVersion = "2.0.0 - 2.*")
public class AtomFeedClientHelperImpl2_0 implements AtomFeedClientHelper {

	@Override
	public AtomFeedClient getAtomFeedClient(EventWorker eventWorker, int connectTimeout, int readTimeout) {
		AtomFeedClient atomFeedClient =  new AtomFeedClientImpl(eventWorker);

		atomFeedClient.setConnectTimeout(connectTimeout);
		atomFeedClient.setReadTimeout(readTimeout);

		return atomFeedClient;
	}

	@Override
	public AFTransactionManager createAtomFeedSpringTransactionManager(PlatformTransactionManager transactionManager) {
		return new AtomFeedSpringTransactionManager(transactionManager);
	}
}
