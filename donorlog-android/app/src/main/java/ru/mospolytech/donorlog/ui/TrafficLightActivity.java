package ru.mospolytech.donorlog.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.ui.adapter.StationAdapter;

public class TrafficLightActivity extends AppCompatActivity {

    private DonorRepository repository;
    private StationAdapter adapter;

    private SwipeRefreshLayout swipeRefreshTraffic;
    private ChipGroup chipGroupBlood;
    private RecyclerView rvStations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_light);

        MaterialToolbar toolbar = findViewById(R.id.toolbarTraffic);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        repository = DonorRepository.getInstance(this);

        initViews();
        loadStations();
    }

    private void initViews() {
        swipeRefreshTraffic = findViewById(R.id.swipeRefreshTraffic);
        chipGroupBlood = findViewById(R.id.chipGroupBlood);
        rvStations = findViewById(R.id.rvStations);

        if (swipeRefreshTraffic != null) {
            swipeRefreshTraffic.setColorSchemeResources(R.color.md_theme_light_primary);
            swipeRefreshTraffic.setOnRefreshListener(() -> {
                repository.syncWithBackend(result -> {
                    swipeRefreshTraffic.setRefreshing(false);
                    loadStations();
                    Toast.makeText(TrafficLightActivity.this, "Данные центров крови обновлены", Toast.LENGTH_SHORT).show();
                });
            });
        }

        rvStations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StationAdapter();
        rvStations.setAdapter(adapter);

        chipGroupBlood.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            String groupKey = "A_POS";
            if (id == R.id.chipOPos) groupKey = "O_POS";
            else if (id == R.id.chipONeg) groupKey = "O_NEG";
            else if (id == R.id.chipAPos) groupKey = "A_POS";
            else if (id == R.id.chipANeg) groupKey = "A_NEG";
            else if (id == R.id.chipBPos) groupKey = "B_POS";
            else if (id == R.id.chipBNeg) groupKey = "B_NEG";
            else if (id == R.id.chipAbPos) groupKey = "AB_POS";
            else if (id == R.id.chipAbNeg) groupKey = "AB_NEG";

            adapter.setSelectedBloodGroup(groupKey);
        });
    }

    private void loadStations() {
        repository.getAllStations(stations -> {
            if (stations != null) {
                adapter.setItems(stations);
            }
        });
    }
}
