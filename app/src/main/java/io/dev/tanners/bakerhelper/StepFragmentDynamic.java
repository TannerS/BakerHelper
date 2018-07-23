package io.dev.tanners.bakerhelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import io.dev.tanners.bakerhelper.model.Step;

public class StepFragmentDynamic extends StepFragmentBase  {
    /**
     *
     */
    public StepFragmentDynamic() {
        // Required empty public constructor
    }

    /**
     * @param mStep
     * @return
     */
    public static StepFragmentDynamic newInstance(Step mStep) {
        StepFragmentDynamic mFragment = new StepFragmentDynamic();
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
        // set step object
        mStep = getArguments().getParcelable(DYNAMIC_STEP_DATA);
        // due to the way this class is designed, must call the super after
        super.onActivityCreated(savedInstanceState);
    }
}
