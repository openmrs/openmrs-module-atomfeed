package org.openmrs.module.atomfeed.scheduler.tasks;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.service.NumberOffsetMarkerServiceImpl;
import org.ict4h.atomfeed.server.service.OffsetMarkerService;
import org.ict4h.atomfeed.transaction.AFTransactionManager;
import org.ict4h.atomfeed.transaction.AFTransactionWork;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.openmrs.scheduler.tasks.AbstractTask;

public class EventRecordsNumberOffsetMarkerTask extends AbstractTask {
	
	final private static int OFFSET_BY_NUMBER_OF_RECORDS_PER_CATEGORY = 1000;
	
	@Override
	public void execute() {
		AFTransactionManager atomFeedSpringTransactionManager =
				EventUtil.getAtomFeedSpringTransactionManager();
		
		atomFeedSpringTransactionManager.executeWithTransaction(new NumberOffsetMarkerTaskTransaction(
			new AllEventRecordsJdbcImpl((JdbcConnectionProvider) atomFeedSpringTransactionManager),
			new AllEventRecordsOffsetMarkersJdbcImpl((JdbcConnectionProvider) atomFeedSpringTransactionManager),
			new ChunkingEntriesJdbcImpl((JdbcConnectionProvider) atomFeedSpringTransactionManager)
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
