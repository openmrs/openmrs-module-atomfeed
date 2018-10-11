package org.openmrs.module.atomfeed.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.atomfeed.AtomfeedConstants.ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION;
import static org.openmrs.module.atomfeed.AtomfeedConstants.ATOMFEED_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.atomfeed.api.utils.AtomfeedUtils.parseJsonFileToFeedConfiguration;

@Component("atomfeed.feedConfigurationService")
public class FeedConfigurationServiceImpl extends BaseOpenmrsService implements FeedConfigurationService {

	private HashMap<String, FeedConfiguration> feedConfigurationByCategory;

	private HashMap<String, FeedConfiguration> feedConfigurationByOpenMrsClass;

	public FeedConfigurationServiceImpl() {
		List<FeedConfiguration> feedConfigurations;
		if (AtomfeedUtils.resourceFileExists(ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION)) {
			feedConfigurations = parseJsonFileToFeedConfiguration(ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION);
		} else {
			feedConfigurations = parseJsonFileToFeedConfiguration(ATOMFEED_PATH_TO_DEFAULT_CONFIGURATION);
		}

		loadFeedConfigurations(feedConfigurations);
	}

	@Override
	public void saveConfig(List<FeedConfiguration> feedConfigurations) {
		AtomfeedUtils.writeFeedConfigurationToJsonFile(feedConfigurations,
				ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION);
		loadFeedConfigurations(feedConfigurations);
	}

	@Override
	public void saveConfig(String value) {
		if (AtomfeedUtils.isValidateJson(value)) {
			List<FeedConfiguration> feedConfigurations = AtomfeedUtils.parseJsonStringToFeedConfiguration(value);
			saveConfig(feedConfigurations);
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
