package io.dev.tanners.bakerhelper.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.db.ListIngredientConverter;
import io.dev.tanners.bakerhelper.aac.db.ListStepConverter;
import io.dev.tanners.bakerhelper.aac.db.config.DBConfig;

@TypeConverters({ListIngredientConverter.class, ListStepConverter.class})
@Entity(tableName = DBConfig.TABLE_NAME_RECIPES)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe implements Parcelable {
    @PrimaryKey
    private int id;
    private String name;
    private int servings;
    private String image;
    private List<Step> steps;
    private List<Ingredient> ingredients;

    /**
     *
     */
    @Ignore
    public Recipe() { }

    /**
     * @param name
     * @param servings
     * @param image
     * @param steps
     * @param ingredients
     */
    @Ignore
    public Recipe(String name, int servings, String image, List<Step> steps, List<Ingredient> ingredients) {
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    /**
     * @param id
     * @param name
     * @param servings
     * @param image
     * @param steps
     * @param ingredients
     */
    public Recipe(int id, String name, int servings, String image, List<Step> steps, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    /**
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public int getServings() {
        return servings;
    }

    /**
     * @param servings
     */
    public void setServings(int servings) {
        this.servings = servings;
    }

    /**
     * @return
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return
     */
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * @param steps
     */
    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    /**
     * @return
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * @param ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *  http://www.parcelabler.com/
     *
     * @param in
     */
    protected Recipe(Parcel in) {
        name = in.readString();
        servings = in.readInt();
        image = in.readString();
        if (in.readByte() == 0x01) {
            steps = new ArrayList<Step>();
            in.readList(steps, Step.class.getClassLoader());
        } else {
            steps = null;
        }
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<Ingredient>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }
    }

    /**
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeString(image);
        if (steps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(steps);
        }
        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        /**
         * @param in
         * @return
         */
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        /**
         * @param size
         * @return
         */
        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
