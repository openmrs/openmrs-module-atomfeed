/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.utils;

import org.junit.Test;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;

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
	
}
