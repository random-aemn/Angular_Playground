package com.lessons.services;

import com.common.utilities.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessons.config.ElasticSearchResources;
import com.lessons.models.elasticsearch.ErrorsDTO;
import com.lessons.models.grid.GridGetRowsResponseDTO;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class ElasticSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);


    @Resource
    private ElasticSearchResources elasticSearchResources;


    private String elasticSearchUrl;
    private AsyncHttpClient asyncHttpClient;
    private final int ES_REQUEST_TIMEOUT_IN_MILLI_SECS = 30000;   // All ES requests timeout after 30 seconds
    private ObjectMapper objectMapper;

    private final Pattern patMatchForwardSlash 	= Pattern.compile("/");
    private final Pattern patMatchDoubleQuote 	= Pattern.compile("\"");
    private final Pattern patMatchBackslash 	= Pattern.compile("\\\\");
    private final Pattern patMatchAscii1To31or128 = Pattern.compile("[\\u0000-\\u001F\\u0080]");

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

        // Verify that required ElasticSearch aliases exist
        verifyElasticSearchAliasesExist();

        logger.debug("init() finished successfully");
    }



    /**
     * Helper method to verify that expected ES aliases exist
     * @throws Exception if one of the ES aliases does not exist
     */
    public void verifyElasticSearchAliasesExist() throws Exception {
        logger.debug("verifyElasticSearchAliasesExist() started.");

        // Create a list of all of aliases
        List<String> aliasesThatShouldExist = Arrays.asList(Constants.APP16_USERS_ES_MAPPING);

        // Loop through all entries in the map and create the indexes.
        for (String aliasName: aliasesThatShouldExist ) {

            if (! doesAliasExist(aliasName)) {
                // One of the ES mapping **ALIASES** not found.  So, throw an error

                throw new RuntimeException("The ES Alias does not exist: " + aliasName + "   Please run the Sync-Service to initialize the ES mappings");
            }
        }

        logger.debug("verifyElasticSearchAliasesExist() finished successfully.");
    }




    /**
     * Helper method to determine if the passed-in ES mapping name or alias exists
     *
     * @param aAliasName holds the ES alias name
     * @return TRUE if the passed-in index or alias exists
     */
    public boolean doesAliasExist(String aAliasName) throws Exception {
        logger.debug("doesAliasExist() started  aAliasName={}", aAliasName);

        if (StringUtils.isEmpty(aAliasName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }

        // Make a synchronous GET call to get a list of all index names
        String url= this.elasticSearchUrl + "/_cat/aliases";
        logger.debug("Invoking GET REST call to -->{}<--", url);
        Response response = this.asyncHttpClient.prepareGet(url)
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLI_SECS)
                .setHeader("accept", "text/plain")
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Critical error in doesAliasExist():  ElasticSearch returned a response status code of " +
                    response.getStatusCode() + ".  Response message is " + response.getResponseBody() );
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
     * Escape certain special filter values to work properly in ElasticSearch
     *   \   -->  \\
     *   "   -->  \"
     *
     * @param aRawFilterValue holds the user's raw value (entered from the c
     * @return string that holds the cleaned value
     */
    public String escapeSpecialElasticSearchChars(String aRawFilterValue) {
        if (StringUtils.isBlank(aRawFilterValue)) {
            // This passed-in string is blank.  So, do not apply the regular expressions and return the original string back
            return aRawFilterValue;
        }

        String cleanValue = aRawFilterValue;

        // WARNING:  Order of search & replace **BELOW** operations is important

        // Convert  \ to \\    *FIRST*
        // NOTE:  Because of Java Regex, you have to use 8 backward slashes to match a \\
        cleanValue = this.patMatchBackslash.matcher(cleanValue).replaceAll("\\\\\\\\");

        // Convert " to \"     *SECOND*
        // NOTE:  Because of Java Regex, you have to use four backward slashes to match a \
        cleanValue = this.patMatchDoubleQuote.matcher(cleanValue).replaceAll("\\\\\"");

        return cleanValue;
    }

    /**
     * Clean-up the passed-in raw query with the following rules:
     *   1) If Double quote is found, then replace it with \"
     *   2) If ASCII value between 1 and 31 is found or 128, then replace it with a space
     *   3) If / is found, then replace it with \\/
     *
     * @param aRawQuery holds the raw query from the front-end
     * @return cleaned-up query
     */
    public String cleanupQuery(String aRawQuery) {

        // Convert  "and " or " and " or " and" to --> AND
        // NOTE:  Do this first before encoding the quotes
        String cleanedQuery = adjustElasticSearchAndOrNotOperatorsToUpperCase(aRawQuery);

        // Convert the pattern match of " to \"
        // NOTE:  Because of Java Regex, you have to use four backward slashes to match a \
        cleanedQuery = this.patMatchDoubleQuote.matcher(cleanedQuery).replaceAll("\\\\\"");

        // If ASCII 1-31 or 128 is found, then replace it with a space
        cleanedQuery = this.patMatchAscii1To31or128.matcher(cleanedQuery).replaceAll(" ");

        // Escape forward slashes:   Convert  / to \\/
        // NOTE:  A user searching for 01/24 would be searching for 01\\/24
        cleanedQuery = this.patMatchForwardSlash.matcher(cleanedQuery).replaceAll("\\\\\\\\/");

        return cleanedQuery;
    }



    public boolean isQueryValid(String aIndexName, String aJsonBodyForQuery) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }
        else if (StringUtils.isEmpty(aJsonBodyForQuery)) {
            throw new RuntimeException("The passed-in aJsonBody is null or empty.");
        }

        // Make a synchronous POST call to run a _validate/query Call -- to see if this query is valid
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/" + aIndexName + "/_validate/query?explain")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLI_SECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonBodyForQuery)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Critical error in isQueryValid():  ElasticSearch returned a response status code of " +
                    response.getStatusCode() + ".  Response message is " + response.getResponseBody() + "\n\n" + aJsonBodyForQuery);
        }

        // Get the JSON response from the response object
        String jsonResponse = response.getResponseBody();

        // Convert the response JSON string into a map and examine it to see if the request really worked
        Map<String, Object> mapResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        boolean isQueryValid = (boolean) mapResponse.get("valid");
        return isQueryValid;
    }




    private String adjustElasticSearchAndOrNotOperatorsToUpperCase(String aString) {
        if (StringUtils.isBlank(aString)) {
            return aString;
        }

        // Convert the string into a list of Strings
        List<String> listOfWords = Arrays.asList(aString.trim().split("\\s+"));

        // Get the iterator
        ListIterator<String> iter = listOfWords.listIterator();

        boolean inQuotes = false;

        // Loop through the list and remove certain items
        while (iter.hasNext())
        {
            String word = iter.next();

            if (word.isEmpty()) {
                continue;
            }

            if (word.equalsIgnoreCase("\"")) {
                // The entire word is an open quote
                inQuotes = !inQuotes;
                continue;
            }

            if ((word.startsWith("\"") && (! word.endsWith("\""))) ||
                    (! word.startsWith("\"") && (word.endsWith("\"")))) {
                // The word either starts or ends with a quote
                inQuotes = !inQuotes;
            }

            if (!inQuotes && (word.equalsIgnoreCase("and") || word.equalsIgnoreCase("or") ||
                    word.equalsIgnoreCase("not") || word.equalsIgnoreCase("to") ))
            {
                // Convert this "and", "or", "to", or "not" word to UPPERCASE  (to make ElasticSearch happy)
                word = word.toUpperCase();
                iter.set(word);
            }
        }

        String returnedString = StringUtils.join(listOfWords, " ");
        return returnedString;
    }


    public GridGetRowsResponseDTO runSearchGetRowsResponseDTO(String aIndexName, String aJsonBody) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }
        else if (StringUtils.isEmpty(aJsonBody)) {
            throw new RuntimeException("The passed-in aJsonBody is null or empty.");
        }

        // Make a synchronous POST call to execute a search and return a response object
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/" + aIndexName + "/_search")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLI_SECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonBody)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Critical error in runSearchGetJsonResponse():  ElasticSearch returned a response status code of " +
                    response.getStatusCode() + ".  Response message is " + response.getResponseBody() + "\n\n" + aJsonBody);
        }


        // Create an empty array list
        List<Map<String, Object>> listOfMaps = new ArrayList<>();

        // Pull the list of matching values from the JSON Response
        String jsonResponse = response.getResponseBody();

        // Convert the response JSON string into a map and examine it to see if the request really worked
        Map<String, Object> mapResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});
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

        if (!innerHitsListOfMaps.isEmpty()) {
            for (Map<String, Object> hit: innerHitsListOfMaps) {

                // Get the source map (that has all of the results)
                @SuppressWarnings("unchecked")
                Map<String, Object> sourceMap = (Map<String, Object>) hit.get("_source");
                if (sourceMap == null) {
                    throw new RuntimeException("Error in runAutoComplete():  The source map was null in the JSON response");
                }

                // Add the sourceMap to the list of maps
                listOfMaps.add(sourceMap);
            }
        }

        Integer totalMatches = 0;


        if (!listOfMaps.isEmpty()) {
            // Get the total matches from the json
            @SuppressWarnings("unchecked")
            Map<String, Object> totalInfoMap = (Map<String, Object>) outerHitsMap.get("total");
            if ((totalInfoMap != null) && (!totalInfoMap.isEmpty())) {
                totalMatches = (Integer) totalInfoMap.get("value");
            }
        }


        // Set the searchAfter clause in the GetResponseRowsDTO object
        // NOTE:  The front-end will pass this back for page 2, page 3, page 4
        //    	so we can run the same ES query and get page 2, page 3, page 4
        String searchAfterClause = getSearchAfterFromEsResponseMap(innerHitsListOfMaps);


        GridGetRowsResponseDTO responseDTO = new GridGetRowsResponseDTO(listOfMaps, totalMatches, searchAfterClause);
        return responseDTO;
    }


    /**
     * Generate the search_after clause by looking at the last result from the last search
     *  1. If the list of maps is empty, return an empty string
     *  2. Loop through the sort model list
     * 	-- Build the search_after by pulling the sorted field name
     * 	-- If the sort field name == "_score", then pull the score
     * @param aListOfHitsMaps holds the list of ES maps (that hold the search results)
     * @return a String that holds the search_after clause
     */
    private String getSearchAfterFromEsResponseMap(List<Map<String, Object>> aListOfHitsMaps) {
        if ((aListOfHitsMaps == null) || (aListOfHitsMaps.isEmpty())) {
            return "";
        }

        // Get the last map
        Map<String, Object> lastMap = aListOfHitsMaps.get( aListOfHitsMaps.size() - 1);
        if (lastMap == null) {
            return "";
        }

        // Get the last source map  (it has the search results for the last match)
        @SuppressWarnings("unchecked")
        Map<String, Object> lastSourceMap = (Map<String, Object>) lastMap.get("_source");
        if (lastSourceMap == null) {
            throw new RuntimeException("Error in getSearchAfterFromEsResponseMap():  The lastSourceMap is null.");
        }

        // Get the list of sort fields from the lastMap.sort
        @SuppressWarnings("unchecked")
        List<Object> listOfSortFields = (List<Object>) lastMap.get("sort");
        if ((listOfSortFields == null) || (listOfSortFields.isEmpty())) {
            throw new RuntimeException("Error in getSearchAfterFromEsResponseMap():  The listOfSortFields is null or empty.  It should have always have one or more items.");
        }

        StringBuilder sbSearchAfterClause = new StringBuilder();
        String lastSortValue;

        for (Object lastSortValueObject: listOfSortFields) {

            if (lastSortValueObject == null) {
                // We are sorting on multiple columns and one of the columns is null.  So, we need null (but without quotes around it)
                lastSortValue = "null";
            }
            else if (lastSortValueObject instanceof Long) {
                // The sort value is a long
                Long lValue = (Long) lastSortValueObject;

                if (lValue == Long.MIN_VALUE) {
                    // The Search_After clause has the SMALLEST long value.  So, change it from -9223372036854775808 to zero
                    // NOTE:  This fixes the problem of sorting on missing/null date fields
                    lastSortValue = "0";
                }
                else {
                    lastSortValue = "\"" + lastSortValueObject + "\"";
                }
            }
            else {
                lastSortValue = "\"" + lastSortValueObject + "\"";
            }

            sbSearchAfterClause.append(lastSortValue)
                    .append(",");
        }

        // Remove the last comma
        sbSearchAfterClause.deleteCharAt(sbSearchAfterClause.length() - 1);

        return sbSearchAfterClause.toString();
    }



    /**
     * Run a search and return a response map
     *
     * @param aIndexName holds the name of the index name to search
     * @param aJsonBody holds the JSON query to execute
     * @return a map that holds the response from ElasticSearch
     */
    public Map<String, Object> runSearchGetResponseMap(String aIndexName, String aJsonBody) throws Exception {
        if (StringUtils.isEmpty(aIndexName)) {
            throw new RuntimeException("The passed-in aIndexName is null or empty.");
        }
        else if (StringUtils.isEmpty(aJsonBody)) {
            throw new RuntimeException("The passed-in aJsonBody is null or empty.");
        }

        // Make a synchronous POST call to execute a search and return a response object
        Response response = this.asyncHttpClient.prepareGet(this.elasticSearchUrl + "/" + aIndexName + "/_search")
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLI_SECS)
                .setHeader("accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setBody(aJsonBody)
                .execute()
                .get();

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Critical error in runSearchGetResponseMap():  ElasticSearch returned a response status code of " +
                    response.getStatusCode() + ".  Response message is " + response.getResponseBody() + "\n\n" + aJsonBody);
        }


        // Pull the list of matching values from the JSON Response
        String jsonResponse = response.getResponseBody();

        // Convert the response JSON string into a map and examine it to see if the request really worked
        Map<String, Object> mapResponse = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        return mapResponse;
    }


    /**
     * Do a bulk update within ES
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
                .setRequestTimeout(this.ES_REQUEST_TIMEOUT_IN_MILLI_SECS)
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
     * This is the default date formatting for dates when we need to STORE the current date/time
     * NOTE:  We want to *STORE* the hours/minutes/seconds
     *        The front-end will re-format dates in grids (if needed)
     * @return String that holds the current date/time with hours/minutes/seconds
     */
    public String getNowAsFormattedDateForElasticSearchDateFields() {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = String.format("%02d/%02d/%02d %02d:%02d:%02d", now.getMonthValue(),
                now.getDayOfMonth(),
                now.getYear(),
                now.getHour(),
                now.getMinute(),
                now.getSecond() );
        return formattedDate;
    }






}




