package org.openmrs.module.atomfeed.api;

import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import java.util.List;

public interface FeedConfigurationService {
    void loadLocalFeedConfiguration(List<FeedConfiguration> value);
    void loadLocalFeedConfiguration(String value);
}
