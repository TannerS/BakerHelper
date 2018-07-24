package io.dev.tanners.bakerhelper;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class NonLandscapeUITest {

    private IdlingResource mIdlingResource;

    /**
     * ALL credit goes too
     * https://stackoverflow.com/questions/28834579/click-on-not-fully-visible-imagebutton-with-espresso
     * This was the only way to get this test to work
     * I have tried idling resource, I tried sleeping the thread, and many solutions on google
     * this was the only way
     */
    public void adapterClick(int mAdapterId, final String mDesc){
        onView(withId(mAdapterId)).check(matches(allOf(isEnabled(), isClickable()))).perform(
                new ViewAction() {
                    @Override
                    public Matcher<View> getConstraints() {
                        return ViewMatchers.isEnabled(); // no constraints, they are checked above
                    }

                    @Override
                    public String getDescription() {
                        return mDesc;
                    }

                    @Override
                    public void perform(UiController uiController, View view) {
                        view.performClick();
                    }
                }
        );
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * load idling resource before test
     */
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    /**
     * check if list item is clickable
     */
    @Test
    public void clickListItem_IsClickable() {
        onData(anything()).inAdapterView(withId(R.id.main_recipe_list)).atPosition(0).onChildView(isClickable());
    }

    /**
     * check if toolbar exist after clicking on list item to open up next activity
     */
    @Test
    public void clickListItem_NextActivityToolbarExist() {
        adapterClick(R.id.main_recipe_list, "Main activity onClick Test");
        onView(withId(R.id.main_toolbar)).check(matches(isDisplayed()));
    }

    /**
     * check if list item even shows
     */
    @Test
    public void listItem_IsDisplayed() {
        onData(anything()).inAdapterView(withId(R.id.main_recipe_list)).atPosition(0).onChildView((isDisplayed()));
    }

    /**
     * click list item
     */
    @Test
    public void clickStepListItem_Click() {
        adapterClick(R.id.main_recipe_list, "Main activity onClick Test");
    }

    /**
     * see if tool bar shows up
     */
    @Test
    public void mainActivityToolbar_isDisplayed() {
        onView(withId(R.id.main_toolbar)).check(matches(isDisplayed()));
    }

    /**
     * see if list has items
     */
    @Test
    public void mainActivityList_hasItems() {
        onView(withId(R.id.main_recipe_list)).check(new RecyclerViewItemCountAssertion());
    }

    /**
     * un register idling resource after test
     */
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    /**
     * All credit: https://stackoverflow.com/a/37339656/2449314
     *
     * check count for recyclerview
     */
    public class RecyclerViewItemCountAssertion implements ViewAssertion {

        /**
         * check count
         *
         * @param view
         * @param noViewFoundException
         */
        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            MainActivity.RecipeAdapter adapter = (MainActivity.RecipeAdapter) recyclerView.getAdapter();
            // see if it has data
            assertThat(adapter.getItemCount(), greaterThan(0));
        }
    }
}
