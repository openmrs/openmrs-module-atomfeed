package org.openmrs.module.atomfeed.api.strategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Patient;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.Obs;
import org.openmrs.PersonAddress;
import org.openmrs.module.atomfeed.api.filter.FeedFilter;
import org.openmrs.module.atomfeed.api.filter.LocationFeedFilterStrategy;
import org.openmrs.module.atomfeed.api.service.XMLParseService;

import javax.xml.bind.JAXBException;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class LocationFeedFilterStrategyTest {

	private static final String BEAN_NAME = "atomfeed.locationFeedFilterStrategy";
	private static final String FILTER = "Poland%Pomorskie%Gdansk%Tczew%80-456";
	private static final String FEED_FILTER_XML;

	@InjectMocks
	private LocationFeedFilterStrategy feedFilterStrategy;

	@Mock
	private XMLParseService xmlParseService;

	@Before
	public void setUp() throws JAXBException {
		MockitoAnnotations.initMocks(this);
		when(xmlParseService.createXMLFromFeedFilter(any(FeedFilter.class))).thenReturn(FEED_FILTER_XML);
		when(xmlParseService.createFeedFilterFromXMLString(FEED_FILTER_XML)).thenReturn(new FeedFilter(BEAN_NAME, FILTER));
	}

	@Test
	public void testCreatingFeedFilterFromPatient() {
		Patient patient = createPatient();

		String createdFeedFilterXML = feedFilterStrategy.createFilterFeed(patient);

		Assert.assertEquals(FEED_FILTER_XML, createdFeedFilterXML);
	}

	@Test
	public void testCreatingFeedFilterFromEncounter() {
		Encounter encounter = createEncounter();

		String createdFeedFilterXML = feedFilterStrategy.createFilterFeed(encounter);

		Assert.assertEquals(FEED_FILTER_XML, createdFeedFilterXML);
	}

	@Test
	public void testCreatingFeedFilterFromVisit() {
		Visit visit = createVisit();

		String createdFeedFilterXML = feedFilterStrategy.createFilterFeed(visit);

		Assert.assertEquals(FEED_FILTER_XML, createdFeedFilterXML);
	}

	@Test
	public void testCreatingFeedFilterFromObs() {
		Obs obs = createObs();

		String createdFeedFilterXML = feedFilterStrategy.createFilterFeed(obs);

		Assert.assertEquals(FEED_FILTER_XML, createdFeedFilterXML);
	}

	private Patient createPatient() {
		Patient patient = new Patient();
		PersonAddress personAddress = new PersonAddress();
		personAddress.setCountry("Poland");
		personAddress.setStateProvince("Pomorskie");
		personAddress.setCountyDistrict("Gdansk");
		personAddress.setCityVillage("Tczew");
		personAddress.setPostalCode("80-456");
		personAddress.setPreferred(true);
		patient.setAddresses(new HashSet<>(Collections.singletonList(personAddress)));
		return patient;
	}

	private Encounter createEncounter() {
		Encounter encounter = new Encounter();
		encounter.setPatient(createPatient());
		return encounter;
	}

	private Visit createVisit() {
		Visit visit = new Visit();
		visit.setPatient(createPatient());
		return visit;
	}

	private Obs createObs() {
		Obs obs = new Obs();
		obs.setEncounter(createEncounter());
		return obs;
	}

	static {
		FEED_FILTER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<feedFilter>"
				+ "<beanName>" + BEAN_NAME + "</beanName>"
				+ "<filter>" + FILTER + "</filter>"
				+ "</feedFilter>";
	}

}
