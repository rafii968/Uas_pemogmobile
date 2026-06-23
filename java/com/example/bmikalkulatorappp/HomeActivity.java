package com.example.bmikalkulatorappp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnBmi, btnTips, btnAi;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Mengunci bahasa agar tetap konsisten saat activity dibuat
        String lang = LocationLanguageManager.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Inisialisasi View
        tvWelcome = findViewById(R.id.tv_welcome);
        btnBmi    = findViewById(R.id.btn_bmi_calculator);
        btnTips   = findViewById(R.id.btn_tips_ideal);
        btnAi     = findViewById(R.id.btn_ai_consultation);
        Button btnScanAi = findViewById(R.id.btn_scan_ai);

        // 2. Set Teks dari String Resource
        if (tvWelcome != null) tvWelcome.setText(R.string.home_pilih);
        if (btnBmi    != null) btnBmi.setText(R.string.btn_bmi_calculator);
        if (btnTips   != null) btnTips.setText(R.string.btn_tips_ideal);
        if (btnAi     != null) btnAi.setText(R.string.btn_ai_consultation);

        // ── ANIMASI: Slide-up staggered pada 4 tombol ──────────────────────
        Button[] buttons    = {btnBmi, btnTips, btnAi, btnScanAi};
        long[]   delays     = {150L,   300L,    450L,  600L};

        for (int i = 0; i < buttons.length; i++) {
            final Button btn = buttons[i];
            if (btn == null) continue;

            btn.setVisibility(View.INVISIBLE);
            final long delay = delays[i];

            btn.postDelayed(() -> {
                btn.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.btn_slide_up);
                btn.startAnimation(anim);
            }, delay);

            // Scale press effect (terasa responsif dan mewah)
            btn.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .scaleX(0.94f).scaleY(0.94f)
                                .setDuration(100)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1f).scaleY(1f)
                                .setDuration(200)
                                .setInterpolator(new android.view.animation.OvershootInterpolator(2f))
                                .start();
                        break;
                }
                return false; // false = onClick tetap terpanggil seperti biasa
            });
        }
        // ── SELESAI ANIMASI ─────────────────────────────────────────────────

        // 3. Logika Navigasi (tidak diubah sama sekali)
        btnBmi.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BmiInputActivity.class);
            startActivity(intent);
        });

        btnTips.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TipsActivity.class);
            startActivity(intent);
        });

        btnAi.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CameraAiActivity.class);
            startActivity(intent);
        });

        btnScanAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CameraScanActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Opsional: Jika ingin kembali ke MainActivity saat tekan back
        super.onBackPressed();
    }
}