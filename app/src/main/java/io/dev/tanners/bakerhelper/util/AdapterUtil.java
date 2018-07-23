package io.dev.tanners.bakerhelper.util;

import android.content.Context;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.model.Ingredient;

public class AdapterUtil {

    /**
     * FOrmat ingredient fields into a string
     *
     * @param mContext
     * @param mItem
     * @return
     */
    public static String formatIngredient(Context mContext, Ingredient mItem)
    {
        StringBuilder mBuilder = new StringBuilder();

        mBuilder.append(mItem.getQuantity());
        mBuilder.append(mContext.getString(R.string.space));
        mBuilder.append(mItem.getMeasure());
        mBuilder.append(mContext.getString(R.string.recipe_ingr_amount_concat));
        mBuilder.append(mItem.getIngredient());
        mBuilder.append(System.getProperty("line.separator"));

        return mBuilder.toString();
    }
}
