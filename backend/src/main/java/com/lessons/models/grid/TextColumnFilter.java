package com.lessons.models.grid;

public class TextColumnFilter extends ColumnFilter{

    private String type;
    private String filter;
    private String filterTo;
    // By default, all finters will add the .filtered suffix for elastic search
    private boolean addFilterSuffix = true;

    public TextColumnFilter() {}

    public TextColumnFilter(String type, String filter, String filterTo) {
        this.type = type;
        this.filter = filter;
        this.filterTo = filterTo;
    }

    public TextColumnFilter(String type, String filter, String filterTo, boolean addFilterSuffix) {
        this.type = type;
        this.filter = filter;
        this.filterTo = filterTo;
        // This constructor gives the called the ability to manually set the addFilterSuffix flag
        this.addFilterSuffix = addFilterSuffix;
    }

    public String getFilterType() {
        return filterType;
    }

    public String getType() {
        return type;
    }

    public String getFilter() {
        return filter;
    }

    public String getFilterTo() {
        return filterTo;
    }

    public boolean getAddFilterSuffix() {
        return addFilterSuffix;
    }
}
