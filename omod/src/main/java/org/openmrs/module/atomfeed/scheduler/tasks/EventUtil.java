package org.openmrs.module.atomfeed.scheduler.tasks;

import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public final class EventUtil {
	
	private EventUtil() {}
	
	public static AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
		return new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
	}
	
	private static PlatformTransactionManager getSpringPlatformTransactionManager() {
		return 	Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
	}
}
