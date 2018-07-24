package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import io.dev.tanners.bakerhelper.model.Step;

/**
 * steps class for static fragments
 */
public class StepFragment extends StepFragmentBase {
    // this is used for static fragment implementation
    // other methods are used for dynamic
    protected FragmentStepData mCallback;

    /**
     *
     */
    public StepFragment() {
        // Required empty public constructor
    }

    /**
     * @param mStep
     * @return
     */
    public static StepFragment newInstance(Step mStep) {
        StepFragment mFragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(DYNAMIC_STEP_DATA, mStep);
        mFragment.setArguments(args);
        return mFragment;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // check for null callback
        if(mCallback != null)
            mStep = mCallback.getStep();
        // need data before calling parent that uses those resources
        super.onActivityCreated(savedInstanceState);
        // set up toolbar
        setUpToolbar();
    }

    /**
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // set context object
        mContext = context;
        // check if context is attached to call back
        if (context instanceof FragmentStepData) {
            mCallback = (FragmentStepData) context;
        } else {
            mCallback = null;
            // throw exception
            throw new RuntimeException(context.toString()
                    + " must implement FragmentStepData");
        }
    }

    /**
     * set up tool bar
     */
    private void setUpToolbar()
    {
        Toolbar mToolbar = (Toolbar) mView.findViewById(R.id.main_toolbar);
        // set short description as ui element
        mToolbar.setTitle(mStep.getShortDescription());
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    /**
     * step call back
     */
    public interface FragmentStepData {
        /**
         * get step object
         *
         * @return
         */
        public Step getStep();
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
