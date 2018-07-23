package io.dev.tanners.bakerhelper.aac.db;

import android.arch.persistence.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Ingredient;

/**
 * convert ing list to string and vice versa
 */
public class ListIngredientConverter {
    /**
     * convert json to object
     *
     * @param mIngredientStr
     * @return
     */
    @TypeConverter
    public static List<Ingredient> toObjectFromString(String mIngredientStr) {
        // get list type
        Type listType = new TypeToken<List<Ingredient>>() {}.getType();

        try {
            // return object from json
            return new Gson().fromJson(mIngredientStr, listType);
        } catch (Exception ex) {

        }

        return null;

    }

    /**
     * convert object to json
     *
     * @param mSteps
     * @return
     */
    @TypeConverter
    public static String FromListToStr(List<Ingredient> mSteps) {
        Gson gson = new Gson();

        if(mSteps == null)
            return null;

        try {
            // return json
            return gson.toJson(mSteps);
        } catch (Exception ex) {


        }

        return null;
    }
}