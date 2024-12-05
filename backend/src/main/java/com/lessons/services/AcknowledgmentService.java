package com.lessons.services;

import com.common.utilities.AuditManager;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AcknowledgmentService {

    @Resource
    private UserService userService;

    @Resource
    private DataSource dataSource;

    @Resource
    private DatabaseService databaseService;



   public void setAcknowledgementDate() {

       Map<String, Object> paramMap = new HashMap<>();
       paramMap.put("userId", userService.getLoggedInUserId());

       AuditManager auditManager = new AuditManager(dataSource, userService.getLoggedInUserName(), databaseService.getMapTableNameToCsvOfColumns());

//               Bind variable MUST start with a colon in the SQL
       String sql = """
               update users
               set acknowledgement_date = now()
               where id = :userId
                              
               """;
//       use the auditManager to run the SQL and add an audit record
       auditManager.runSqlUpdateOne(sql, paramMap, "users_aud");

    }



}
