package com.lessons.sync.services;

import com.common.utilities.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessons.sync.config.ElasticSearchResources;
import com.lessons.sync.models.ElasticSearchDtoGetCount;
import com.lessons.sync.models.ErrorsDTO;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("com.lessons.sync.services.ElasticSearchService")
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    @Resource
    private ElasticSearchResources elasticSearchResources;


    private String elasticSearchUrl;
    private AsyncHttpClient asyncHttpClient;
    private final int ES_REQUEST_TIMEOUT_IN_MILLISECS = 30000;   // All ES requests timeout after 30 seconds
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws Exception {
        if (this.elasticSearchResources == null) {
            throw new RuntimeException("Error in init():  The elasticSearchResources object is null");
        }

        logger.debug("init() started.  ES url={}", elasticSearchResources.getElasticSearchUrl());

        this.objectMapper = new ObjectMapper();

        // In order to make outgoing calls to ElasticSearch you need 2 things:
        //  1) The elastic search url
        //  2) The initialized AsyncHttpClient object
        this.elasticSearchUrl = elasticSearchResources.getElasticSearchUrl();
        this.asyncHttpClient = elasticSearchResources.getAsyncHttpClient();

        // The sync service needs
        destroyIndexesWithSameNameAsAliases();

        logger.debug("init() finished successfully");
    }



    /**
     * Helper method to verify that expected ES aliases exist
     * @throws Exception if one of the ES aliases does not eixst
     */
    public void verifyElasticSearchAliasesExist() throws Exception {
        logger.debug("verifyMappingsExist() started.");

        // Create a list of all of aliases
        List<String> aliasesThatShouldExist = Arrays.asList(Constants.APP16_USERS_ES_MAPPING);

        // Loop through all entries in the map and create the indexes.
        for (String aliasName: aliasesThatShouldExist ) {

            if (! doesAliasExist(aliasName)) {
                // One of the ES mapping **ALIASES** not found.  So, throw an error

                throw new RuntimeException("The ES Alias does not exist: " + aliasName + "   Please run the Sync-Service to initialize the ES mappings");
            }
        }

        logger.debug("verifyMappingsExist() finished successfully.");
    }



    /**
     * Destroy any indexes that have the SAME name as the alias (so the sync service can do its jobs)
     *
     * @throws Exception if something wrong happens
     */
    private void destroyIndexesWithSameNameAsAliases() throws Exception {
        List<String> allAliasNames = Arrays.asList(Constants.APP16_USERS_ES_MAPPING);

        for (String aliasName : allAliasNames) {
            if (doesIndexExist(aliasName)) {
                // There is an index with the SAME name as an alias.  So, delete the index
                deleteIndex(aliasName);
            }
        }
    }


    /**
     * Create a new ES Index
     *
     * @param aIndexName   holds the name of the new index to create
     * @param aJsonMapping holds the mapping of this index
     * @throws Exception if something bad happens
     */
    public void createIndex(String aIndexName, String aJsonMapping) throws Exception {
        logger.debug("createIndex() started.  aIndexName={}", aIndexName);

        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        String url = this.elasticSearchUrl + "/" + aIndexName;
        logger.debug("Going to this url:  {}", url);

        // Make a synchronous POST call to ElasticSearch to create this an index
        Response response = this.asyncHttpClient.preparePut(url)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonMapping)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in createIndex:  ES url is " + url + "\nES returned a status code of " +
                    response.getStatusCode() + " with an error of: " +
                    response.getResponseBody());
        }

        logger.info("Successfully created this ES index: {}", aIndexName);
    }

    /**
     * Do a bulk update within ES
     *
     * @param aBulkUpdateJson Holds the JSON bulk string
     * @param aWaitForRefresh Holds TRUE if we will wait for a refresh
     * @throws Exception if something bad happens
     */
    public void bulkUpdate(String aBulkUpdateJson, boolean aWaitForRefresh) throws
            Exception {
        if (StringUtils.isEmpty(aBulkUpdateJson)) {
            throw new RuntimeException("The passed-in JSON is null or empty.");
        }

        String url = this.elasticSearchUrl + "/_bulk";
        if (aWaitForRefresh) {
            url = url + "?refresh=wait_for";
        }

        // Make a synchronous POST call to do a bulk-index request
        Response response = this.asyncHttpClient.preparePost(url)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aBulkUpdateJson)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in bulkUpdate:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        // Examine the JSON response to see if the errors="true" flag was set
        //  1. Convert the response JSON string into an errorsDto object
        //  2. Look at the errorsDTO object.isErrors() method
        // 	NOTE:  This is substantially faster as the ErrorDTO tells Jackson to ignore the other fields
        String jsonResponse = response.getResponseBody();
        ErrorsDTO errorsDTO = objectMapper.readValue(jsonResponse, ErrorsDTO.class);

        if (errorsDTO.isErrors()) {
            // ElasticSearch returned a 200 response, but the bulk update failed
            logger.error("Error in bulkUpdate:  ES returned a status code of {} with an error of {}", response.getStatusCode(), response.getResponseBody());
            throw new RuntimeException("Error in bulkUpdate:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

    }

    /**
     * Delete the index from ElasticSearch
     *
     * @param aIndexName holds the index name (or alias name)
     */
    public void deleteIndex(String aIndexName) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        // Make a synchronous POST call to delete this ES Index
        Response response = this.asyncHttpClient.prepareDelete(this.elasticSearchUrl + "/" +
                aIndexName)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in deleteIndex:  ES returned a status code of " +
                    response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        logger.debug("Successfully deleted this index: {}", aIndexName);
    }


    /**
     * Helper method to determine if the passed-in ES mapping name or alias exists
     *
     * @param aAliasName holds the ES alias name
     * @return TRUE if the passed-in index or alias exists
     */
    public boolean doesAliasExist(String aAliasName) throws Exception {

        if (StringUtils.isEmpty(aAliasName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        // Make a synchronous GET call to get a list of all index names
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/aliases")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "text/plain")
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Critical error in doesAliasExist():  ElasticSearch returned a response status code of " +
                    response.getStatusCode() + ".  Response message is " + response.getResponseBody());
        }

        // Loop through the lines of data -- looking for the passed-in index name
        String linesOfInfo = response.getResponseBody();
        if (StringUtils.isNotEmpty(linesOfInfo)) {
            String[] lines = linesOfInfo.split("\n");

            for (String line : lines) {
                String[] indexParts = line.split(" ");
                if (indexParts.length >= 3) {
                    String actualAliasName = indexParts[0];

                    if (actualAliasName.equalsIgnoreCase(aAliasName)) {
                        logger.debug("doesAliasExist() returns true for {}", aAliasName);
                        return true;
                    }
                }
            }
        }

        // The index name was not found in the system.  So, return false
        logger.debug("doesAliasExist() returns false for {}", aAliasName);
        return false;

    }  // end of doesAliasExist()


    /**
     * Helper method to determine if the passed-in ES mapping name or alias exists
     *
     * @param aIndexName holds the ES mapping name or alias
     * @return TRUE if the passed-in index or alias exists
     */
    public boolean doesIndexExist(String aIndexName) throws Exception {

        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        // Make a synchronous GET call to get a list of all index names
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/indices")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "text/plain")
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Critical error in doesIndexExist():  ElasticSearch returned a response status code of " +
                    response.getStatusCode() + ".  Response message is " + response.getResponseBody());
        }

        // Loop through the lines of data -- looking for the passed-in index name
        String linesOfInfo = response.getResponseBody();
        if (StringUtils.isNotEmpty(linesOfInfo)) {
            String[] lines = linesOfInfo.split("\n");

            for (String line : lines) {
                String[] indexParts = line.split(" ");
                if (indexParts.length >= 3) {
                    String actualIndexName = indexParts[2];

                    if (actualIndexName.equalsIgnoreCase(aIndexName)) {
                        logger.debug("doesIndexExist() returns true for {}", aIndexName);
                        return true;
                    }
                }
            }
        }

        // The index name was not found in the system.  So, return false
        return false;

    }  // end of doesIndexExist()


    /**
     * Return a list of ES index names with the passed-in prefix
     * NOTE:  The passed-in prefix can be null or empty
     *
     * @param aPrefix holds a string of characters used to match index names
     * @return List of strings that hold matching ES index names
     */
    public List<String> getIndexesThatStartWith(String aPrefix) throws Exception {

        // Make a synchronous POST call to ElasticSearch to get all indicies (if any) that are used by this alias
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/indices/" + aPrefix + "*")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in getIndexesUsedByAlias:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        // Convert the ES response into a list of java maps
        String esJsonResponse = response.getResponseBody();
        List<Map<String, Object>> listOfMaps = this.objectMapper.readValue(esJsonResponse, new TypeReference<List<Map<String, Object>>>() {
        });


        ArrayList<String> indexNamesMatchingPrefix = new ArrayList<>();

        // Loop through the list of maps, pulling-out the name from the map
        for (Map<String, Object> indexMapDetails : listOfMaps) {
            String indexName = (String) indexMapDetails.get("index");
            if (StringUtils.isNotEmpty(indexName)) {
                indexNamesMatchingPrefix.add(indexName);
            }
        }

        // Return an unmodifiable list
        return Collections.unmodifiableList(indexNamesMatchingPrefix);
    }


    /**
     * Submit an alias change to ElasticSearch
     *
     * @param aJsonBody holds the JSON for a list of actions to add/delete aliases
     * @throws Exception if something wrong happens
     */
    public void setAliases(String aJsonBody) throws Exception {
        if (StringUtils.isEmpty(aJsonBody)) {
            throw new RuntimeException("The passed-in JSON body is null or empty.");
        }

        // Make a synchronous POST call to ElasticSearch to set/unset these aliases
        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/_aliases")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonBody)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in setAliases:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

    }


    /**
     * Query ES for a list of indicies that are currently used by this index
     *
     * @param aAliasName holds the alias name to query
     * @return a list of ES indeces that are used by this alias (empty list if none are found)
     * @throws Exception if something wrong happens
     */
    public List<String> getIndexesUsedByAlias(String aAliasName) throws Exception {
        if (StringUtils.isEmpty(aAliasName)) {
            throw new RuntimeException("The passed-in alias name is null or empty.");
        }

        // Make a synchronous POST call to ElasticSearch to get all indicies (if any) that are used by this alias
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/_cat/aliases/" + aAliasName)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in getIndexesUsedByAlias:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        // Convert the ES response into a list of java maps
        String esJsonResponse = response.getResponseBody();
        List<Map<String, Object>> listOfMaps = this.objectMapper.readValue(esJsonResponse, new TypeReference<List<Map<String, Object>>>() {
        });


        ArrayList<String> indexNamesUsingAlias = new ArrayList<>();

        // Loop through the list of maps, pulling-out the name from the map
        for (Map<String, Object> indexMapDetails : listOfMaps) {
            String indexName = (String) indexMapDetails.get("index");
            if (StringUtils.isNotEmpty(indexName)) {
                indexNamesUsingAlias.add(indexName);
            }
        }

        // Return an unmodifiable list
        return Collections.unmodifiableList(indexNamesUsingAlias);
    }


    public Map<String, Object> runSearchGetFirstMap(String aIndexName, String aJsonQuery) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in alias name is null or empty.");
        }

        // Make a synchronous POST call to ElasticSearch to run the search
        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/" + aIndexName + "/_search")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonQuery)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            // ElasticSearch returned a non-200 status response
            throw new RuntimeException("Error in runSearchGetJsonResponse:  ES returned a status code of " + response.getStatusCode() + " with an error of: " + response.getResponseBody());
        }

        // Pull the list of matching values from the JSON Response
        String jsonResponse = response.getResponseBody();

        // Convert the response JSON string into a map and examine it to see if the request really worked
        Map<String, Object> mapResponse = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});

        @SuppressWarnings("unchecked")
        Map<String, Object> outerHitsMap = (Map<String, Object>) mapResponse.get("hits");
        if (outerHitsMap == null) {
            throw new RuntimeException("Error in runAutoComplete():  The outer hits value was not found in the JSON response");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> innerHitsListOfMaps = (List<Map<String, Object>>) outerHitsMap.get("hits");
        if (innerHitsListOfMaps == null) {
            throw new RuntimeException("Error in runAutoComplete():  The inner hits value was not found in the JSON response");
        }

        if (innerHitsListOfMaps.size() == 0) {
            // No results were found
            return null;
        }
        else {
            // Return the first map
            return innerHitsListOfMaps.get(0);
        }
    }



    public Integer runSearchGetCount(String aIndexName) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        String jsonRequest = "{\n" +
                "  \"query\": {\n" +
                "          \"match_all\": {}\n" +
                "    }\n" +
                "  }\n" +
                "} ";

        // Make a synchronous POST call to execute a search and return the count
        Response response = this.asyncHttpClient.preparePost(this.elasticSearchUrl + "/" + aIndexName + "/_count")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLISECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(jsonRequest)
                .execute()
                .get();

        // Get the JSON response
        String esJsonResponse = response.getResponseBody();

        // Convert the response JSON string into an ElasticSearchDtoGetCount object
        // NOTE:  This is substantially faster as Jackson can ignore the other fields
        ElasticSearchDtoGetCount esCount = objectMapper.readValue(esJsonResponse, ElasticSearchDtoGetCount.class);

        return esCount.getCount();
    }

}




