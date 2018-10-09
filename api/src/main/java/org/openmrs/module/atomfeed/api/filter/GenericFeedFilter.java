package org.openmrs.module.atomfeed.api.filter;

import org.openmrs.OpenmrsObject;

public interface GenericFeedFilter {

	String createFilterTag(OpenmrsObject object);

	boolean isFilterTagValid(String tag);
}
