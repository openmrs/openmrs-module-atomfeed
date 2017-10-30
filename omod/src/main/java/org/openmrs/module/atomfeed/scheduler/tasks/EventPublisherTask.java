package org.openmrs.module.atomfeed.scheduler.tasks;

import java.util.List;

import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.publisher.EventRecordsPublishingService;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.springframework.transaction.PlatformTransactionManager;

public class EventPublisherTask extends AbstractTask {
	
	@Override
	public void execute() {
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager = getAtomFeedSpringTransactionManager();

		atomFeedSpringTransactionManager.executeWithTransaction(new PublishTransaction(
			new AllEventRecordsJdbcImpl(atomFeedSpringTransactionManager),
			new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager)
		));
	}
	
	private AtomFeedSpringTransactionManager getAtomFeedSpringTransactionManager() {
		return new AtomFeedSpringTransactionManager(getSpringPlatformTransactionManager());
	}
	
	private PlatformTransactionManager getSpringPlatformTransactionManager() {
		List<PlatformTransactionManager> platformTransactionManagers = Context.getRegisteredComponents(PlatformTransactionManager.class);
		return platformTransactionManagers.get(0);
	}
	
	private static class PublishTransaction extends AFTransactionWorkWithoutResult {
		
		private AllEventRecords allEventRecords;
		
		private AllEventRecordsQueue allEventRecordsQueue;
		
		PublishTransaction(AllEventRecords allEventRecords, AllEventRecordsQueue allEventRecordsQueue) {
			super();
			this.allEventRecords = allEventRecords;
			this.allEventRecordsQueue = allEventRecordsQueue;
		}
		
		@Override
		protected void doInTransaction() {
			EventRecordsPublishingService.publish(allEventRecords, allEventRecordsQueue);
		}
		
		@Override
		public AFTransactionWork.PropagationDefinition getTxPropagationDefinition() {
			return AFTransactionWork.PropagationDefinition.PROPAGATION_REQUIRED;
		}
	}
}
