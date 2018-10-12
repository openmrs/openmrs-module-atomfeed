package org.openmrs.module.atomfeed.api.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.service.impl.XMLParseServiceImpl;
import org.openmrs.module.atomfeed.api.service.XMLParseService;

import javax.xml.bind.JAXBException;

public class XMLParseServiceTest {

	private static final String XML;
	private static final String FILTER = "filterValue";
	private static final String BEAN_NAME = "beanName";

	private XMLParseService parseService = new XMLParseServiceImpl();

	@Test
	public void testCreatingTagFromXML() throws JAXBException {
		FeedFilter feedFilter = parseService.createFeedFilterFromXMLString(XML);
		Assert.assertEquals(BEAN_NAME, feedFilter.getBeanName());
		Assert.assertEquals(FILTER, feedFilter.getFilter());
	}

	@Test
	public void testCreatingXMLFromTag() throws JAXBException {
		FeedFilter feedFilter = new FeedFilter(BEAN_NAME, FILTER);
		String xml = parseService.createXMLFromFeedFilter(feedFilter);
		Assert.assertEquals(XML, xml);
	}

	static {
		XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<feedFilter>"
				+ "<beanName>" + BEAN_NAME + "</beanName>"
				+ "<filter>" + FILTER + "</filter>"
				+ "</feedFilter>";
	}
}
