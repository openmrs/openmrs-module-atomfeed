package org.openmrs.module.atomfeed.api.filter;

import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.Obs;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.AtomfeedConstants;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.utils.FeedFilterUtil;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;

@Component("atomfeed.locationFeedFilterStrategy")
public class LocationFeedFilterStrategy extends FeedFilterStrategy implements GenericFeedFilterStrategy {

	@Override
	public String createFilterFeed(OpenmrsObject object) {
		String filter = extractFilter(object);
		if (filter == null) {
			return null;
		}
		return createFeedFilterXML(filter);
	}

	@Override
	public boolean isFilterTagValid(String filter) {
		return FeedFilterUtil.isFilterValid(filter, getLocationFilterProperty());
	}

	@Override
	protected String getBeanName() {
		return "atomfeed.locationFeedFilterStrategy";
	}

	private String extractFilter(OpenmrsObject object) {
		if (object instanceof Patient) {
			return extractCityVillageFromPatient((Patient) object);
		} else if (object instanceof Encounter) {
			return extractCityVillageFromEncounter((Encounter) object);
		} else if (object instanceof Visit) {
			return extractCityVillageFromVisit((Visit) object);
		} else if (object instanceof Obs) {
			return extractCityVillageFromObs((Obs) object);
		} else {
			return null;
		}
	}

	private String createFeedFilterXML(String filter) {
		if (filter == null) {
			return null;
		}
		FeedFilter feedFilter = new FeedFilter(getBeanName(), filter);

		String xml;
		try {
			xml = getXmlParseService().createXMLFromFeedFilter(feedFilter);
		} catch (JAXBException e) {
			throw new AtomfeedException(e);
		}
		return xml;
	}

	private String getLocationFilterProperty() {
		return Context.getAdministrationService().getGlobalProperty(AtomfeedConstants.FilterProperties.PREFERRED_LOCATION_FILTER);
	}

	private String extractCityVillageFromPatient(Patient patient) {
		PersonAddress personAddress = patient.getPersonAddress();
		if (personAddress == null) {
			return null;
		}
		return FeedFilterUtil.createLocationFilter(personAddress);
	}

	private String extractCityVillageFromEncounter(Encounter encounter) {
		Patient patient = encounter.getPatient();
		if (patient == null) {
			return null;
		}
		return extractCityVillageFromPatient(patient);
	}

	private String extractCityVillageFromVisit(Visit visit) {
		Patient patient = visit.getPatient();
		if (patient == null) {
			return null;
		}
		return extractCityVillageFromPatient(patient);
	}

	private String extractCityVillageFromObs(Obs obs) {
		Encounter encounter = obs.getEncounter();
		if (encounter == null) {
			return null;
		}
		return extractCityVillageFromEncounter(encounter);
	}
}
