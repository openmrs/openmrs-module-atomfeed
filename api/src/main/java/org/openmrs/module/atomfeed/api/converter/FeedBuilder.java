package org.openmrs.module.atomfeed.api.converter;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import java.util.Map;

public interface FeedBuilder {

	Map<String, String> getLinks(OpenmrsObject openmrsObject, FeedConfiguration feedConfiguration);

}
