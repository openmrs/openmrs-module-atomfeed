/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.utils;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.model.GeneralConfiguration;

public class AtomfeedUtilsTest {

	private static final String SAMPLE_FEED_CONFIGURATION_JSON = "sampleFeedConfiguration.json";
	private static final String INCORRECT_FEED_CONFIGURATION_JSON = "incorrectFeedConfiguration.json";
	private static final String PATH_TO_NOT_EXISTING_FILE = "pathToNotExistingFile";
	private static final GeneralConfiguration EXPECTED_FEED_CONFIGURATION = new GeneralConfiguration();

	@Before
	public void setUp() {
		FeedConfiguration expectedFeedConfiguration = new FeedConfiguration();
		expectedFeedConfiguration.setOpenMrsClass("org.openmrs.Patient");
		expectedFeedConfiguration.setEnabled(false);
		expectedFeedConfiguration.setTitle("Title");
		expectedFeedConfiguration.setCategory("patient");
		expectedFeedConfiguration.setFeedWriter("custom.PatientWriter");
		final LinkedHashMap<String, String> expectedLinkTemplates = new LinkedHashMap<>();
		expectedLinkTemplates.put("rest", "openmrs/ws/rest/v1/patient{uuid}?v=full");
		expectedLinkTemplates.put("fhir", "openmrs/ws/fhir/v1/patient{uuid}?v=full");
		expectedFeedConfiguration.setLinkTemplates(expectedLinkTemplates);

		List<String> expectedFeedFilter = Collections.singletonList("testBeanName");

		EXPECTED_FEED_CONFIGURATION.setFeedFilterBeans(expectedFeedFilter);
		EXPECTED_FEED_CONFIGURATION.setFeedConfigurations(Collections.singletonList(expectedFeedConfiguration));
	}
	
	@Test
	public void readResourceFile_shouldReadSampleFile() throws AtomfeedException {
		final String sampleResourcePath = "sampleTextFile.txt";
		final String expectedResult = "sampleContent";
		
		String result = AtomfeedUtils.readResourceFile(sampleResourcePath);
		assertEquals(result, expectedResult);
	}
	
	@Test(expected = AtomfeedException.class)
	public void readResourceFile_shouldThrowIoExceptionIfFileDoesNotExist() throws AtomfeedException {
		AtomfeedUtils.readResourceFile(PATH_TO_NOT_EXISTING_FILE);
	}

	@Test
	public void parseJsonFileToFeedConfiguration_shouldParseSampleFeedConfigurationResource() throws AtomfeedException {
		GeneralConfiguration result = AtomfeedUtils.parseJsonFileToFeedConfiguration(SAMPLE_FEED_CONFIGURATION_JSON);
		Assert.assertEquals(EXPECTED_FEED_CONFIGURATION.getFeedConfigurations().get(0),
				result.getFeedConfigurations().get(0));
		Assert.assertEquals(EXPECTED_FEED_CONFIGURATION.getFeedFilterBeans(), result.getFeedFilterBeans());
	}

	@Test(expected = AtomfeedException.class)
	public void parseJsonFileToFeedConfiguration_shouldThrowJsonParseException() throws AtomfeedException {
		AtomfeedUtils.parseJsonFileToFeedConfiguration(INCORRECT_FEED_CONFIGURATION_JSON);
	}

	@Test
	public void parseJsonStringToFeedConfiguration_shouldParseSampleFeedConfigurationResource() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(SAMPLE_FEED_CONFIGURATION_JSON);
		GeneralConfiguration result = AtomfeedUtils.parseJsonStringToFeedConfiguration(json);
		Assert.assertEquals(EXPECTED_FEED_CONFIGURATION.getFeedConfigurations().get(0),
				result.getFeedConfigurations().get(0));
		Assert.assertEquals(EXPECTED_FEED_CONFIGURATION.getFeedFilterBeans(), result.getFeedFilterBeans());
	}

	@Test(expected = AtomfeedException.class)
	public void parseJsonStringToFeedConfiguration_shouldThrowJsonParseException() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(INCORRECT_FEED_CONFIGURATION_JSON);
		AtomfeedUtils.parseJsonStringToFeedConfiguration(json);
	}

	@Test
	public void isValidateJson_correct() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(SAMPLE_FEED_CONFIGURATION_JSON);
		Assert.assertTrue(AtomfeedUtils.isValidateJson(json));
	}

	@Test
	public void isValidateJson_incorrect() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(INCORRECT_FEED_CONFIGURATION_JSON);
		Assert.assertFalse(AtomfeedUtils.isValidateJson(json));
	}

	@Test
	public void writeFeedConfigurationToJsonFile() throws AtomfeedException {
		GeneralConfiguration generalConfiguration = AtomfeedUtils.parseJsonFileToFeedConfiguration(
				SAMPLE_FEED_CONFIGURATION_JSON);
		
		final String path = "newFile.txt";
		AtomfeedUtils.writeFeedConfigurationToJsonFile(generalConfiguration, path);
		
		String expected = AtomfeedUtils.readResourceFile(SAMPLE_FEED_CONFIGURATION_JSON);
		String result = AtomfeedUtils.readResourceFile(path) + System.getProperty("line.separator");
		
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void resourceFileExists_exist() throws AtomfeedException {
		Assert.assertTrue(AtomfeedUtils.resourceFileExists(SAMPLE_FEED_CONFIGURATION_JSON));
	}
	
	@Test
	public void resourceFileExists_notExist() throws AtomfeedException {
		Assert.assertFalse(AtomfeedUtils.resourceFileExists(PATH_TO_NOT_EXISTING_FILE));
	}
}
