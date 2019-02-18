package org.openmrs.module.atomfeed.api.converter.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.converter.FeedBuilder;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import java.util.HashMap;
import java.util.Map;

public abstract class SubResourceFeedBuilder implements FeedBuilder {

	private static final String OBJECT_UUID_PATTERN = "{uuid}";

	private static final String PARENT_UUID_PATTERN = "{parent-uuid}";

	@Override
	public Map<String, String> getLinks(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
		String paretnUuid = getParentUuid(openmrsObject);
		String uuid = openmrsObject.getUuid();
		Map<String, String> urls = new HashMap<>();
		for (Map.Entry<String, String> entry : feedConfiguration.getLinkTemplates().entrySet()) {
			String template = entry.getValue();
			template = template.replace(OBJECT_UUID_PATTERN, uuid);
			template = template.replace(PARENT_UUID_PATTERN, paretnUuid);
			urls.put(entry.getKey(), template);
		}
		return urls;
	}

	protected abstract String getParentUuid(OpenmrsObject openmrsObject);
}
