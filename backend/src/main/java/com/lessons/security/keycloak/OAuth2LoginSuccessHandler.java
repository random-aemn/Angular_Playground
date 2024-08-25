package com.lessons.security.keycloak;

import com.lessons.security.AuthenticationService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * Create a success handler class that extends SavedRequestAwareAuthenticationSuccessHandler
 * -- This ensures that upon successful authentication, the user is taken to the user's ORIGINAL url
 */
@Component
@ConditionalOnProperty(name="security.mode", havingValue="keycloak", matchIfMissing=false)
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

    @Resource
    private AuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        logger.debug("onAuthenticationSuccess() started");

        // The user successfully authenticated against the OAUTH2 Server

        // Create the UserInfo object and set it as the principal
        //   1) Replace the OAuth2 Principal object with our own UserInfo object
        //   2) Update the database to indicate that the user successfully logged-in
        this.authenticationService.onAuthenticationSuccessForOauth2(request, response, authentication);


        // Proceed to the user's original URL
        super.onAuthenticationSuccess(request, response, authentication);
    }


}
