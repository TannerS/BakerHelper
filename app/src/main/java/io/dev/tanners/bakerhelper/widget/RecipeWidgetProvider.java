package io.dev.tanners.bakerhelper.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import io.dev.tanners.bakerhelper.MainActivity;
import io.dev.tanners.bakerhelper.R;

/**
 * A convenience class to aid in implementing an AppWidget provider.
 * Everything you can do with AppWidgetProvider, you can do with a regular BroadcastReceiver.
 * AppWidgetProvider merely parses the relevant fields out of the Intent
 * that is received in onReceive(Context,Intent), and calls hook methods with the received extras.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {
    public static final int PENDING_INTENT_RECIPE_CLICK = 2;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {






        Log.i("WIDGET", "updateAppWidget");
















        Bundle mOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

        int mWidth = mOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews mRemoteViews;

//        if(mWidth < 300) {
            mRemoteViews = getSingleViewLayout(context);

        mRemoteViews = createSinglePendingIntent(context, mRemoteViews);

//        } else {
//            mRemoteViews = getListViewLayout(context);
//        }










        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
    }


    /**
     * Get the view hierarchy for our widget hierarchy
     * This is for gridview
     *
     * @param mContext
     * @return
     */
    private static RemoteViews getListViewLayout(Context mContext)
    {
        // Construct the RemoteViews object
        // this will have all the views for the layut
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget_grid);

        return views;
    }

    /**
     * Get the view hierarchy for our widget hierarchy
     * This is for single recipe
     *
     * @param mContext
     * @return
     */
    private static RemoteViews getSingleViewLayout(Context mContext)
    {
        // Construct the RemoteViews object
        // this will have all the views for the layut
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);

        return views;
    }

    /**
     * This is the widget's way off implement 'onClick'
     * for single recipe view
     * NOTE: pending intent can also open up a broadcast, service or activity. our use will be an example
     *
     * @param mContext
     * @param mRemoteViews
     */
    private static RemoteViews createSinglePendingIntent(Context mContext, RemoteViews mRemoteViews)
    {
        // create class to load
        Intent intent = new Intent(mContext, MainActivity.class);
        // set up pendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, PENDING_INTENT_RECIPE_CLICK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Widgets allow click handlers to only launch pending intents
        // id is the id of the xml element in the widget layout
        mRemoteViews.setOnClickPendingIntent(R.id.main_recipe_item_name, pendingIntent);

        return mRemoteViews;
    }

    /**
     * This is the widget's way off implement 'onClick'
     * for gridview item's recipe view
     * NOTE: pending intent can also open up a broadcast, service or activity. our use will be an example
     *
     * @param mContext
     * @param mRemoteViews
     */
    private void createListPendingIntent(Context mContext, RemoteViews mRemoteViews)
    {
//        // create class to load
//        Intent intent = new Intent(mContext, MainActivity.class);
//        // set up pendingIntent
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, PENDING_INTENT_RECIPE_CLICK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Widgets allow click handlers to only launch pending intents
//        // id is the id from the xml element in the widget layout
//        mRemoteViews.setOnClickPendingIntent(R.id., pendingIntent);
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

        Log.i("WIDGET", "onUpdate");


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

