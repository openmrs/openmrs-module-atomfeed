package org.openmrs.module.atomfeed.api.converter.impl;

import org.openmrs.OpenmrsObject;
import org.openmrs.PersonAttribute;
import org.springframework.stereotype.Component;

@Component("atomfeed.PersonAttributeFeedBuilder")
public class PersonAttributeFeedBuilder extends SubResourceFeedBuilder {

	@Override
	protected String getParentUuid(OpenmrsObject openmrsObject) {
		return ((PersonAttribute) openmrsObject).getPerson().getUuid();
	}	
}
