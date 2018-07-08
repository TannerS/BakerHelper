package io.dev.tanners.bakerhelper.recipe;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;
import io.dev.tanners.bakerhelper.model.Step;

// in our case the title is the short description
// and the list contains only a single item (step)
public class StepWrapper extends ExpandableGroup<Step> {

    public StepWrapper(String mTitle, List<Step> items) {
        super(mTitle, items);
    }
}