package org.openmrs.module.atomfeed.api.converter.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.PersonAddress;
import org.springframework.stereotype.Component;

@Component("atomfeed.PersonAddressFeedBuilder")
public class PersonAddressFeedBuilder extends SubResourceFeedBuilder {

	@Override
	protected String getParentUuid(OpenmrsObject openmrsObject) {
		return ((PersonAddress) openmrsObject).getPerson().getUuid();
	}
}
