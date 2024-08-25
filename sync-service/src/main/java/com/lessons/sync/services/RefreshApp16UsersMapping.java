package com.lessons.sync.services;

import com.common.utilities.Constants;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lessons.sync.interfaces.RefreshMapping;
import com.lessons.sync.models.MappingApp16UsersDTO;
import com.lessons.sync.models.RecordDetailsDTO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class RefreshApp16UsersMapping implements RefreshMapping {
    private static final Logger logger = LoggerFactory.getLogger(RefreshApp16UsersMapping.class);
    private ObjectMapper objectMapper;


    @Resource
    private DataSource dataSource;

    @Resource
    private ElasticSearchService elasticSearchService;


    @PostConstruct
    public void init() {
        // Initialize the objectMapper (which is used to convert the SF328FormDTO object into JSON)
        this.objectMapper = new ObjectMapper();

        // Tell the object mapper to convert Objects to snake case
        // For example.  object.getPersonName --> "person_name" in the json
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        // Escape non-nulls
        this.objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
    }


    @Override
    public String getIndexName() {
        return Constants.APP16_USERS_ES_MAPPING;
    }

    @Override
    public RecordDetailsDTO getBeforeCountInfo() {
        String lastUpdatedDate = null;

        // Get the total number of user records from the database (that are not the SYSTEM USER TYPE)
        String sql = "select count(id) from users";
        JdbcTemplate jt = new JdbcTemplate(this.dataSource);
        Integer totalAlertsCount = jt.queryForObject(sql, Integer.class);

        if (totalAlertsCount == null) {
            throw new RuntimeException("Error in getBeforeCountInfo():  The totalUserCount object is null.  This should never happen.");
        }

        // Get the last login_date
        if (totalAlertsCount > 0) {
            sql = "select to_char( max(last_updated_date), 'mm/dd/yyyy HH24:MI:SS') from users";
            lastUpdatedDate = jt.queryForObject(sql, String.class);
        }

        RecordDetailsDTO recordDetailsDTO = new RecordDetailsDTO(totalAlertsCount, lastUpdatedDate);
        return recordDetailsDTO;
    }

    @Override
    public RecordDetailsDTO getAfterCountInfo(String aNewIndexName) throws Exception {
        String lastUpdatedDate = null;

        // Construct a json query to get the most recent last_updated_date found in the app16_users index
        // NOTE:  Run a search against all records sorted by last_updated_date and get the first record
        String jsonQuery = """
                {
                  "_source": ["last_updated_date"],   "query": {
                    "match_all": {}
                  },
                  "size": 1,
                  "sort": [{ "last_updated_date": "desc"}, {"user_id": "desc"}]
                }
                """;

        Map<String, Object> firstHitMap = this.elasticSearchService.runSearchGetFirstMap(aNewIndexName, jsonQuery);
        if (firstHitMap != null) {

            @SuppressWarnings("unchecked")
            Map<String, Object> sourceMap = (Map<String, Object>) firstHitMap.get("_source");
            if (sourceMap != null) {
                lastUpdatedDate = (String) sourceMap.get("last_updated_date");
            }
        }


        // Get the total number of records in the alerts mapping
        Integer totalUserCountInEs = this.elasticSearchService.runSearchGetCount(aNewIndexName);

        RecordDetailsDTO recordDetailsDTO = new RecordDetailsDTO(totalUserCountInEs, lastUpdatedDate);
        return recordDetailsDTO;
    }




    @Override
    public void addDataToIndex(String aNewIndexName) throws Exception {
        logger.debug("addDataToIndex() started  aIndexName={}", getIndexName());

        // Get a list of SF328 Form DTOs from the database
        List<MappingApp16UsersDTO> listOfDTOs = getListOfDTOsFromDatabase();

        StringBuilder bulkJsonRequest = new StringBuilder();
        final int BATCH_SIZE = 500;    // Every 500 records submit a bulk request
        int recordNumber = 0;

        // Loop through the ReportDTO objects building a Bulk JSON string
        for (MappingApp16UsersDTO singleRecordDTO: listOfDTOs) {
            recordNumber++;

            // Construct the first line (of this bulk update request)
            String line1 = "{ \"index\": { \"_index\": \"" + aNewIndexName + "\", \"_id\": " + singleRecordDTO.getUserId() + " }}\n";

            // Construct the 2nd line of this bulk update request (using the objectMapper to convert this object to JSON)
            String line2 = objectMapper.writeValueAsString(singleRecordDTO) + "\n";

            bulkJsonRequest.append(line1);
            bulkJsonRequest.append(line2);

            if ((recordNumber % BATCH_SIZE) == 0) {
                // We have reached bulk size.  So, submit a request

                // Submit the Bulk Request to ElasticSearch
                elasticSearchService.bulkUpdate(bulkJsonRequest.toString(), true);

                // Clear the json request string
                bulkJsonRequest.setLength(0);
            }
        }

        if (!bulkJsonRequest.isEmpty()) {
            // There is one final partial batch left.  So, submit the info
            elasticSearchService.bulkUpdate(bulkJsonRequest.toString(), true);
        }

        logger.debug("addDataToIndex() finished aIndexName={}", getIndexName());
    }

    private List<MappingApp16UsersDTO> getListOfDTOsFromDatabase() {
        JdbcTemplate jt = new JdbcTemplate(dataSource);

        String sql = """
                    select u.id as user_id,
                           u.full_name as full_name,
                           to_char(u.created_date, 'mm/dd/yyyy hh24:mi:ss') as created_date,
                           to_char(u.last_updated_date, 'mm/dd/yyyy hh24:mi:ss') as last_updated_date
                    from users u
                    order by u.id;
                    """;

        BeanPropertyRowMapper<MappingApp16UsersDTO> rowMapper = new BeanPropertyRowMapper<>(MappingApp16UsersDTO.class);

        List<MappingApp16UsersDTO> listOfDTOs = jt.query(sql, rowMapper);

        return listOfDTOs;
    }

}
