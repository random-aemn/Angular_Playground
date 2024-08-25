package com.lessons.sync.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${app.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${app.datasource.url}")
    private String url;

    @Value("${app.datasource.username}")
    private String username;

    @Value("${app.datasource.password}")
    private String password;

    @Value("${app.datasource.maxPoolSize:20}")
    private int maxPoolSize;

    @Value("${app.datasource.schema}")
    private String schemaName;

    @Value("${app.datasource.flyway-migrate-on-startup:true}")
    private boolean runFlywayMigrateOnStartup;

    @Value("${app.datasource.flyway-clean-on-startup:false}")
    private boolean runFlywayCleanOnStartup;



    @Bean
    public DataSource dataSource() {
        logger.debug("database username={}  ", this.username);

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(this.driverClassName);
        hikariConfig.setJdbcUrl(this.url);
        hikariConfig.setUsername(this.username);
        hikariConfig.setPassword(this.password);
        hikariConfig.setMaximumPoolSize(this.maxPoolSize);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("cvf_sync_jdbc_connection_pool");
        hikariConfig.setSchema(this.schemaName);

        // Create the DataSource
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        if (runFlywayMigrateOnStartup) {
            // Initialize the flyway object by setting the data source and schema name
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(schemaName)
                    .cleanDisabled(false)
                    .load();

            if (runFlywayCleanOnStartup) {
                logger.debug("Running flyway clean on startup.");
                flyway.clean();
            }

            // Use the flyway object to do a migrate on webapp startup
            flyway.migrate();
        }
        logger.debug("Flyway Migrate Finished.");

        return dataSource;
    }

}
