package io.dev.tanners.bakerhelper.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.content.Loader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.RecipeHelper;
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.aac.db.RecipeExecutor;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.network.NetworkCall;
import io.dev.tanners.bakerhelper.network.NetworkData;
import io.dev.tanners.bakerhelper.network.RecipeLoader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/*
    As per docs,

    "The App Widget host calls the configuration Activity and the
    configuration Activity should always return a result.
    The result should include the App Widget ID passed by the
    Intent that launched the Activity (saved in the Intent extras as EXTRA_APPWIDGET_ID)."

    and rest of code walk through...

    https://developer.android.com/guide/topics/appwidgets/#Configuring
    https://stackoverflow.com/a/40709721/2449314

    This class has a flow to it....

    1) onCreate -> load data from database
    2) if data exist, loadviews
    3) else, make network call
    4) after network call, populate data
    5) after db is populated, use existing list of data (data is in db but no need to call if same data already is in memory)
    6) set views
 */
public class RecipeWidgetConfigure extends RecipeHelper {
    private int mWidgetId;
    private List<Recipe> mRecipes;
    private static final int WIDGET_RECIPE_LOADER_NETWORK = 987654321;
    private static final int WIDGET_RECIPE_LOADER_DB = -987654321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get widget id
        getWidgetId();
        // load loader
        // this will get recipes from database
        // TODO check for data then network ?
        // then it will load the videos on the callback
        loadLoader(WIDGET_RECIPE_LOADER_DB, new LoaderManager.LoaderCallbacks<Boolean>() {
            @NonNull
            @Override
            public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
                return new RecipeLoader(RecipeWidgetConfigure.this, null, new RecipeLoader.OnLoadInBackGroundCallBack() {
                    @Override
                    public boolean _do() {
                        if(!getRecipes())
                        {
                            // no db data, so lets do network call
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                });
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
                // did db have data?
                if(!data)
                {
                    // no so try network to get data
                    getNetworkData();
                }
                else{
                    // if data load here, else this will be called in the network callback
                    // on the next loader call
                    setUpWidgetConfiguration();
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Boolean> loader) {

            }
        });
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

    private boolean getRecipes()
    {
        final RecipeDatabase mDb = RecipeDatabase.getInstance(getApplicationContext());
        mRecipes = mDb.getRecipeDao().loadAllRecipes();

        return mRecipes.size() > 0;
    }

    /**
     * Set up widget's configuration
     */
    private void setUpWidgetConfiguration()
    {
        updateWidgetViews();
        setActivityResult();
    }

    /**
     * Run after network call
     */
    @Override
    protected void onPostRequest()
    {
        // load loader but this time do db add
        loadLoader(WIDGET_RECIPE_LOADER_NETWORK, new LoaderManager.LoaderCallbacks<Boolean>() {
            @NonNull
            @Override
            public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
                return new RecipeLoader(RecipeWidgetConfigure.this, null, new RecipeLoader.OnLoadInBackGroundCallBack() {
                    @Override
                    public boolean _do() {
                        if(mRecipes != null || mRecipes.size() == 0)
                        {
                            final RecipeDatabase mDb = RecipeDatabase.getInstance(getApplicationContext());
                            // list should be full from network call
                            mDb.getRecipeDao().insertRecipes(mRecipes);
                            return true;
                        }

                        return false;
                    }
                });
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
                // did db have data?
                if(!data)
                {
                    // display error of no data from network call
                    displayMessage(findViewById(R.id.widget_main_container), R.string.widget_data_network_loading_error);
                }
                else {
                    // if data load here, this was our last chance to do so
                    setUpWidgetConfiguration();
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Boolean> loader) {

            }
        });
    }

    /**
     * Load data from db
     */
    protected void loadLoader(int mLoaderId, LoaderManager.LoaderCallbacks<Boolean> mLoaderManagerCallback)
    {
        // lets try to add data to db before using it now
        LoaderManager mLoaderManager = getSupportLoaderManager();

        Loader<Boolean> mLoader = mLoaderManager.getLoader(mLoaderId);


        if(mLoader != null)
            mLoaderManager.initLoader(mLoaderId, null, mLoaderManagerCallback).forceLoad();
        else
            mLoaderManager.restartLoader(mLoaderId, null, mLoaderManagerCallback).forceLoad();
    }


    /**
     * Update the widget's view since this has to be done manually
     */
    private void updateWidgetViews()
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        RemoteViews mViews = new RemoteViews(getPackageName(),
                R.layout.main_recipe);

        //TODO load list that memics main actiivty
//        mViews.setTextViewText(R.id.main_recipe_item_name, );


        appWidgetManager.updateAppWidget(mWidgetId, mViews);
    }

}

