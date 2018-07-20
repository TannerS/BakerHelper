package io.dev.tanners.bakerhelper.aac.db.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class TimestampConverter {
    @TypeConverter
    public static Date toDateStamp(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}