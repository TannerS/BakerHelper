package io.dev.tanners.bakerhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import io.dev.tanners.bakerhelper.model.Step;

public class StepActivity extends AppCompatActivity implements StepFragment.FragmentStepData {
    public static final String STEP_DATA = "STEP_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
    }

    @Override
    public Step getStep() {
        if(getIntent() != null && getIntent().hasExtra(STEP_DATA))
            return getIntent().getParcelableExtra(STEP_DATA);

        return null;
    }
}
