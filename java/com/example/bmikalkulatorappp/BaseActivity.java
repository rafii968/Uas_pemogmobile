package com.example.bmikalkulatorappp;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    /**
     * Metode untuk menerapkan Locale (Bahasa) yang tersimpan
     * ke konteks Activity.
     */
    protected Context wrapContext(Context context) {
        // Ambil kode bahasa yang tersimpan ("su" atau "id")
        String langCode = LocationLanguageManager.getLanguage(context);

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            // Menggunakan createConfigurationContext() untuk mengembalikan konteks baru
            return context.createConfigurationContext(config);
        }

        config.locale = locale;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return context;
    }

    /**
     * Dipanggil sebelum Activity dibuat untuk memastikan locale sudah benar.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(wrapContext(newBase));
    }
}