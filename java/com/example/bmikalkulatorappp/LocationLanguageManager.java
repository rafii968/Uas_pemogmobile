package com.example.bmikalkulatorappp;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationLanguageManager {

    private static final String PREFS_NAME = "app_language_pref";
    private static final String KEY_LANG = "key_lang";
    private static final String KEY_REGION = "region";

    public static void updateRegion(Context context) {
        // --- JALUR SUNDA DIPUTUS DISINI ---
        // Kita paksa nilainya ke Bekasi & Indonesia, bodo amat sama GPS.
        String region = "BKS";
        String lang = "id";

        // Kita simpan paksa biar nimpa data "su" yang lama
        saveRegion(context, region);
        saveLanguage(context, lang);
    }

    public static int getLogoResource(Context ctx) {
        // Karena region dipaksa BKS, ini otomatis bakal balik ke logo_bekasi
        return ctx.getResources().getIdentifier("logo_bekasi", "drawable", ctx.getPackageName());
    }

    public static void saveLanguage(Context ctx, String lang) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_LANG, lang).apply();
    }

    public static String getLanguage(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_LANG, "id");
    }

    public static void saveRegion(Context ctx, String region) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_REGION, region).apply();
    }

    public static String getRegion(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_REGION, "BKS");
    }
}