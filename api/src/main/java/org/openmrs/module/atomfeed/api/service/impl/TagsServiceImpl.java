package org.openmrs.module.atomfeed.api.service.impl;

import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.TagService;
import org.openmrs.module.atomfeed.api.service.XMLParseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

public class TagsServiceImpl implements TagService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TagService.class);

	@Autowired
	private XMLParseService xmlParseService;

	@Override
	public List<FeedFilter> getFeedFiltersFromTags(List tags) {
		List<FeedFilter> feedFilters = new ArrayList<>();

		for (Object tag : tags) {
			if (tag instanceof String) {
				try {
					FeedFilter feedFilter = xmlParseService.createFeedFilterFromXMLString((String) tag);
					if (feedFilter.getBeanName() != null && feedFilter.getFilter() != null) {
						feedFilters.add(feedFilter);
					}
				}
				catch (JAXBException e) {
					LOGGER.warn(String.format("Cannot parse tag to XML: %s", (String) tag));
				}
			}
		}
		return feedFilters;
	}
}
