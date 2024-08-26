package com.lessons.models.grid;

public class NumberColumnFilter extends ColumnFilter {

    private String type;
    private Integer filter;
    private Integer filterTo;
    private boolean addFilterSuffix = true;

    public NumberColumnFilter() {}

    public NumberColumnFilter(String type, Integer filter, Integer filterTo) {
        this.type = type;
        this.filter = filter;
        this.filterTo = filterTo;
    }

    public NumberColumnFilter(String type, Integer filter, Integer filterTo, boolean addFilterSuffix) {
        this.type = type;
        this.filter = filter;
        this.filterTo = filterTo;
        this.addFilterSuffix = addFilterSuffix;
    }

    public String getFilterType() {
        return filterType;
    }

    public String getType() {
        return type;
    }

    public Integer getFilter() {
        return filter;
    }

    public Integer getFilterTo() {
        return filterTo;
    }

    public boolean getAddFilterSuffix() {
        return addFilterSuffix;
    }
}
