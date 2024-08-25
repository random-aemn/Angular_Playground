package com.lessons.sync.models;

import java.util.Objects;

public class RecordDetailsDTO {
    private int totalRecords;
    private String lastUpdateDate;

    // ---------------- Constructor && Getters -----------------

    public RecordDetailsDTO(int totalRecords, String lastUpdateDate) {
        this.totalRecords = totalRecords;
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordDetailsDTO that = (RecordDetailsDTO) o;
        return getTotalRecords() == that.getTotalRecords() &&
                Objects.equals(getLastUpdateDate(), that.getLastUpdateDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRecords, lastUpdateDate);
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }


}
