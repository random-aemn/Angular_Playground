package com.lessons.sync;

import com.lessons.sync.services.FileService;
import com.lessons.sync.services.RefreshService;
import com.lessons.sync.services.VersionService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableScheduling
public class SyncApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(SyncApplication.class);


    @Value("${run.service_mode}")
    private boolean runInServiceMode;

    @Value("${run.sync_on_startup}")
    private boolean runSyncOnStartup;

    @Value("${app.datasource.flyway-clean-on-startup:false}")
    private boolean runFlywayCleanOnStartup;

    @Value("${s3.enabled}")
    private Boolean isS3Enabled;

    @Resource
    private VersionService versionService;

    @Resource
    private RefreshService refreshService;

    @Resource
    private FileService fileService;


    /**
     * The Sync Application program starts here
     * @param args holds an array of passed-in arguments from the command-line
     */
    public static void main( String[] args ) {
        logger.debug("main() started.");

        // Start up Spring Boot but disable the banner
        SpringApplication app = new SpringApplication(SyncApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }


    /**
     * Spring calls this method on app.run()
     */
    @Override
    public void run(String... args) {

        // Log a message that shows the version and commit Id
        logger.info("Sync Service is Up / {} / {}\n", versionService.getAppVersion(), versionService.getCommitId() );

        // Delete the S3 file uploads if the database is being cleaned
        if (runFlywayCleanOnStartup && isS3Enabled){
            this.fileService.deleteAllUploadedFilesInS3();
        }

        if (runSyncOnStartup) {
            // Get a reference to the RefreshService

            // Refresh all mappings on startup
            this.refreshService.refreshAlLMappings();
        }

        if (!runInServiceMode) {
            // Not running in service mode.  So stop here.
            logger.info("Not running in service mode.  So, exiting now.");
            System.exit(0);
        }

    }


    /**
     * Run this method every day at 0500 server time
     */
    @Scheduled(cron = "${sync.refresh.cron:0 0 5 * * ?}")
    private void sync() {

        // Refresh all mappings
        this.refreshService.refreshAlLMappings();

        if (!runInServiceMode) {
            // Not running in service mode.  So stop here.
            logger.info("Not running in service mode.  So, exiting now.");
            System.exit(0);
        }

    }
}
