package com.tofallis.baking.data;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.OffsetDateTime;

public class DateConverter {
    @TypeConverter
    public static OffsetDateTime toDate(String dateTime) {
        return dateTime == null ? null : OffsetDateTime.parse(dateTime);
    }

    @TypeConverter
    public static String toTimestamp(OffsetDateTime date) {
        return date == null ? null : date.toString();
    }
}
