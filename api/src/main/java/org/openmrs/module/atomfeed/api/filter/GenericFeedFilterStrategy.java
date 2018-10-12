package org.openmrs.module.atomfeed.api.filter;

import org.openmrs.OpenmrsObject;

public interface GenericFeedFilterStrategy {

	/**
	 * Creates filter tag
	 * @param object OpenMRS object for which tag is created
	 * @return XML string that represents filter tag for object
	 */
	String createFilterFeed(OpenmrsObject object);

	/**
	 * Checks if filter is valid.
	 * @param tag formatted as XML
	 * @return true if tag is valid
	 */
	 boolean isFilterTagValid(String tag);
}
