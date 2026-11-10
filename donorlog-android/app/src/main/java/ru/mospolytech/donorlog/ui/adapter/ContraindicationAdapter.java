package ru.mospolytech.donorlog.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mospolytech.donorlog.R;

public class ContraindicationAdapter extends RecyclerView.Adapter<ContraindicationAdapter.ViewHolder> {

    public static class ContraItem {
        public final String title;
        public final String badge;
        public final String desc;

        public ContraItem(String title, String badge, String desc) {
            this.title = title;
            this.badge = badge;
            this.desc = desc;
        }
    }

    private final List<ContraItem> allItems = new ArrayList<>();
    private final List<ContraItem> filteredItems = new ArrayList<>();

    public void setItems(List<ContraItem> items) {
        allItems.clear();
        filteredItems.clear();
        if (items != null) {
            allItems.addAll(items);
            filteredItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredItems.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredItems.addAll(allItems);
        } else {
            String lower = query.toLowerCase().trim();
            for (ContraItem item : allItems) {
                if (item.title.toLowerCase().contains(lower) || item.desc.toLowerCase().contains(lower)) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contraindication_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContraItem item = filteredItems.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvBadge.setText(item.badge);
        holder.tvDesc.setText(item.desc);

        if ("Бессрочно".equalsIgnoreCase(item.badge)) {
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_red);
            holder.tvBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_red));
        } else {
            holder.tvBadge.setBackgroundResource(R.drawable.bg_badge_yellow);
            holder.tvBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_yellow));
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvBadge;
        final TextView tvDesc;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvContraTitle);
            tvBadge = itemView.findViewById(R.id.tvContraBadge);
            tvDesc = itemView.findViewById(R.id.tvContraDesc);
        }
    }
}
