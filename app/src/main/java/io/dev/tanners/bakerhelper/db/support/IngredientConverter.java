package io.dev.tanners.bakerhelper.db.support;

import android.arch.persistence.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Ingredient;

/**
 * All credit: https://stackoverflow.com/questions/44986626/android-room-database-how-to-handle-arraylist-in-an-entity
 * https://stackoverflow.com/questions/43117731/what-is-type-typetoken
 */
public class IngredientConverter {
    @TypeConverter
    public static List<Ingredient> ingredientsFromString(String value) {
        Type listType = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String ingredientsToString(List<Ingredient> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}