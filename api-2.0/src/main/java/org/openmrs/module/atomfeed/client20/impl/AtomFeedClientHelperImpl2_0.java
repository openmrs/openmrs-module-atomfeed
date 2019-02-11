package org.openmrs.module.atomfeed.client20.impl;

import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientHelper;
import org.springframework.stereotype.Component;

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
}
