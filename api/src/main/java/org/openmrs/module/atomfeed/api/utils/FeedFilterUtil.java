package org.openmrs.module.atomfeed.api.utils;

import org.openmrs.PersonAddress;

public final class FeedFilterUtil {

	private static final String FILTER_DELIMITER = "%";
	private static final String SKIP_VALUE = "*";

	private FeedFilterUtil() { }

	public static boolean isFilterValid(String filter, String filterProperty) {
		String[] filterValues = filter.split(FILTER_DELIMITER);
		String[] filterPropertyValues = filterProperty.split(FILTER_DELIMITER);

		if (filterValues.length != filterPropertyValues.length) {
			return false;
		}

		for (int i = 0; i < filterValues.length; i++) {
			if (filterValueDoesNotMatch(filterPropertyValues[i], filterValues[i])) {
				return false;
			}
		}

		return true;
	}

	public static String createLocationFilter(PersonAddress personAddress) {
		return personAddress.getCountry()
			+ FILTER_DELIMITER
			+ personAddress.getStateProvince()
			+ FILTER_DELIMITER
			+ personAddress.getCountyDistrict()
			+ FILTER_DELIMITER
			+ personAddress.getCityVillage()
			+ FILTER_DELIMITER
			+ personAddress.getPostalCode();
	}

	private static boolean filterValueDoesNotMatch(String filterPropertyValue, String filterValue) {
		return !SKIP_VALUE.equals(filterPropertyValue)
				&& !filterPropertyValue.equalsIgnoreCase(filterValue);
	}
}
