package org.openmrs.module.atomfeed.api.converter.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.PersonName;
import org.springframework.stereotype.Component;

@Component("atomfeed.PersonNameFeedBuilder")
public class PersonNameFeedBuilder extends SubResourceFeedBuilder {

	@Override
	protected String getParentUuid(OpenmrsObject openmrsObject) {
		return ((PersonName) openmrsObject).getPerson().getUuid();
	}
}
