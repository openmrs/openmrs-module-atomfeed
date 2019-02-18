package org.openmrs.module.atomfeed.api.converter.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.PatientIdentifier;
import org.springframework.stereotype.Component;

@Component("atomfeed.PatientIdentifierFeedBuilder")
public class PatientIdentifierFeedBuilder extends SubResourceFeedBuilder {

	@Override
	protected String getParentUuid(OpenmrsObject openmrsObject) {
		return ((PatientIdentifier) openmrsObject).getPatient().getUuid();
	}
}
