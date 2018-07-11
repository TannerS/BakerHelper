package io.dev.tanners.bakerhelper.aac;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Recipe;

// here we can change the method of getting the data
// for now its a db call, but we could change it here to be a network call
public class RecipeRepository {
    private Context mContext;

    public RecipeRepository(Context mContext) throws IOException {
        this.mContext = mContext;
        parseJsonData();
    }

    private List<Recipe> parseJsonData() throws IOException {
        // mapper object
        ObjectMapper mapper = new ObjectMapper();
        // name of asset file to read recipe data from
        final String BAKING_DATA = "baking.json";
        // create object from file and return
        return Arrays.asList(
                mapper.readValue(
                        mContext.getAssets().open(BAKING_DATA),
                        // https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
                        Recipe[].class
                )
        );
    }

    public LiveData<List<Recipe>> getAllRecipes() throws IOException {
        MutableLiveData<List<Recipe>> mData = new MutableLiveData<List<Recipe>>();
        mData.setValue(parseJsonData());
        return mData;
    }
}
