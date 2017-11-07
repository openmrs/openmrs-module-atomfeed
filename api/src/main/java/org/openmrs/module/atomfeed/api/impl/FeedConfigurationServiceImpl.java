package org.openmrs.module.atomfeed.api.impl;

import java.util.HashMap;
import java.util.List;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.springframework.stereotype.Component;

@Component("atomfeed.feedConfigurationService")
public class FeedConfigurationServiceImpl extends BaseOpenmrsService implements FeedConfigurationService {

    private HashMap<String, FeedConfiguration> feedConfigurationByCategory;
    
    private HashMap<String, FeedConfiguration> feedConfigurationByOpenMrsClass;
    
    public FeedConfigurationServiceImpl() {
        if (AtomfeedUtils.resourceFileExists(AtomfeedConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION)) {
            loadFeedConfigurations(
                    AtomfeedUtils.parseJsonFileToFeedConfiguration(AtomfeedConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION)
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
            AtomfeedConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
        loadFeedConfigurations(value);
    }
    
    @Override
    public void saveConfig(String value) {
        if (AtomfeedUtils.isValidateJson(value)) {
            List<FeedConfiguration> localConfiguration = AtomfeedUtils.parseJsonStringToFeedConfiguration(value);
            AtomfeedUtils.writeFeedConfigurationToJsonFile(localConfiguration,
                AtomfeedConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
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
