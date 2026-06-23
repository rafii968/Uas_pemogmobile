package com.example.bmikalkulatorappp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.SliderViewHolder> {

    private final List<Integer> sliderItems;
    private final OnItemClickListener listener;

    // Interface buat ngirim perintah klik ke Activity
    public interface OnItemClickListener {
        void onItemClick(int imageResId);
    }

    public TipsAdapter(List<Integer> sliderItems, OnItemClickListener listener) {
        this.sliderItems = sliderItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tips_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int itemResId = sliderItems.get(position);
        holder.imageView.setImageResource(itemResId);

        // Logika ketika item gambar di-klik
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(itemResId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgSlider);
        }
    }
}