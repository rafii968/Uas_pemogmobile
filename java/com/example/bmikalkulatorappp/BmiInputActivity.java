package com.example.bmikalkulatorappp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button; // Ganti ke Button kalau pake desain terbaru
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BmiInputActivity extends AppCompatActivity {

    private EditText etTinggi, etBerat;
    private RadioGroup rgGender;
    private Button btnHitung; // Sesuaikan dengan tipe di XML (Button/ImageButton)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_input);

        // 1. Inisialisasi View
        etTinggi = findViewById(R.id.et_tinggi);
        etBerat = findViewById(R.id.et_berat);
        rgGender = findViewById(R.id.rg_gender);
        btnHitung = findViewById(R.id.btn_hitung); // Sudah gue benerin typonya

        // 2. Logika Klik Tombol
        btnHitung.setOnClickListener(v -> {
            String t = etTinggi.getText().toString();
            String b = etBerat.getText().toString();
            int selectedGender = rgGender.getCheckedRadioButtonId();

            // Cek apakah input kosong
            if (!t.isEmpty() && !b.isEmpty() && selectedGender != -1) {
                try {
                    float tinggiMeter = Float.parseFloat(t) / 100;
                    float beratKg = Float.parseFloat(b);
                    float bmi = beratKg / (tinggiMeter * tinggiMeter);

                    // 3. Kirim hasil (Kunci disamakan dengan ResultActivity)
                    Intent intent = new Intent(this, BmiResultActivity.class);
                    intent.putExtra("bmi_score", (double) bmi); // Pake "bmi_score"
                    intent.putExtra("tinggi", (double) Float.parseFloat(t));
                    intent.putExtra("berat", (double) beratKg);

                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Inputnya harus angka ya, Lur!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lengkapin dulu datanya, Lur!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}