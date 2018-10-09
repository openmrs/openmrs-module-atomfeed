package org.openmrs.module.atomfeed.api;

import java.util.Collection;
import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.model.GeneralConfiguration;

public interface FeedConfigurationService extends OpenmrsService {
    
    void saveConfig(GeneralConfiguration generalConfiguration);
    
    void saveConfig(String value);
    
    FeedConfiguration getFeedConfigurationByCategory(String category);
    
    FeedConfiguration getFeedConfigurationByOpenMrsClass(String openMrsClass);

    Collection<FeedConfiguration> getAllFeedConfigurations();

    List<String> getFeedFilterBeans();

    GeneralConfiguration getGeneralConfiguration();
}
