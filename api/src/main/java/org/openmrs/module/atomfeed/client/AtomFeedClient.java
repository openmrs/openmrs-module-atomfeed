package org.openmrs.module.atomfeed.client;

import java.net.URI;

public interface AtomFeedClient {

    void process();

    URI getUri();

    void setUri(URI uri);

    int getReadTimeout();

    void setReadTimeout(int readTimeout);

    int getConnectTimeout();

    void setConnectTimeout(int connectTimeout);

    boolean isControlEventProcessing();

    void setControlsEventProcessing(boolean value);

    int getMaxFailedEvents();

    void setMaxFailedEvents(int maxFailedEvents);

    int getFailedEventMaxRetry();

    void setFailedEventMaxRetry(int failedEventMaxRetry);

    int getFailedEventsBatchProcessSize();

    void setFailedEventsBatchProcessSize(int failedEventsBatchProcessSize);

    boolean isHandleRedirection();

    void setHandleRedirection(boolean handleRedirection);
}