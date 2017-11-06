package org.openmrs.module.atomfeed.scheduler.tasks;

import net.sf.ehcache.util.FindBugsSuppressWarnings;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.service.NumberOffsetMarkerServiceImpl;
import org.ict4h.atomfeed.server.service.OffsetMarkerService;
import org.ict4h.atomfeed.transaction.AFTransactionWorkWithoutResult;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

public class EventRecordsNumberOffsetMarkerTask extends AbstractTask {
	
	final private static int OFFSET_BY_NUMBER_OF_RECORDS_PER_CATEGORY = 1000;
	
	@Autowired
	private PlatformTransactionManager springPlatformTransactionManager;
	
	@FindBugsSuppressWarnings("")
	@Override
	public void execute() {
		AtomFeedSpringTransactionManager atomFeedSpringTransactionManager =
				new AtomFeedSpringTransactionManager(springPlatformTransactionManager);
		AllEventRecords allEventRecords =
				new AllEventRecordsJdbcImpl(atomFeedSpringTransactionManager);
		AllEventRecordsOffsetMarkers eventRecordsOffsetMarkers =
				new AllEventRecordsOffsetMarkersJdbcImpl(atomFeedSpringTransactionManager);
		ChunkingEntries chunkingEntries =
				new ChunkingEntriesJdbcImpl(atomFeedSpringTransactionManager);
		
		atomFeedSpringTransactionManager.executeWithTransaction(new AFTransactionWorkWithoutResult() {
			@Override
			protected void doInTransaction() {
				OffsetMarkerService markerService =
						new NumberOffsetMarkerServiceImpl(allEventRecords, chunkingEntries, eventRecordsOffsetMarkers);
				markerService.markEvents(OFFSET_BY_NUMBER_OF_RECORDS_PER_CATEGORY);
			}
			
			@Override
			public PropagationDefinition getTxPropagationDefinition() {
				return PropagationDefinition.PROPAGATION_REQUIRED;
			}
		});
	}
	

}
