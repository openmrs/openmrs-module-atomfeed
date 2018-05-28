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
import org.junit.Ignore;
import org.junit.Test;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

public class AtomfeedUtilsTest {

	private static final String sampleFeedConfigurationPath = "sampleFeedConfiguration.json";
	private static final String incorrectFeedConfigurationPath = "incorrectFeedConfiguration.json";
	private static final String notExistingFilePath = "pathToNotExistingFile";
	private static final FeedConfiguration expectedFeedConfiguration = new FeedConfiguration();

	@Before
	public void setUp() {
		expectedFeedConfiguration.setOpenMrsClass("org.openmrs.Patient");
		expectedFeedConfiguration.setEnabled(false);
		expectedFeedConfiguration.setTitle("Title");
		expectedFeedConfiguration.setCategory("patient");
		expectedFeedConfiguration.setFeedWriter("custom.PatientWriter");
		final HashMap<String, String> expectedLinkTemplates = new HashMap<>();
		expectedLinkTemplates.put("rest", "openmrs/ws/rest/v1/patient{uuid}?v=full");
		expectedLinkTemplates.put("fhir", "openmrs/ws/fhir/v1/patient{uuid}?v=full");
		expectedFeedConfiguration.setLinkTemplates(expectedLinkTemplates);
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
		AtomfeedUtils.readResourceFile(notExistingFilePath);
	}

	@Test
	public void parseJsonFileToFeedConfiguration_shouldParseSampleFeedConfigurationResource() throws AtomfeedException {
		List<FeedConfiguration> result = AtomfeedUtils.parseJsonFileToFeedConfiguration(sampleFeedConfigurationPath);
		Assert.assertEquals(expectedFeedConfiguration, result.get(0));
	}

	@Test(expected = AtomfeedException.class)
	public void parseJsonFileToFeedConfiguration_shouldThrowJsonParseException() throws AtomfeedException {
		AtomfeedUtils.parseJsonFileToFeedConfiguration(incorrectFeedConfigurationPath);
	}

	@Test
	public void parseJsonStringToFeedConfiguration_shouldParseSampleFeedConfigurationResource() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(sampleFeedConfigurationPath);
		List<FeedConfiguration> result = AtomfeedUtils.parseJsonStringToFeedConfiguration(json);
		Assert.assertEquals(expectedFeedConfiguration, result.get(0));
	}

	@Test(expected = AtomfeedException.class)
	public void parseJsonStringToFeedConfiguration_shouldThrowJsonParseException() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(incorrectFeedConfigurationPath);
		AtomfeedUtils.parseJsonStringToFeedConfiguration(json);
	}

	@Test
	public void isValidateJson_correct() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(sampleFeedConfigurationPath);
		Assert.assertTrue(AtomfeedUtils.isValidateJson(json));
	}

	@Test
	public void isValidateJson_incorrect() throws AtomfeedException {
		String json = AtomfeedUtils.readResourceFile(incorrectFeedConfigurationPath);
		Assert.assertFalse(AtomfeedUtils.isValidateJson(json));
	}

	@Test
	public void writeFeedConfigurationToJsonFile() throws AtomfeedException {
		List<FeedConfiguration> list = AtomfeedUtils.parseJsonFileToFeedConfiguration(sampleFeedConfigurationPath);
		
		final String path = "newFile.txt";
		AtomfeedUtils.writeFeedConfigurationToJsonFile(list, path);
		
		String expected = AtomfeedUtils.readResourceFile(sampleFeedConfigurationPath);
		String result = AtomfeedUtils.readResourceFile(path);
		
		Assert.assertEquals(expected, result);
	}
	
	@Test
	public void resourceFileExists_exist() throws AtomfeedException {
		Assert.assertTrue(AtomfeedUtils.resourceFileExists(sampleFeedConfigurationPath));
	}
	
	@Test
	public void resourceFileExists_notExist() throws AtomfeedException {
		Assert.assertFalse(AtomfeedUtils.resourceFileExists(notExistingFilePath));
	}
}
