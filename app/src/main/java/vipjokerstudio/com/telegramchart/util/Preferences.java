package vipjokerstudio.com.telegramchart.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String TAG = "Preferences";
    private static final String USER_SETTINGS = "user_settings";
    public static final String IS_DARK_THEME = "is_dark_theme";
    private static SharedPreferences preferences;

    public static void init(Context context) {
        preferences = context.getSharedPreferences(USER_SETTINGS, Context.MODE_PRIVATE);
    }

    public static void saveThemeDarkTheme(boolean isDark){
        saveBoolean(IS_DARK_THEME,isDark);
    }

    public static boolean isDarkTheme(){
        return getBoolean(IS_DARK_THEME,false);
    }

    private static void saveString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    private static String getString(String key, String def) {
        return preferences.getString(key, def);
    }

    private static String getString(String key) {
        return getString(key, null);
    }

    private static void saveBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    private static boolean getBoolean(String key, boolean def) {
        return preferences.getBoolean(key, def);
    }


}
