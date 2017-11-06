package org.openmrs.module.atomfeed.scheduler.tasks;

import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsQueue;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsQueueJdbcImpl;
import org.ict4h.atomfeed.server.service.publisher.EventRecordsPublishingService;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.scheduler.tasks.AbstractTask;

public class EventPublisherTask extends AbstractTask {
	
	@Override
	public void execute() {
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager
				= EventUtil.getAtomFeedSpringTransactionManager();

		atomFeedSpringTransactionManager.executeWithTransaction(new PublishTransaction(
			new AllEventRecordsJdbcImpl(atomFeedSpringTransactionManager),
			new AllEventRecordsQueueJdbcImpl(atomFeedSpringTransactionManager)
		));
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
