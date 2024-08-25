package com.lessons.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

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

    @Value("${app.datasource.maxPoolSize:10}")
    private int maxPoolSize;

    @Value("${app.datasource.schema}")
    private String schemaName;


    @Bean
    public DataSource dataSource() {
        logger.debug("dataSource() started");
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(this.driverClassName);
        hikariConfig.setJdbcUrl(this.url);
        hikariConfig.setUsername(this.username);
        hikariConfig.setPassword(this.password);
        hikariConfig.setMaximumPoolSize(this.maxPoolSize);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("cvf_webapp_jdbc_connection_pool");
        hikariConfig.setSchema(this.schemaName);

        // Create the DataSource (and attempt to connect to it)
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        logger.debug("JDBC Connection Pool has a size of {} connections", this.maxPoolSize);

        verifyUsersTableExists(dataSource);

        logger.debug("dataSource() finished successfully.");
        return dataSource;
    }



    public void verifyUsersTableExists(DataSource aDataSource) {
        logger.debug("verifyUsersTableExists() started");
        String sql = "select id, is_locked, full_name from users where cert_username=?";

        JdbcTemplate jt = new JdbcTemplate(aDataSource);

        // If this query blows-up then there is something wrong with the database configuration
        jt.queryForRowSet(sql, "bogus");

        logger.debug("verifyUsersTableExists() finished");
    }


}