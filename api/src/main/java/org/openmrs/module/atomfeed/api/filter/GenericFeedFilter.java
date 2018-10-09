package org.openmrs.module.atomfeed.api.filter;

import org.openmrs.OpenmrsObject;

public interface GenericFeedFilter {

	public String createFilterTag(OpenmrsObject object);

	public boolean isFilterTagValid(String tag);
}
