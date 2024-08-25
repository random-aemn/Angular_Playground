package com.lessons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;



@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${disable.cors}")
    private boolean disableCors;


    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/" };

    /**
     * Added to allow spring boot to find html content in the frontend dependency jar
     * @param registry holds the ResourceHandlerRegistry object
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }


    /**
     * Allow CORS requests to come from anywhere
     * -- Should be used for local debugging only
     * @param registry holds the CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (disableCors) {
            registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE");
        }
    }


}