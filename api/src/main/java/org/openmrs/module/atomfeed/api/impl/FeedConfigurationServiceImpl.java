package org.openmrs.module.atomfeed.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Component("atomfeed.feedConfigurationService")
public class FeedConfigurationServiceImpl extends BaseOpenmrsService implements FeedConfigurationService {

    private HashMap<String, FeedConfiguration> feedConfigurationByCategory;
    
    private HashMap<String, FeedConfiguration> feedConfigurationByOpenMrsClass;
    
    public FeedConfigurationServiceImpl() {
        if (AtomfeedUtils.resourceFileExists(AtomfeedConstants.ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION)) {
            loadFeedConfigurations(
                    AtomfeedUtils.parseJsonFileToFeedConfiguration(AtomfeedConstants.ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION)
            );
        } else {
            loadFeedConfigurations(
                    AtomfeedUtils.parseJsonFileToFeedConfiguration(AtomfeedConstants.ATOMFEED_PATH_TO_DEFAULT_CONFIGURATION)
            );
        }
    }
    
    @Override
    public void saveConfig(List<FeedConfiguration> value) {
        AtomfeedUtils.writeFeedConfigurationToJsonFile(value,
            AtomfeedConstants.ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION);
        loadFeedConfigurations(value);
    }
    
    @Override
    public void saveConfig(String value) {
        if (AtomfeedUtils.isValidateJson(value)) {
            List<FeedConfiguration> localConfiguration = AtomfeedUtils.parseJsonStringToFeedConfiguration(value);
            AtomfeedUtils.writeFeedConfigurationToJsonFile(localConfiguration,
                AtomfeedConstants.ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION);
            loadFeedConfigurations(localConfiguration);
        }
    }
    
    @Override
    public FeedConfiguration getFeedConfigurationByCategory(String category) {
        return feedConfigurationByCategory.get(category);
    }
    
    @Override
    public FeedConfiguration getFeedConfigurationByOpenMrsClass(String openMrsClass) {
        return feedConfigurationByOpenMrsClass.get(openMrsClass);
    }

    @Override
    public Collection<FeedConfiguration> getAllFeedConfigurations() {
        return feedConfigurationByCategory.values();
    }

    
    private void loadFeedConfigurations(List<FeedConfiguration> feedConfigurations) {
        HashMap<String, FeedConfiguration> byCategory = new HashMap<>();
        HashMap<String, FeedConfiguration> byOpenMrsClass = new HashMap<>();
        for (FeedConfiguration configuration : feedConfigurations) {
            byCategory.put(configuration.getCategory(), configuration);
            byOpenMrsClass.put(configuration.getOpenMrsClass(), configuration);
        }
        feedConfigurationByCategory = byCategory;
        feedConfigurationByOpenMrsClass = byOpenMrsClass;
    }
}
