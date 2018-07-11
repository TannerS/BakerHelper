package io.dev.tanners.bakerhelper.network;

import java.util.List;

import io.dev.tanners.bakerhelper.model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;
import static io.dev.tanners.bakerhelper.network.NetworkData.FILE;

public interface NetworkCall {
    @GET(FILE)
    Call<List<Recipe>> getRecipes();
}