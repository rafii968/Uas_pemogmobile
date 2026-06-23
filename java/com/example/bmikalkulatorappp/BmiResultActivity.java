package com.example.bmikalkulatorappp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BmiResultActivity extends AppCompatActivity {

    private TextView tvBmiScore, tvBmiStatus, tvAiForecast;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_result);

        tvBmiScore = findViewById(R.id.tv_bmi_score);
        tvBmiStatus = findViewById(R.id.tv_bmi_status);
        tvAiForecast = findViewById(R.id.tv_ai_forecast);
        btnBack = findViewById(R.id.btn_back_result);

        // Ambil data dari Activity Utama
        double bmi = getIntent().getDoubleExtra("bmi_score", 0);
        double berat = getIntent().getDoubleExtra("berat", 0);
        double tinggi = getIntent().getDoubleExtra("tinggi", 0);

        tvBmiScore.setText(String.format(Locale.US, "%.1f", bmi));
        updateStatus(bmi);

        // JALANKAN LOGIKA AI FORECASTER
        generateAiForecast(bmi, berat, tinggi);

        btnBack.setOnClickListener(v -> finish());
    }

    private void updateStatus(double bmi) {
        String status;
        if (bmi < 18.5) status = "KURANG BERAT BADAN";
        else if (bmi < 25) status = "BERAT BADAN IDEAL";
        else if (bmi < 30) status = "KELEBIHAN BERAT BADAN";
        else status = "OBESITAS";
        tvBmiStatus.setText(status);
    }

    private void generateAiForecast(double bmi, double berat, double tinggi) {
        double tinggiMeter = tinggi / 100;
        double beratIdeal = 22.0 * (tinggiMeter * tinggiMeter);
        double selisih = berat - beratIdeal;
        int estimasiHari = (int) Math.abs(selisih * 14); // 1kg sehat dlm 14 hari

        StringBuilder sb = new StringBuilder();
        sb.append("🤖 [AI ANALYSIS]\n");

        if (bmi > 25) {
            sb.append("Kondisi: Surplus Lemak Aktif.\n\n");
            sb.append("PREDIKSI: Kamu butuh waktu sekitar ").append(estimasiHari).append(" hari untuk mencapai berat ideal ").append(String.format("%.1f", beratIdeal)).append(" kg.\n\n");
            sb.append("SARAN: Lakukan Defisit Kalori (500kkal/hari) & Cardio ringan.");
        } else if (bmi < 18.5) {
            sb.append("Kondisi: Defisit Nutrisi/Massa Otot.\n\n");
            sb.append("PREDIKSI: Postur idealmu tercapai dalam ").append(estimasiHari).append(" hari ke depan.\n\n");
            sb.append("SARAN: Tingkatkan asupan Protein & Weight Training.");
        } else {
            sb.append("Kondisi: Homeostasis Stabil.\n\n");
            sb.append("PREDIKSI: Kamu akan tetap di kondisi prima selama 365 hari ke depan jika pola tidur dijaga.\n\n");
            sb.append("SARAN: Pertahankan VO2 Max-mu!");
        }

        tvAiForecast.setText(sb.toString());
    }
}