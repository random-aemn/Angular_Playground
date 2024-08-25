package com.common.utilities;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Class to help make Audit entry generation easier
 */
public class AuditManager {

    private final DataSource   dataSource;
    private final Integer      transactionId;
    private final String       loggedInUserName;
    private final JdbcTemplate jt;
    private final Map<String, String> mapTableNameToCsvOfColumnNames;    // The table name and column names are all in LOWER CASE within this map

    public AuditManager(DataSource dataSource, String userName, Map<String, String> aMapTableNameToCsvOfColumnNames) {
        this.dataSource = dataSource;
        this.loggedInUserName = escapeSqlQuotes(userName);
        this.mapTableNameToCsvOfColumnNames = aMapTableNameToCsvOfColumnNames;
        this.jt = new JdbcTemplate(this.dataSource);

        this.transactionId = getNextTransactionId();
    }


    /**
     * Escape apostrophes so that the string can be used safely as a string in SQL
     * @param aString holds any string
     * @return an escaped string with single apostrophe -> double apostrophe
     */
    public String escapeSqlQuotes(String aString) {
        if (aString == null) {
            return aString;
        }

        return aString.replaceAll("'", "''");
    }

    /**
     * @return unique number to audit transactions
     */
    private Integer getNextTransactionId() {
        String sql = "select nextval('seq_transaction_ids')";
        Integer nextId = jt.queryForObject(sql, Integer.class);
        return nextId;
    }

    /**
     * Create an ADD Audit Record
     *
     * @param recordMap Map of columns and values that were added
     * @param tableName Name of the audit table to update
     */
    public void addAuditRecordForAdd(Map<String, Object> recordMap, String tableName) {

        createAuditRecord(recordMap, tableName, Constants.ADD_AUDIT_TYPE);
    }


    /**
     * Create an UPDATE Audit Record
     *
     * @param recordMap Map of columns and values that were deleted
     * @param tableName Name of the audit table to update
     */
    public void addAuditRecordForUpdate(Map<String, Object> recordMap, String tableName) {

        createAuditRecord(recordMap, tableName, Constants.UPDATE_AUDIT_TYPE);
    }


    /**
     * Create an Audit record of Type: auditType, in Table: tableName, with Columns: recordMap
     *
     * @param recordMap      Map of ColumnNames and values modified in the sql call
     * @param tableName      Name of the Audit Table to update
     * @param auditEntryType Type of audit entry
     */
    private void createAuditRecord(Map<String, Object> recordMap, String tableName, Integer auditEntryType) {

        //Make sure the previous sql command was successful
        if (recordMap.size() <= 1) {
            throw new RuntimeException("Critical error when modifying record: I expected to retrieve one database record, but did not.");
        }

        //Add additional Audit fields
        recordMap.put("username", loggedInUserName);
        recordMap.put("transaction_id", transactionId);
        recordMap.put("audit_type", auditEntryType);

        //Create Column and Values substrings
        String columnString = "";
        String valuesString = "";
        for (String key : recordMap.keySet()) {
            columnString += key + ", ";
            valuesString += ":" + key + ", ";
        }

        //Append on the timestamp
        columnString += "timestamp";
        valuesString += "now()";

        //Put it all together
        String sql = "insert into " + tableName + "(" + columnString + ") values(" + valuesString + ");";

        // Add transaction to audit record
        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(dataSource);

        int rowsAdded = np.update(sql, recordMap);

        if (rowsAdded != 1) {
            throw new RuntimeException("I expected to add one audit record. Instead, I added " + rowsAdded + " records.  This should never happen.");
        }
    }




    /**
     * Run the SQL and audit the INSERT operation (throw an exception if 1 record is not inesrted)
     *
     * @param aSqlInsert SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlInsertOne(String aSqlInsert, String aAuditTableName) {
        int totalRowsAffected = runBatchSql(aSqlInsert, aAuditTableName, Constants.ADD_AUDIT_TYPE);

        if (totalRowsAffected != 1) {
            throw new RuntimeException("Error in runSqlInsertOne():  I expected 1 record to be inserted but " + totalRowsAffected + " records were inserted instead.");
        }
    }


    /**
     * Run the SQL and audit the INSERT operation (throw an exception if 1 record is not inesrted)
     *
     * @param aSqlInsert SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlInsertOne(String aSqlInsert, Map<String, Object> aParamMap, String aAuditTableName) {
        int totalRowsAffected = runBatchSqlWithParamMap(aSqlInsert,  aParamMap, aAuditTableName,Constants.ADD_AUDIT_TYPE);

        if (totalRowsAffected != 1) {
            throw new RuntimeException("Error in runSqlInsertOne():  I expected 1 record to be inserted but " + totalRowsAffected + " records were inserted instead.");
        }
    }


    /**
     * Run the SQL and audit the insert operation (for a single insert)
     *
     * @param aSqlInsert SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlInsertMany(String aSqlInsert, String aAuditTableName) {
        runBatchSql(aSqlInsert, aAuditTableName, Constants.ADD_AUDIT_TYPE);
    }


    /**
     * Run the SQL and audit the insert operation (for a single insert)
     *
     * @param aSqlInsert SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlInsertMany(String aSqlInsert, Map<String, Object> aParamMap, String aAuditTableName) {
        runBatchSqlWithParamMap(aSqlInsert,  aParamMap, aAuditTableName,Constants.ADD_AUDIT_TYPE);
    }




    /**
     * Run the SQL and audit the UPDATE operation (throw an exception if 1 record is not updated)
     *
     * @param aSqlUpdate SQL command that has a single update
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlUpdateOne(String aSqlUpdate, String aAuditTableName) {
        int totalRowsAffected = runBatchSql(aSqlUpdate, aAuditTableName, Constants.UPDATE_AUDIT_TYPE);

        if (totalRowsAffected != 1) {
            throw new RuntimeException("Error in runSqlUpdateOne():  I expected 1 record to be updated but " + totalRowsAffected + " records were updated instead.");
        }
    }


    /**
     * Run the SQL and audit the UPDATE operation (throw an exception if 1 record is not updated)
     *
     * @param aSqlUpdate SQL command that has a single update
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlUpdateOne(String aSqlUpdate, Map<String, Object> aParamMap, String aAuditTableName) {
        int totalRowsAffected = runBatchSqlWithParamMap(aSqlUpdate,  aParamMap, aAuditTableName,Constants.UPDATE_AUDIT_TYPE);

        if (totalRowsAffected != 1) {
            throw new RuntimeException("Error in runSqlUpdateOne():  I expected 1 record to be inserted but " + totalRowsAffected + " records were inserted instead.");
        }
    }


    /**
     * Run the SQL and audit the Update operation
     *
     * @param aSqlUpdate SQL command that has a single update
     * @param aAuditTableName Name of the audit table
     */
    public int runSqlUpdateMany(String aSqlUpdate, String aAuditTableName) {
        int totalRowsUpdated = runBatchSql(aSqlUpdate, aAuditTableName, Constants.UPDATE_AUDIT_TYPE);
        return totalRowsUpdated;
    }


    /**
     * Run the SQL and audit the update operation
     *
     * @param aSqlUpdate SQL command that has a SQL update
     * @param aParamMap holds the parameter map
     * @param aAuditTableName Name of the audit table
     */
    public int runSqlUpdateMany(String aSqlUpdate, Map<String, Object> aParamMap, String aAuditTableName) {
        int totalRowsUpdated = runBatchSqlWithParamMap(aSqlUpdate,  aParamMap, aAuditTableName,Constants.UPDATE_AUDIT_TYPE);
        return totalRowsUpdated;
    }





    /**
     * Run the SQL and audit the DELETE operation  (throw an exception if 1 record is not deleted)
     *
     * @param aSqlDelete SQL command that has a single delete
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlDeleteOne(String aSqlDelete, String aAuditTableName) {
        int totalRowsAffected = runBatchSql(aSqlDelete, aAuditTableName, Constants.DELETE_AUDIT_TYPE);

        if (totalRowsAffected != 1) {
            throw new RuntimeException("Error in runSqlDeleteOne():  I expected 1 record to be deleted but " + totalRowsAffected + " records were deleted instead.");
        }
    }


    /**
     * Run the SQL and audit the DELETE operation (throw an exception if 1 record is not deleted)
     *
     * @param aSqlDelete SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public void runSqlDeleteOne(String aSqlDelete, Map<String, Object> aParamMap, String aAuditTableName) {
        int totalRowsAffected = runBatchSqlWithParamMap(aSqlDelete,  aParamMap, aAuditTableName,Constants.DELETE_AUDIT_TYPE);

        if (totalRowsAffected != 1) {
            throw new RuntimeException("Error in runSqlDeleteOne():  I expected 1 record to be deleted but " + totalRowsAffected + " records were deleted instead.");
        }
    }


    /**
     * Run the SQL and audit the DELETE operation
     *
     * @param aSqlDelete SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public int runSqlDeleteMany(String aSqlDelete, String aAuditTableName) {
        int totalRowsDeleted = runBatchSql(aSqlDelete, aAuditTableName, Constants.DELETE_AUDIT_TYPE);
        return totalRowsDeleted;
    }


    /**
     * Run the SQL and audit the DELETE operation
     *
     * @param aSqlDelete SQL command that has a single insert
     * @param aAuditTableName Name of the audit table
     */
    public int runSqlDeleteMany(String aSqlDelete, Map<String, Object> aParamMap, String aAuditTableName) {
        int totalRowsDeleted = runBatchSqlWithParamMap(aSqlDelete,  aParamMap, aAuditTableName,Constants.DELETE_AUDIT_TYPE);
        return totalRowsDeleted;
    }




    /**
     * Run the SQL and audit the batch insert/update/delete operatio
     *
     * @param aSql holds the insert/update/delete SQL call
     * @param aAuditTableName holds the audit table name
     * @param aAuditType holds the audit type  (0 for create, 1 for update, 2 for delete)
     * @return total number of rows affected
     */
    private int runBatchSql(String aSql, String aAuditTableName, Integer aAuditType) {

        // Get the regular table from (by removing the last 4 chars off of the audit tabl ename
        String regularTableName = aAuditTableName.substring(0, aAuditTableName.length() - 4);

        // Get a csv of column names for this table
        String csvRegularColumnNames = getCsvOfColumnNamesFromTableName(regularTableName);

        // Construct the SQL for the 4 additional audit columns
        String auditColumnsSql = ", now() as timestamp, "
                + "'" + loggedInUserName + "' as username, "
                + aAuditType + " as audit_type, "
                + transactionId + " as transaction_id";

        // Construct the SQL that will perform an insert *AND* add audit records (if any records are inserted)
        String sql =
                "with insert_set as ( " +
                        aSql + " returning * " +
                        ") " +
                        "insert into " + aAuditTableName + "(" + csvRegularColumnNames + ",timestamp,username,audit_type,transaction_id)" +
                        "( select " + csvRegularColumnNames + auditColumnsSql +
                        " from insert_set " +
                        ")";

        // Execute the SQL (and add audit records)
        int totalRowsAffected = jt.update(sql);

        return totalRowsAffected;
    }


    /**
     * Run the SQL and audit the batch insert/update/delete operatio
     *
     * @param aSql holds the insert/update/delete SQL call
     * @param aParamMap holds the parameter Map
     * @param aAuditTableName holds the audit table name
     * @param aAuditType holds the audit type  (0 for create, 1 for update, 2 for delete)
     * @return total number of rows affected
     */
    private int runBatchSqlWithParamMap(String aSql, Map<String, Object> aParamMap, String aAuditTableName, Integer aAuditType) {

        // Get the regular table from (by removing the last 4 chars off of the audit tabl ename
        String regularTableName = aAuditTableName.substring(0, aAuditTableName.length() - 4);

        // Get a csv of column names for this table
        String csvRegularColumnNames = getCsvOfColumnNamesFromTableName(regularTableName);

        // Construct the SQL for the 4 additional audit columns
        String auditColumnsSql = ", now() as timestamp, "
                + "'" + loggedInUserName + "' as username, "
                + aAuditType + " as audit_type, "
                + transactionId + " as transaction_id";


        // Construct the SQL that will perform an insert *AND* add audit records (if any records are inserted)
        String sql = "with insert_set as ( " +
                aSql + " returning * " +
                ") " +
                "insert into " + aAuditTableName + "(" + csvRegularColumnNames + ",timestamp,username,audit_type,transaction_id)" +
                "( select " + csvRegularColumnNames + auditColumnsSql +
                " from insert_set " +
                ")";

        // Execute the SQL (and add audit records)
        NamedParameterJdbcTemplate np = new NamedParameterJdbcTemplate(this.dataSource);
        int totalRowsAffected = np.update(sql, aParamMap);

        return totalRowsAffected;
    }


    private String getCsvOfColumnNamesFromTableName(String aTableName) {
        String tableNameInLowerCase = aTableName.toLowerCase();

        // Get the CSV of column names from the cached map of key=table value=csv-of-column-names
        String csvColumnNames = this.mapTableNameToCsvOfColumnNames.get(tableNameInLowerCase);

        if (csvColumnNames == null) {
            throw new RuntimeException("Critical Error in getCsvOfColumnNamesFromTableName():  The map of csv column names does not have an entry for this table name: " + tableNameInLowerCase);
        }

        return csvColumnNames;
    }


    public Integer getCurrentTransactionId() {
        return this.transactionId;
    }

}
