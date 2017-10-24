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

public class FeedConfiguration {
	
	private String openMrsClass;
	
	private boolean enabled;
	
	private String title;
	
	private HashMap<String, String> linkTemplates;
	
	private String feedWriter;
	
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
}
