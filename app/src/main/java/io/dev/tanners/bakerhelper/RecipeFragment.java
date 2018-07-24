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
import io.dev.tanners.bakerhelper.util.AdapterUtil;
import io.dev.tanners.bakerhelper.widget.config.GlobalConfig;
import static android.content.Context.MODE_PRIVATE;
import static io.dev.tanners.bakerhelper.widget.RecipeWidgetConfigure.PENDING_INTENT_RECIPE_EXTRA;
import static io.dev.tanners.bakerhelper.widget.RecipeWidgetProvider.PENDING_INTENT_RECIPE_EXTRA_CONFIG;

public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean>, DataUtil {
    // identifies loader used here
    private final static int RECIPE_FRAGMENT_LOADER = 2468;
    // keys for onrestore list pos data
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
    // the intent that the newIntent bool uses
    private Intent newDataIntent;

    /**
     *
     */
    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * @return
     */
    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    /**
     * Load new data from activity due to pending intent from widget
     */
    @Override
    public void loadNewData(Intent mIntent) {
        // clear current data to trigger update
        mRecipe = null;
        // set bool for later use on selecting which method to load data
        newIntent = true;
        // set class var to be used later
        this.newDataIntent = mIntent;
        // load loader that will call db async with
        // info on what data to grab from db
        loadLoader();
    }

    /**
     * interface that calls the host's method that implements this interface
     */
    public interface FragmentData {
        public Recipe getData();
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recipe, container, false);
        // return view
        return view;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // set up fragment
        setUpFragment();
    }

    /**
     * set up fragment's data and UI
     */
    private void setUpFragment()
    {
        // get data from activity
        if(mCallback != null) {
            mRecipe = mCallback.getData();
            // data returned from callback means recipe was passed from main activity
            if(mRecipe != null)
                loadResources();
            // if this is null, chances are the data passed in from getData
            // which is a callback used that grabs data passed into the
            // host activity via intent, was not passed in that way
            else
            {
                // if null, could mean data
                // is passed via pref from widget
                loadLoader();
            }
        }
    }

    /**
     * load loader used for data grathering
     */
    private void loadLoader()
    {
        // get loader manager
        LoaderManager mLoaderManager = getActivity().getSupportLoaderManager();
        // get loader
        Loader<Boolean> mLoader = mLoaderManager.getLoader(RECIPE_FRAGMENT_LOADER);
        // check loader instance
        if(mLoader != null)
            mLoaderManager.initLoader(RECIPE_FRAGMENT_LOADER, null, this).forceLoad();
        else
            mLoaderManager.restartLoader(RECIPE_FRAGMENT_LOADER, null, this).forceLoad();
    }

    /**
     * load ui resources from recipe data
     */
    private void loadResources()
    {
        // if data is not null
        if(mRecipe != null)
        {
            // set up ingredients if not null
            if(mRecipe.getIngredients() != null)
                setUpIngredientList(mRecipe.getIngredients());
            // set up steps, with call back
            if(mRecipe.getSteps() !=null)
                setUpStepList(mRecipe.getSteps(), setStepAdapterStateCallback());
            // set up toolbar
            setUpToolbar();
        }
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // save context locally
        mContext = context;
        // check if needed fragment is attached to context
        try {
            mCallback = (FragmentData) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentData");
        }
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    /**
     * save list's positions
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(mStepRecyclerView != null)
            savedInstanceState.putParcelable(STEP_ADAPTER_POS, mStepRecyclerView.getLayoutManager().onSaveInstanceState());
        if(mIngredientRecyclerView != null)
            savedInstanceState.putParcelable(ING_ADAPTER_POS, mIngredientRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    /**
     * restore list's positions
     *
     * @param savedInstanceState
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // check for null saved state
        if(savedInstanceState != null)
        {
            // restore steps list's pos
            mStepRecyclerView.getLayoutManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(STEP_ADAPTER_POS)
            );
            // restore ingredient list's pos
            mIngredientRecyclerView.getLayoutManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(ING_ADAPTER_POS)
            );
        }
    }

    /**
     * set up toolbar
     */
    private void setUpToolbar()
    {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.main_toolbar);
        // use name of recipe item
        mToolbar.setTitle(mRecipe.getName());
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    /**
     * set up step's adapter callback
     * this behaviors differently based on ui config
     *
     * @return
     */
    private OnClicked setStepAdapterStateCallback()
    {
        // not a tablet
        if(view.findViewById(R.id.recipe_step_container) == null)
        {
            // onclick will launch step activity
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
            // onlick will load steps into complementary fragment
            return new OnClicked() {
                @Override
                public void stepAction(Step mStep) {
                    // In two-pane mode, add initial BodyPartFragments to the screen
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    // Creating a new head fragment
                    StepFragmentDynamic mStepFragment = StepFragmentDynamic.newInstance(mStep);
                    // replace fragment
                    fragmentManager.beginTransaction()
                            .replace(R.id.recipe_step_container, mStepFragment)
                            .commit();
                }
            };

        }
    }

    /**
     * set up step's adapter
     *
     * @param mSteps
     * @param mOnClicked
     */
    private void setUpStepList(List<Step> mSteps, OnClicked mOnClicked)
    {
        mStepRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_steps);
        LinearLayoutManager mStepRecyclerLayoutManager = new LinearLayoutManager(mContext);
        mStepRecyclerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // set adapter with callback
        StepAdapter mStepAdapter = new StepAdapter(mSteps, mOnClicked);
        mStepRecyclerView.setLayoutManager(mStepRecyclerLayoutManager);
        mStepRecyclerView.setAdapter(mStepAdapter);
    }

    /**
     * set up ingredient's adapter
     *
     * @param mIngredients
     */
    private void setUpIngredientList(List<Ingredient> mIngredients)
    {
        mIngredientRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_ingredients);
        LinearLayoutManager mIngredientRecyclerLayoutManager = new LinearLayoutManager(mContext);
        mIngredientRecyclerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        // set adapter will callback
        IngredientAdapter mIngredientAdapter = new IngredientAdapter(mIngredients);
        mIngredientRecyclerView.setLayoutManager(mIngredientRecyclerLayoutManager);
        mIngredientRecyclerView.setAdapter(mIngredientAdapter);
    }

    /**
     * ingredients Adapter
     */
    private class IngredientAdapter  extends BaseBakerAdapter<Ingredient> {

        /**
         * @param mIngredients
         */
        public IngredientAdapter(List<Ingredient> mIngredients) {
            this.mBase = mIngredients;
        }

        /**
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient, parent, false);

            return new IngredientViewHolder(view);
        }

        /**
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            IngredientViewHolder mHolder = (IngredientViewHolder) holder;
            // get item at pos
            Ingredient mItem = mBase.get(position);
            // set textview with ingredients formatted into one string
            mHolder.mIngredientDetail.setText(AdapterUtil.formatIngredient(mContext, mItem));
        }

        /**
         * format ingredients into one string
         *
         * @param mItem
         * @return
         */
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

        /**
         * @return
         */
        @Override
        public int getItemCount() {
            return mBase == null ? 0 : mBase.size();
        }

        /**
         *
         */
        private class IngredientViewHolder extends RecyclerView.ViewHolder {
            private TextView mIngredientDetail;

            public IngredientViewHolder(View itemView) {
                super(itemView);
                // get textview reference
                mIngredientDetail = itemView.findViewById(R.id.recipe_ingredient_text);
            }
        }
    }

    /**
     * step adapter
     */
    private class StepAdapter extends BaseBakerAdapter<Step> {
        private OnClicked mOnClick;

        /**
         * @param mSteps
         * @param mOnClick
         */
        public StepAdapter(List<Step> mSteps, OnClicked mOnClick) {
            this.mBase = mSteps;
            this.mOnClick = mOnClick;
        }

        /**
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_item, parent, false);

            return new StepViewHolder(view);
        }

        /**
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StepViewHolder mHolder = (StepViewHolder) holder;
            Step mStep = mBase.get(position);
            // add short description as item ui
            mHolder.mShortDescription.setText(mStep.getShortDescription());
        }

        /**
         * hold's steps views
         */
        private class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView mShortDescription;

            /**
             * @param itemView
             */
            public StepViewHolder(View itemView) {
                super(itemView);

                // due to weird bug, the textview and view need to have the onclick set
                itemView.setOnClickListener(this);
                mShortDescription = itemView.findViewById(R.id.recipe_step_short_desc);
                mShortDescription.setOnClickListener(this);
            }

            /**
             * @param v
             */
            @Override
            public void onClick(View v) {
                // get data at pos
                Step mStep = (Step) mBase.get(getAdapterPosition());
                // call callback method
                mOnClick.stepAction(mStep);
            }
        }
    }

    /**
     *
     */
    public interface OnClicked {
        public void stepAction(Step mStep);
    }

    /**
     * get shared preferences object
     *
     * @param mWidgetId
     * @return
     */
    private SharedPreferences getSharedPreferences(int mWidgetId)
    {
        return getActivity().getSharedPreferences(
                GlobalConfig.getWidgetSharedPreferenceKey(mWidgetId),
                MODE_PRIVATE
        );
    }

    /**
     * @param id
     * @param args
     * @return
     */
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
                // intent data came from provider
                if(getActivity().getIntent().hasExtra(PENDING_INTENT_RECIPE_EXTRA)) {
                    int mWidgetId = getActivity().getIntent().getIntExtra(PENDING_INTENT_RECIPE_EXTRA, -1);
                    loadRecipeDataFromWidget(mWidgetId);
                    return true;
                }
                // intent data came from configuration class
                else if(getActivity().getIntent().hasExtra(PENDING_INTENT_RECIPE_EXTRA_CONFIG)) {
                    int mWidgetId = getActivity().getIntent().getIntExtra(PENDING_INTENT_RECIPE_EXTRA_CONFIG, -1);
                    loadRecipeDataFromWidget(mWidgetId);
                    return true;
                }
                // if all else fails
                return false;
            }
        });
    }

    /**
     * load recipe based on shared preference based off widget id
     *
     * @param mWidgetId
     */
    private void loadRecipeDataFromWidget(int mWidgetId)
    {
        // get db instance
        final RecipeDatabase mDb = RecipeDatabase.getInstance(getContext());
        // use the widget id as part of the key to get the proper recipe
        mRecipe = mDb.getRecipeDao().loadRecipeById(
                getSharedPreferences(mWidgetId).getInt(
                        GlobalConfig.getWidgetSharedPreferenceDbIdKey(mWidgetId),
                        AppWidgetManager.INVALID_APPWIDGET_ID
                )
        );
    }

    /**
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Boolean> loader, Boolean data) {
        // load ui resources
        // unlike the other call, this is called
        // if other means of retrieving the data fail
        loadResources();
    }

    /**
     * @param loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Boolean> loader) {
        // not needed
    }
}
