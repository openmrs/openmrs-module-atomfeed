/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.atomfeed.api.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import org.openmrs.module.atomfeed.api.exceptions.AtomfeedIoException;

;

public final class AtomfeedUtils {
	
	public static String readResourceFile(String file) throws AtomfeedIoException {
        try (InputStream in = AtomfeedUtils.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new AtomfeedIoException("Resource '" + file + "' doesn't exist");
            }
            return IOUtils.toString(in);
        } catch (IOException e) {
            throw new AtomfeedIoException(e);
        }
    }
}
