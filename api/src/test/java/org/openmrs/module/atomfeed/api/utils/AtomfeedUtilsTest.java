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

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

public class AtomfeedUtilsTest {

	private static final String SAMPLE_FEED_CONFIGURATION_JSON = "sampleFeedConfiguration.json";
	private static final String INCORRECT_FEED_CONFIGURATION_JSON = "incorrectFeedConfiguration.json";
	private static final String PATH_TO_NOT_EXISTING_FILE = "pathToNotExistingFile";
	private static final FeedConfiguration EXPECTED_FEED_CONFIGURATION = new FeedConfiguration();

	@Before
	public void setUp() {
		EXPECTED_FEED_CONFIGURATION.setOpenMrsClass("org.openmrs.Patient");
		EXPECTED_FEED_CONFIGURATION.setEnabled(false);
		EXPECTED_FEED_CONFIGURATION.setTitle("Title");
		EXPECTED_FEED_CONFIGURATION.setCategory("patient");
		EXPECTED_FEED_CONFIGURATION.setFeedWriter("custom.PatientWriter");
		final HashMap<String, String> expectedLinkTemplates = new HashMap<>();
		expectedLinkTemplates.put("rest", "openmrs/ws/rest/v1/patient{uuid}?v=full");
		expectedLinkTemplates.put("fhir", "openmrs/ws/fhir/v1/patient{uuid}?v=full");
		EXPECTED_FEED_CONFIGURATION.setLinkTemplates(expectedLinkTemplates);
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
		List<FeedConfiguration> result = AtomfeedUtils.parseJsonFileToFeedConfiguration(SAMPLE_FEED_CONFIGURATION_JSON);
		Assert.assertEquals(EXPECTED_FEED_CONFIGURATION, result.get(0));
	}

	@Test(expected = AtomfeedException.class)
	public void parseJsonFileToFeedConfiguration_shouldThrowJsonParseException() throws AtomfeedException {
		AtomfeedUtils.parseJsonFileToFeedConfiguration(INCORRECT_FEED_CONFIGURATION_JSON);
	}

	@Test
	public void parseJsonStringToFeedConfiguration_shouldParseSampleFeedConfigurationResource() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(SAMPLE_FEED_CONFIGURATION_JSON);
		List<FeedConfiguration> result = AtomfeedUtils.parseJsonStringToFeedConfiguration(json);
		Assert.assertEquals(EXPECTED_FEED_CONFIGURATION, result.get(0));
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
		List<FeedConfiguration> feedConfigurations = AtomfeedUtils.parseJsonFileToFeedConfiguration(
				SAMPLE_FEED_CONFIGURATION_JSON);
		
		final String path = "newFile.txt";
		AtomfeedUtils.writeFeedConfigurationToJsonFile(feedConfigurations, path);
		
		String expected = AtomfeedUtils.readResourceFile(SAMPLE_FEED_CONFIGURATION_JSON);
		String result = AtomfeedUtils.readResourceFile(path) + "\n";
		
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
