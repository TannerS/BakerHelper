package io.dev.tanners.bakerhelper.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RemoteViews;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.model.Recipe;

/*
    As per docs,

    "The App Widget host calls the configuration Activity and the
    configuration Activity should always return a result.
    The result should include the App Widget ID passed by the
    Intent that launched the Activity (saved in the Intent extras as EXTRA_APPWIDGET_ID)."

    and rest of code walk through...

    https://developer.android.com/guide/topics/appwidgets/#Configuring
    https://stackoverflow.com/a/40709721/2449314
 */
public class RecipeWidgetConfigure extends AppCompatActivity {
    private int mWidgetId;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






    }

    /**
     * Set activities return data (has to include widget id)
     */
    private void setActivityResult()
    {
        Intent mResult = new Intent();
        mResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
        setResult(RESULT_OK, mResult);
        finish();
    }



    /**
     *  Get the widget id from the intent that called this activity
     */
    private void getWidgetId()
    {
        Intent mIntent = getIntent();
        Bundle mExtras = mIntent.getExtras();

        if (mExtras != null) {
            mWidgetId = mExtras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    /**
     * Get recipe data from DB
     */
    private void setUpWidgetConfiguration()
    {

        getWidgetId();
        updateWidgetViews();
        setActivityResult();
    }

    /**
     * Update the widget's view since this has to be done manually
     */
    private void updateWidgetViews()
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        RemoteViews mViews = new RemoteViews(getPackageName(),
                R.layout.main_recipe);

//        mViews.setTextViewText(R.id.main_recipe_item_name, );


        appWidgetManager.updateAppWidget(mWidgetId, mViews);
    }





}
