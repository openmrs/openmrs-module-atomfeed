package org.openmrs.module.atomfeed.api.filter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "feedFilter")
public class FeedFilter {

	@XmlElement(name = "beanName")
	private String beanName;

	@XmlElement(name = "filter")
	private String filter;

	public FeedFilter() {
	}

	public FeedFilter(String beanName, String filter) {
		this.beanName = beanName;
		this.filter = filter;
	}

	public String getBeanName() {
		return beanName;
	}

	public String getFilter() {
		return filter;
	}
}
