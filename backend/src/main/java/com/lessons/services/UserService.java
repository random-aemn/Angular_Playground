package com.lessons.services;

import com.common.utilities.AuditManager;
import com.common.utilities.Constants;
import com.lessons.models.authentication.InitialUserInfoDTO;
import com.lessons.models.authentication.KeycloakUserInfoDTO;
import com.lessons.models.authentication.UserInfoDTO;
import com.lessons.security.MyUserInfo;
import com.lessons.security.keycloak.MyDefaultOidcUser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private DatabaseService databaseService;

    @Value("${security.mode}")
    private String securityMode;

    @Value("${authenticate.set_last_login_date.is_enabled:true}")
    private boolean enableSettingLastLoginDateOnLogin;

    @PostConstruct
    public void init() {
        logger.debug("init() started.  securityMode={}", this.securityMode);
    }


    public MyUserInfo getUserInfo() {
        if (this.securityMode.equalsIgnoreCase("keycloak")) {
            return getUserInfoFromKeycloak();
        }
        else {
            return getUserInfoFromNonKeyCloak();
        }
    }

    public String getLoggedInUserName() {
        MyUserInfo userinfo = getUserInfo();

        return userinfo.getUsername();
    }

    public String getLoggedInFullName() {
        MyUserInfo userinfo = getUserInfo();

        return userinfo.getLoggedInFullName();
    }

    private MyUserInfo getUserInfoFromNonKeyCloak() {
        // Get the UserInfo object from Spring Security
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            throw new RuntimeException("Error in getUserInfoFromNonKeyCloak():  SecurityContext is null.  This should never happen.");
        }

        Authentication auth = securityContext.getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Error in getUserInfoFromNonKeyCloak():  Authentication is null.  This should never happen.");
        }

        MyUserInfo userInfo = (MyUserInfo) auth.getPrincipal();
        if (userInfo == null) {
            throw new RuntimeException("Error in getUserInfoFromNonKeyCloak():  UserInfo is null.  This should never happen.");
        }

        return userInfo;
    }


    private MyUserInfo getUserInfoFromKeycloak() {
        // Get the UserInfo object from Spring Security
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            throw new RuntimeException("Error in getUserInfoFromKeycloak():  SecurityContext is null.  This should never happen.");
        }

        Authentication auth = securityContext.getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Error in getUserInfoFromKeycloak():  Authentication is null.  This should never happen.");
        }

        MyDefaultOidcUser myDefaultOidcUser = (MyDefaultOidcUser) auth.getPrincipal();
        if (myDefaultOidcUser == null) {
            throw new RuntimeException("Error in getUserInfoFromKeycloak():  MyDefaultOidcUser is null.  This should never happen.");
        }

        MyUserInfo myUserInfo = myDefaultOidcUser.getMyUserInfo();
        if (myUserInfo == null) {
            throw new RuntimeException("Error in getUserInfoFromKeycloak():  MyUserInfo is null.  This should never happen.");
        }

        return myUserInfo;
    }

    public Integer getLoggedInUserId() {
        MyUserInfo userinfo = getUserInfo();

        return userinfo.getId();
    }


    /**
     * Run a SQL Transaction to insert the Users record and add a record to ES (for this user)
     *
     * @param aKeycloakUserInfoDTO holds all of the keycloak information about this user
     * @return new userid
     */
    private Integer insertUsersAndUserRolesRecordsInTransaction(KeycloakUserInfoDTO aKeycloakUserInfoDTO) {
        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(new DataSourceTransactionManager(dataSource));

        // This transaction will throw a TransactionTimedOutException after 60 seconds (causing the transaction to rollback)
        tt.setTimeout(Constants.SQL_TRANSACTION_TIMEOUT_SECS);


        // Tell the tt object that this method returns a String
        Integer returnedUserId = tt.execute(new TransactionCallback<Integer>() {

            @Override
            public Integer doInTransaction(TransactionStatus aStatus) {
                // All database calls in this block are part of a SQL Transaction
                try {
                    AuditManager auditManager = new AuditManager(dataSource, Constants.SYSTEM_USER, databaseService.getMapTableNameToCsvOfColumns());

                    // Get the next unique id
                    Integer newUserId = databaseService.getNextId();

                    // Construct the SQL to get these columns of data
                    String sql = """
                                    insert into users (id, cert_username, is_locked, created_date, last_login_date, last_updated_date,
                                                       first_name, last_name, full_name, email)
                                    values (:userId, :userName, false, now(), now(), now(), :firstName, :lastName, :fullName, :email )
                                 """;

                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("userId",    newUserId);
                    paramMap.put("userName",  aKeycloakUserInfoDTO.getCertUsername() );
                    paramMap.put("firstName", aKeycloakUserInfoDTO.getFirstName() );
                    paramMap.put("lastName",  aKeycloakUserInfoDTO.getLastName() );
                    paramMap.put("fullName",  aKeycloakUserInfoDTO.getFullName() );
                    paramMap.put("email"   ,  aKeycloakUserInfoDTO.getEmailAddress() );

                    // Execute SQL #1:  insert the USERS record  (and add the audit record)
                    auditManager.runSqlInsertOne(sql, paramMap, "users_aud");



                    // Generate a list of requested role IDs
                    List<Integer> requestedRoleIds = getRoleIdsForListOfRoleNames( aKeycloakUserInfoDTO.getRoleNamesGranted() );

                    // Execute SQL #2:  Insert the USERS_ROLES records (and add audit records)
                    insertUsersRolesRecords(newUserId, requestedRoleIds, auditManager);

                    return newUserId;
                } catch (Exception e) {
                    RuntimeException re = new RuntimeException(e);
                    re.setStackTrace(e.getStackTrace());
                    throw re;
                }
            }
        });

        return returnedUserId;
    }


    private void insertUsersRolesRecords(Integer aUserId, List<Integer> aRequestedRoleIds, AuditManager aAuditManager) {
        String sql;

        // Build the SQL to insert records into the USERS_ROLES table
        if ((aRequestedRoleIds != null) && (! aRequestedRoleIds.isEmpty())) {
            sql = "insert into users_roles(id, user_id, role_id) values\n";
            for (Integer roleId : aRequestedRoleIds) {
                sql = sql + String.format("(%s, %s, %s),", databaseService.getNextId(), aUserId, roleId);
            }

            // Remove the last comma from the string
            sql = sql.substring(0, sql.length() - 1);

            // Execute SQL #2:  insert the users_roles records (and add the audit records)
            aAuditManager.runSqlInsertMany(sql, "users_roles_aud");
        }
    }


    private List<Integer> getRoleIdsForListOfRoleNames(List<String> aListOfRoleNames) {
        if ((aListOfRoleNames == null) || (aListOfRoleNames.isEmpty())) {
            return new ArrayList<>();
        }

        // Construct the SQL to identify the role ids for these role names
        String sql = """
                        select id
                        from roles
                        where upper(name) IN ( :roleNames )
                        order by 1
                     """;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("roleNames", aListOfRoleNames);

        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);

        // Execute the SQL, generating a list of roleIds
        List<Integer> requestedRoleIds = np.queryForList(sql, paramMap, Integer.class);

        return requestedRoleIds;
    }


    public synchronized InitialUserInfoDTO getInitialUserInfoOrInsertRecordSynchronized(KeycloakUserInfoDTO aKeycloakUserInfoDTO) {
        return getInitialUserInfoOrInsertRecord(aKeycloakUserInfoDTO);

    }

    /**
     * Returns an InitialUserInfoDTO object holding information about the user
     * NOTE:  If there is info in the Users table, then create a new Users record
     *
     * @param aKeycloakUserInfoDTO  Holds an object that contains all of the info found in keycloak for this user
     * @return InitialUserInfoDTO with info about the user
     */
    public InitialUserInfoDTO getInitialUserInfoOrInsertRecord(KeycloakUserInfoDTO aKeycloakUserInfoDTO) {

        // Construct the SQL to get information about this users record
        String sql = "select id, is_locked " +
                     "from users " +
                     "where cert_username=?";

        Integer userId;
        boolean isLocked;

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        SqlRowSet rs = jt.queryForRowSet(sql, aKeycloakUserInfoDTO.getCertUsername());

        if (rs.next() ) {
            // This user *EXISTS* in the USERS table

            // Get the information from the USERS table for this user
            userId    = rs.getInt("id");
            isLocked  = rs.getBoolean("is_locked");

            // Update the last login date in the database
            updateUserRolesRecordsAndLastLoginDateInTransaction(userId, aKeycloakUserInfoDTO.getRoleNamesGranted() );
        }
        else {
            // This user *DOES NOT EXIST* in the USERS table.  This is the first time entering the system

            //     I N S E R T       N E W        U S E R S        R E C O R D           (and generate a userid)
            userId = insertUsersAndUserRolesRecordsInTransaction(aKeycloakUserInfoDTO);
            isLocked = false;
        }

        // Create an InitialUserInfo object that holds the userId, isLocked, and, full name
        InitialUserInfoDTO initialUserInfoDTO = new InitialUserInfoDTO(userId, isLocked);

        // Return the InitialUserInfo object
        return initialUserInfoDTO;
    }



    private void updateUserRolesRecordsAndLastLoginDateInTransaction(Integer aUserId, List<String> aRolesGrantedInKeycloak) {
        if (!enableSettingLastLoginDateOnLogin) {
            // Do not set last login date
            // NOTE:  This should only be set on local-dev-mode so that every REST call does NOT run this transaction
            return;
        }

        TransactionTemplate tt = new TransactionTemplate();
        tt.setTransactionManager(new DataSourceTransactionManager(this.dataSource));

        // This transaction will throw a TransactionTimedOutException after 60 seconds (causing the transaction to rollback)
        tt.setTimeout(Constants.SQL_TRANSACTION_TIMEOUT_SECS);

        tt.execute(new TransactionCallbackWithoutResult()
        {
            protected void doInTransactionWithoutResult(TransactionStatus aStatus)
            {
                AuditManager auditManager = new AuditManager(dataSource, Constants.SYSTEM_USER, databaseService.getMapTableNameToCsvOfColumns());

                // Construct the SQL to get these columns of data
                String sql = "update users set last_login_date = now() where id = :userId ";

                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("userId", aUserId);

                // Execute SQL #1:  Update the USERS.last_login_date record AND add an audit record            (throws an exception if one record is NOT updated)
                auditManager.runSqlUpdateOne(sql, paramMap, "users_aud");

                // Execute SQL #2:  Update the USERS_ROLES records (if they do not match what was returned from keycloak)
                updateUserRoleRecordsIfNeeded(aUserId, aRolesGrantedInKeycloak, auditManager);

                // Commit the transaction if I get to the end of this method
            }
        });


    }

    /**
     *  1. Get the list of assigned role ids that match the passed-in role names
     *  2. Get the list of role_id from the users_roles table for the passed-in aUserid
     *  3. If they match, then do nothing
     *  4. If they do not match, then delete and insert new users_roles records for this user
     *
     * @param aUserId
     * @param aRolesGrantedInKeycloak
     * @param auditManager
     */
    private void updateUserRoleRecordsIfNeeded(Integer aUserId, List<String> aRolesGrantedInKeycloak, AuditManager auditManager) {
        List<Integer> listOfRoleIdsThatMatchTheRoleNamesInKeycloak = getRoleIdsForListOfRoleNames( aRolesGrantedInKeycloak );

        List<Integer> actualAssignedRoleIdsForUser = getRoleIdsGrantedToUserid(aUserId);

        boolean areListsIdentical = CollectionUtils.isEqualCollection(listOfRoleIdsThatMatchTheRoleNamesInKeycloak, actualAssignedRoleIdsForUser);
        if (!areListsIdentical) {
            // There are differences between the roles granted in keycloak and the roles granted in the USERS_ROLES table

            String sql = "DELETE from users_roles where user_id= :userid ";

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userid", aUserId);

            // Execute SQL to delete the existing USERS_ROLES records  (and add audit records)
            auditManager.runSqlDeleteMany(sql, paramMap, "users_roles_aud");

            // Execute SQL #2 to insert the USERS_ROLES records  (and add audit records)
            insertUsersRolesRecords(aUserId, listOfRoleIdsThatMatchTheRoleNamesInKeycloak, auditManager);
        }

    }


    private List<Integer> getRoleIdsGrantedToUserid(Integer aUserId) {
        String sql = """
                        select role_id 
                        from users_roles
                        where user_id=?
                        order by 1
                    """;

        JdbcTemplate jt = new JdbcTemplate(this.dataSource);

        List<Integer> grantedRoleIds = jt.queryForList(sql, Integer.class, aUserId);

        return grantedRoleIds;
    }




    public Map<String, Boolean> generateUiControlAccessMap(List<String> aListOfRoleNamesGranted) {

        // Construct the SQL to get list of all ui-controls that are granted to this user's passed-in list of roles
        String sql = "select distinct ui.name " +
                     "from uicontrols ui " +
                     "join roles r on (r.name IN ( :roleList )) " +
                     "join roles_uicontrols ru ON (r.id=ru.role_id) AND (ui.id=ru.uicontrol_id)";

        // Create a parameter map (required to use bind variables with postgres IN clause)
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("roleList", aListOfRoleNamesGranted);

        // Execute the query and generate a map of allowed page routes
        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);
        SqlRowSet rs = np.queryForRowSet(sql, paramMap);

        // Create the map
        Map<String, Boolean> accessMap = new HashMap<>();

        // Loop through the SqlRowSet, putting the results into a map
        while (rs.next()) {
            accessMap.put(rs.getString("name"), true);
        }

        // Return the map
        return accessMap;
    }


    public UserInfoDTO getUserInfoForFrontend() {
        MyUserInfo userInfo = getUserInfo();

        String  loggedInUsername           = userInfo.getUsername();
        String  loggedInFullName           = userInfo.getLoggedInFullName();
        Integer loggedInUserId             = userInfo.getId();
        Map<String, Boolean> pageRoutesMap = userInfo.getUiControlAccessMap();

        // Create the UserInfoDTO object
        UserInfoDTO userInfoDTO = new UserInfoDTO(loggedInUserId, loggedInUsername, loggedInFullName,pageRoutesMap, "");

        return userInfoDTO;
    }

}


