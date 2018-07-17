package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import io.dev.tanners.bakerhelper.model.Step;

public class StepFragment extends StepFragmentBase {
    // this is used for static fragment implementation
    // other methods are used for dynamic
    protected FragmentStepData mCallback;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(Step mStep) {
        StepFragment mFragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(DYNAMIC_STEP_DATA, mStep);
        mFragment.setArguments(args);
        return mFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(mCallback != null)
            mStep = mCallback.getStep();
        // need data before calling parent that uses those resources
        super.onActivityCreated(savedInstanceState);

        setUpToolbar();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;

        if (context instanceof FragmentStepData) {
            mCallback = (FragmentStepData) context;
        } else {
            mCallback = null;

            throw new RuntimeException(context.toString()
                    + " must implement FragmentStepData");
        }
    }

    private void setUpToolbar()
    {
        Toolbar mToolbar = (Toolbar) mView.findViewById(R.id.main_toolbar);
        mToolbar.setTitle(mStep.getShortDescription());
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
    }

    public interface FragmentStepData {
        Step getStep();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
