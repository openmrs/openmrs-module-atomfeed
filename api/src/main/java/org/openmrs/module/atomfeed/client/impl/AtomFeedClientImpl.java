package org.openmrs.module.atomfeed.client.impl;

import java.net.URI;
import java.util.HashMap;

import org.ict4h.atomfeed.client.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;

import org.ict4h.atomfeed.client.service.EventWorker;
import org.openmrs.module.atomfeed.client.AtomFeedClient;
import org.openmrs.module.atomfeed.transaction.support.AtomFeedSpringTransactionManager;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("atomfeed.AtomFeedClient")
@Scope("request")
public class AtomFeedClientImpl implements AtomFeedClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AtomFeedClient.class);

    private final AtomFeedProperties atomFeedProperties;

    private URI uri;

    private EventWorker eventWorker;

    public AtomFeedClientImpl() {
        this(null);
    }

    public AtomFeedClientImpl(EventWorker eventWorker) {
        atomFeedProperties =  new AtomFeedProperties();
        this.eventWorker = eventWorker;
    }

    @Override
    public void process() {
        LOGGER.info("{} started processing", getClass().getName());
        validateConfiguration();

        org.ict4h.atomfeed.client.service.AtomFeedClient atomFeedClient = createAtomFeedClient();
        atomFeedClient.processEvents();
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public int getReadTimeout() {
        return atomFeedProperties.getReadTimeout();
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        atomFeedProperties.setReadTimeout(readTimeout);
    }

    @Override
    public int getConnectTimeout() {
        return atomFeedProperties.getConnectTimeout();
    }

    @Override
    public void setConnectTimeout(int connectTimeout) {
        atomFeedProperties.setConnectTimeout(connectTimeout);
    }

    @Override
    public boolean isControlEventProcessing() {
        return atomFeedProperties.controlsEventProcessing();
    }

    @Override
    public void setControlsEventProcessing(boolean value) {
        atomFeedProperties.setControlsEventProcessing(value);
    }

    @Override
    public int getMaxFailedEvents() {
        return atomFeedProperties.getMaxFailedEvents();
    }

    @Override
    public void setMaxFailedEvents(int maxFailedEvents) {
        atomFeedProperties.setMaxFailedEvents(maxFailedEvents);
    }

    @Override
    public int getFailedEventMaxRetry() {
        return atomFeedProperties.getFailedEventMaxRetry();
    }

    @Override
    public void setFailedEventMaxRetry(int failedEventMaxRetry) {
        atomFeedProperties.setFailedEventMaxRetry(failedEventMaxRetry);
    }

    @Override
    public int getFailedEventsBatchProcessSize() {
        return atomFeedProperties.getFailedEventsBatchProcessSize();
    }

    @Override
    public void setFailedEventsBatchProcessSize(int failedEventsBatchProcessSize) {
        atomFeedProperties.setFailedEventsBatchProcessSize(failedEventsBatchProcessSize);
    }

    @Override
    public boolean isHandleRedirection() {
        return atomFeedProperties.isHandleRedirection();
    }

    @Override
    public void setHandleRedirection(boolean handleRedirection) {
        atomFeedProperties.setHandleRedirection(handleRedirection);
    }

    private org.ict4h.atomfeed.client.service.AtomFeedClient createAtomFeedClient() {
        HashMap<String, String> clientCookies = new HashMap<>();
        AtomFeedSpringTransactionManager atomFeedSpringTransactionManager =
                AtomfeedUtils.getAtomFeedSpringTransactionManager();
        this.eventWorker = this.eventWorker != null ? this.eventWorker : new FeedEventWorkerImpl();

        return new org.ict4h.atomfeed.client.service.AtomFeedClient(
                new AllFeeds(this.atomFeedProperties, clientCookies),
                new AllMarkersJdbcImpl(atomFeedSpringTransactionManager),
                new AllFailedEventsJdbcImpl(atomFeedSpringTransactionManager),
                this.atomFeedProperties,
                atomFeedSpringTransactionManager,
                this.uri,
                this.eventWorker
        );
    }

    private void validateConfiguration() {
        if (uri == null) {
            throw new AtomfeedException("URI is not set");
        }
    }
}
