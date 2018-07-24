package io.dev.tanners.bakerhelper.aac;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.aac.db.RecipeExecutor;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.network.NetworkCall;
import io.dev.tanners.bakerhelper.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipeRepository {
    private static RecipeRepository sInstance;
    // live data of the recipes data
    private MutableLiveData<List<Recipe>> mData;
    private Context mContext;

    /**
     * @param mContext
     * @throws IOException
     */
    public RecipeRepository(Context mContext) throws IOException {
        this.mContext = mContext;
    }

    public static RecipeRepository getInstance(Context mContext) {
        if (sInstance == null) {
            try {
                sInstance = new RecipeRepository(mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sInstance;
    }

//    /**
//     * get all recipes
//     *
//     * @return
//     * @throws IOException
//     */
//    public LiveData<List<Recipe>> getAllRecipes() throws IOException {
//        // live data of the lst
//        mData = new MutableLiveData<List<Recipe>>();
//        // db reference
//        final RecipeDatabase mDb = RecipeDatabase.getInstance(mContext);
//
//        mData.setValue(mDb.getRecipeDao().loadAllRecipes());
//
//        return mData;
//
//
//
//
//
//        // call to get data async from the db
////        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
////            @Override
////            public void run() {
////                // postValue to fix a error
////                // https://stackoverflow.com/a/44293595/2449314
////                mData.postValue(mDb.getRecipeDao().loadAllRecipes());
////            }
////        });
//
//        // data will be empty on return but live data will update it when it comes
//        // if the class calling this method uses the live data's
//        // observer onChange method
////        return mData;
//    }

    /**
     * Get data from network
     */
    public void pullNewData()
    {
        Log.i("VIEWMODEL", "PULLING NEW DATA");
        ObjectMapper mMapper = new ObjectMapper();
        // set up network api connection with json maooer
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConfig.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mMapper))
                .build();

        NetworkCall mNetworkCall = mRetrofit.create(NetworkCall.class);
        // enqueue callback on data call
        mNetworkCall.getRecipes().enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    // set object if data
                    setUpDbData(response.body());
                } else { }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
            }
        });
    }

    /**
     * get database instance and insert list of recipes
     */
    private void setUpDbData(final List<Recipe> mData)
    {
        RecipeExecutor.getInstance().mDiskIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        // get db instance
                        final RecipeDatabase mDb = RecipeDatabase.getInstance(mContext);
                        // insert recipes into db
                        mDb.getRecipeDao().insertRecipes(mData);
                    }
                }
        );
    }



    /**
     * get all recipes
     *
     * @return
     * @throws IOException
     */
    public LiveData<List<Recipe>> getRecipes() throws IOException {
        // live data of the lst
        mData = new MutableLiveData<List<Recipe>>();
        // db reference
        final RecipeDatabase mDb = RecipeDatabase.getInstance(mContext);

        // call to get data async from the db
        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                // postValue to fix a error
                // https://stackoverflow.com/a/44293595/2449314
                mData.postValue(mDb.getRecipeDao().loadAllRecipes());
            }
        });

        // data may be empty on return but live data will update it when it comes
        // if the class calling this method uses the live data's
        // observer onChange method
        return mData;
    }
}
