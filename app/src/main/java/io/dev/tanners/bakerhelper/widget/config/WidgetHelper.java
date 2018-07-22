//package io.dev.tanners.bakerhelper.widget.config;
//
//import android.content.Context;
//import android.content.Intent;
//
//import io.dev.tanners.bakerhelper.RecipeActivity;
//
//import static io.dev.tanners.bakerhelper.widget.RecipeWidgetProvider.PENDING_INTENT_RECIPE_EXTRA;
//
//public class WidgetHelper {
//    private static Intent mIntent;
//
//    public static Intent getIntent(Context mContext, int mWidgetId)
//    {
//        mIntent = new Intent(mContext, RecipeActivity.class);
//        mIntent.setAction(ACTION_WIDGET_CLICK);
//        // pass in widget id to the activity which will be used to get proper widget id
//        // to b used to get the proper wiget data based off that id
//        mIntent.putExtra(PENDING_INTENT_RECIPE_EXTRA, mWidgetId);
//
//        return mIntent;
//    }
//}
