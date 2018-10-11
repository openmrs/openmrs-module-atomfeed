package org.openmrs.module.atomfeed.api.service;

import java.util.Collection;
import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

public interface FeedConfigurationService extends OpenmrsService {
    
    void saveConfig(List<FeedConfiguration> feedConfigurations);
    
    void saveConfig(String value);
    
    FeedConfiguration getFeedConfigurationByCategory(String category);
    
    FeedConfiguration getFeedConfigurationByOpenMrsClass(String openMrsClass);

    Collection<FeedConfiguration> getAllFeedConfigurations();
}
