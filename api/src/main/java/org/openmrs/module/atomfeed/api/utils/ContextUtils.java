package org.openmrs.module.atomfeed.api.utils;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.converter.FeedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextUtils.class);

	public static <T> T getRegisteredComponentSafely(String beanName, Class<T> clazz) {
		try {
			return Context.getRegisteredComponent(beanName, clazz);
		} catch (APIException ex) {
			LOGGER.debug("Could not fetch '{}' component", ex);
			return null;
		}
	}

	public static FeedBuilder getFeedBuilder(OpenmrsObject openmrsObject) {
		FeedBuilder builder = getCustomFeedBuilder(openmrsObject);
		if (builder == null) {
			builder = getRegisteredComponentSafely(AtomfeedConstants.DEFAULT_FEED_BUILDER, FeedBuilder.class);
		}
		return builder;
	}

	private static FeedBuilder getCustomFeedBuilder(OpenmrsObject openmrsObject) {
		String feedBuilderBeanId = AtomfeedConstants.FEED_BUILDER_BEAN_ID_PREFIX +
				openmrsObject.getClass().getSimpleName() + AtomfeedConstants.FEED_BUILDER_BEAN_ID_SUFIX;

		return getRegisteredComponentSafely(feedBuilderBeanId, FeedBuilder.class);
	}
}
