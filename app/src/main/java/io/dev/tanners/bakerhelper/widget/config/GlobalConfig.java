package io.dev.tanners.bakerhelper.widget.config;

/**
 * widget config data
 */
public class GlobalConfig {
    private static final String WIDGET_SHARED_PREFERENCE = "WIDGET_SHARED_PREFERENCE";
    private static final String WIDGET_SHARED_PREFERENCE_WIDGET_ID = "WIDGET_SHARED_PREFERENCE_WIDGET_ID";
    private static final String WIDGET_SHARED_PREFERENCE_NAME = "WIDGET_SHARED_PREFERENCE_NAME";
    private static final String WIDGET_SHARED_PREFERENCE_ID = "WIDGET_SHARED_PREFERENCE_DB_ID";

    /**
     * format key with widget id to keep it unique
     *
     * @param mkey
     * @param appWidgetId
     * @return
     */
    public static String formatKeyName(String mkey, int appWidgetId)
    {
        return mkey + "_" + appWidgetId;
    }

    /**
     * get key for data id
     *
     * @param appWidgetId
     * @return
     */
    public static String getWidgetSharedPreferenceDbIdKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE_ID, appWidgetId);
    }

    /**
     * get key for data's name
     *
     * @param appWidgetId
     * @return
     */
    public static String getWidgetSharedPreferenceNameKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE_NAME, appWidgetId);
    }

    /**
     * get key for shared pref's for the current widget
     *
     * @param appWidgetId
     * @return
     */
    public static String getWidgetSharedPreferenceKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE, appWidgetId);
    }

    /**
     * get key for widget id
     *
     * @param appWidgetId
     * @return
     */
    public static String getWidgetSharedPreferenceWidgetIdKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE_WIDGET_ID, appWidgetId);
    }
}
