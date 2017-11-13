package org.openmrs.module.atomfeed.web.controller;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.EventFeedServiceImpl;
import org.ict4h.atomfeed.server.service.feedgenerator.FeedGeneratorFactory;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.ict4h.atomfeed.server.service.helper.ResourceHelper;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.atomfeed.utils.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/atomfeed")
public class AtomfeedController {
	private static Logger logger = Logger.getLogger(AtomfeedController.class);
	private AtomFeedSpringTransactionManager atomTxManager;
	private EventFeedService eventFeedService;

	@Autowired
	public AtomfeedController(PlatformTransactionManager transactionManager) {
		atomTxManager = new AtomFeedSpringTransactionManager(transactionManager);
		this.eventFeedService = new EventFeedServiceImpl(new FeedGeneratorFactory().getFeedGenerator(
				new AllEventRecordsJdbcImpl(atomTxManager),
				new AllEventRecordsOffsetMarkersJdbcImpl(atomTxManager),
				new ChunkingEntriesJdbcImpl(atomTxManager),
				new ResourceHelper()));
	}

	public AtomfeedController(EventFeedService eventFeedService) {
		this.eventFeedService = eventFeedService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{category}/recent")
	@ResponseBody
	public String getRecentEventFeedForCategory(HttpServletRequest httpServletRequest, @PathVariable String category) {
		return EventFeedServiceHelper.getRecentFeed(eventFeedService, new UrlUtil().getRequestURL(httpServletRequest),
				category, logger, atomTxManager);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{category}/{n}")
	@ResponseBody
	public String getEventFeedWithCategory(HttpServletRequest httpServletRequest,
						@PathVariable String category, @PathVariable int n) {
		return EventFeedServiceHelper.getEventFeed(eventFeedService, new UrlUtil().getRequestURL(httpServletRequest),
				category, n, logger, atomTxManager);
	}
}
