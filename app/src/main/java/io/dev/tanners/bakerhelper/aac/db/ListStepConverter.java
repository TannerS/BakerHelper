package io.dev.tanners.bakerhelper.aac.db;

import android.arch.persistence.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Step;

public class ListStepConverter {
    /**
     * convert json to object
     *
     * @param mStepStr
     * @return
     */
    @TypeConverter
    public static List<Step> toObjectFromString(String mStepStr) {
        // get list type
        Type listType = new TypeToken<List<Step>>() {}.getType();
        // return object from json
        return new Gson().fromJson(mStepStr, listType);
    }

    /**
     * convert object to string
     *
     * @param mSteps
     * @return
     */
    @TypeConverter
    public static String FromListToStr(List<Step> mSteps) {
        Gson gson = new Gson();
        // return json
        return gson.toJson(mSteps);
    }
}