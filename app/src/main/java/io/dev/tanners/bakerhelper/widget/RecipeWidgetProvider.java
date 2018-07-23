package io.dev.tanners.bakerhelper.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.RecipeActivity;
import io.dev.tanners.bakerhelper.widget.config.GlobalConfig;
import static android.content.Context.MODE_PRIVATE;

/**
 * A convenience class to aid in implementing an AppWidget provider.
 * Everything you can do with AppWidgetProvider, you can do with a regular BroadcastReceiver.
 * AppWidgetProvider merely parses the relevant fields out of the Intent
 * that is received in onReceive(Context,Intent), and calls hook methods with the received extras.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {
    public static final String PENDING_INTENT_RECIPE_EXTRA_CONFIG = "PENDING_INTENT_RECIPE_EXTRA_CONFIG";
    public static final int PENDING_INTENT_RECIPE_CLICK_CONFIG = 56432;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // to hold current view
        RemoteViews mRemoteViews = null;
        // check if the widget size is normal or gridview size
        if(checkWidgetSizeIsWide(appWidgetManager, appWidgetId))
            // the widget view is size for a single view
            mRemoteViews = getSingleViewLayout(context, appWidgetId);
        else
            // the widget view is size for a grid view
            mRemoteViews = getListViewLayout(context, appWidgetId);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }

    /**
     * determine size of widget to know what UI/data to load
     *
     * @param appWidgetManager
     * @param appWidgetId
     * @return
     */
    private static boolean checkWidgetSizeIsWide(AppWidgetManager appWidgetManager, int appWidgetId)
    {
        Bundle mOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

        int mWidth = mOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        return (mWidth < 200);
    }

    /**
     * Get the view hierarchy for our widget hierarchy
     * This is for gridview
     *
     * @param mContext
     * @return
     */
    private static RemoteViews getListViewLayout(Context mContext, int appWidgetId)
    {
        // Construct the RemoteViews object
        // this will have all the views for the layut
        RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_list);
        // intent for our grid view service to act as the adapter
        Intent intent = new Intent(mContext, WidgetListService.class);
        // pass in widget id
        // source: https://stackoverflow.com/questions/11350287/ongetviewfactory-only-called-once-for-multiple-widgets
        intent.setData(Uri.fromParts("", String.valueOf(appWidgetId), null));
        // set adapter
        mRemoteViews.setRemoteAdapter(R.id.widget_gridview, intent);
        // place holder for empty spaces
        mRemoteViews.setEmptyView(R.id.widget_gridview, R.id.empty_gridview_text);
        // return views
        return mRemoteViews;
    }

    /**
     * Get the view hierarchy for our widget hierarchy
     * This is for single recipe
     *
     * @param mContext
     * @return
     */
    private static RemoteViews getSingleViewLayout(Context mContext, int appWidgetId)
    {
        // Construct the RemoteViews object
        // this will have all the views for the layout
        RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);
        // get shared preferences
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(GlobalConfig.getWidgetSharedPreferenceKey(appWidgetId), MODE_PRIVATE);
        // get recipe name
        String mRecipeName = mSharedPreferences.getString(
                GlobalConfig.getWidgetSharedPreferenceNameKey(appWidgetId),
                "INVALID"
        );
        // load name into UI
        mRemoteViews.setTextViewText(
                R.id.main_recipe_item_name,
                mRecipeName
        );
        /*
         * This is the widget's way off implement 'onClick'
         * for single recipe view
         * NOTE: pending intent can also open up a broadcast, service or activity. our use will be an example
         */
        // create intent to load
        Intent intent = new Intent(mContext, RecipeActivity.class);
        // pass in widget id to the activity which will be used to get proper widget id
        // to b used to get the proper wiget data based off that id
        intent.putExtra(PENDING_INTENT_RECIPE_EXTRA_CONFIG, appWidgetId);
        // set up pendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                mContext,
                PENDING_INTENT_RECIPE_CLICK_CONFIG + appWidgetId,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        // Widgets allow click handlers to only launch pending intents
        // id is the id of the xml element in the widget layout
        mRemoteViews.setOnClickPendingIntent(R.id.main_recipe_item_name, pendingIntent);
        // return views
        return mRemoteViews;
    }

    /**
     * This happens at every update interval (which is specified in recipe_widget_info),
     * this can maybe be seen as a starting point
     *
     *
     * @param context
     * @param appWidgetManager  gives information on all widgets on home screen, and can force updates on all widgets
     * @param appWidgetIds      id of widgets
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // This will loop every widget instance
        for (int appWidgetId : appWidgetIds) {
            // update widget. this function will have all functionality to do the update
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        // run this on every widget update for possible UI layout changes
        // to be acted upon in the updateAppWidget app
        // such as re-sizing
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }
}

