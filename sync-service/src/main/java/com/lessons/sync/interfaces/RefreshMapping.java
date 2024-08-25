package com.lessons.sync.interfaces;

import com.lessons.sync.models.RecordDetailsDTO;

public interface RefreshMapping {

    /* this method returns the name of the index that is being created */
    public String getIndexName();

    /* This method returns the count of info *BEFORE* creating the ElasticSearch mapping */
    public RecordDetailsDTO getBeforeCountInfo();

    /* This method returns the count of info from the mapping  (used *AFTER* creating the mapping) */
    public RecordDetailsDTO getAfterCountInfo(String aNewIndexName) throws Exception;

    /* This method adds data to the ElsaticSearch mapping */
    public void addDataToIndex(String aNewIndexName) throws Exception;

}
