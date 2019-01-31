package org.openmrs.module.atomfeed.api.converter.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.converter.FeedBuilder;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("atomfeed.DefaultFeedBuilder")
public class DefaultFeedBuilder implements FeedBuilder  {

	private static final String UUID_PATTERN = "{uuid}";

	@Override
	public Map<String, String> getLinks(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration) {
		String uuid = openmrsObject.getUuid();
		Map<String, String> urls = new HashMap<>();
		for (Map.Entry<String, String> entry : feedConfiguration.getLinkTemplates().entrySet()) {
			urls.put(entry.getKey(), entry.getValue().replace(UUID_PATTERN, uuid));
		}
		return urls;
	}
}
