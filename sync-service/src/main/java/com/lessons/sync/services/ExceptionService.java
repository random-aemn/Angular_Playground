package com.lessons.sync.services;

import com.common.utilities.Constants;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Service
public class ExceptionService {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionService.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private DatabaseService databaseService;

    @Resource
    private VersionService versionService;


    private String appVersion;
    private final String APPLICATION_NAME = "cvf-sync-service";


    @PostConstruct
    public void init() {
        // Get the application version from the VersionService
        this.appVersion = this.versionService.getAppVersion();
    }


    /**
     * Insert a new exception record in the database
     * @param aException holds the source exception object (that was raised)
     * @return the ID of the unique exception
     */
    public Integer saveException(Exception aException) {

        // Generate a unique ID for the exception
        Integer exceptionId = databaseService.getNextId();

        // Get the stack trace as a string separated by newline characters
        String stackTraceAsString = getStackTraceAsString(aException);

        // Capture the cause of the traceback if it is known, otherwise set it to null
        String causeAsString = null;
        if (aException.getCause() != null) {
            causeAsString = aException.getCause().toString();
        }

        // Construct the SQL to insert this record into the exceptions table
        String sql = "insert into exceptions(id, user_id, cert_username, url, event_date, message, cause, app_name, app_version, stack_trace)\n" +
                     "values(:id, :user_id, :cert_username, :url, now(), :message, :cause, :app_name, :app_version, :stack_trace);\n";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id",             exceptionId);
        paramMap.put("user_id",        null);
        paramMap.put("cert_username",  Constants.SYSTEM_USER);
        paramMap.put("url",            "N/A");
        paramMap.put("message",        aException.getLocalizedMessage());
        paramMap.put("cause",          causeAsString);
        paramMap.put("stack_trace",    stackTraceAsString);
        paramMap.put("app_version",    this.appVersion);
        paramMap.put("app_name",       this.APPLICATION_NAME);

        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);

        // Add a record to the exceptions table
        np.update(sql, paramMap);

        return exceptionId;
    }


    /**
     * @param aException holds the exception object to examine
     * @return the stack trace as a string with newlines
     */
    private String getStackTraceAsString(Exception aException) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : aException.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }




}
