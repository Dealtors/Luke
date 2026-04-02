package com.example.luke;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.textViewAddress.setText(item.getAddress());

        StringBuilder itemsText = new StringBuilder();
        for (String s : item.getItems()) {
            itemsText.append("• ").append(s).append("\n");
        }
        holder.textViewItems.setText(itemsText.toString().trim());

        if (item.getDate() != null && !item.getDate().isEmpty()) {
            holder.textViewDate.setText(item.getDate());
            holder.textViewDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAddress;
        TextView textViewItems;
        TextView textViewDate;

        ViewHolder(View itemView) {
            super(itemView);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewItems = itemView.findViewById(R.id.textViewItems);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}