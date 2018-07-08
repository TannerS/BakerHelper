package io.dev.tanners.bakerhelper.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import io.dev.tanners.bakerhelper.db.config.DBConfig;
import io.dev.tanners.bakerhelper.db.support.IngredientConverter;
import io.dev.tanners.bakerhelper.db.support.StepConverter;

import static io.dev.tanners.bakerhelper.model.Step.CREATOR;

@Entity(tableName = DBConfig.TABLE_NAME_RECIPE)
@TypeConverters({IngredientConverter.class, StepConverter.class})
public class Recipe implements Parcelable {
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
        steps = new ArrayList<>();
        ingredients = new ArrayList<>();
    }

    @Ignore
    public Recipe(String name, int servings, String image) {
        this();
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    public Recipe(int id, String name, int servings, String image) {
        this();
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }
//
//    public Recipe(Parcel in) {
//        id = in.readInt();
//        name = in.readString();
//        servings = in.readInt();
//        image = in.readString();
//
//        in.readTypedList(steps, Step.CREATOR);
//        in.readTypedList(ingredients, Ingredient.CREATOR);
//    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
//        dest.writeString(name);
//        dest.writeInt(servings);
//        dest.writeString(image);
//        dest.writeTypedList(steps);
//        dest.writeTypedList(ingredients);
//    }

//    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
//        @Override
//        public Recipe createFromParcel(Parcel in) {
//            return new Recipe(in);
//        }
//
//        @Override
//        public Recipe[] newArray(int size) {
//            return new Recipe[size];
//        }
//    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    /*
    http://www.parcelabler.com/
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

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };


}
