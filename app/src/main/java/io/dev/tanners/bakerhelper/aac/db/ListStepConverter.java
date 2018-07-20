package io.dev.tanners.bakerhelper.aac.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Step;

public class ListStepConverter {
    @TypeConverter
    public static List<Step> toObjectFromString(String mStepStr) {

        Type listType = new TypeToken<List<Step>>() {
        }.getType();

        return new Gson().fromJson(mStepStr, listType);

    }

    @TypeConverter
    public static String FromListToStr(List<Step> mSteps) {
        Gson gson = new Gson();
        return gson.toJson(mSteps);
    }
}