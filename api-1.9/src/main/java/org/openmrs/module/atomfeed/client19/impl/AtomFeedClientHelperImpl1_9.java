package org.openmrs.module.atomfeed.client19.impl;

import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.client.AtomFeedClientHelper;
import org.springframework.stereotype.Component;

@Component(value = "atomfeed.ClientHelper1_9")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.* - 2.0.0")
public class AtomFeedClientHelperImpl1_9 implements AtomFeedClientHelper {

	@Override
	public AtomFeedClient getAtomFeedClient(EventWorker eventWorker, int connectTimeout, int readTimeout) {
		AtomFeedClient atomFeedClient =  new AtomFeedClientImpl(eventWorker);

		atomFeedClient.setConnectTimeout(connectTimeout);
		atomFeedClient.setReadTimeout(readTimeout);

		return atomFeedClient;
	}
}
