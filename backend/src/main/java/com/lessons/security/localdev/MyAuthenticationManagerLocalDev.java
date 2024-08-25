package com.lessons.security.localdev;

import com.lessons.security.AuthenticationService;
import com.lessons.security.MyUserInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;


@ConditionalOnProperty(name="security.mode", havingValue="localdev", matchIfMissing=false)
@Component
public class MyAuthenticationManagerLocalDev implements AuthenticationManager {
    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationManagerLocalDev.class);

    @Resource
    private AuthenticationService authenticationService;

    @PostConstruct
    public void init() {
        logger.debug("init() started");
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.debug("authenticate() started.   authentication={}", authentication);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            // Users is already authenticated, so do nothing
            return SecurityContextHolder.getContext().getAuthentication();
        }

        // Generate a MyUserInfo for a local dev user
        MyUserInfo myUserInfo = authenticationService.generateUserInfoForDevelopment();

        // Return an AuthenticationToken object
        PreAuthenticatedAuthenticationToken preApprovedToken = new PreAuthenticatedAuthenticationToken(myUserInfo, null, myUserInfo.getAuthorities());
        preApprovedToken.setAuthenticated(true);

        logger.debug("authenticate() finished.  preApprovedToken={}", preApprovedToken);
        return preApprovedToken;
    }

}
