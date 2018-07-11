package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.model.support.BaseBakerAdapter;

public class RecipeFragment extends Fragment {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";
    public final static String RECIPE_RESTORE_DATA = "DATA_FOR_RECIPE_RESTORE";
    public final static String STEP_ADAPTER_POS = "POS_OF_STEP";
    public final static String ING_ADAPTER_POS = "POS_OF_ING";

    private Recipe mRecipe;
    private RecyclerView mStepRecyclerView;
    private RecyclerView mIngredientRecyclerView;
    private LinearLayoutManager mStepRecyclerLayoutManager;
    private LinearLayoutManager mIngredientRecyclerLayoutManager;
    private IngredientAdapter mIngredientAdapter;
    private StepAdapter mStepAdapter;
    private View view;
    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    private FragmentData mCallback;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    // interface that calls the host's method that implements this interface
    public interface FragmentData {
        public Recipe getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recipe, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // get data from activity
        mRecipe = mCallback.getData();

        setUpIngredientList(mRecipe.getIngredients());
        setUpStepList(mRecipe.getSteps());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // todo save and restore adapter pos//
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (FragmentData) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentData");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    // https://stackoverflow.com/a/29166336/2449314
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable(STEP_ADAPTER_POS, mStepRecyclerView.getLayoutManager().onSaveInstanceState());
        savedInstanceState.putParcelable(ING_ADAPTER_POS, mIngredientRecyclerView.getLayoutManager().onSaveInstanceState());

        // TODO is this needed when we have the getIntent().hasExtra?
        // TODO this is for current state, such as scroll position
    }

    @Override
    // https://stackoverflow.com/a/29166336/2449314
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            mStepRecyclerView.getLayoutManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(STEP_ADAPTER_POS)
            );

            mIngredientRecyclerView.getLayoutManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(ING_ADAPTER_POS)
            );
        }
    }

    private void setUpStepList(List<Step> mSteps)
    {
        mStepRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_steps);
        mStepRecyclerLayoutManager = new LinearLayoutManager(getContext());
        mStepAdapter = new StepAdapter(mSteps);
        mStepRecyclerView.setLayoutManager(mStepRecyclerLayoutManager);
        mStepRecyclerView.setAdapter(mStepAdapter);
    }

    private void setUpIngredientList(List<Ingredient> mIngredients)
    {
        mIngredientRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_ingredients);
        mIngredientRecyclerLayoutManager = new LinearLayoutManager(getContext());
        mIngredientAdapter = new IngredientAdapter(mIngredients);
        mIngredientRecyclerView.setLayoutManager(mIngredientRecyclerLayoutManager);
        mIngredientRecyclerView.setAdapter(mIngredientAdapter);
    }

    private class IngredientAdapter  extends BaseBakerAdapter<Ingredient> {

        public IngredientAdapter(List<Ingredient> mIngredients) {
            this.mBase = mIngredients;
            Log.i("ADAPTER", String.valueOf(mBase.size()));
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
            Ingredient mItem = mBase.get(position);

            mHolder.mIngredientDetail.setText(formatIngredient(mItem));
        }

        private String formatIngredient(Ingredient mItem)
        {
            StringBuilder mBuilder = new StringBuilder();

            mBuilder.append(mItem.getQuantity());
            mBuilder.append(getString(R.string.space));
            mBuilder.append(mItem.getMeasure());
            mBuilder.append(getString(R.string.recipe_ingr_amount_concat));
            mBuilder.append(mItem.getIngredient());

            return mBuilder.toString();
        }

        @Override
        public int getItemCount() {
            return mBase == null ? 0 : mBase.size();
        }

        private class IngredientViewHolder extends RecyclerView.ViewHolder {
            private TextView mIngredientDetail;

            public IngredientViewHolder(View itemView) {
                super(itemView);

                mIngredientDetail = itemView.findViewById(R.id.recipe_ingredient_text);
            }
        }
    }

    private class StepAdapter extends BaseBakerAdapter<Step> {

        public StepAdapter(List<Step> mSteps) {
            this.mBase = mSteps;
        }

        @NonNull
        @Override
        public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_header, parent, false);

            return new StepViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StepViewHolder mHolder = (StepViewHolder) holder;
            Step mStep = mBase.get(position);

            mHolder.mShortDescription.setText(mStep.getShortDescription());
        }

        private class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mShortDescription;

            public StepViewHolder(View itemView) {
                super(itemView);

                mShortDescription = itemView.findViewById(R.id.recipe_step_short_desc);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
