package com.example.luke;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PointItemAdapter extends RecyclerView.Adapter<PointItemAdapter.ViewHolder> {

    private List<Product> items;

    public PointItemAdapter(List<Product> items) {
        this.items = items;
    }

    public void updateItems(List<Product> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_point_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = items.get(position);

        holder.textViewName.setText(item.getName());
        holder.textViewNeeded.setText("Нужно: " + item.getTotalQuantity() + " шт");
        holder.textViewCollected.setText("Собрано: " + item.getCollectedQuantity() + " шт");
        holder.progressBar.setProgress(item.getProgress());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewNeeded;
        TextView textViewCollected;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewItemName);
            textViewNeeded = itemView.findViewById(R.id.textViewNeeded);
            textViewCollected = itemView.findViewById(R.id.textViewCollected);
            progressBar = itemView.findViewById(R.id.progressBarItem);
        }
    }
}
