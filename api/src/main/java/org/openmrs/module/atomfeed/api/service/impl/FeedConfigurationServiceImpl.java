package org.openmrs.module.atomfeed.api.service.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.model.GeneralConfiguration;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.openmrs.module.atomfeed.AtomfeedConstants.ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION;
import static org.openmrs.module.atomfeed.AtomfeedConstants.ATOMFEED_PATH_TO_DEFAULT_CONFIGURATION;
import static org.openmrs.module.atomfeed.api.utils.AtomfeedUtils.parseJsonFileToFeedConfiguration;

@Component("atomfeed.feedConfigurationService")
public class FeedConfigurationServiceImpl extends BaseOpenmrsService implements FeedConfigurationService {

	private List<String> feedFilters;

	private HashMap<String, FeedConfiguration> feedConfigurationByCategory;

	private HashMap<String, FeedConfiguration> feedConfigurationByOpenMrsClass;

	public FeedConfigurationServiceImpl() {
		GeneralConfiguration generalConfiguration;
		if (AtomfeedUtils.resourceFileExists(ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION)) {
			generalConfiguration = parseJsonFileToFeedConfiguration(ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION);
		} else {
			generalConfiguration = parseJsonFileToFeedConfiguration(ATOMFEED_PATH_TO_DEFAULT_CONFIGURATION);
		}

		loadFeedConfigurations(generalConfiguration);
	}

	@Override
	public void saveConfig(GeneralConfiguration generalConfiguration) {
		AtomfeedUtils.writeFeedConfigurationToJsonFile(generalConfiguration,
				ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION);
		loadFeedConfigurations(generalConfiguration);
	}

	@Override
	public void saveConfig(String value) {
		if (AtomfeedUtils.isValidateJson(value)) {
			GeneralConfiguration localConfiguration = AtomfeedUtils.parseJsonStringToFeedConfiguration(value);
			saveConfig(localConfiguration);
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

	@Override
	public List<String> getFeedFilterBeans() {
		return feedFilters;
	}

	@Override
	public GeneralConfiguration getGeneralConfiguration() {
		GeneralConfiguration generalConfiguration = new GeneralConfiguration();
		generalConfiguration.setFeedFilterBeans(feedFilters);
		generalConfiguration.setFeedConfigurations(new ArrayList<>(feedConfigurationByCategory.values()));
		return generalConfiguration;
	}

	private void loadFeedConfigurations(GeneralConfiguration generalConfiguration) {
		HashMap<String, FeedConfiguration> byCategory = new LinkedHashMap<>();
		HashMap<String, FeedConfiguration> byOpenMrsClass = new LinkedHashMap<>();
		for (FeedConfiguration configuration : generalConfiguration.getFeedConfigurations()) {
			byCategory.put(configuration.getCategory(), configuration);
			byOpenMrsClass.put(configuration.getOpenMrsClass(), configuration);
		}
		feedConfigurationByCategory = byCategory;
		feedConfigurationByOpenMrsClass = byOpenMrsClass;

		feedFilters = generalConfiguration.getFeedFilterBeans();
	}
}
