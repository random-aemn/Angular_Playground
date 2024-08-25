package com.lessons.services;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Properties;

@Service("com.lessons.services.VersionService")
public class VersionService {

    private static final Logger logger = LoggerFactory.getLogger(VersionService.class);

    private String appVersion = null;
    private String commitId = null;          // Holds the latest commit MD5 value from git

    @PostConstruct
    public void init() throws Exception{

        // Read the contents of the git.properties file to get the latest git commit
        initializeCommitInfo();
    }



    private void initializeCommitInfo() throws Exception {
        // Use a try-with-resources block to open git.properties, read it, and close it at the end of the try block
        // NOTE:  The git-commit-id-plugin plugin creates the git.properties file and this code reads that file
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("git.properties")) {

            if (inputStream == null) {
                logger.warn("Warning:  Could not find git.properties.  So, could not find commit id.");

                // set the app version to a value in dev mode, so the exception service can save the record
                this.appVersion = "dev-mode";
                return;
            }

            final Properties properties = new Properties();

            // Load the file into a properties object
            properties.load(inputStream);

            // Pull out the git commitId from the properties
            this.commitId = properties.getProperty("git.commit.id");

            // Pull out the version from the properties
            this.appVersion = properties.getProperty("git.build.version");
        }
    }



    public String getAppVersion() {
        return appVersion;
    }

    public String getCommitId() {
        return commitId;
    }
}
