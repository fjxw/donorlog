package ru.mospolytech.donorlog.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.ComponentType;
import ru.mospolytech.donorlog.data.model.DonationEntity;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DonationEntity donation);
    }

    private final List<DonationEntity> items = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<DonationEntity> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationEntity item = items.get(position);
        ComponentType ct = ComponentType.fromString(item.getComponentType());

        holder.tvType.setText(ct.getTitleRu());
        holder.tvDate.setText(dateFormat.format(new Date(item.getDonationDate())));
        holder.tvStation.setText(item.getStationName());
        holder.tvVolume.setText(item.getVolumeMl() + " мл");

        String metrics = "Hb: " + (int) item.getHemoglobin() + " г/л • АД: " + item.getSystolicBp() + "/" + item.getDiastolicBp() + " • ЧСС: " + item.getPulse();
        holder.tvMetrics.setText(metrics);

        if (ct == ComponentType.WHOLE_BLOOD) {
            holder.ivIcon.setImageResource(R.drawable.ic_blood_drop);
        } else if (ct == ComponentType.PLASMA) {
            holder.ivIcon.setImageResource(R.drawable.ic_heart);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_blood_drop);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivIcon;
        final TextView tvType;
        final TextView tvDate;
        final TextView tvStation;
        final TextView tvVolume;
        final TextView tvMetrics;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivComponentIcon);
            tvType = itemView.findViewById(R.id.tvComponentType);
            tvDate = itemView.findViewById(R.id.tvDonationDate);
            tvStation = itemView.findViewById(R.id.tvStationName);
            tvVolume = itemView.findViewById(R.id.tvVolume);
            tvMetrics = itemView.findViewById(R.id.tvHealthMetrics);
        }
    }
}
