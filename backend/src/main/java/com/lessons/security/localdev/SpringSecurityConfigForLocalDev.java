package com.lessons.security.localdev;

import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@ConditionalOnProperty(name="security.mode", havingValue="localdev", matchIfMissing=false)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)         // Needed for @PreAuthorize to work
public class SpringSecurityConfigForLocalDev {

    @Resource
    private MyRequestHeaderAuthFilterLocalDev myRequestHeaderAuthFilterLocalDev;



    /**
     * Configure Spring Security for Local Dev (FAKE) authentication
     *
     * @param aHttpSecurity holds the HttpSecurity object that is configured to setup Spring Security
     * @return SecurityFilterChain object that will implement security
     * @throws Exception if something goes wrong
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity aHttpSecurity) throws Exception {

        // Running in http mode    (no SSL encryption)
        aHttpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                    .authorizeRequests()     // Filters out any URLs that are ignored.  This should be before any authorization filters
                    .requestMatchers(new AntPathRequestMatcher("/resources/**"),  new AntPathRequestMatcher("/error")).permitAll()
                    .anyRequest().authenticated()
                .and()
                    .requiresChannel().requestMatchers(new AntPathRequestMatcher("/**")).requiresInsecure()
                .and()
                    .addFilter(this.myRequestHeaderAuthFilterLocalDev)
                    .headers().frameOptions().disable()                       // By default X-Frame-Options is set to denied.
                .and()
                    .anonymous().disable()
                    .csrf().disable();


        return aHttpSecurity.build();
    }


}