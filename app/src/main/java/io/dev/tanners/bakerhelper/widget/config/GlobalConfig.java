package io.dev.tanners.bakerhelper.widget.config;

public class GlobalConfig {
    public static final String WIDGET_SHARED_PREFERENCE = "WIDGET_SHARED_PREFERENCE";
    public static final String WIDGET_SHARED_PREFERENCE_WIDGET_ID = "WIDGET_SHARED_PREFERENCE_WIDGET_ID";
    public static final String WIDGET_SHARED_PREFERENCE_NAME = "WIDGET_SHARED_PREFERENCE_NAME";
    public static final String WIDGET_SHARED_PREFERENCE_DB_ID = "WIDGET_SHARED_PREFERENCE_DB_ID";

    public static String formatKeyName(String mkey, int appWidgetId)
    {
        return mkey + "_" + appWidgetId;
    }

}
