package org.openmrs.module.atomfeed.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.atomfeed.api.service.XMLParseService;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

@Component("atomfeed.xmlParseService")
public class XMLParseServiceImpl extends BaseOpenmrsService implements XMLParseService {

	@Override
	public FeedFilter createFeedFilterFromXMLString(String xml) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(FeedFilter.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (FeedFilter) unmarshaller.unmarshal(new StringReader(xml));
	}

	@Override
	public String createXMLFromFeedFilter(FeedFilter feedFilter) throws JAXBException {
		if (feedFilter == null) {
			return null;
		}
		JAXBContext jaxbContext = JAXBContext.newInstance(FeedFilter.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		StringWriter sw = new StringWriter();
		marshaller.marshal(feedFilter, sw);
		return sw.toString();
	}
}
