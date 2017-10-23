package org.openmrs.module.atomfeed.api.utils;

import org.openmrs.module.atomfeed.AtomfeedmoduleConstants;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;

@Component
public class FeedConfigurationManager {

    private HashMap<String, FeedConfiguration> feedConfiguration;

    public FeedConfigurationManager() {
        if (AtomfeedUtils.resourceFileExists(AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION)) {
            loadFeedConfigurations(
                    AtomfeedUtils.parseJsonFileToFeedConfiguration(AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION)
            );
        } else {
            loadFeedConfigurations(
                    AtomfeedUtils.parseJsonFileToFeedConfiguration(AtomfeedmoduleConstants.ATOMFEED_PATH_TO_DAFAULT_CONFIGURATION)
            );
        }
    }

    public void loadLocalFeedConfiguration(FeedConfiguration[] value) {
        AtomfeedUtils.writeFeedConfigurationToJsonFile(value,
            AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
        loadFeedConfigurations(value);
    }

    public void loadLocalFeedConfiguration(String value) {
        if (AtomfeedUtils.isValidateJson(value)) {
            FeedConfiguration[] localConfiguration = AtomfeedUtils.parseJsonStringToFeedConfiguration(value);
            AtomfeedUtils.writeFeedConfigurationToJsonFile(localConfiguration,
                AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
            loadFeedConfigurations(localConfiguration);
        }
    }

    public FeedConfiguration getFeedConfigurationByTitle(String title) {
        return feedConfiguration.get(title);
    }

    private void loadFeedConfigurations(FeedConfiguration[] feedConfigurations) {
        HashMap<String, FeedConfiguration> tmp = new HashMap<>();
        for (FeedConfiguration configuration : Arrays.asList(feedConfigurations)) {
            tmp.put(configuration.getTitle(), configuration);
        }
        feedConfiguration = tmp;
    }
}
