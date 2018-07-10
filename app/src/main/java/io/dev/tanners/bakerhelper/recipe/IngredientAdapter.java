package io.dev.tanners.bakerhelper.recipe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import io.dev.tanners.bakerhelper.R;
import io.dev.tanners.bakerhelper.model.Ingredient;

public class IngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Ingredient> mIngredients;
    private Context mContext;

    public IngredientAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public IngredientAdapter( Context mContext, List<Ingredient> mIngredients) {
        this(mContext);
        this.mIngredients = mIngredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IngredientViewHolder mHolder = (IngredientViewHolder) holder;
        Ingredient mItem = mIngredients.get(position);
//
//
//        StringBuilder mBuilder = new StringBuilder();
//
//        mBuilder.append(mContext.getString(R.string.recipe_ingr_amount_start));
//        mBuilder.append(mItem.getQuantity());
//        mBuilder.append(Context.getString(R.string.space));
//        mBuilder.append(mItem.getMeasure());
//        mBuilder.append(mContext.getString(R.string.recipe_ingr_amount_concat));
//        mBuilder.append(mItem.getIngredient());

        mHolder.mIngredientDetail.setText(formatIngredient(mItem));
    }

    private String formatIngredient(Ingredient mItem)
    {
        StringBuilder mBuilder = new StringBuilder();

        mBuilder.append(mItem.getQuantity());
        mBuilder.append(mContext.getString(R.string.space));
        mBuilder.append(mItem.getMeasure());
        mBuilder.append(mContext.getString(R.string.recipe_ingr_amount_concat));
        mBuilder.append(mItem.getIngredient());

        return mBuilder.toString();
//        StringBuilder mBuilder = new StringBuilder();
//
//        for(Ingredient mIngredient : mIngredients) {
//            mBuilder.append(mIngredient.getQuantity());
//            mBuilder.append(mIngredient.getMeasure());
//            mBuilder.append(" of ");
//            mBuilder.append(mIngredient.getIngredient());
//            mBuilder.append(System.lineSeparator());
//        }
//
//        return mBuilder.toString();
    }

    @Override
    public int getItemCount() {
        return mIngredients == null ? 0 : mIngredients.size();
    }

    private class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView mIngredientDetail;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            mIngredientDetail = itemView.findViewById(R.id.recipe_ingredient_text);
        }
    }

    public void updateAdapter(List<Ingredient> mIngredients)
    {
        this.mIngredients = mIngredients;
        notifyDataSetChanged();
    }
}
