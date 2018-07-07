package io.dev.tanners.bakerhelper.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import java.util.List;
import io.dev.tanners.bakerhelper.db.config.DBConfig;
import io.dev.tanners.bakerhelper.db.support.IngredientConverter;
import io.dev.tanners.bakerhelper.db.support.StepConverter;

@Entity(tableName = DBConfig.TABLE_NAME_RECIPE)
@TypeConverters({IngredientConverter.class, StepConverter.class})
public class Recipe {
    @PrimaryKey(autoGenerate = false)
    private int id;
    private String name;
    private int servings;
    private String image;
    private List<Step> steps;
    private List<Ingredient> ingredients;

    @Ignore
    public Recipe() {
        // needed for parser
    }

    @Ignore
    public Recipe(String name, int servings, String image) {
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    public Recipe(int id, String name, int servings, String image) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
