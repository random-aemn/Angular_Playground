package com.common.utilities;


public class Constants {
    public static String SYSTEM_USER = "SYSTEM";

    // Audit Transaction Types
    public static final int ADD_AUDIT_TYPE = 0;
    public static final int UPDATE_AUDIT_TYPE = 1;
    public static final int DELETE_AUDIT_TYPE = 2;

    // Names of the ES mappings
    public static String APP16_USERS_ES_MAPPING = "app16_users";


    public static final int SQL_TRANSACTION_TIMEOUT_SECS = 60;   // Total seconds to wait before spring-jdbc rolls-back a SQL transaction


}
