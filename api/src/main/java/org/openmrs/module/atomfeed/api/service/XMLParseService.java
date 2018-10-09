package org.openmrs.module.atomfeed.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;

import javax.xml.bind.JAXBException;

public interface XMLParseService extends OpenmrsService {

	FeedFilter createFeedFilterFromXMLString(String xml) throws JAXBException;

	String createXMLFromFeedFilter(FeedFilter feedFilter) throws JAXBException;
}
