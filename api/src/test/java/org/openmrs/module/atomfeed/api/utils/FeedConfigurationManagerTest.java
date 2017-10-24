package org.openmrs.module.atomfeed.api.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.atomfeed.api.FeedConfigurationManager;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;

import java.util.HashMap;
import java.util.List;

public class FeedConfigurationManagerTest {

    private static final String sampleFeedConfigurationPath = "sampleFeedConfiguration.json";
    private static final String sampleFeedConfigurationPath2 = "sampleFeedConfiguration2.json";
    private static final HashMap<String, String> links = new HashMap<>();;

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
                false,
                "Title",
                links,
                "custom.PatientWriter"
        );

        List<FeedConfiguration> array = AtomfeedUtils.parseJsonFileToFeedConfiguration(sampleFeedConfigurationPath);
        FeedConfigurationManager manager = new FeedConfigurationManager();
        manager.loadLocalFeedConfiguration(array);
        Assert.assertEquals(expectedFeedConfiguration, manager.getFeedConfigurationByTitle("Title"));
    }

    @Test
    public void loadLocalFeedConfiguration_shouldLoadFeedConfigurationFromStringCorrectly() throws AtomfeedException {
        final FeedConfiguration expectedFeedConfiguration = new FeedConfiguration(
                "org.openmrs.TEST2",
                true,
                "Title2",
                links,
                "custom"
        );

        String json = AtomfeedUtils.readResourceFile(sampleFeedConfigurationPath2);
        FeedConfigurationManager manager = new FeedConfigurationManager();
        manager.loadLocalFeedConfiguration(json);
        Assert.assertEquals(expectedFeedConfiguration, manager.getFeedConfigurationByTitle("Title2"));
    }
}