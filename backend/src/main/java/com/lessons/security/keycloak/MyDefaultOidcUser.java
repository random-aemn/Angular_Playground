package com.lessons.security.keycloak;

import com.lessons.security.MyUserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Collection;

public class MyDefaultOidcUser extends DefaultOidcUser {

    private MyUserInfo myUserInfo;

    public MyDefaultOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken) {
        super(authorities, idToken);
    }

    public MyDefaultOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, String nameAttributeKey) {
        super(authorities, idToken, nameAttributeKey);
    }

    public MyDefaultOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
        super(authorities, idToken, userInfo);
    }

    public MyDefaultOidcUser(Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo, String nameAttributeKey) {
        super(authorities, idToken, userInfo, nameAttributeKey);
    }

    public MyDefaultOidcUser(MyUserInfo aUserInfo, Collection<? extends GrantedAuthority> authorities, OidcIdToken idToken, OidcUserInfo userInfo) {
        super(authorities, idToken, userInfo);
        this.myUserInfo = aUserInfo;
    }


    public MyUserInfo getMyUserInfo() {
        return this.myUserInfo;
    }
}
