package com.example.bmikalkulatorappp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
        } else {
            mulaiAplikasi();
        }
    }

    private void mulaiAplikasi() {
        // 1. Paksa update lokasi & bahasa SEBELUM ambil data
        LocationLanguageManager.updateRegion(this);

        String lang = LocationLanguageManager.getLanguage(this);
        LocaleHelper.setLocale(this, lang);

        ImageView logoPemda = findViewById(R.id.logo_pemda);
        TextView textWelcome = findViewById(R.id.text_selamat_datang);

        if (logoPemda != null) {
            logoPemda.setImageResource(LocationLanguageManager.getLogoResource(this));
        }

        if (textWelcome != null) {
            // Kita panggil setText ulang biar bahasanya beneran berubah di Splash
            textWelcome.setText(R.string.welcome_message);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            // Kasih jeda dikit 500ms biar GPS-nya "bangun" setelah dikasih izin
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mulaiAplikasi();
            }, 500);
        }
    }
}