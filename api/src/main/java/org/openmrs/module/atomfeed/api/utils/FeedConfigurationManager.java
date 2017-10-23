package org.openmrs.module.atomfeed.api.utils;

import org.openmrs.module.atomfeed.AtomfeedmoduleConstants;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

@Component
public class FeedConfigurationManager {

    private HashMap<String, FeedConfiguration> feedConfiguration;

    public FeedConfigurationManager() {
        if (localConfigurationExists()) {
            loadConfigurations(AtomfeedUtils
                    .parseFileToJsonConfigurationResource(AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION));
        } else {
            loadConfigurations(AtomfeedUtils
                    .parseFileToJsonConfigurationResource(AtomfeedmoduleConstants.ATOMFEED_PATH_TO_DAFAULT_CONFIGURATION));
        }
    }

    public void loadLocalConfiguration(FeedConfiguration[] value) {
        AtomfeedUtils.parseJsonConfigurationResourceToFile(value,
            AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
        loadConfigurations(value);
    }

    public void loadLocalConfiguration(String value) {
        if (AtomfeedUtils.isValidateJson(value)) {
            FeedConfiguration[] localConfiguration = AtomfeedUtils.parseStringToJsonConfigurationResource(value);
            AtomfeedUtils.parseJsonConfigurationResourceToFile(localConfiguration,
                AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
            loadConfigurations(localConfiguration);
        }
    }

    public FeedConfiguration getConfigurationByTitle(String title) {
        return feedConfiguration.get(title);
    }

    private void loadConfigurations(FeedConfiguration[] feedConfigurations) {
        HashMap<String, FeedConfiguration> tmp = new HashMap<>();
        for (FeedConfiguration configuration : Arrays.asList(feedConfigurations)) {
            tmp.put(configuration.getTitle(), configuration);
        }
        feedConfiguration = tmp;
    }

    private boolean localConfigurationExists() {
        InputStream in = AtomfeedUtils.class.getClassLoader().getResourceAsStream(
            AtomfeedmoduleConstants.ATOMFEED_PATH_TO_LOCAL_CONFIGURATION);
        return in != null;
    }
}
