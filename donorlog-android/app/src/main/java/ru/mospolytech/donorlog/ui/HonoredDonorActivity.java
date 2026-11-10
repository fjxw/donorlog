package ru.mospolytech.donorlog.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.util.MedicalCalculator;
import ru.mospolytech.donorlog.util.UserSession;

public class HonoredDonorActivity extends AppCompatActivity {

    private DonorRepository repository;
    private UserSession session;

    // Region Switcher
    private TextView tvCurrentRegion;
    private MaterialButton btnChangeRegion;

    // Hero Achievement Card
    private TextView tvDonorTierTitle;
    private TextView tvMoscowBadgeStatus;
    private CircularProgressIndicator pbCircularHonored;
    private TextView tvCircularPercent;
    private TextView tvHeroTitle;
    private TextView tvHeroSubtitle;
    private TextView tvHeroBestScheme;

    // Scope Toggle & Sections
    private MaterialButtonToggleGroup toggleGroupScope;
    private LinearLayout layoutMoscowSection;
    private LinearLayout layoutRussiaSection;

    // Moscow Scheme Views
    private TextView tvMoscowScheme1Status;
    private LinearProgressIndicator pbMoscowScheme1;
    private TextView tvMoscowScheme2Status;
    private LinearProgressIndicator pbMoscowScheme2;
    private TextView tvMoscowScheme3Status;
    private TextView tvMoscowScheme4Status;

    // Federal Scheme Views
    private TextView tvScheme1Status;
    private LinearProgressIndicator pbScheme1;
    private TextView tvScheme2Status;
    private LinearProgressIndicator pbScheme2;
    private TextView tvScheme3Status;
    private TextView tvScheme4Status;

    private int cachedWholeBlood = 0;
    private int cachedPlasma = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_honored_donor);

        MaterialToolbar toolbar = findViewById(R.id.toolbarHonored);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        repository = DonorRepository.getInstance(this);
        session = UserSession.getInstance(this);

        initViews();
        setupRegionSwitcher();
        setupScopeToggle();
        loadDonationStatistics();
    }

    private void initViews() {
        tvCurrentRegion = findViewById(R.id.tvCurrentRegion);
        btnChangeRegion = findViewById(R.id.btnChangeRegion);

        tvDonorTierTitle = findViewById(R.id.tvDonorTierTitle);
        tvMoscowBadgeStatus = findViewById(R.id.tvMoscowBadgeStatus);
        pbCircularHonored = findViewById(R.id.pbCircularHonored);
        tvCircularPercent = findViewById(R.id.tvCircularPercent);
        tvHeroTitle = findViewById(R.id.tvHeroTitle);
        tvHeroSubtitle = findViewById(R.id.tvHeroSubtitle);
        tvHeroBestScheme = findViewById(R.id.tvHeroBestScheme);

        toggleGroupScope = findViewById(R.id.toggleGroupScope);
        layoutMoscowSection = findViewById(R.id.layoutMoscowSection);
        layoutRussiaSection = findViewById(R.id.layoutRussiaSection);

        tvMoscowScheme1Status = findViewById(R.id.tvMoscowScheme1Status);
        pbMoscowScheme1 = findViewById(R.id.pbMoscowScheme1);
        tvMoscowScheme2Status = findViewById(R.id.tvMoscowScheme2Status);
        pbMoscowScheme2 = findViewById(R.id.pbMoscowScheme2);
        tvMoscowScheme3Status = findViewById(R.id.tvMoscowScheme3Status);
        tvMoscowScheme4Status = findViewById(R.id.tvMoscowScheme4Status);

        tvScheme1Status = findViewById(R.id.tvScheme1Status);
        pbScheme1 = findViewById(R.id.pbScheme1);
        tvScheme2Status = findViewById(R.id.tvScheme2Status);
        pbScheme2 = findViewById(R.id.pbScheme2);
        tvScheme3Status = findViewById(R.id.tvScheme3Status);
        tvScheme4Status = findViewById(R.id.tvScheme4Status);
    }

    private void setupRegionSwitcher() {
        updateRegionDisplay();

        btnChangeRegion.setOnClickListener(v -> {
            String[] regions = {"г. Москва", "Московская область", "Санкт-Петербург", "Другой регион РФ"};
            int selectedIndex = 0;
            String cur = session.getRegion();
            for (int i = 0; i < regions.length; i++) {
                if (regions[i].equalsIgnoreCase(cur)) {
                    selectedIndex = i;
                    break;
                }
            }

            new AlertDialog.Builder(this)
                    .setTitle("Выберите регион донорства")
                    .setSingleChoiceItems(regions, selectedIndex, (dialog, which) -> {
                        session.setRegion(regions[which]);
                        updateRegionDisplay();
                        applyStatisticsToUI();
                        dialog.dismiss();
                        Toast.makeText(this, "Регион изменен: " + regions[which], Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void setupScopeToggle() {
        boolean isMoscow = session.isMoscowRegion();
        if (isMoscow) {
            toggleGroupScope.check(R.id.btnTabMoscow);
            layoutMoscowSection.setVisibility(View.VISIBLE);
            layoutRussiaSection.setVisibility(View.GONE);
        } else {
            toggleGroupScope.check(R.id.btnTabRussia);
            layoutMoscowSection.setVisibility(View.GONE);
            layoutRussiaSection.setVisibility(View.VISIBLE);
        }

        toggleGroupScope.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnTabMoscow) {
                    layoutMoscowSection.setVisibility(View.VISIBLE);
                    layoutRussiaSection.setVisibility(View.GONE);
                    updateHeroCardForMoscow();
                } else {
                    layoutMoscowSection.setVisibility(View.GONE);
                    layoutRussiaSection.setVisibility(View.VISIBLE);
                    updateHeroCardForRussia();
                }
            }
        });
    }

    private void updateRegionDisplay() {
        String reg = session.getRegion();
        tvCurrentRegion.setText(reg);
        if (session.isMoscowRegion()) {
            toggleGroupScope.check(R.id.btnTabMoscow);
        } else {
            toggleGroupScope.check(R.id.btnTabRussia);
        }
    }

    private void loadDonationStatistics() {
        repository.getAllDonations(donations -> {
            cachedWholeBlood = 0;
            cachedPlasma = 0;

            if (donations != null) {
                for (DonationEntity d : donations) {
                    String ct = d.getComponentType();
                    if ("WHOLE_BLOOD".equalsIgnoreCase(ct) || "PLATELETS".equalsIgnoreCase(ct)) {
                        cachedWholeBlood++;
                    } else if ("PLASMA".equalsIgnoreCase(ct)) {
                        cachedPlasma++;
                    }
                }
            }

            applyStatisticsToUI();
        });
    }

    private void applyStatisticsToUI() {
        // Moscow Calculations
        MedicalCalculator.RoadmapProgress moscowRoad = MedicalCalculator.calculateMoscowRoadmap(cachedWholeBlood, cachedPlasma);

        // Moscow Scheme 1
        int m1Pct = Math.min(100, (cachedWholeBlood * 100) / 20);
        tvMoscowScheme1Status.setText(cachedWholeBlood + " из 20 (" + m1Pct + "%)");
        pbMoscowScheme1.setProgress(m1Pct);

        // Moscow Scheme 2
        int m2Pct = Math.min(100, (cachedPlasma * 100) / 30);
        tvMoscowScheme2Status.setText(cachedPlasma + " из 30 (" + m2Pct + "%)");
        pbMoscowScheme2.setProgress(m2Pct);

        // Moscow Scheme 3
        if (cachedWholeBlood >= 13) {
            int m3Tot = Math.min(20, cachedWholeBlood + cachedPlasma);
            int m3Pct = (m3Tot * 100) / 20;
            tvMoscowScheme3Status.setText(m3Tot + " из 20 (" + m3Pct + "%)");
        } else {
            tvMoscowScheme3Status.setText("Требуется минимум 13 кроводач (текущее: " + cachedWholeBlood + ")");
        }

        // Moscow Scheme 4
        int m4Tot = Math.min(30, cachedWholeBlood + cachedPlasma);
        int m4Pct = (m4Tot * 100) / 30;
        tvMoscowScheme4Status.setText(m4Tot + " из 30 (" + m4Pct + "%)");

        // Russia Calculations
        MedicalCalculator.RoadmapProgress road = MedicalCalculator.calculateBestRoadmap(cachedWholeBlood, cachedPlasma);

        // Federal Scheme 1
        int s1Pct = Math.min(100, (cachedWholeBlood * 100) / 40);
        tvScheme1Status.setText(cachedWholeBlood + " из 40 (" + s1Pct + "%)");
        pbScheme1.setProgress(s1Pct);

        // Federal Scheme 2
        int s2Pct = Math.min(100, (cachedPlasma * 100) / 60);
        tvScheme2Status.setText(cachedPlasma + " из 60 (" + s2Pct + "%)");
        pbScheme2.setProgress(s2Pct);

        // Federal Scheme 3
        if (cachedWholeBlood >= 25) {
            int s3Tot = Math.min(40, cachedWholeBlood + cachedPlasma);
            int s3Pct = (s3Tot * 100) / 40;
            tvScheme3Status.setText(s3Tot + " из 40 (" + s3Pct + "%)");
        } else {
            tvScheme3Status.setText("Требуется минимум 25 кроводач (текущее: " + cachedWholeBlood + ")");
        }

        // Federal Scheme 4
        int s4Tot = Math.min(60, cachedWholeBlood + cachedPlasma);
        int s4Pct = (s4Tot * 100) / 60;
        tvScheme4Status.setText(s4Tot + " из 60 (" + s4Pct + "%)");

        // Hero Card Setup
        if (toggleGroupScope.getCheckedButtonId() == R.id.btnTabMoscow) {
            updateHeroCardForMoscow();
        } else {
            updateHeroCardForRussia();
        }
    }

    private void updateHeroCardForMoscow() {
        MedicalCalculator.RoadmapProgress moscowRoad = MedicalCalculator.calculateMoscowRoadmap(cachedWholeBlood, cachedPlasma);
        int totalDonations = cachedWholeBlood + cachedPlasma;

        String tier = "Ступень: Начальный донор";
        if (totalDonations >= 20) tier = "Звание: Почетный донор Москвы";
        else if (totalDonations >= 10) tier = "Ступень: Опытный донор";
        else if (totalDonations >= 3) tier = "Ступень: Регулярный донор";
        tvDonorTierTitle.setText(tier);

        tvHeroTitle.setText("Почетный донор Москвы");
        int remaining = Math.max(0, moscowRoad.targetCount - moscowRoad.completedCount);
        tvHeroSubtitle.setText("Выполнено: " + moscowRoad.completedCount + " из " + moscowRoad.targetCount + " (осталось " + remaining + ")");
        tvHeroBestScheme.setText("Рекомендуемый путь: " + moscowRoad.schemeTitle);

        pbCircularHonored.setProgress(moscowRoad.percent);
        tvCircularPercent.setText(moscowRoad.percent + "%");
        tvMoscowBadgeStatus.setText(moscowRoad.isCompleted ? "Звание получено!" : moscowRoad.percent + "% выполнено");
    }

    private void updateHeroCardForRussia() {
        MedicalCalculator.RoadmapProgress road = MedicalCalculator.calculateBestRoadmap(cachedWholeBlood, cachedPlasma);
        int totalDonations = cachedWholeBlood + cachedPlasma;

        String tier = "Ступень: Начальный донор";
        if (totalDonations >= 40) tier = "Звание: Почетный донор России";
        else if (totalDonations >= 20) tier = "Ступень: Заслуженный донор";
        else if (totalDonations >= 10) tier = "Ступень: Опытный донор";
        else if (totalDonations >= 3) tier = "Ступень: Регулярный донор";
        tvDonorTierTitle.setText(tier);

        tvHeroTitle.setText("Почетный донор России");
        int remaining = Math.max(0, road.targetCount - road.completedCount);
        tvHeroSubtitle.setText("Выполнено: " + road.completedCount + " из " + road.targetCount + " (осталось " + remaining + ")");
        tvHeroBestScheme.setText("Рекомендуемый путь: " + road.schemeTitle);

        pbCircularHonored.setProgress(road.percent);
        tvCircularPercent.setText(road.percent + "%");
        tvMoscowBadgeStatus.setText(road.isCompleted ? "Звание получено!" : road.percent + "% выполнено");
    }
}
