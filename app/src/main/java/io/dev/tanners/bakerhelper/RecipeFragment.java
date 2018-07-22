package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import io.dev.tanners.bakerhelper.test.IdlingResourceHelper;

public class RecipeFragment extends Fragment {
    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";
    public final static String STEP_ADAPTER_POS = "POS_OF_STEP";
    public final static String ING_ADAPTER_POS = "POS_OF_ING";

    private Recipe mRecipe;
    private RecyclerView mStepRecyclerView;
    private RecyclerView mIngredientRecyclerView;
    private View view;
    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    private FragmentData mCallback;
    private Context mContext;

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
        if(mCallback != null) {
            mRecipe = mCallback.getData();
        }

        if(mRecipe != null)
        {
            if(mRecipe.getIngredients() != null)
                setUpIngredientList(mRecipe.getIngredients());
            if(mRecipe.getSteps() !=null)
                setUpStepList(mRecipe.getSteps(), setStepAdapterStateCallback());
            setUpToolbar();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(mStepRecyclerView != null)
            savedInstanceState.putParcelable(STEP_ADAPTER_POS, mStepRecyclerView.getLayoutManager().onSaveInstanceState());
        if(mIngredientRecyclerView != null)
            savedInstanceState.putParcelable(ING_ADAPTER_POS, mIngredientRecyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
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


    private void setUpToolbar()
    {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.main_toolbar);
        mToolbar.setTitle(mRecipe.getName());
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    private OnClicked setStepAdapterStateCallback()
    {
        // not a tablet
        if(view.findViewById(R.id.recipe_step_container) == null)
        {
            return new OnClicked() {
                @Override
                public void stepAction(Step mStep) {
                    Intent intent = new Intent(mContext, StepActivity.class);
                    intent.putExtra(StepActivity.STEP_DATA, mStep);
                    startActivity(intent);
                }
            };
        }
        // possible tablet
        else {
            return new OnClicked() {
                @Override
                public void stepAction(Step mStep) {
                    // In two-pane mode, add initial BodyPartFragments to the screen
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    // Creating a new head fragment
                    StepFragmentDynamic mStepFragment = StepFragmentDynamic.newInstance(mStep);

                    fragmentManager.beginTransaction()
                            .replace(R.id.recipe_step_container, mStepFragment)
                            .commit();
                }
            };

        }
    }

    private void setUpStepList(List<Step> mSteps, OnClicked mOnClicked)
    {
        mStepRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_steps);
        LinearLayoutManager mStepRecyclerLayoutManager = new LinearLayoutManager(mContext);
        mStepRecyclerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        StepAdapter mStepAdapter = new StepAdapter(mSteps, mOnClicked);
        mStepRecyclerView.setLayoutManager(mStepRecyclerLayoutManager);
        mStepRecyclerView.setAdapter(mStepAdapter);
    }

    private void setUpIngredientList(List<Ingredient> mIngredients)
    {
        mIngredientRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_ingredients);
        LinearLayoutManager mIngredientRecyclerLayoutManager = new LinearLayoutManager(mContext);
        mIngredientRecyclerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        IngredientAdapter mIngredientAdapter = new IngredientAdapter(mIngredients);
        mIngredientRecyclerView.setLayoutManager(mIngredientRecyclerLayoutManager);
        mIngredientRecyclerView.setAdapter(mIngredientAdapter);
    }

    private class IngredientAdapter  extends BaseBakerAdapter<Ingredient> {

        public IngredientAdapter(List<Ingredient> mIngredients) {
            this.mBase = mIngredients;
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
        private OnClicked mOnClick;

        public StepAdapter(List<Step> mSteps, OnClicked mOnClick) {
            this.mBase = mSteps;
            this.mOnClick = mOnClick;
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

                // due to wierd bug, the textview and view need to have the onclick set
                itemView.setOnClickListener(this);
                mShortDescription = itemView.findViewById(R.id.recipe_step_short_desc);
                mShortDescription.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Step mStep = (Step) mBase.get(getAdapterPosition());
                mOnClick.stepAction(mStep);
            }
        }
    }

    public interface OnClicked {
        public void stepAction(Step mStep);
    }

}
