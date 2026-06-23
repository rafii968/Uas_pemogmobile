package com.example.bmikalkulatorappp;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo        = findViewById(R.id.intro_logo);
        Button    startButton = findViewById(R.id.start_button);

        // ── 1. LOGO: Blur-to-clear ────────────────────────────────────────────
        if (logo != null) {
            logo.setAlpha(0f);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+: blur nyata pakai RenderEffect
                logo.setRenderEffect(
                        android.graphics.RenderEffect.createBlurEffect(
                                40f, 40f, android.graphics.Shader.TileMode.CLAMP));

                logo.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setStartDelay(200)
                        .withEndAction(() -> {
                            // Setelah fade in, hilangkan blur perlahan
                            ValueAnimator blurAnim = ValueAnimator.ofFloat(40f, 0f);
                            blurAnim.setDuration(800);
                            blurAnim.addUpdateListener(va -> {
                                float val = (float) va.getAnimatedValue();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    if (val <= 0.5f) {
                                        logo.setRenderEffect(null);
                                    } else {
                                        logo.setRenderEffect(
                                                android.graphics.RenderEffect.createBlurEffect(
                                                        val, val,
                                                        android.graphics.Shader.TileMode.CLAMP));
                                    }
                                }
                            });
                            blurAnim.start();
                        })
                        .start();

            } else {
                // Android < 12: simulasi dengan scale besar → normal + fade
                logo.setScaleX(1.25f);
                logo.setScaleY(1.25f);
                logo.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(1300)
                        .setStartDelay(200)
                        .setInterpolator(new android.view.animation.DecelerateInterpolator())
                        .start();
            }
        }

        // ── 2. TOMBOL: Slide-up muncul setelah logo ──────────────────────────
        if (startButton != null) {
            startButton.setVisibility(View.INVISIBLE);
            startButton.postDelayed(() -> {
                startButton.setVisibility(View.VISIBLE);
                startButton.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.btn_slide_up));
            }, 900);

            // ── 3. TOMBOL: Efek bounce keren saat ditekan ────────────────────
            startButton.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .scaleX(0.90f).scaleY(0.90f)
                                .setDuration(120)
                                .setInterpolator(new android.view.animation.AccelerateInterpolator())
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.animate()
                                .scaleX(1.08f).scaleY(1.08f)
                                .setDuration(120)
                                .withEndAction(() ->
                                        v.animate()
                                                .scaleX(1f).scaleY(1f)
                                                .setDuration(300)
                                                .setInterpolator(new OvershootInterpolator(3f))
                                                .start()
                                ).start();
                        v.performClick();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
                        break;
                }
                return true;
            });

            // ── 4. Navigasi (sama persis dengan kode asli kamu) ──────────────
            startButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Biar gak bisa balik ke layar MULAI lagi
            });
        }
    }
}