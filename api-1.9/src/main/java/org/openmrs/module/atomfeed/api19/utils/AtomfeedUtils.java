package org.openmrs.module.atomfeed.api19.utils;

import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;

public class AtomfeedUtils {

	public static AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
		return new AtomFeedSpringTransactionManager(org.openmrs.module.atomfeed.api.utils.AtomfeedUtils.getSpringPlatformTransactionManager());
	}

	private AtomfeedUtils() { }
}
