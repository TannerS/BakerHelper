package io.dev.tanners.bakerhelper.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.List;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.aac.db.ListIngredientConverter;
import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.util.AdapterUtil;
import io.dev.tanners.bakerhelper.widget.config.GlobalConfig;
import static android.content.Context.MODE_PRIVATE;

/**
 *
 */
public class WidgetListFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final String INTENT_EXTRA_KEY = "WidgetListFactory_INTENT_KEY";
    private List<Ingredient> mIngredients = null;
    private int mWidgetId;
    private Context mContext;

    /**
     * @param applicationContext
     */
    public WidgetListFactory(Context applicationContext, Intent mIntent) {
        mContext = applicationContext;
        // source: https://stackoverflow.com/questions/11350287/ongetviewfactory-only-called-once-for-multiple-widgets
        mWidgetId = Integer.valueOf(mIntent.getData().getSchemeSpecificPart());
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        // get shared preferences
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(GlobalConfig.getWidgetSharedPreferenceKey(mWidgetId), MODE_PRIVATE);
        // get string of array
        String mIngredentStr = mSharedPreferences.getString(
            GlobalConfig.getWidgetSharedPreferenceIngredientKey(mWidgetId),
            "INVALID"
        );
        // convert json string back to list object
        try {
            mIngredients = ListIngredientConverter.toObjectFromString(mIngredentStr);
        } catch (Exception e) {
            // problem converting data
            // do nothing
            e.printStackTrace();
        }
    }

    /**
     * called on start and when notifyAppWidgetViewDataChanged is called
     */
    @Override
    public void onDataSetChanged() {

    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        // not used
    }

    /**
     * Get item count
     *
     * @return
     */
    @Override
    public int getCount() {
        return mIngredients == null ? 0 : mIngredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        // load views
        RemoteViews mSubRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_ingredient);
        // set ingredient text
        mSubRemoteViews.setTextViewText(R.id.recipe_ingredient_text, AdapterUtil.formatIngredient(mContext, this.mIngredients.get(position)));
        // return text
        return mSubRemoteViews;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * 1 to reat all items the same
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * @return
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
}

