package org.openmrs.module.atomfeed.api.service;

import com.sun.syndication.feed.atom.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.impl.TagsServiceImpl;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class TagsServiceTest {

	private static final String FEED_FILTER_XML_PATTERN;
	private static final String BAD_FEED_FILTER_XML_1;
	private static final String BAD_FEED_FILTER_XML_2;
	private static final String BAD_FEED_FILTER_XML_3;
	private static final int CORRECT_TAGS_NUMBER = 3;
	private static final String BEAN_NAME_PREFIX = "BeanName-";
	private static final String FILTER_PREFIX = "Filter-";
	private static final String CANNOT_PARSE_ERROR = "Cannot parse";

	@InjectMocks
	private TagsServiceImpl tagsService;

	@Mock
	private XMLParseService xmlParseService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGettingFeedFiltersFromTags() throws JAXBException {
		List tags = prepareTagsList();

		List<FeedFilter> feedFilters = tagsService.getFeedFiltersFromTags(tags);

		Assert.assertEquals(CORRECT_TAGS_NUMBER, feedFilters.size());
		for (int i = 0; i < feedFilters.size(); i++) {
			Assert.assertEquals(BEAN_NAME_PREFIX + i, feedFilters.get(i).getBeanName());
			Assert.assertEquals(FILTER_PREFIX + i, feedFilters.get(i).getFilter());
		}
	}

	private List prepareTagsList() throws JAXBException {
		List tags = new ArrayList();

		int i = 0;
		// Add 3 correct Feed Filters
		for (; i < CORRECT_TAGS_NUMBER; i++) {
			tags.add(createFeedFilter(i));
		}
		// Add 3 bad formatted FeedFilters
		tags.add(createBadFeedFilter(BAD_FEED_FILTER_XML_1, i++));
		tags.add(createBadFeedFilter(BAD_FEED_FILTER_XML_2, i++));
		tags.add(createBadFeedFilter(BAD_FEED_FILTER_XML_3, i));
		// Add 1 Tag which cannot be parsed to FeedFilter
		tags.add(createCategory("patient"));
		tags.add(createCategory("CREATED"));
		// Add 1 object that is not a String
		tags.add(new Object());

		return tags;

	}

	private Category createFeedFilter(int i) throws JAXBException {
		String feedFilter = String.format(FEED_FILTER_XML_PATTERN, BEAN_NAME_PREFIX + i, FILTER_PREFIX + i);
		when(xmlParseService.createFeedFilterFromXMLString(feedFilter))
				.thenReturn(new FeedFilter(BEAN_NAME_PREFIX + i, FILTER_PREFIX + i));
		Category category = new Category();
		category.setTerm(feedFilter);
		return category;
	}

	private Category createBadFeedFilter(String xml, int i) throws JAXBException {
		String feedFilter = String.format(xml, BEAN_NAME_PREFIX + i, FILTER_PREFIX + i);
		when(xmlParseService.createFeedFilterFromXMLString(feedFilter))
				.thenThrow(new JAXBException(CANNOT_PARSE_ERROR));
		Category category = new Category();
		category.setTerm(feedFilter);
		return category;
	}

	private Category createCategory(String term) throws JAXBException {
		Category category = new Category();
		category.setTerm(term);
		when(xmlParseService.createFeedFilterFromXMLString(term))
				.thenThrow(new JAXBException(CANNOT_PARSE_ERROR));
		return category;
	}

	static {
		FEED_FILTER_XML_PATTERN = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<feedFilter>"
				+ "<beanName>%s</beanName>"
				+ "<filter>%s</filter>"
				+ "</feedFilter>";
		// Root markup is incorrect
		BAD_FEED_FILTER_XML_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<feedFilterWRONG>"
				+ "<beanName>%s</beanName>"
				+ "<filter>%s</filter>"
				+ "</feedFilterWRONG>";
		// BeanName markup is incorrect
		BAD_FEED_FILTER_XML_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<feedFilter>"
				+ "<beanNameWRONG>%s</beanNameWRONG>"
				+ "<filter>%s</filter>"
				+ "</feedFilter>";
		// Filter markup is incorrect
		BAD_FEED_FILTER_XML_3 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<feedFilter>"
				+ "<beanName>%s</beanName>"
				+ "<filterWRONG>%s</filterWRONG>"
				+ "</feedFilter>";
	}

}
