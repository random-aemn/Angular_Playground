package com.lessons.sync.utilities;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DateUtilities {
    private static final Logger logger = LoggerFactory.getLogger(DateUtilities.class);

    private static final List<String> dateFormats = Arrays.asList(
            "yyyyMMdd", "yyyy-MM-dd",  "yyyy/MM/dd", "MM/dd/yyyy", "d-MM-yy", "dd-MM-yy", "dd-MMM-yy",  "dd-MMM-yyyy", "dd/MM/yyyy", "MMddyyyy", "Mddyyyy");


    /**
     * Convert a string into a Date object
     *
     * @param aString a raw string that holds a date in one of many formats
     * @return Date object
     */
    public static Date convertToDate(String aString) {
        if (StringUtils.isEmpty(aString) || (aString.length() == 1)) {
            return null;
        }


        // Loop through all of the date formats (attempting to convert this string to a date)
        for (String dateFormat : dateFormats) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                simpleDateFormat.setLenient(false);   // Parsing requires exact match

                // Attempt to convert the String to a Date
                Date parsedDate = simpleDateFormat.parse(aString);

                return parsedDate;
            } catch (Exception e) {
                // I could not parse this date -- so try the next

            }
        }

        // If I got this far, then I could not parse the date using *any* of the date formats
        logger.warn("I could not parse this date using any of the date formats:  {}", aString);
        return null;
    }



}

