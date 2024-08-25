package com.lessons.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class MyUserInfo implements UserDetails, Serializable {

    private Integer                id;                                   // Holds the user's ID in the database
    private String                 username;                           // The part of the Cn=.... that holds this user's client name (from PKI client cert)
    private String                 userFullName;
    private List<GrantedAuthority> grantedAuthorities;    // List of roles for this user (found in the database)
    private Map<String, Boolean>   uiControlAccessMap;
    private boolean                isLocked = false;
    private boolean                rolesAreConflicting = false;
    private String                 rolesAreConflictingMessage;
    private boolean                userHasHeartbeatRole = false;

    public String getPassword() {
        // Must implement this method in order to implement the UserDetails interface
        // NOTE:  There is no password as we are using PKI authentication
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }

    public boolean isAccountNonLocked() {
        // Must implement this method in order to implement the UserDetails interface
        return !this.isLocked;
    }

    public boolean isCredentialsNonExpired() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }

    public boolean isEnabled() {
        // Must implement this method in order to implement the UserDetails interface
        return true;
    }


    /**
     * @return an array of GrantedAuthority objects for this user
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Must implement this method in order to implement the UserDetails interface
        return this.grantedAuthorities;
    }


    /**
     * @return a list of Strings (for this user)
     */
    public List<String> getRoles() {
        List<String> roles = new ArrayList<>(this.grantedAuthorities.size());
        for (GrantedAuthority grantedAuthority: this.grantedAuthorities) {
            String role = grantedAuthority.toString().replaceFirst("ROLE_", "");
            if (! role.equalsIgnoreCase("USER_FOUND_IN_VALID_LIST_OF_USERS")) {
                roles.add(role);
            }
        }
        return roles;
    }


    public boolean getIsLockedOut() {
        return this.isLocked;
    }

    public String getLoggedInFullName() {
        return this.userFullName;
    }

    public Integer getId() {
        return id;
    }

    public Map<String, Boolean> getUiControlAccessMap() {
        return uiControlAccessMap;
    }


    public String toString() {
        return "username=" + this.username;
    }

    public MyUserInfo withId(Integer id) {
        this.id = id;
        return this;
    }

    public MyUserInfo withUsername(String aUsername) {
        this.username = aUsername;
        return this;
    }


    public MyUserInfo withGrantedAuthorities(List<GrantedAuthority> aGrantedAuthorities) {
        this.grantedAuthorities = aGrantedAuthorities;

        // Check if the user has the heartbeat role
        this.userHasHeartbeatRole = doesRoleListContainHeartbeatRole(aGrantedAuthorities);

        return this;
    }


    private boolean doesRoleListContainHeartbeatRole(List<GrantedAuthority> aGrantedAuthorities) {
        if (aGrantedAuthorities != null) {
            for (GrantedAuthority grantedAuthority: aGrantedAuthorities) {
                if (grantedAuthority.toString().equalsIgnoreCase("ROLE_HEARTBEAT")) {
                    // The passed-in list of granted authorities DOES contain the heartbeat role
                    return true;
                }
            }
        }

        // This passed-in list of granted authorities does NOT contain the heartbeat role
        return false;
    }


    public MyUserInfo withUiControlAccessMap(Map<String, Boolean> aAccessMap) {
        this.uiControlAccessMap = aAccessMap;
        return this;
    }

    public MyUserInfo withIsLocked(boolean aIsLocked) {
        this.isLocked = aIsLocked;
        return this;
    }


    public boolean isRolesAreConflicting() {
        return rolesAreConflicting;
    }

    public String getRolesAreConflictingMessage() {
        return rolesAreConflictingMessage;
    }


    public MyUserInfo withRolesAreConflicting(boolean aRolesAreConflicting) {
        this.rolesAreConflicting = aRolesAreConflicting;
        return this;
    }

    public MyUserInfo withRolesAreConflictingMessage(String aRolesAreConflictingMessage) {
        this.rolesAreConflictingMessage = aRolesAreConflictingMessage;
        return this;
    }

    public MyUserInfo withUsersFullName(String aUsersFullName) {
        this.userFullName = aUsersFullName;
        return this;
    }

    public boolean doesUserHaveHeartbeatRole() {
        return this.userHasHeartbeatRole;
    }
}

