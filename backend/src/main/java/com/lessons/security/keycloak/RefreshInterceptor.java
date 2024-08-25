package com.lessons.security.keycloak;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RefreshInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RefreshInterceptor.class);

    private final String refreshHeaderValue;


    /**
     * Constructor #1 for the RefreshInterceptor  (no url is provided)
     * @param aRedirectWaitTimeInSecs holds the number of seconds to wait before the web browser redirects to itself
     */
    public RefreshInterceptor(int aRedirectWaitTimeInSecs) {
        // Construct the value of the refresh header using the wait time in seconds
        this.refreshHeaderValue = String.format("%d", aRedirectWaitTimeInSecs);

        logger.debug("RefreshInterceptor() constructor finished:  Refresh header value is {}", this.refreshHeaderValue);
    }


    /**
     * Constructor #2 for the RefreshInterceptor:  Wait-time-in-secs and the URL are provided
     * @param aRedirectWaitTimeInSecs holds the number of seconds to wait before the web browser redirects to the url
     * @param aRedirectUrl            holds the url to redirect to
     */
    public RefreshInterceptor(int aRedirectWaitTimeInSecs, String aRedirectUrl) {
        // Construct the value of the refresh header using the wait time in seconds *AND* the url
        this.refreshHeaderValue = String.format("%d; URL='%s')", aRedirectWaitTimeInSecs, aRedirectUrl);

        logger.debug("RefreshInterceptor() constructor finished:  Refresh header value is {}", this.refreshHeaderValue);
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            // Add the "Refresh" header to each outgoing response
            // NOTE:  This causes the user's browser to redirect to the main page after N seconds
            response.setHeader("Refresh", this.refreshHeaderValue);
        }

    }
}
