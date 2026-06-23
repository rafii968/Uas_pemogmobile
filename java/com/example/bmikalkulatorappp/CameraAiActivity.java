package com.example.bmikalkulatorappp;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager; // Import tambahan buat urusan keyboard
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CameraAiActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<ChatMessage> chatList = new ArrayList<>();
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // JURUS PAMUNGKAS: Paksa resize layout sebelum setContentView
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.activity_chat_ai);

        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back_chat);

        adapter = new ChatAdapter(chatList);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // FITUR AUTO-SCROLL: Biar chat otomatis naik pas keyboard muncul
        rvChat.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                rvChat.postDelayed(() -> {
                    if (!chatList.isEmpty()) {
                        rvChat.scrollToPosition(chatList.size() - 1);
                    }
                }, 100);
            }
        });

        // Sambutan Awal
        addMessage("Halo Rafii! Saya asisten kesehatanmu. Ada yang mau ditanyain soal diet, olahraga, atau cara biar berat badan ideal?", false);

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                addMessage(msg, true);
                etMessage.setText("");

                // Efek "Sedang Mengetik..."
                addMessage("Asisten sedang mengetik...", false);
                int typingIndex = chatList.size() - 1;

                new Handler().postDelayed(() -> {
                    if (chatList.size() > typingIndex) {
                        chatList.remove(typingIndex);
                        adapter.notifyItemRemoved(typingIndex);
                        processSmartResponse(msg);
                    }
                }, 1000);
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void processSmartResponse(String input) {
        String lower = input.toLowerCase();
        String response;
        Random rand = new Random();

        if (lower.contains("diet") || lower.contains("makan")) {
            String[] choices = {
                    "Diet itu kuncinya konsisten, Rafii. Coba kurangi gorengan dan perbanyak serat ya!",
                    "Jangan skip sarapan ya! Pilih menu yang tinggi protein biar kenyang lebih lama.",
                    "Coba mulai ganti nasi putih ke nasi merah atau ubi rebus. Lebih sehat buat BMI kamu!"
            };
            response = choices[rand.nextInt(choices.length)];
        }
        else if (lower.contains("olahraga") || lower.contains("gerak") || lower.contains("lari")) {
            String[] choices = {
                    "Gak perlu ke gym, Rafii. Jalan kaki 30 menit sehari udah ngebantu banget kok!",
                    "Coba deh rutin skipping atau push-up di rumah. Pelan-pelan aja yang penting rutin.",
                    "Olahraga kardio paling bagus buat bakar lemak. Mau coba jogging sore ini?"
            };
            response = choices[rand.nextInt(choices.length)];
        }
        else if (lower.contains("turun") || lower.contains("naik") || lower.contains("cara")) {
            response = "Kalo mau hasil maksimal, pastiin istirahat cukup 7-8 jam ya. Otot itu tumbuh pas kita tidur!";
        }
        else if (lower.contains("halo") || lower.contains("hai")) {
            response = "Halo juga Rafii! Apa kabar? Ada yang bisa saya bantu buat program kesehatanmu hari ini?";
        }
        else if (lower.contains("terima kasih") || lower.contains("makasih") || lower.contains("thanks")) {
            response = "Sama-sama! Semangat terus ya jaga kesehatannya!";
        }
        else {
            response = "Wah, pertanyaan bagus. Tapi gak usah aneh-aneh deh. Intinya jaga pola makan dan tetap aktif ya!";
        }

        addMessage(response, false);
    }

    private void addMessage(String text, boolean isUser) {
        chatList.add(new ChatMessage(text, isUser));
        adapter.notifyItemInserted(chatList.size() - 1);
        rvChat.scrollToPosition(chatList.size() - 1);
    }
}