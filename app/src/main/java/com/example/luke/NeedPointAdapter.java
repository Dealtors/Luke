package com.example.luke;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NeedPointAdapter extends RecyclerView.Adapter<NeedPointAdapter.ViewHolder> {

    private List<NeedPoint> needPoints;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NeedPoint point);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public NeedPointAdapter(List<NeedPoint> needPoints) {
        this.needPoints = needPoints;
    }

    public void updateItems(List<NeedPoint> newItems) {
        this.needPoints = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_need_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NeedPoint point = needPoints.get(position);

        holder.textViewAddress.setText(point.getAddress());
        holder.textViewTime.setText(point.getTimeUntil());
        holder.textViewContact.setText(point.getContactName() + ", " + point.getContactPhone());
        holder.textViewNeed.setText(point.getNeed());
        holder.imageViewIcon.setImageResource(point.getIconResource());

        if ("СРОЧНО".equals(point.getUrgency())) {
            holder.textViewUrgency.setVisibility(View.VISIBLE);
            holder.textViewUrgency.setText("СЕГОДНЯ СРОЧНО 🪙");
        } else {
            holder.textViewUrgency.setVisibility(View.GONE);
        }

        StringBuilder itemsText = new StringBuilder();
        for (String item : point.getItems()) {
            itemsText.append("• ").append(item).append("\n");
        }
        holder.textViewItems.setText(itemsText.toString().trim());

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                listener.onItemClick(needPoints.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return needPoints.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAddress;
        TextView textViewTime;
        TextView textViewItems;
        TextView textViewContact;
        TextView textViewNeed;
        TextView textViewUrgency;
        ImageView imageViewIcon;

        ViewHolder(View itemView) {
            super(itemView);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewItems = itemView.findViewById(R.id.textViewItems);
            textViewContact = itemView.findViewById(R.id.textViewContact);
            textViewNeed = itemView.findViewById(R.id.textViewNeed);
            textViewUrgency = itemView.findViewById(R.id.textViewUrgency);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        }
    }
}
