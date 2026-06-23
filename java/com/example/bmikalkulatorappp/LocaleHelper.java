package com.example.bmikalkulatorappp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import java.util.Locale;

public class LocaleHelper {

    private static final String PREF_NAME = "app_language_pref";
    private static final String KEY_LANG = "key_lang";

    public static Context setLocale(Context context, String lang) {
        saveLanguage(context, lang);
        return updateResources(context, lang);
    }

    private static Context updateResources(Context context, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }

    public static void saveLanguage(Context context, String lang) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(KEY_LANG, lang).apply();
    }

    public static String getSavedLanguage(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // PASTIKAN DEFAULT DISINI ADALAH "id"
        return pref.getString(KEY_LANG, "id");
    }
}