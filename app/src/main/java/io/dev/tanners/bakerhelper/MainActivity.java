package io.dev.tanners.bakerhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import io.dev.tanners.bakerhelper.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.db.RecipeExecutor;
import io.dev.tanners.bakerhelper.db.config.DBConfig;
import io.dev.tanners.bakerhelper.model.Recipe;

public class MainActivity extends AppCompatActivity {
    // reference to database object
    private RecipeDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // load database and data (if data is needed)
        try {
            initDb();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                // loop all recipes
////                for (Recipe mRecipe : mRecipes) {
//                    // insert recipe data
//                    for(Recipe mRecipe : mDb.getRecipeDao().loadAllRecipes())
//                    {
//                        Log.i("TESTTESTTEST", ((new Gson()).toJson(mRecipe)));
//                    }
////                }
//            }
//        });
    }

    private void initDb() throws IOException {
        // init db
        mDb = RecipeDatabase.getInstance(getApplicationContext());
        // load db objects if needed
        loadDatabase();
    }

    private void loadDatabase() throws IOException {
        // check if data base has been created before in another app instance
        if(!getDatabasePath(DBConfig.DATABASE_NAME).exists())
        {
            Log.i("DATABASE", "DATABASE NOT CREATED ON START");
            // database does not exist, load in all data for one time only
            insertMultiple(parseJsonData());
        }
        else
        {
            Log.i("DATABASE", "DATABASE CREATED ON START");
            // database does exist, do nothing
        }
    }

    private List<Recipe> parseJsonData() throws IOException {
        // mapper object
        ObjectMapper mapper = new ObjectMapper();
        // name of asset file to read recipe data from
        final String BAKING_DATA = "baking.json";
        // create object from file and return
        return Arrays.asList(
                mapper.readValue(
                        getAssets().open(BAKING_DATA),
                        // https://stackoverflow.com/questions/6349421/how-to-use-jackson-to-deserialise-an-array-of-objects
                        Recipe[].class
                )
        );
    }

    private void insertMultiple(final List<Recipe> mRecipes) {
        // get executor to be able to run insert on separate thread
        RecipeExecutor.getInstance().mDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                // loop all recipes
                for (Recipe mRecipe : mRecipes) {
                    // insert recipe data
                    mDb.getRecipeDao().insertRecipe(mRecipe);
                }
            }
        });
    }
}
