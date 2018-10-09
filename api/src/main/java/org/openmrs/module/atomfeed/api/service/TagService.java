package org.openmrs.module.atomfeed.api.service;

import org.openmrs.module.atomfeed.api.filter.FeedFilter;

import java.util.List;

public interface TagService {

	List<FeedFilter> getFeedFiltersFromTags(List tags);
}
