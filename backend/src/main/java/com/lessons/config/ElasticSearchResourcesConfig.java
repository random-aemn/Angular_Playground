package com.lessons.config;

import com.lessons.security.SSLContextFactory;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Realm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.File;


@Configuration
public class ElasticSearchResourcesConfig {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchResourcesConfig.class);

    @Value("${es.url:}")
    private String elasticSearchUrl;

    @Value("${es.ssl_enabled:false}")
    private boolean sslEnabled;


    // -------------- Optional SSL Parameters ------------------------
    @Value("${es.key-store:}")
    private String keyStoreFilePath;

    @Value("${es.key-store-password:}")
    private String keyStorePassword;

    @Value("${es.key-store-type:}")
    private String keyStoreType;

    @Value("${es.trust-store:}")
    private String trustStoreFilePath;

    @Value("${es.trust-store-password:}")
    private String trustStorePassword;

    @Value("${es.trust-store-type:}")
    private String turstStoreType;

    @Value("${es.authentication.principal:}")
    private String esPrincipal;

    @Value("${es.authentication.password:}")
    private String esPassword;



    @Bean
    public ElasticSearchResources elasticSearchResources() throws Exception {
        logger.debug("In elasticSearchResources()  this.sslEnabled={} sPrincipal={} esPassword={} elasticSearchUrl={}", this.sslEnabled, this.esPrincipal, this.esPassword, this.elasticSearchUrl);

        // Set the AsyncHttpClient settings
        com.ning.http.client.AsyncHttpClientConfig.Builder configBuilder = new com.ning.http.client.AsyncHttpClientConfig.Builder();
        configBuilder.setReadTimeout(-1);
        configBuilder.setAcceptAnyCertificate(true);
        configBuilder.setFollowRedirect(true);

        if (StringUtils.isNotBlank(esPrincipal)) {
            // Set the ES connection username/password
            logger.debug("In elasticSearchResources()    Setting principal and password for realm");
            Realm realm = new Realm.RealmBuilder()
                    .setPrincipal(esPrincipal)
                    .setPassword(esPassword)
                    .setUsePreemptiveAuth(true)
                    .setScheme(Realm.AuthScheme.BASIC)
                    .build();
            configBuilder.setRealm(realm);
        }

        if (sslEnabled) {
            // initialize the sslContext and store it in the configBuilder object
            SSLContext sslContext = generateSslContext();
            configBuilder.setSSLContext(sslContext);
        }

        // Create a new AsyncHttpClient object
        com.ning.http.client.AsyncHttpClientConfig config = configBuilder.build();
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(config);


        // Store the AsyncHttpClient and elasticSearc url in the ElasticSearchResources object
        // NOTE:  THe elastic search url is injected from the application.yaml
        //        The AsyncHttpClient is constructed with java code
        ElasticSearchResources elasticSearchResources = new ElasticSearchResources(this.elasticSearchUrl, asyncHttpClient);

        // Return a spring bean that holds the AsyncHttpClient and elasticsearch url
        return elasticSearchResources;
    }


    /**
     * Helper method to generate the SSL Context
     * @return a SSLContext object
     */
    private SSLContext generateSslContext() throws Exception {

        if (StringUtils.isEmpty(this.keyStorePassword)) {
            throw new RuntimeException("Critical Error Creating SSL Context:  The keystore jks password is empty.  Check this property 'es.key-store-password' in the application.yaml");
        }
        else if (StringUtils.isEmpty(this.trustStorePassword)) {
            throw new RuntimeException("Critical Error Creating SSL Context:  The truststore jks password is empty.  Check this property 'es.trust-store-password' in the application.yaml");
        }
        else if ((StringUtils.isEmpty(this.keyStoreType)) || (! this.keyStoreType.equalsIgnoreCase("JKS"))) {
            throw new RuntimeException("Critical Error Creating SSL Context:  The truststore type must be JKS.   Check this property 'es.trust-store-type'");
        }
        else if ((StringUtils.isEmpty(this.turstStoreType)) || (! this.turstStoreType.equalsIgnoreCase("JKS"))) {
            throw new RuntimeException("Critical Error Creating SSL Context:  The truststore type must be JKS.   Check this property 'es.trust-store-type'");
        }

        File keyStoreJkdFile = new File(this.keyStoreFilePath);
        if (! keyStoreJkdFile.exists()) {
            throw new RuntimeException("Critical Error:  This keystore JKS file was not found: " + this.keyStoreFilePath + "  Check this property 'es.key-store' in the application.yaml");
        }

        File trustStoreJkdFile = new File(this.trustStoreFilePath);
        if (! trustStoreJkdFile.exists()) {
            throw new RuntimeException("Critical Error:  This truststore JKS file was not found: " + this.trustStoreFilePath + "  Check this property 'es.trust-store' in the application.yaml");

        }

        logger.debug("Generating SSL Context from keystore {} and truststore {}", this.keyStoreFilePath, this.trustStoreFilePath);
        SSLContext sslContext = SSLContextFactory.makeContext(keyStoreJkdFile, this.keyStorePassword, trustStoreJkdFile, this.trustStorePassword);
        return sslContext;
    }

}
