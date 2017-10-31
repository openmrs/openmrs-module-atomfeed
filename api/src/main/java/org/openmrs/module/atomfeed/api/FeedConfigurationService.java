package org.openmrs.module.atomfeed.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import java.util.List;

public interface FeedConfigurationService extends OpenmrsService {
    void saveConfig(List<FeedConfiguration> value);
    void saveConfig(String value);
}
