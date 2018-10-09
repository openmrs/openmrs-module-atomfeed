package org.openmrs.module.atomfeed.api.service;

import java.util.List;

public interface TagService {

	List<String> getFeedFiltersFromTags(List tag);

	String getBeanNameFromFeedFilter(String feedFilter);
}
