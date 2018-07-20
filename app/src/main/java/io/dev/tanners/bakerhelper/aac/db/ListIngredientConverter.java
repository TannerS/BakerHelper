package io.dev.tanners.bakerhelper.aac.db;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Step;

public class ListIngredientConverter {
    @TypeConverter
    public static List<Ingredient> toObjectFromString(String mIngredientStr) {

        Type listType = new TypeToken<List<Ingredient>>() {}.getType();

        return new Gson().fromJson(mIngredientStr, listType);

    }

    @TypeConverter
    public static String FromListToStr(List<Ingredient> mSteps) {
        Gson gson = new Gson();
        return gson.toJson(mSteps);
    }
}