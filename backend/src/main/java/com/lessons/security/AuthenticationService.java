package com.lessons.security;

import com.lessons.models.authentication.InitialUserInfoDTO;
import com.lessons.models.authentication.KeycloakUserInfoDTO;
import com.lessons.security.keycloak.MyDefaultOidcUser;
import com.lessons.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Resource
    private UserService userService;

    @PostConstruct
    public void init() {
        logger.debug("init() started");
    }


    /**
     * The user has successfully authenticated against an Oauth2 (Keycloak) server
     * @param aRequest holds the HTML request object
     * @param aResponse holds the response object
     * @param aAuthentication holds the Authentication object
     */
    public void onAuthenticationSuccessForOauth2(HttpServletRequest aRequest,
                                                 HttpServletResponse aResponse,
                                                 Authentication aAuthentication) {
        logger.debug("onAuthenticationSuccessForOauth2() started");

        MyDefaultOidcUser myDefaultOidcUser = (MyDefaultOidcUser) aAuthentication.getPrincipal();

        // TODO: Mark this user as authenticated


        logger.debug("onAuthenticationSuccessForOauth2() finished");
    }




    /**
     * Generate the MyUserInfo object for a KEYCLOAK user
     *
     * @param aOidcUser holds information from keycloak
     * @return MyUserInfo object
     */
    public MyUserInfo generateUserInfoWithOauth2InfoFromKeyCloak(OidcUser aOidcUser) {
        try {
            logger.debug("generateUserInfoWithOauth2InfoFromKeyCloak() started.  aOidcUser={}", aOidcUser);

            // Get all of the keycloak info into a single object
            KeycloakUserInfoDTO keycloakUserInfoDTO = new KeycloakUserInfoDTO(aOidcUser);

            // Get information about this user from the Users table (in the database)
            // NOTE:  Use the non-synchronized version of this method in production
            InitialUserInfoDTO userInfoDTO = this.userService.getInitialUserInfoOrInsertRecord(keycloakUserInfoDTO);

            // Generate the user's granted access map from all roles granted
            Map<String, Boolean> uiControlAccessMap = userService.generateUiControlAccessMap( keycloakUserInfoDTO.getRoleNamesGranted() );

            // Create a UserInfo object with information from the database and the keycloak server
            MyUserInfo myUserInfo = new MyUserInfo()
                    .withId(                  userInfoDTO.getUserId() )
                    .withIsLocked(            userInfoDTO.getIsLocked() )
                    .withUsername(            keycloakUserInfoDTO.getCertUsername() )
                    .withUsersFullName(       keycloakUserInfoDTO.getFullName() )
                    .withGrantedAuthorities(  keycloakUserInfoDTO.getGrantedAuthorities())
                    .withUiControlAccessMap(  uiControlAccessMap);

            return myUserInfo;
        }
        catch (Exception e) {
            // Log the error and re-throw the runtime exception
            logger.error("Error in generateUserInfoWithOauth2InfoFromKeyCloak", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }



    /**
     * Generate the MyUserInfo object for a local developer user
     *
     * @return MyUserInfo that holds information about the authenticated user
     */
    public MyUserInfo generateUserInfoForDevelopment() {
        try {
            // Get the information about this user from keycloak
            KeycloakUserInfoDTO keycloakUserInfoDTO = generateKeycloakInfoForDevelopment();

            // Get information about this user from teh Users table (in the database)
            // NOTE:  Use the synchronized version of this method in development
            InitialUserInfoDTO userInfoDTO = this.userService.getInitialUserInfoOrInsertRecordSynchronized(keycloakUserInfoDTO);

            // Generate the user's granted access map from all roles granted
            Map<String, Boolean> uiControlAccessMap = userService.generateUiControlAccessMap( keycloakUserInfoDTO.getRoleNamesGranted() );

            // Create a UserInfo object with information from the database and the keycloak server
            MyUserInfo myUserInfo = new MyUserInfo()
                    .withId(                  userInfoDTO.getUserId() )
                    .withIsLocked(            userInfoDTO.getIsLocked() )
                    .withUsername(            keycloakUserInfoDTO.getCertUsername() )
                    .withUsersFullName(       keycloakUserInfoDTO.getFullName() )
                    .withGrantedAuthorities(  keycloakUserInfoDTO.getGrantedAuthorities())
                    .withUiControlAccessMap(  uiControlAccessMap);

            return myUserInfo;
        }
        catch (Exception e) {
            // Log the error and re-throw the runtime exception
            logger.error("Error in generateUserInfoForDevelopment", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace());
            throw re;
        }
    }


    /**
     * @return KeycloakUserInfoDTO that simulates what Keycloak would have returned if running on a cluster
     */
    private KeycloakUserInfoDTO generateKeycloakInfoForDevelopment() {
        // Username, full name, and roles would be passed-in from keycloak
        String keycloakCertUsername = "joe.smith";
        String keycloakFullName     = "Joe Smith";
        String keycloakFirstName    = "Joe";
        String keycloakLastName     = "Smith";
        String keycloakEmail        = "joe.smith@zztop.com";

        // Simulate keycloak has granted this user these roles
        List<GrantedAuthority> keycloakRolesGranted = new ArrayList<>();
        keycloakRolesGranted.add(new SimpleGrantedAuthority("ROLE_CVF_ADMIN"));

        KeycloakUserInfoDTO keycloakUserInfoDTO = new KeycloakUserInfoDTO(keycloakCertUsername,
                                                                          keycloakFullName,
                                                                          keycloakFirstName,
                                                                          keycloakLastName,
                                                                          keycloakEmail,
                                                                          keycloakRolesGranted);
        return keycloakUserInfoDTO;
    }


}
