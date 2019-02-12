package org.openmrs.module.atomfeed.scheduler.tasks;

import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.utils.ContextUtils;
import org.springframework.transaction.PlatformTransactionManager;

public final class EventUtil {
	
	private EventUtil() {}
	
	public static AFTransactionManager getAtomFeedSpringTransactionManager() {
		return ContextUtils.getAtomFeedClientHelper().createAtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
	}
	
	private static PlatformTransactionManager getSpringPlatformTransactionManager() {
		return 	Context.getRegisteredComponents(PlatformTransactionManager.class).get(0);
	}
}
