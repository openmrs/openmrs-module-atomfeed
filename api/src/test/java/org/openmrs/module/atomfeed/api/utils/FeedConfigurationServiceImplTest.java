package org.openmrs.module.atomfeed.api.utils;

import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.service.impl.FeedConfigurationServiceImpl;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.model.GeneralConfiguration;

import static org.openmrs.module.atomfeed.api.utils.AtomfeedUtils.parseJsonFileToFeedConfiguration;

public class FeedConfigurationServiceImplTest {

    private static final LinkedHashMap<String, String> links = new LinkedHashMap<>();
    
    private static final String SAMPLE_FEED_CONFIGURATION_PATH = "sampleFeedConfiguration.json";

    private static final FeedConfiguration EXPECTED_FEED_CONFIGURATION = new FeedConfiguration(
            "org.openmrs.Patient",
            "Title",
            "patient",
            links,
            "custom.PatientWriter"
    );
    
    private static final String SAMPLE_FEED_CONFIGURATION_PATH2 = "defaultBeanConfiguration.json";
    private static final FeedConfiguration EXPECTED_FEED_CONFIGURATION2 = new FeedConfiguration(
            "org.openmrs.TEST2",
            "Title2",
            "patient",
            links,
            "custom"
    );

    private static final String PATIENT_CATEGORY = "patient";

    private static final String VISIT_CATEGORY = "visit";

    @Mock
    private FeedConfigurationServiceImpl feedConfigurationService;
    
    @Before
    public void setUp() {
        links.clear();
        links.put("rest", "openmrs/ws/rest/v1/patient{uuid}?v=full");
        links.put("fhir", "openmrs/ws/fhir/v1/patient{uuid}?v=full");
    }

    @Test
    public void loadLocalFeedConfiguration_shouldLoadFeedConfigurationFromArrayCorrectly() throws AtomfeedException {
        FeedConfigurationServiceImpl manager = prepareServiceWithLoadedConfigFromArray();
        Assert.assertEquals(EXPECTED_FEED_CONFIGURATION,
                manager.getFeedConfigurationByCategory(EXPECTED_FEED_CONFIGURATION.getCategory()));
        Assert.assertEquals(EXPECTED_FEED_CONFIGURATION,
                manager.getFeedConfigurationByOpenMrsClass(EXPECTED_FEED_CONFIGURATION.getOpenMrsClass()));
    }

    @Test
    public void loadLocalFeedConfiguration_shouldLoadFeedConfigurationFromStringCorrectly() throws AtomfeedException {
        FeedConfigurationServiceImpl manager = prepareServiceWithLoadedConfigFromString();

        Assert.assertEquals(EXPECTED_FEED_CONFIGURATION2,
                manager.getFeedConfigurationByCategory(EXPECTED_FEED_CONFIGURATION2.getCategory()));
        Assert.assertEquals(EXPECTED_FEED_CONFIGURATION2,
                manager.getFeedConfigurationByOpenMrsClass(EXPECTED_FEED_CONFIGURATION2.getOpenMrsClass()));
    }

    @Test
    public void loadLocalFeedConfiguration_shouldReturnNullIfFeedConfigurationDoesNotExist() throws AtomfeedException {
        FeedConfigurationServiceImpl manager = prepareServiceWithLoadedConfigFromArray();
        final String notExistingKey = "notExistingKey";
        
        Assert.assertNull(manager.getFeedConfigurationByCategory(notExistingKey));
        Assert.assertNull(manager.getFeedConfigurationByOpenMrsClass(notExistingKey));
    }

    @Test
    public void getAllFeedConfigurations_shouldReturnConfigurationsInCorrectOrder() {
        FeedConfigurationServiceImpl manager = prepareServiceWithLoadedConfigFromArray();

        Assert.assertEquals(PATIENT_CATEGORY,
                ((FeedConfiguration) manager.getAllFeedConfigurations().toArray()[0]).getCategory());
        Assert.assertEquals(VISIT_CATEGORY,
                ((FeedConfiguration) manager.getAllFeedConfigurations().toArray()[1]).getCategory());
    }

    private FeedConfigurationServiceImpl prepareServiceWithLoadedConfigFromArray() {
        GeneralConfiguration generalConfiguration = parseJsonFileToFeedConfiguration(SAMPLE_FEED_CONFIGURATION_PATH);
        FeedConfigurationServiceImpl service = new FeedConfigurationServiceImpl();
        service.saveConfig(generalConfiguration);
        return service;
    }
    
    private FeedConfigurationServiceImpl prepareServiceWithLoadedConfigFromString() {
        String str = AtomfeedUtils.readResourceFile(SAMPLE_FEED_CONFIGURATION_PATH2);
        FeedConfigurationServiceImpl service = new FeedConfigurationServiceImpl();
        service.saveConfig(str);
        return service;
    }
}
