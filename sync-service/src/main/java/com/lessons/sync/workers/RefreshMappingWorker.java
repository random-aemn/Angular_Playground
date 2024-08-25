package com.lessons.sync.workers;

import com.lessons.sync.interfaces.RefreshMapping;
import com.lessons.sync.models.RecordDetailsDTO;
import com.lessons.sync.services.ElasticSearchService;
import com.lessons.sync.services.ExceptionService;
import com.lessons.sync.services.RefreshService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RefreshMappingWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RefreshMappingWorker.class);

    private final RefreshMapping       refreshMapping;
    private final ElasticSearchService elasticSearchService;
    private final ExceptionService exceptionService;

    private final int TOTAL_ATTEMPTS = 3;


    public RefreshMappingWorker(RefreshMapping aRefreshMapping, ElasticSearchService aElasticSearchService, ExceptionService aExceptionService) {
        this.refreshMapping = aRefreshMapping;
        this.elasticSearchService = aElasticSearchService;
        this.exceptionService = aExceptionService;
    }



    public void run(){
        try {
            this.refreshMapping();
        }
        catch (Exception e)
        {
            logger.error("Error in run", e);

            // Save the exception info to the EXCEPTIONS table
            this.exceptionService.saveException(e);
        }
    }

    /**
     * @param aFilename holds the name of the filename to look for in the classpath
     * @return the file contents as a string
     * @throws IOException if something bad heppens
     */
    public static String readFileInClasspathToString(String aFilename) throws IOException {
        try (InputStream inputStream =  RefreshService.class.getResourceAsStream(aFilename)) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /**
     * @return the current date/time as a string (to be used on the index names)
     */
    private static String getCurrentDateTime()
    {
        DateFormat df = new SimpleDateFormat("yyyMMdd_HHmmss");
        return(df.format(new Date()));
    }

    /**
     * Create an alias frms_users that points to refreshed data
     *  1. Create a new index called              frms_users_YYYYMMDD_HH24MISS
     *  2. Query the DB and add records to        frms_users_YYYYMMDD_HH24MISS
     *  3. Have the frms_users alias point to     frms_users_YYYYMMDD_HH24MISS
     *  4. Delete any other indexes that start with frms_users_
     */
    private void refreshMapping() throws Exception {
        String indexName = this.refreshMapping.getIndexName();
        String esNewIndexName = null;
        int totalAttempts = 1;

        RecordDetailsDTO recordDetailsBefore;
        RecordDetailsDTO recordDetailsAfter;

        // Loop up to 10 times attempting to create the index
        while (totalAttempts <= TOTAL_ATTEMPTS) {
            logger.debug("Starting Refreshing of {} attempt {}", indexName, totalAttempts);

            // Construct the name of the real ES mapping
            esNewIndexName = indexName + "_" + getCurrentDateTime();

            // Get the total records from the database *before* attempting to refresh
            recordDetailsBefore = this.refreshMapping.getBeforeCountInfo();

            // Delete the index if it already exists
            deleteIndexIfExists(esNewIndexName);

            // Create the new index
            createNewIndex(indexName, esNewIndexName);

            // Add the contract spec data to this index
            this.refreshMapping.addDataToIndex(esNewIndexName);

            // Get the total records from ElasticSearch *after*
            recordDetailsAfter = this.refreshMapping.getAfterCountInfo(esNewIndexName);

            // Verify that the totals match
            logger.info("Verify Step for {}:   Before Total={}  After Total={}   Before lastUpdateDate={}  After lastUpdateDate={}",
                    indexName, recordDetailsBefore.getTotalRecords(), recordDetailsAfter.getTotalRecords(), recordDetailsBefore.getLastUpdateDate(), recordDetailsAfter.getLastUpdateDate()  );

            if(recordDetailsBefore.equals(recordDetailsAfter)) {
                // The total records before and after match!  So, break out of this loop
                logger.info("Verify Step Passed for {} after attempt {}", indexName, totalAttempts);
                break;
            }
            else {
                // The records do not match.  Wait 2 seconds and try again
                logger.info("VERIFY STEP FAILED for {} after attempt {}", indexName, totalAttempts);
                deleteIndexIfExists(esNewIndexName);
                Thread.sleep(2000);
            }

            totalAttempts++;
        }

        if (totalAttempts >= TOTAL_ATTEMPTS) {

            RuntimeException ex = new RuntimeException("Error in refreshMapping():  Failed to refresh after " + (totalAttempts-1) + " attempts. Index: "+ indexName +" Giving up.");
            exceptionService.saveException(ex);
            throw ex;
        }

        // Switch the alias
        setAliasToUseThisIndex(indexName, esNewIndexName);

        // Remove old indexes (but leave the newly-created index alone)
        deleteOldIndexesThatStartWith(indexName + "_", esNewIndexName);

        logger.debug("Successfully refreshed {} after {} attempts.\n", indexName, totalAttempts);
    }

    private void deleteIndexIfExists(String aIndexName) throws Exception {
        if (elasticSearchService.doesIndexExist(aIndexName)) {
            // Index exists.  So, delete it
            elasticSearchService.deleteIndex(aIndexName);
        }
    }

    /**
     * Get a list of indexes that have the aliasName -- e.g., reports20200103 and reports2010.....
     * Loop through all of the old indexes
     * -- If the index name is not the same as the passed-in new index name
     *      Then delete the index
     *
     * @param aIndexPrefix holds the ES index prefix (of all indexes to delete)
     * @param aIndexToNotErase holds the name of the index to not delete
     */
    private void deleteOldIndexesThatStartWith(String aIndexPrefix, String aIndexToNotErase) {

        try {
            List<String> allIndexNamesWithAliasPrefix = elasticSearchService.getIndexesThatStartWith(aIndexPrefix);

            for (String oldIndexName : allIndexNamesWithAliasPrefix) {
                if (! oldIndexName.equalsIgnoreCase(aIndexToNotErase) && oldIndexName.matches(aIndexPrefix + "[0-9].*")) {

                    // I found an index that does *NOT* have the same name as the newly-created index.  So, delete it
                    elasticSearchService.deleteIndex(oldIndexName);
                }
            }
        }
        catch (Exception e) {
            // Catch the exception and **IGNORE IT**
            logger.warn("Warning in deleteOldIndexesThatStartWith():  This exception will be ignored as the Sync-Service finished up to this point", e);
        }
    }

    /**
     * Switch the aliases
     *  1) Get the list of indeces that are used by the current alias name
     *  2) Construct the JSON to
     *      a) Remove the existing indeces
     *      b) Add the newly-created index
     *  3) Submit the JSON to make this alias change
     *
     * @param aAliasName holds the alias to create
     * @param esNewIndexName holds the index (that this alias points to)
     */
    private void setAliasToUseThisIndex(String aAliasName, String esNewIndexName) throws Exception {
        if (StringUtils.isBlank(aAliasName)) {
            throw new RuntimeException("Error in setAliasToUseThisIndex():  The passed-in alias name is blank.");
        }
        else if (StringUtils.isBlank(esNewIndexName)) {
            throw new RuntimeException("Error in setAliasToUseThisIndex():  The passed-in new index name is blank.");
        }

        List<String> currentIndexesAliasUses = elasticSearchService.getIndexesUsedByAlias(aAliasName);

        String jsonAliasChange = "{" +
                "     \"actions\": [" +
                "       {" +
                "          \"add\": {\n" +
                "             \"index\": \"" + esNewIndexName + "\"," +
                "             \"alias\": \"" + aAliasName + "\"" +
                "          }" +
                "       }";


        for (String indexName: currentIndexesAliasUses) {
            jsonAliasChange = jsonAliasChange + ",{\n" +
                    "          \"remove\": {\n" +
                    "             \"index\": \"" + indexName + "\",\n" +
                    "             \"alias\": \"" + aAliasName + "\"\n" +
                    "          }\n" +
                    "       }";
        }


        // Complete the JSON string
        jsonAliasChange = jsonAliasChange + "]}";

        // Submit the JSON request
        elasticSearchService.setAliases(jsonAliasChange);
    }



    private void createNewIndex(String aAliasName, String aNewIndexName) throws Exception {

        // Construct the path of the file  (actually stored in common-backend/src/main/resources)
        String mappingFilename = "/es/" + aAliasName + ".mapping.json";

        // Get the json mapping as a string
        String jsonMapping = readFileInClasspathToString(mappingFilename);

        // Create a new index
        elasticSearchService.createIndex(aNewIndexName, jsonMapping);
    }
}
