package io.dev.tanners.bakerhelper.aac;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import java.io.IOException;
import java.util.List;
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.aac.db.RecipeExecutor;
import io.dev.tanners.bakerhelper.model.Recipe;

public class RecipeRepository {
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

    /**
     * get all recipes
     *
     * @return
     * @throws IOException
     */
    public LiveData<List<Recipe>> getAllRecipes() throws IOException {
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

        // data will be empty on return but live data will update it when it comes
        // if the class calling this method uses the live data's
        // observer onChange method
        return mData;
    }
}
