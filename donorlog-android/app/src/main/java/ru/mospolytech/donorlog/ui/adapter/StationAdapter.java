package ru.mospolytech.donorlog.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.BloodStationEntity;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {

    private final List<BloodStationEntity> items = new ArrayList<>();
    private String selectedBloodGroup = "A_POS";

    public void setItems(List<BloodStationEntity> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setSelectedBloodGroup(String group) {
        this.selectedBloodGroup = group;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodStationEntity item = items.get(position);

        holder.tvName.setText(item.getName());
        holder.tvAddress.setText(item.getCity() + ", " + item.getAddress());
        holder.tvSchedule.setText(item.getSchedule());
        holder.tvPhone.setText(item.getPhone());

        // Status for selected blood group
        String groupStatus = getStatusForGroup(item, selectedBloodGroup);
        applyStatusTheme(holder.cardStation, holder.tvStatus, groupStatus);
    }

    private String getStatusForGroup(BloodStationEntity item, String group) {
        if ("O_POS".equalsIgnoreCase(group)) return item.getOPosStatus();
        if ("O_NEG".equalsIgnoreCase(group)) return item.getONegStatus();
        if ("A_POS".equalsIgnoreCase(group)) return item.getAPosStatus();
        if ("A_NEG".equalsIgnoreCase(group)) return item.getANegStatus();
        if ("B_POS".equalsIgnoreCase(group)) return item.getBPosStatus();
        if ("B_NEG".equalsIgnoreCase(group)) return item.getBNegStatus();
        if ("AB_POS".equalsIgnoreCase(group)) return item.getAbPosStatus();
        if ("AB_NEG".equalsIgnoreCase(group)) return item.getAbNegStatus();
        return item.getNeedStatus();
    }

    private void applyStatusTheme(MaterialCardView card, TextView tvStatus, String status) {
        int strokeColor;
        if ("RED".equalsIgnoreCase(status)) {
            tvStatus.setText("Критическая потребность");
            tvStatus.setTextColor(ContextCompat.getColor(tvStatus.getContext(), R.color.status_red));
            tvStatus.setBackgroundResource(R.drawable.bg_badge_red);
            strokeColor = Color.parseColor("#4DE11D48"); // Subtle ruby stroke
        } else if ("YELLOW".equalsIgnoreCase(status)) {
            tvStatus.setText("Повышенная потребность");
            tvStatus.setTextColor(ContextCompat.getColor(tvStatus.getContext(), R.color.status_yellow));
            tvStatus.setBackgroundResource(R.drawable.bg_badge_yellow);
            strokeColor = Color.parseColor("#4DF59E0B"); // Subtle amber stroke
        } else {
            tvStatus.setText("Достаточный запас");
            tvStatus.setTextColor(ContextCompat.getColor(tvStatus.getContext(), R.color.status_green));
            tvStatus.setBackgroundResource(R.drawable.bg_badge_green);
            strokeColor = Color.parseColor("#4D10B981"); // Subtle emerald stroke
        }
        if (card != null) {
            card.setCardBackgroundColor(ContextCompat.getColor(card.getContext(), R.color.donor_surface_card));
            card.setStrokeColor(strokeColor);
            card.setStrokeWidth(3);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView cardStation;
        final TextView tvName;
        final TextView tvAddress;
        final TextView tvSchedule;
        final TextView tvPhone;
        final TextView tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            cardStation = itemView.findViewById(R.id.cardStation);
            tvName = itemView.findViewById(R.id.tvStationName);
            tvAddress = itemView.findViewById(R.id.tvStationAddress);
            tvSchedule = itemView.findViewById(R.id.tvStationSchedule);
            tvPhone = itemView.findViewById(R.id.tvStationPhone);
            tvStatus = itemView.findViewById(R.id.tvStationStatus);
        }
    }
}
