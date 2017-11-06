package org.openmrs.module.atomfeed.scheduler.tasks;

import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.service.NumberOffsetMarkerServiceImpl;
import org.ict4h.atomfeed.server.service.OffsetMarkerService;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.scheduler.tasks.AbstractTask;

public class EventRecordsNumberOffsetMarkerTask extends AbstractTask {
	
	final private static int OFFSET_BY_NUMBER_OF_RECORDS_PER_CATEGORY = 1000;
	
	@Override
	public void execute() {
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager =
				EventUtil.getAtomFeedSpringTransactionManager();
		
		atomFeedSpringTransactionManager.executeWithTransaction(new NumberOffsetMarkerTaskTransaction(
			new AllEventRecordsJdbcImpl(atomFeedSpringTransactionManager),
			new AllEventRecordsOffsetMarkersJdbcImpl(atomFeedSpringTransactionManager),
			new ChunkingEntriesJdbcImpl(atomFeedSpringTransactionManager)
		));
	}
	
	private static class NumberOffsetMarkerTaskTransaction extends AFTransactionWorkWithoutResult {
		
		private AllEventRecords allEventRecords;
		
		private AllEventRecordsOffsetMarkers allEventRecordsOffsetMarkers;
		
		private ChunkingEntries chunkingEntries;
		
		private NumberOffsetMarkerTaskTransaction(AllEventRecords allEventRecords,
				AllEventRecordsOffsetMarkers allEventRecordsOffsetMarkers, ChunkingEntries chunkingEntries) {
			super();
			this.allEventRecords = allEventRecords;
			this.allEventRecordsOffsetMarkers = allEventRecordsOffsetMarkers;
			this.chunkingEntries = chunkingEntries;
		}
		
		@Override
		protected void doInTransaction() {
			OffsetMarkerService markerService =	new NumberOffsetMarkerServiceImpl(
					allEventRecords, chunkingEntries, allEventRecordsOffsetMarkers);
			markerService.markEvents(OFFSET_BY_NUMBER_OF_RECORDS_PER_CATEGORY);
		}
		
		@Override
		public AFTransactionWork.PropagationDefinition getTxPropagationDefinition() {
			return AFTransactionWork.PropagationDefinition.PROPAGATION_REQUIRED;
		}
	}
}
