package org.openmrs.module.atomfeed.api.service.impl;

import com.sun.syndication.feed.atom.Category;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.TagService;
import org.openmrs.module.atomfeed.api.service.XMLParseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Component("atomfeed.tagsService")
public class TagsServiceImpl implements TagService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TagService.class);

	@Autowired
	private XMLParseService xmlParseService;

	@Override
	public List<FeedFilter> getFeedFiltersFromTags(List tags) {
		List<FeedFilter> feedFilters = new ArrayList<>();

		ListIterator tagIterator = tags.listIterator();
		while (tagIterator.hasNext()) {
			Object tag = tagIterator.next();
			if (tag instanceof Category) {
				try {
					FeedFilter feedFilter = xmlParseService.createFeedFilterFromXMLString(((Category) tag).getTerm());
					if (feedFilter.getBeanName() != null && feedFilter.getFilter() != null) {
						feedFilters.add(feedFilter);
						tagIterator.remove(); // Remove processed tag
					}
				} catch (JAXBException e) {
					LOGGER.warn(String.format("Cannot parse tag to XML: %s", ((Category) tag).getTerm()));
				}
			}
		}
		return feedFilters;
	}
}
