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

    private HashMap<String, FeedConfiguration> feedConfiguration;

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

    public void saveConfig(List<FeedConfiguration> value) {
        AtomfeedUtils.writeFeedConfigurationToJsonFile(value,
            AtomfeedConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
        loadFeedConfigurations(value);
    }

    public void saveConfig(String value) {
        if (AtomfeedUtils.isValidateJson(value)) {
            List<FeedConfiguration> localConfiguration = AtomfeedUtils.parseJsonStringToFeedConfiguration(value);
            AtomfeedUtils.writeFeedConfigurationToJsonFile(localConfiguration,
                AtomfeedConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
            loadFeedConfigurations(localConfiguration);
        }
    }

    public FeedConfiguration getFeedConfigurationByCategory(String category) {
        return feedConfiguration.get(category);
    }

    private void loadFeedConfigurations(List<FeedConfiguration> feedConfigurations) {
        HashMap<String, FeedConfiguration> tmp = new HashMap<>();
        for (FeedConfiguration configuration : feedConfigurations) {
            tmp.put(configuration.getCategory(), configuration);
        }
        feedConfiguration = tmp;
    }
}
