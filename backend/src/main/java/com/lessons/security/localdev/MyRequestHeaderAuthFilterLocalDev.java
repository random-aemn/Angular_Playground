package com.lessons.security.localdev;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.stereotype.Component;


@ConditionalOnProperty(name="security.mode", havingValue="localdev", matchIfMissing=false)
@Component
public class MyRequestHeaderAuthFilterLocalDev extends RequestHeaderAuthenticationFilter
{

    @Resource
    private MyAuthenticationManagerLocalDev myAuthenticationManagerLocalDev;


    @PostConstruct
    public void init() {
        this.setAuthenticationManager(this.myAuthenticationManagerLocalDev);
    }



    /*
     * getPreAuthenticatedPrincipal()
     *
     * This is called when a request is made, the returned object identifies the
     * user and will either be {@literal null} or a String.
     *
     * This method will throw an exception if
     * exceptionIfHeaderMissing is set to true (default) and the required header is missing.
     */
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request)
    {
        return "bogus_user";
    }

}
