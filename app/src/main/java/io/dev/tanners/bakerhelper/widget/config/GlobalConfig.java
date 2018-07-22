package io.dev.tanners.bakerhelper.widget.config;

public class GlobalConfig {
    private static final String WIDGET_SHARED_PREFERENCE = "WIDGET_SHARED_PREFERENCE";
    private static final String WIDGET_SHARED_PREFERENCE_WIDGET_ID = "WIDGET_SHARED_PREFERENCE_WIDGET_ID";
    private static final String WIDGET_SHARED_PREFERENCE_NAME = "WIDGET_SHARED_PREFERENCE_NAME";
    private static final String WIDGET_SHARED_PREFERENCE_ID = "WIDGET_SHARED_PREFERENCE_DB_ID";

    public static String formatKeyName(String mkey, int appWidgetId)
    {
        return mkey + "_" + appWidgetId;
    }

    public static String getWidgetSharedPreferenceDbIdKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE_ID, appWidgetId);
    }


    public static String getWidgetSharedPreferenceNameKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE_NAME, appWidgetId);
    }

    public static String getWidgetSharedPreferenceKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE, appWidgetId);
    }

    public static String getWidgetSharedPreferenceWidgetIdKey(int appWidgetId)
    {
        return formatKeyName(WIDGET_SHARED_PREFERENCE_WIDGET_ID, appWidgetId);
    }
}
