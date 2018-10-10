package org.openmrs.module.atomfeed.api.utils;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.PersonAddress;

public class FeedFilterUtilTest {

	private static final String FILTER = "Poland%Pomorskie%Gdansk%Tczew%80-456";
	private static final String FILTER_PROPERTY_1 = "Poland%*%*%*%*";
	private static final String FILTER_PROPERTY_2 = "*%*%*%*%80-456";
	private static final String FILTER_PROPERTY_3 = "USA%*%*%*%*";
	private static final String BAD_FILTER_PROPERTY_1 = "%*%*%*%*";
	private static final String BAD_FILTER_PROPERTY_2 = "*%*%*%*";

	@Test
	public void testFilterValidation() {
		Assert.assertTrue(FeedFilterUtil.isFilterValid(FILTER, FILTER_PROPERTY_1));
		Assert.assertTrue(FeedFilterUtil.isFilterValid(FILTER, FILTER_PROPERTY_2));

		Assert.assertFalse(FeedFilterUtil.isFilterValid(FILTER, FILTER_PROPERTY_3));
	}

	@Test
	public void testBadFormattedFilters() {
		Assert.assertFalse(FeedFilterUtil.isFilterValid(FILTER, BAD_FILTER_PROPERTY_1));
		Assert.assertFalse(FeedFilterUtil.isFilterValid(FILTER, BAD_FILTER_PROPERTY_2));
	}

	@Test
	public void testCreatingFilter() {
		PersonAddress personAddress = new PersonAddress();
		personAddress.setCountry("Poland");
		personAddress.setStateProvince("Pomorskie");
		personAddress.setCountyDistrict("Gdansk");
		personAddress.setCityVillage("Tczew");
		personAddress.setPostalCode("80-456");

		String createdFilter = FeedFilterUtil.createLocationFilter(personAddress);

		Assert.assertEquals(FILTER, createdFilter);
	}
}
