package org.openmrs.module.atomfeed.api.utils;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.impl.FeedConfigurationServiceImpl;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

public class FeedConfigurationServiceImplTest {

    private static final String sampleFeedConfigurationPath = "sampleFeedConfiguration.json";
    private static final String sampleFeedConfigurationPath2 = "sampleFeedConfiguration2.json";
    private static final HashMap<String, String> links = new HashMap<>();

    @Before
    public void setUp() {
        links.clear();
        links.put("rest", "openmrs/ws/rest/v1/patient{uuid}?v=full");
        links.put("fhir", "openmrs/ws/fhir/v1/patient{uuid}?v=full");
    }

    @Test
    public void loadLocalFeedConfiguration_shouldLoadFeedConfigurationFromArrayCorrectly() throws AtomfeedException {
        final FeedConfiguration expectedFeedConfiguration = new FeedConfiguration(
                "org.openmrs.Patient",
                "Title",
                "patient",
                links,
                "custom.PatientWriter"
        );

        List<FeedConfiguration> array = AtomfeedUtils.parseJsonFileToFeedConfiguration(sampleFeedConfigurationPath);
        FeedConfigurationServiceImpl manager = new FeedConfigurationServiceImpl();
        manager.saveConfig(array);
        Assert.assertEquals(expectedFeedConfiguration, manager.getFeedConfigurationByTitle("Title"));
    }

    @Test
    public void loadLocalFeedConfiguration_shouldLoadFeedConfigurationFromStringCorrectly() throws AtomfeedException {
        final FeedConfiguration expectedFeedConfiguration = new FeedConfiguration(
                "org.openmrs.TEST2",
                "Title2",
                "patient",
                links,
                "custom"
        );

        String json = AtomfeedUtils.readResourceFile(sampleFeedConfigurationPath2);
        FeedConfigurationServiceImpl manager = new FeedConfigurationServiceImpl();
        manager.saveConfig(json);
        Assert.assertEquals(expectedFeedConfiguration, manager.getFeedConfigurationByTitle("Title2"));
    }
}