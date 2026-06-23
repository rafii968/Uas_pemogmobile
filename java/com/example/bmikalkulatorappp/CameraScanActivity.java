package com.example.bmikalkulatorappp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.bmikalkulatorappp.databinding.ActivityCameraScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraScanActivity extends AppCompatActivity {

    private ActivityCameraScanBinding binding;
    private PoseDetector poseDetector;
    private ExecutorService cameraExecutor;

    // Simpan hasil pose terakhir untuk dipakai saat tombol capture ditekan
    private Pose lastDetectedPose = null;
    private int imageWidth = 0;
    private int imageHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ── Setup ML Kit Pose Detector ────────────────────────────────────
        AccuratePoseDetectorOptions options =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                        .build();
        poseDetector = PoseDetection.getClient(options);
        cameraExecutor = Executors.newSingleThreadExecutor();

        // ── Cek Izin Kamera (sama seperti semula) ────────────────────────
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 101);
        }

        // ── Tombol (sama seperti semula) ──────────────────────────────────
        binding.btnCapture.setOnClickListener(v -> analyzeCurrentPose());
        binding.btnSaveScan.setOnClickListener(v -> finish());

        // Instruksi awal
        binding.textInstruction.setText("Posisikan seluruh tubuh terlihat di kamera");
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview (sama seperti semula)
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                // ── ImageAnalysis untuk ML Kit (BARU) ─────────────────────
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    processImageProxy(imageProxy);
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis   // tambahkan analyzer
                );

            } catch (Exception e) {
                Toast.makeText(this, "Gagal buka kamera", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // ── ML Kit: proses setiap frame dari kamera ───────────────────────────
    @SuppressLint("UnsafeOptInUsageError")
    private void processImageProxy(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        imageWidth  = imageProxy.getWidth();
        imageHeight = imageProxy.getHeight();

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        poseDetector.process(image)
                .addOnSuccessListener(pose -> {
                    lastDetectedPose = pose;

                    // Kasih feedback real-time ke user
                    runOnUiThread(() -> {
                        if (pose.getAllPoseLandmarks().isEmpty()) {
                            binding.textInstruction.setText("⚠️ Tubuh tidak terdeteksi, mundur sedikit...");
                        } else {
                            binding.textInstruction.setText("✅ Pose terdeteksi! Tekan tombol untuk scan");
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e("PoseDetect", "Gagal: " + e.getMessage()))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    // ── Analisa pose saat tombol capture ditekan ─────────────────────────
    private void analyzeCurrentPose() {
        if (lastDetectedPose == null || lastDetectedPose.getAllPoseLandmarks().isEmpty()) {
            Toast.makeText(this, "Pose belum terdeteksi, coba mundur dari kamera", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnCapture.setEnabled(false);
        binding.cardResult.setVisibility(View.GONE);

        // Animasi loading (dipertahankan dari kode asli)
        binding.textInstruction.setText("Mendeteksi titik-titik tubuh...");

        new android.os.Handler().postDelayed(() ->
                binding.textInstruction.setText("Menganalisa proporsi tubuh..."), 800);

        new android.os.Handler().postDelayed(() ->
                binding.textInstruction.setText("Menghitung estimasi BMI..."), 1600);

        new android.os.Handler().postDelayed(() -> {
            String hasil = hitungEstimasiBMI(lastDetectedPose);

            binding.textScanResult.setText("HASIL: " + hasil);
            binding.cardResult.setVisibility(View.VISIBLE);
            binding.textInstruction.setText("Analisa Selesai!");
            binding.btnCapture.setEnabled(true);

        }, 2500);
    }

    // ── Logika estimasi BMI dari titik-titik pose ─────────────────────────
    private String hitungEstimasiBMI(Pose pose) {
        // Ambil landmark penting
        PoseLandmark bahu_kiri  = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark bahu_kanan = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark pinggul_kiri  = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
        PoseLandmark pinggul_kanan = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
        PoseLandmark lutut_kiri  = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
        PoseLandmark lutut_kanan = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
        PoseLandmark kepala     = pose.getPoseLandmark(PoseLandmark.NOSE);
        PoseLandmark pergelangan_kaki_kiri  = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
        PoseLandmark pergelangan_kaki_kanan = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

        // Jika landmark penting tidak terdeteksi
        if (bahu_kiri == null || bahu_kanan == null ||
                pinggul_kiri == null || pinggul_kanan == null || kepala == null) {
            return "TIDAK TERDETEKSI - Coba mundur lebih jauh";
        }

        // 1. Lebar bahu (pixel)
        float lebarBahu = Math.abs(bahu_kanan.getPosition().x - bahu_kiri.getPosition().x);

        // 2. Lebar pinggul (pixel)
        float lebarPinggul = Math.abs(pinggul_kanan.getPosition().x - pinggul_kiri.getPosition().x);

        // 3. Tinggi badan estimasi (dari kepala ke kaki, dalam pixel)
        float tinggiBadan = 0;
        if (pergelangan_kaki_kiri != null && pergelangan_kaki_kanan != null) {
            float kakiY = (pergelangan_kaki_kiri.getPosition().y + pergelangan_kaki_kanan.getPosition().y) / 2;
            tinggiBadan = kakiY - kepala.getPosition().y;
        } else if (lutut_kiri != null && lutut_kanan != null) {
            // Fallback: estimasi dari kepala ke lutut × 1.6
            float lututY = (lutut_kiri.getPosition().y + lutut_kanan.getPosition().y) / 2;
            tinggiBadan = (lututY - kepala.getPosition().y) * 1.6f;
        }

        if (tinggiBadan <= 0) return "TIDAK TERDETEKSI - Pastikan seluruh tubuh terlihat";

        // 4. Rasio lebar bahu terhadap tinggi badan
        //    Orang kurus  → rasio kecil (~0.20-0.25)
        //    Orang ideal  → rasio sedang (~0.26-0.33)
        //    Orang gemuk  → rasio besar (>0.34)
        float rasio = lebarBahu / tinggiBadan;

        // 5. Rasio pinggul terhadap bahu (indikator tambahan lemak tubuh)
        float rasio_pinggul_bahu = lebarPinggul / lebarBahu;

        // Klasifikasi
        if (rasio < 0.22f) {
            return "KURUS (Underweight)";
        } else if (rasio <= 0.30f) {
            if (rasio_pinggul_bahu > 1.1f) {
                return "NORMAL cenderung BERLEBIH";
            }
            return "IDEAL (Normal)";
        } else if (rasio <= 0.36f) {
            return "BERLEBIH (Overweight)";
        } else {
            return "OBESITAS";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poseDetector.close();
        cameraExecutor.shutdown();
    }
}