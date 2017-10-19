/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.utils;

import org.junit.Assert;
import org.junit.Test;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class AtomfeedUtilsTest {
	
	@Test
	public void readResourceFile_shouldReadSampleFile() throws AtomfeedIoException {
		final String sampleResourcePath = "sampleTextFile.txt";
		final String expectedResult = "sampleContent";
		
		String result = AtomfeedUtils.readResourceFile(sampleResourcePath);
		assertEquals(result, expectedResult);
	}
	
	@Test(expected = AtomfeedIoException.class)
	public void readResourceFile_shouldThrowIoExceptionIfFileDoesNotExist() throws AtomfeedIoException {
		final String sampleFilePath = "pathToNotExistingFile";
		AtomfeedUtils.readResourceFile(sampleFilePath);
	}
	
	@Test
	public void parseJsonConfigurationResource_shouldParseSampleFeedConfigurationResource() throws AtomfeedIoException {
		final String sampleResoucePath = "sampleFeedConfiguration.json";

		final String expectedOpenMrsClass = "org.openmrs.Patient";
		final boolean expectedEnabled = false;
		final String expectedTitle = "Title";
		final String expectedFeedWriter = "custom.PatientWriter";
		final HashMap<String, String> expectedLinkTemplates = new HashMap<>();
		expectedLinkTemplates.put("rest", "openmrs/ws/rest/v1/patient{uuid}?v=full");
		expectedLinkTemplates.put("fhir", "openmrs/ws/fhir/v1/patient{uuid}?v=full");

		FeedConfiguration result = AtomfeedUtils.parseJsonConfigurationResource(sampleResoucePath);

		Assert.assertEquals(result.getOpenMrsClass(), expectedOpenMrsClass);
		Assert.assertEquals(result.isEnabled(), expectedEnabled);
		Assert.assertEquals(result.getTitle(), expectedTitle);
		Assert.assertEquals(result.getFeedWriter(), expectedFeedWriter);
		Assert.assertEquals(result.getLinkTemplates(), expectedLinkTemplates);
	}
}
