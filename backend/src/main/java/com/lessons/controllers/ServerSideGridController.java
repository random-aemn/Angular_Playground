package com.lessons.controllers;

import com.common.utilities.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessons.models.grid.GridGetRowsRequestDTO;
import com.lessons.models.grid.GridGetRowsResponseDTO;
import com.lessons.models.grid.SortModel;
import com.lessons.services.ServerSideGridService;
import com.lessons.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

@Controller
public class ServerSideGridController {

    @Resource
    private ServerSideGridService serverSideGridService;



    private final List<String> esUsersFieldsToSearch = Arrays.asList("id.sort", "csv_data_source_labels", "entity_name", "case_state_label",
            "assigned_full_name", "created_date.text",  "created_full_name", "created_full_name", "last_updated_full_name", "last_updated_date.text", "cage_code", "entity_name");

    private final List<String> esUsersFieldToReturn = Arrays.asList("id", "csv_data_source_labels", "entity_name", "case_state_id", "case_state_label",
            "assigned_full_name", "created_date", "last_updated_date", "created_full_name", "last_updated_full_name", "cage_code", "entity_name",
            "total_low_priority_alerts", "total_alerts", "total_medium_priority_alerts", "total_high_priority_alerts", "total_none_priority_alerts");



    /**
     * @param aGridRequestDTO holds the Request information
     * @return ResponseEntity that holds a GridGetRowsResponseDTO object
     * @throws Exception if something bad happens
     */
    @RequestMapping(value = "/api/grid/get-rows/userse/all", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAnyRole('APP16_SUPERVISOR', 'APP16_SPECIALIST', 'APP16_ADMIN', 'APP16_REVIEWER')")
    public ResponseEntity<?> getRowsForSupervisorQueueAllCases(@RequestBody GridGetRowsRequestDTO aGridRequestDTO) throws Exception {

        if (aGridRequestDTO.getStartRow() >= aGridRequestDTO.getEndRow() ) {
            // This is an invalid request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("The endRow must be greater than the startRow.");
        }

        // Default search query is blank as we are searching **OPEN** Case records in CVF_CASES
        String defaultQueryString = "";

        // Change the sort field from "priority" to "priority.sort"  (so the sort is case insensitive -- see the mapping)
        changeSortFieldToUseElasticFieldsForSorting(aGridRequestDTO, "id");

        // Set Default sorting
        //  1) If the sorting model is not empty, then do nothing
        //  2) If the sorting model to empty and rawSearchQuery is empty, then sort by "id" ascending
        //  3) If the sorting model is empty and rawSearchQuery is not empty, then sort by "_score" descending
        setDefaultSorting(aGridRequestDTO, "id");

        // Run the search and generate a GridGetRowsResponseDTO object
        GridGetRowsResponseDTO responseDTO = serverSideGridService.getPageOfData(Constants.APP16_USERS_ES_MAPPING,
                this.esUsersFieldsToSearch,
                this.esUsersFieldToReturn,
                defaultQueryString,
                aGridRequestDTO);

        // Return the GridGetRowsResponseDTO object and a 200 status code
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDTO);
    }




    /**
     * Set default sorting
     *  1) If the sorting model is not empty and does not contain the "id" score, then append "id" ascending
     *  2) If the sorting model to empty and rawSearchQuery is empty, then sort by "id" ascending
     *  3) If the sorting model is empty and rawSearchQuery is not empty, then sort by "_score" descending
     * ASSUMPTION:  Every query must have a sort field set so that the "infinity scroll" works
     *              So, every query sent to ElasticSearch must be sorted on something -- either the "score" for sorting on relevancy or some sort field
     *              If no sort field is provided, the the ES query will be sorted by the default ID field
     *
     * @param aGridRequestDTO holds information about the grid request
     */
    private void setDefaultSorting(GridGetRowsRequestDTO aGridRequestDTO, String aNameOfIdField) {
        if (CollectionUtils.isNotEmpty( aGridRequestDTO.getSortModel() )) {
            // Sorting model is not empty.  So, user is sorting by a column.  Append the id field ascending (if it's not already there)

            for (SortModel sortModel: aGridRequestDTO.getSortModel() ) {
                if ((sortModel.getColId() != null) && (sortModel.getColId().equalsIgnoreCase(aNameOfIdField)) ) {
                    // The SortModel is not empty and *ALREADY* contains the "id" field.  So, do nothing.
                    return;
                }
            }

            // The SortModel is not empty and does *NOT* contain the "id" column.  So, add the "id" ascending to the list of sort models
            SortModel sortById = new SortModel(aNameOfIdField, "asc");
            aGridRequestDTO.getSortModel().add(sortById);
            return;
        }

        List<SortModel> sortModelList;

        // The sorting model is empty
        if (StringUtils.isBlank(aGridRequestDTO.getRawSearchQuery())) {
            // The sorting model is empty and rawSearchQuery is blank
            // -- User is *not* running a search.  So, sort by "id" ascending

            // NOTE:  By default the grids will sort by last_updated_date desc *FIRST* and the id *SECOND*
            //        It's important that the default sorting is CONSISTENT (which means we CANNOT have ties when sorting)
            //        So, using the ID as a second field to sort on ensures that there are no ties
            //        If the default sorting is NOT CONSISTENT, then serverSideGridService.getPageOfData() REST call will not get the next page correctly
            SortModel sortByLastUpdatedDate = new SortModel("last_updated_date", "desc");
            SortModel sortById = new SortModel(aNameOfIdField, "asc");
            sortModelList = Arrays.asList(sortByLastUpdatedDate, sortById);
        }
        else {
            // The sorting mode is empty and rawSearchQuery is not empty
            // -- User is running a search.  SO, sort by "_score" descending *AND* by "id"
            //    NOTE:  When using the search_after technique to get the next page, we need to sort by _score *AND* id
            SortModel sortByScore = new SortModel("_score", "desc");
            SortModel sortById = new SortModel(aNameOfIdField, "asc");
            sortModelList = Arrays.asList(sortByScore, sortById);
        }


        aGridRequestDTO.setSortModel(sortModelList);
    }  // end of setDefaultSorting()



    private void changeSortFieldToUseElasticFieldsForSorting(GridGetRowsRequestDTO aGridRequestDTO, String aNameOfIdField) {

        if (CollectionUtils.isNotEmpty(aGridRequestDTO.getSortModel())) {
            for (SortModel sortModel: aGridRequestDTO.getSortModel() ) {
                String sortFieldName = sortModel.getColId();
                if (! sortFieldName.equalsIgnoreCase(aNameOfIdField)) {
                    sortFieldName = sortFieldName + ".sort";
                    sortModel.setColId(sortFieldName);
                }
            }
        }
    }    // end of changeSortFieldToUseElasticFieldsForSorting()



}
