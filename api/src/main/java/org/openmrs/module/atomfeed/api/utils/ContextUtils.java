package org.openmrs.module.atomfeed.api.utils;

import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.client.AtomFeedClientHelper;

import java.util.List;

public class ContextUtils {

	public static AtomFeedClientHelper getAtomFeedClientHelper() {
		return getFirstRegisteredComponent(AtomFeedClientHelper.class);
	}

	public static <T> T getFirstRegisteredComponent(Class<T> clazz) {
		List<T> list = Context.getRegisteredComponents(clazz);
		if (list.isEmpty()) {
			throw new AtomfeedException(String.format("Not found any instances of '%s' component in the context",
					clazz.getName()));
		}
		return Context.getRegisteredComponents(clazz).get(AtomfeedConstants.ZERO);
	}

	private ContextUtils() { }
}
