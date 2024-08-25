package com.lessons.models.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeycloakUserInfoDTO {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserInfoDTO.class);

    private final String certUsername;
    private final String fullName;
    private final String firstName;
    private final String lastName;
    private final String emailAddress;
    private final List<GrantedAuthority> grantedAuthorities;
    private final List<String> roleNamesGranted;

    // ------------------------- Constructor & Getters ---------------------------------------


    public KeycloakUserInfoDTO(String aCertUsername, String aFullName, String aFirstName, String aLastName,
                               String aEmailAddress, List<GrantedAuthority> aGrantedAuthorities) {
        this.certUsername = aCertUsername;
        this.fullName = aFullName;
        this.firstName = aFirstName;
        this.lastName = aLastName;
        this.emailAddress = aEmailAddress;
        this.grantedAuthorities = aGrantedAuthorities;

        this.roleNamesGranted = getListOfUpperCaseRoleNamesFromGrantedAuthorities(this.grantedAuthorities);
    }


    @SuppressWarnings("unchecked")
    public KeycloakUserInfoDTO(OidcUser aOidcUser) {
        logger.debug("KeycloakUserInfoDTO() constructor started.  aOidcUser={}", aOidcUser.toString());

        this.grantedAuthorities = new ArrayList<>();

        // Get the roles from keycloak and put them into the object called oauth2RolesGranted
        Map<String, Object> realmAccessMap = (Map<String, Object>) aOidcUser.getAttributes().get("realm_access");
        if ((realmAccessMap != null) && (realmAccessMap.size() == 1)) {
            List<String> roleNames = (List<String>) realmAccessMap.get("roles");

            if (roleNames != null) {
                for (String roleName: roleNames) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + roleName);
                    this.grantedAuthorities.add(grantedAuthority);
                }
            }
        }


        this.certUsername = aOidcUser.getName();
        this.fullName     = aOidcUser.getFullName();
        this.emailAddress = aOidcUser.getEmail();
        this.firstName    = aOidcUser.getGivenName();
        this.lastName     = aOidcUser.getFullName();

        this.roleNamesGranted = getListOfUpperCaseRoleNamesFromGrantedAuthorities(this.grantedAuthorities);

        logger.debug("KeycloakUserInfoDTO() constructor finished.  this.toString={}", this.toString() );
    }


    public String toString() {
        return String.format("certUsername='%s'  fullName='%s'  emailAddress='%s'  firstName='%s'  lastName='%s'  roleNamesGranted=%s",
                                this.certUsername, this.fullName, this.emailAddress, this.firstName, this.lastName, this.roleNamesGranted.toString() );
    }

    private List<String> getListOfUpperCaseRoleNamesFromGrantedAuthorities(List<GrantedAuthority> aListOfGrantedAuthorities) {
        if ((aListOfGrantedAuthorities == null) || (aListOfGrantedAuthorities.isEmpty()) ) {
            return null;
        }

        List<String> roleNamesInUpperCase = new ArrayList<>();
        for (GrantedAuthority grantedAuthority: aListOfGrantedAuthorities) {
            String roleName = grantedAuthority.toString().toUpperCase().replaceFirst("ROLE_", "");

            roleNamesInUpperCase.add(roleName);
        }

        return roleNamesInUpperCase;
    }

    // ----------------------------------------------- Getters -----------------------------------------------

    public String getCertUsername() {
        return certUsername;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public List<GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public List<String> getRoleNamesGranted() {
        return roleNamesGranted;
    }
}
