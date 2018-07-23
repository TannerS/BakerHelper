package io.dev.tanners.bakerhelper.model;

import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.dev.tanners.bakerhelper.aac.db.ListIngredientConverter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient implements Parcelable {
    private String quantity;
    private String measure;
    private String ingredient;

    /**
     *
     */
    public Ingredient() {
        // needed for parser
    }

    /**
     * @param quantity
     * @param measure
     * @param ingredient
     */
    public Ingredient(String quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    /**
     * @return
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * @return
     */
    public String getMeasure() {
        return measure;
    }

    /**
     * @param measure
     */
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * @return
     */
    public String getIngredient() {
        return ingredient;
    }

    /**
     * @param ingredient
     */
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * @param in
     */
    protected Ingredient(Parcel in) {
        quantity = in.readString();
        measure = in.readString();
        ingredient = in.readString();
    }

    /**
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        /**
         * @param in
         * @return
         */
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        /**
         * @param size
         * @return
         */
        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
