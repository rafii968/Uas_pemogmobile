package com.example.bmikalkulatorappp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private ViewPager2 viewPagerTips;
    private Button btnKembali;

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = LocationLanguageManager.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        viewPagerTips = findViewById(R.id.viewPagerTips);
        btnKembali = findViewById(R.id.btn_kembali_tips);

        // Kumpulan Gambar Tips Lu (Pastiin nama-nama file ini ada di folder drawable lu ya!)
        List<Integer> listGambarTips = new ArrayList<>();
        listGambarTips.add(R.drawable.tipsbadan2);
        listGambarTips.add(R.drawable.polamakan);
        listGambarTips.add(R.drawable.tipsjagabody);

        // Hubungkan adapter secara bersih tanpa tumpang tindih struktur
        TipsAdapter adapter = new TipsAdapter(listGambarTips, imageResId -> showZoomedImageDialog(imageResId));
        viewPagerTips.setAdapter(adapter);

        // Pengaturan transisi geser
        viewPagerTips.setOffscreenPageLimit(3);
        viewPagerTips.getChildAt(0).setOverScrollMode(ViewPager2.OVER_SCROLL_NEVER);

        btnKembali.setOnClickListener(v -> finish());
    }

    // Fungsi Pop-up buat nampilin poster versi GEDE pas diklik
    private void showZoomedImageDialog(int imageResId) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_zoom_poster);

        ImageView imgZoomed = dialog.findViewById(R.id.img_zoomed);
        ImageView btnClose = dialog.findViewById(R.id.btn_close_zoom);

        imgZoomed.setImageResource(imageResId);

        // Klik tombol silang atau klik areanya langsung buat nutup pop-up
        btnClose.setOnClickListener(v -> dialog.dismiss());
        imgZoomed.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}