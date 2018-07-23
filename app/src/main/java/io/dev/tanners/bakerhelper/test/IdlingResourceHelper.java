/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dev.tanners.bakerhelper.test;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Credit goes too https://github.com/googlesamples/android-testing/tree/master/ui/espresso/IdlingResourceSample
 */
public class IdlingResourceHelper implements IdlingResource {

    @Nullable
    private volatile ResourceCallback mResourceCallback;

    /**
     * used across threads to give the state of the test
     * in other words, if something is done or not based on
     * boolean's value
     */
    private AtomicBoolean mIsIdle = new AtomicBoolean(false);

    /*
     * get name of test/class
     *
     * @return
     */
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * check if idling
     *
     * @return
     */
    @Override
    public boolean isIdleNow() {
        return mIsIdle.get();
    }

    /**
     * call back for idling resource
     *
     * @param callback
     */
    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;
    }

    /**
     * set state
     *
     * @param mIsIdle
     */
    public void setState(boolean mIsIdle) {
        this.mIsIdle.set(mIsIdle);
        // if idle and got a callback
        if (mIsIdle && mResourceCallback != null) {
            // call the callback
            mResourceCallback.onTransitionToIdle();
        }
    }

    /**
     * constructor
     */
    public IdlingResourceHelper() {
        super();
    }
}
