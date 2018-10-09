package org.openmrs.module.atomfeed.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneralConfiguration {

	private List<String> feedFilterBeans;

	private List<FeedConfiguration> feedConfigurations;

	public GeneralConfiguration() {
		feedFilterBeans = new ArrayList<>();
		feedConfigurations = new ArrayList<>();
	}

	public GeneralConfiguration(List<String> feedFilterBeans,
			List<FeedConfiguration> feedConfigurations) {
		this.feedFilterBeans = feedFilterBeans;
		this.feedConfigurations = feedConfigurations;
	}

	public List<String> getFeedFilterBeans() {
		return feedFilterBeans;
	}

	public void setFeedFilterBeans(List<String> feedFilterBeans) {
		this.feedFilterBeans = feedFilterBeans;
	}

	public List<FeedConfiguration> getFeedConfigurations() {
		return feedConfigurations;
	}

	public void setFeedConfigurations(List<FeedConfiguration> feedConfigurations) {
		this.feedConfigurations = feedConfigurations;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GeneralConfiguration that = (GeneralConfiguration) o;
		return Objects.equals(feedFilterBeans, that.feedFilterBeans) &&
				Objects.equals(feedConfigurations, that.feedConfigurations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(feedFilterBeans, feedConfigurations);
	}
}
