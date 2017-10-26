/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.model;

import java.util.HashMap;
import java.util.Objects;

public class FeedConfiguration {
	
	private String openMrsClass;
	
	private boolean enabled;
	
	private String title;
	
	private String category;
	
	private HashMap<String, String> linkTemplates;
	
	private String feedWriter;
	
	public FeedConfiguration() {
	}
	
	public FeedConfiguration(String openMrsClass, String title, String category,
			HashMap<String, String> linkTemplates, String feedWriter) {
		this.openMrsClass = openMrsClass;
		this.enabled = false;
		this.title = title;
		this.category = category;
		this.linkTemplates = linkTemplates;
		this.feedWriter = feedWriter;
	}
	
	public String getOpenMrsClass() {
		return openMrsClass;
	}
	
	public void setOpenMrsClass(String openMrsClass) {
		this.openMrsClass = openMrsClass;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public HashMap<String, String> getLinkTemplates() {
		return linkTemplates;
	}
	
	public void setLinkTemplates(HashMap<String, String> linkTemplates) {
		this.linkTemplates = linkTemplates;
	}
	
	public String getFeedWriter() {
		return feedWriter;
	}
	
	public void setFeedWriter(String feedWriter) {
		this.feedWriter = feedWriter;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		
		FeedConfiguration feedConfiguration = (FeedConfiguration) o;
		return Objects.equals(openMrsClass, feedConfiguration.openMrsClass)
				&& Objects.equals(enabled, feedConfiguration.enabled)
				&& Objects.equals(title, feedConfiguration.title)
				&& Objects.equals(category, feedConfiguration.category)
				&& Objects.equals(linkTemplates, feedConfiguration.linkTemplates)
				&& Objects.equals(feedWriter, feedConfiguration.feedWriter);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(openMrsClass, enabled, title, category, linkTemplates, feedWriter);
	}
}
