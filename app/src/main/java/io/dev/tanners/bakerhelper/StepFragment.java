package io.dev.tanners.bakerhelper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import io.dev.tanners.bakerhelper.model.Step;

// TODO fix text size on this page in tablet mode
// TODO put fake photo to test image size and style (just in case)

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

        super.onActivityCreated(savedInstanceState);
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

    public interface FragmentStepData {
        Step getStep();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
