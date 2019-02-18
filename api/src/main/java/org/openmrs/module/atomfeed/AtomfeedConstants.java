package org.openmrs.module.atomfeed;

public final class AtomfeedConstants {

    public static final String ATOMFEED_PATH_TO_DEFAULT_CONFIGURATION = "defaultAtomfeedConfig.json";

    public static final String ATOMFEED_PATH_TO_CUSTOM_CONFIGURATION = "customAtomfeedConfig.json";
    
    public static final String DEFAULT_FEED_WRITER_1_9 = "atomfeed.DefaultFeedWriter1_9";

    public static final String DEFAULT_FEED_WRITER_2_0 = "atomfeed.DefaultFeedWriter2_0";

    public static final int ZERO = 0;

    public static final String DEFAULT_FEED_BUILDER = "atomfeed.DefaultFeedBuilder";

    public static final String FEED_BUILDER_BEAN_ID_PREFIX = "atomfeed.";

    public static final String FEED_BUILDER_BEAN_ID_SUFIX = "FeedBuilder";

    public static final class FilterProperties {
        public static final String PREFERRED_LOCATION_FILTER = "atomfeed.filter.location";
    }
}
