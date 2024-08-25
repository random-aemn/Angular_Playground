package com.lessons.security.keycloak;

import com.lessons.security.AuthenticationService;
import com.lessons.security.MyUserInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;



@Configuration
@ConditionalOnProperty(name="security.mode", havingValue="keycloak", matchIfMissing=false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)         // Needed for @PreAuthorize to work
public class SpringSecurityConfigForKeycloak  {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfigForKeycloak.class);


    @Resource
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Resource
    private AuthenticationService authenticationService;



    /**
     * Configure Spring Security for Keycloak or OAUTH2 Authentication
     *
     * @param aHttpSecurity holds the HttpSecurity object that is configured to setup Spring Security
     * @return SecurityFilterChain object that will implement security
     * @throws Exception if something goes wrong
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity aHttpSecurity) throws Exception {
        logger.debug("securityFilterChainF() [KEYCLOAK] started.");


        // This is the parameter name passed-in when using the request-cache
        // -- Set to "continue" and the user will see ?continue=      on the URL when connecting for the 1st time
        // -- Set to null       and the user will NOT see the ?stuff= on the URL when connecting for the 1st time
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);


        // Running in https mode with OAuth2 authentication
        aHttpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                    .authorizeRequests()
                    .requestMatchers(new AntPathRequestMatcher("/assets/**"),
                                     new AntPathRequestMatcher( "**.js"),
                                     new AntPathRequestMatcher("**.css"),
                                     new AntPathRequestMatcher("/error")).permitAll()
                    .anyRequest()
                    .authenticated()
                .and()
                    .requiresChannel().anyRequest().requiresSecure()
                .and()
                .oauth2Login()

                    /*
                     * Update the Principal object stored in the session
                     * Replace the defaultOidcUser with MyDefaultOidcUser object   (that holds the additional MyUserInfo)
                     */
                    .userInfoEndpoint(userInfo -> userInfo
                            .oidcUserService(this.oidcUserService()))


                    /*
                     * The oAuth2LoginSuccessHandler success handler class will do 2 things:
                     *   1) Update the database to indicate that the user successfully authenticated
                     *   2) Redirect the user from keycloak to the user's ORIGINAL requested url (because it extends SavedRequestAwareAuthenticationSuccessHandler)
                     */
                    .successHandler(this.oAuth2LoginSuccessHandler)

                .and()
                    .csrf().disable()

                .requestCache( (aCache) -> aCache.requestCache(requestCache) );


        return aHttpSecurity.build();
    }


    /**
     * Generate a new principal pboject that holds MyUserInfo *AND* the original token info from Keycloak
     *
     * @return MyDefaultOidcUser object
     */
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (OidcUserRequest aOidcUserRequest) -> {
            // Get the OidcUser object (that holds information from Keycloak)
            OidcUser oidcUser = delegate.loadUser(aOidcUserRequest);

            // Get the MyUserInfo object (that holds additional info from our database)
            MyUserInfo myUserInfo = this.authenticationService.generateUserInfoWithOauth2InfoFromKeyCloak(oidcUser);

            // Create a new Principal object that holds MyUserInfo *AND* the original token information from keycloak
            MyDefaultOidcUser myDefaultOidcUser = new MyDefaultOidcUser(myUserInfo, myUserInfo.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo());
            return myDefaultOidcUser;
        };
    }



    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        // ForwardedHeaderFilter handles non-standard headers
        //   X-Forwarded-Host, X-Forwarded-Port, X-Forwarded-Proto, X-Forwarded-Ssl, and X-Forwarded-Prefix.
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();

        FilterRegistrationBean<ForwardedHeaderFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}