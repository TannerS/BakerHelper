package io.dev.tanners.bakerhelper.aac;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.aac.db.RecipeExecutor;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.util.SimpleSnackBarBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RecipeRepository {
    private MutableLiveData<List<Recipe>> mData;
    private Context mContext;

    public RecipeRepository(Context mContext) throws IOException {
        this.mContext = mContext;
    }

    public LiveData<List<Recipe>> getAllRecipes() throws IOException {
//        mData = new MutableLiveData<List<Recipe>>();
//         final LiveData<List<Recipe>> mData = new MutableLiveData<List<Recipe>>();
         mData = new MutableLiveData<List<Recipe>>();
//        LiveData<List<Recipe>> mData = new LiveData<List<Recipe>>();

//         TODO dagger 2 ?
        final RecipeDatabase mDb =   RecipeDatabase.getInstance(mContext);

        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                // https://stackoverflow.com/a/44293595/2449314
                mData.postValue(mDb.getRecipeDao().loadAllRecipes());
            }
        });

        // data will be empty on return but live data will update it when it comes
        return mData;
    }
}
