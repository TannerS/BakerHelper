package io.dev.tanners.bakerhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import io.dev.tanners.bakerhelper.model.Step;

public class StepActivity extends AppCompatActivity implements StepFragment.FragmentStepData {
    // used for intent key
    public static final String STEP_DATA = "STEP_DATA";

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
    }

    /**
     * @return
     */
    @Override
    public Step getStep() {
        // get data from intent
        if(getIntent() != null && getIntent().hasExtra(STEP_DATA))
            return getIntent().getParcelableExtra(STEP_DATA);
        // return null if no data
        return null;
    }
}
