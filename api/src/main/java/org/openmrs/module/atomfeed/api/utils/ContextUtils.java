package org.openmrs.module.atomfeed.api.utils;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.writers.FeedWriter;
import org.openmrs.module.atomfeed.client.AtomFeedClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContextUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

	public static AtomFeedClientHelper getAtomFeedClientHelper() {
		return getFirstRegisteredComponent(AtomFeedClientHelper.class);
	}

	public static FeedWriter getDefaultFeedWriter() {
		FeedWriter feedWriter = null;
		feedWriter = getRegisteredComponentSafely(AtomfeedConstants.DEFAULT_FEED_WRITER_1_9, FeedWriter.class);
		if (feedWriter == null) {
			feedWriter = getRegisteredComponentSafely(AtomfeedConstants.DEFAULT_FEED_WRITER_2_0, FeedWriter.class);
		}
		if (feedWriter == null) {
			feedWriter = getFirstRegisteredComponent(FeedWriter.class);
		}
		return feedWriter;
	}

	public static <T> T getFirstRegisteredComponent(Class<T> clazz) {
		List<T> list = Context.getRegisteredComponents(clazz);
		if (list.isEmpty()) {
			throw new AtomfeedException(String.format("Not found any instances of '%s' component in the context",
					clazz.getName()));
		}
		return Context.getRegisteredComponents(clazz).get(AtomfeedConstants.ZERO);
	}

	public static <T> T getRegisteredComponentSafely(String beanName, Class<T> clazz) {
		try {
			return Context.getRegisteredComponent(beanName, clazz);
		} catch (APIException ex) {
			LOGGER.debug("Could not fetch '{}' component", ex);
			return null;
		}
	}

	private ContextUtils() { }
}
