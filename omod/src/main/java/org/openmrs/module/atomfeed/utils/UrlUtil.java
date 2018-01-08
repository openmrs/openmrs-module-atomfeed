package org.openmrs.module.atomfeed.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openmrs.api.context.Context;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlUtil {
    private static Logger logger = Logger.getLogger(UrlUtil.class);

    public String getRequestURL(HttpServletRequest request) {
        String requestUrl = getServiceUriFromRequest(request);
        URI uri;

        if (requestUrl == null) {
            requestUrl = getBaseUrlFromOpenMrsGlobalProperties(request);
        }

        requestUrl = requestUrl != null ? requestUrl : formUrl(request.getScheme(), request.getServerName(),
                request.getServerPort(), request.getRequestURI(), request.getQueryString());
        try {
            uri = new URI(requestUrl);

            if (null != uri && uri.getScheme().equals("https") && uri.getPort() == 80) {
                requestUrl = formUrl("https", request.getServerName(), 443,
                        request.getRequestURI(), request.getQueryString());
            }
        } catch (URISyntaxException e) {
            throw new AtomfeedException("Bad URI: ", e);
        }

        return requestUrl;
    }

    private String getBaseUrlFromOpenMrsGlobalProperties(HttpServletRequest request) {
        String restUri = Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix");
        if (StringUtils.isNotBlank(restUri)) {
            try {
                URI uri = new URI(restUri);
                return formUrl(uri.getScheme(), uri.getHost(), uri.getPort(), request.getRequestURI(), request.getQueryString());
            } catch (URISyntaxException e) {
                logger.warn("Invalid url is set in global property webservices.rest.uriPrefix");
            }
        }
        return null;
    }

    private String getServiceUriFromRequest(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null) {
            return null;
        }
        return formUrl(scheme, request.getServerName(), request.getServerPort(), request.getRequestURI(), request.getQueryString());
    }

    private String formUrl(String protocol, String hostname, int port, String path, String queryString) {
        URL url;
        try {
            URI uri = new URI(protocol, null, hostname, port, path, queryString, null);
            url = uri.toURL();
        } catch (URISyntaxException e) {
            throw new AtomfeedException("Bad URI: ", e);
        } catch (MalformedURLException e) {
            throw new AtomfeedException("An URL is Malformed", e);
        }

        return url.toString();
    }
}