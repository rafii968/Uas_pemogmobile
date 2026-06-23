package com.example.bmikalkulatorappp;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.tvMessage.setText(message.getMessage());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvMessage.getLayoutParams();

        if (message.isUser()) {
            // Chat User: Kanan, Warna Ungu Muda
            params.gravity = Gravity.END;
            holder.tvMessage.setBackgroundResource(R.drawable.bg_bubble_user);
            holder.tvMessage.setTextColor(android.graphics.Color.BLACK);
        } else {
            // Chat AI: Kiri, Warna Putih
            params.gravity = Gravity.START;
            holder.tvMessage.setBackgroundResource(R.drawable.bg_bubble_ai);
            holder.tvMessage.setTextColor(android.graphics.Color.BLACK);
        }
        holder.tvMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_chat_message);
        }
    }
}