package io.dev.tanners.bakerhelper;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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
import io.dev.tanners.bakerhelper.aac.db.RecipeDatabase;
import io.dev.tanners.bakerhelper.model.Ingredient;
import io.dev.tanners.bakerhelper.model.Recipe;
import io.dev.tanners.bakerhelper.model.Step;
import io.dev.tanners.bakerhelper.model.support.BaseBakerAdapter;
import io.dev.tanners.bakerhelper.network.GenericLoader;
import io.dev.tanners.bakerhelper.support.DataUtil;
import io.dev.tanners.bakerhelper.widget.config.GlobalConfig;
import static android.content.Context.MODE_PRIVATE;
import static io.dev.tanners.bakerhelper.widget.RecipeWidgetConfigure.PENDING_INTENT_RECIPE_EXTRA;
import static io.dev.tanners.bakerhelper.widget.RecipeWidgetProvider.PENDING_INTENT_RECIPE_EXTRA_CONFIG;

public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>, DataUtil {
    private final static int RECIPE_FRAGMENT_LOADER = 2468;
//    public final static String RECIPE_DATA = "DATA_FOR_RECIPE";
    public final static String STEP_ADAPTER_POS = "POS_OF_STEP";
    public final static String ING_ADAPTER_POS = "POS_OF_ING";
    private Recipe mRecipe;
    private RecyclerView mStepRecyclerView;
    private RecyclerView mIngredientRecyclerView;
    private View view;
    // Define a new interface OnImageClickListener that triggers a callback in the host activity
    private FragmentData mCallback;
    private Context mContext;
    // state to mark if the data being passed in is a newIntent
    private boolean newIntent = false;
    // the intent that the newIntentbool uses
    private Intent newDataIntent;

    public RecipeFragment() {
        // Required empty public constructor
    }

    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    /**
     * Load new data from activity due to pending intent from widget
     */
    @Override
    public void loadNewData(Intent mIntent) {
        Log.d("WIDGET", "NEW LOAD");
        // clar current data to trigger update
        mRecipe = null;
        newIntent = true;
        this.newDataIntent = mIntent;

        loadLoader();
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
        setUpFragment();
//        // get data from activity
//        if(mCallback != null) {
//            mRecipe = mCallback.getData();
//
//            if(mRecipe != null)
//                loadResources();
//            else
//            {
//                // if null, could mean data
//                // is passed via pref from widget
//                loadLoader();
//            }
//        }
    }

    private void setUpFragment()
    {
        // get data from activity
        if(mCallback != null) {
            mRecipe = mCallback.getData();

            if(mRecipe != null)
                loadResources();
            else
            {
                // if null, could mean data
                // is passed via pref from widget
                loadLoader();
            }
        }
    }

    private void loadLoader()
    {
        LoaderManager mLoaderManager = getActivity().getSupportLoaderManager();

        Loader<Boolean> mLoader = mLoaderManager.getLoader(RECIPE_FRAGMENT_LOADER);
        // check loader instance
        if(mLoader != null)
            mLoaderManager.initLoader(RECIPE_FRAGMENT_LOADER, null, this).forceLoad();
        else
            mLoaderManager.restartLoader(RECIPE_FRAGMENT_LOADER, null, this).forceLoad();
    }

    private void loadResources()
    {
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

    private SharedPreferences getSharedPreferences(int mWidgetId)
    {
        return getActivity().getSharedPreferences(
                GlobalConfig.getWidgetSharedPreferenceKey(mWidgetId),
                MODE_PRIVATE
        );
    }

    @NonNull
    @Override
    public Loader<Boolean> onCreateLoader(int id, @Nullable Bundle args) {
        return new GenericLoader(mContext, args, new GenericLoader.OnLoadInBackGroundCallBack() {
            @Override
            public boolean _do() {
                // needed a way to pass in widget id to this fragment, so used the intent
                // this id is used for the shared prefs to get proper widget data
                // since the configure class and provider both have the same way to pass intent
                // we check for both ways, this is a fix where the pending intents were getting confused
                // with one another
                if(newIntent)
                {
                    // get widget id from passed in intent, not old acitvity intent
                    int mWidgetId = newDataIntent.getIntExtra(PENDING_INTENT_RECIPE_EXTRA_CONFIG, -1);
                    loadRecipeDataFromWidget(mWidgetId);
                    newIntent = false;
                    return true;
                }
                if(getActivity().getIntent().hasExtra(PENDING_INTENT_RECIPE_EXTRA)) {
                    int mWidgetId = getActivity().getIntent().getIntExtra(PENDING_INTENT_RECIPE_EXTRA, -1);
                    loadRecipeDataFromWidget(mWidgetId);
                    return true;
                }
                else if(getActivity().getIntent().hasExtra(PENDING_INTENT_RECIPE_EXTRA_CONFIG)) {
                    int mWidgetId = getActivity().getIntent().getIntExtra(PENDING_INTENT_RECIPE_EXTRA_CONFIG, -1);
                    loadRecipeDataFromWidget(mWidgetId);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadRecipeDataFromWidget(int mWidgetId)
    {
        final RecipeDatabase mDb = RecipeDatabase.getInstance(getContext());

        // use the widget id as part of the key to get the proper recipe
        mRecipe = mDb.getRecipeDao().loadRecipeById(
                getSharedPreferences(mWidgetId).getInt(
                        GlobalConfig.getWidgetSharedPreferenceDbIdKey(mWidgetId),
                        AppWidgetManager.INVALID_APPWIDGET_ID
                )
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        loadResources();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {

    }




}
