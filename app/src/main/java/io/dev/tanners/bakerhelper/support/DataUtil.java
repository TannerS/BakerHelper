package io.dev.tanners.bakerhelper.support;

import android.content.Intent;

/**
 * call back to load new intent from activity -> fragment to pass new data
 * this is here due to limitations in the language with having multiple inner nested
 * classes in java
 */
public interface DataUtil {
    /**
     * load new data via intent
     *
     * @param mIntent
     */
    public void loadNewData(Intent mIntent);
}
