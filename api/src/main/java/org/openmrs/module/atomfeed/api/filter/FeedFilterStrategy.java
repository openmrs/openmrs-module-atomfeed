package org.openmrs.module.atomfeed.api.filter;

import org.openmrs.module.atomfeed.api.service.XMLParseService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FeedFilterStrategy {

	@Autowired
	private XMLParseService xmlParseService;

	protected XMLParseService getXmlParseService() {
		return xmlParseService;
	}

	protected abstract String getBeanName();
}
