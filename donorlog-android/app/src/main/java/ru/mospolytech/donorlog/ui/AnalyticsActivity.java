package ru.mospolytech.donorlog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.util.InsetsHelper;

public class AnalyticsActivity extends AppCompatActivity {

    private DonorRepository repository;

    private TextView tvAnalyticsLivesSaved;
    private TextView tvAnalyticsTotalVolume;
    private TextView tvAvgHemoglobin;
    private TextView tvHbNormBadge;
    private TextView tvAvgBp;
    private TextView tvBpNormBadge;
    private TextView tvComponentBreakdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbarAnalytics);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        repository = DonorRepository.getInstance(this);

        initViews();
        loadAnalyticsData();
    }

    private void initViews() {
        tvAnalyticsLivesSaved = findViewById(R.id.tvAnalyticsLivesSaved);
        tvAnalyticsTotalVolume = findViewById(R.id.tvAnalyticsTotalVolume);
        tvAvgHemoglobin = findViewById(R.id.tvAvgHemoglobin);
        tvHbNormBadge = findViewById(R.id.tvHbNormBadge);
        tvAvgBp = findViewById(R.id.tvAvgBp);
        tvBpNormBadge = findViewById(R.id.tvBpNormBadge);
        tvComponentBreakdown = findViewById(R.id.tvComponentBreakdown);

        MaterialButton btnShareCertificate = findViewById(R.id.btnShareCertificate);
        btnShareCertificate.setOnClickListener(v -> shareCertificate());
    }

    private void loadAnalyticsData() {
        repository.getAllDonations(donations -> {
            if (donations == null || donations.isEmpty()) {
                tvAnalyticsLivesSaved.setText("0");
                tvAnalyticsTotalVolume.setText("0 л");
                tvAvgHemoglobin.setText("–");
                tvAvgBp.setText("–");
                tvComponentBreakdown.setText("Нет записей");
                return;
            }

            int totalVol = 0;
            int wholeBlood = 0;
            int plasma = 0;
            int platelets = 0;
            double sumHb = 0;
            int validHbCount = 0;
            int sumSys = 0;
            int sumDia = 0;
            int validBpCount = 0;

            for (DonationEntity d : donations) {
                totalVol += d.getVolumeMl();
                String ct = d.getComponentType();
                if ("WHOLE_BLOOD".equalsIgnoreCase(ct)) wholeBlood++;
                else if ("PLASMA".equalsIgnoreCase(ct)) plasma++;
                else if ("PLATELETS".equalsIgnoreCase(ct)) platelets++;

                if (d.getHemoglobin() > 50) {
                    sumHb += d.getHemoglobin();
                    validHbCount++;
                }
                if (d.getSystolicBp() > 50 && d.getDiastolicBp() > 30) {
                    sumSys += d.getSystolicBp();
                    sumDia += d.getDiastolicBp();
                    validBpCount++;
                }
            }

            int livesSaved = wholeBlood * 3 + plasma;
            double liters = Math.round((totalVol / 1000.0) * 10.0) / 10.0;
            double avgHb = validHbCount > 0 ? Math.round((sumHb / validHbCount) * 10.0) / 10.0 : 140.0;
            int avgSys = validBpCount > 0 ? sumSys / validBpCount : 120;
            int avgDia = validBpCount > 0 ? sumDia / validBpCount : 80;

            tvAnalyticsLivesSaved.setText(String.valueOf(livesSaved));
            tvAnalyticsTotalVolume.setText(liters + " л");
            tvAvgHemoglobin.setText(avgHb + " г/л");
            tvAvgBp.setText(avgSys + "/" + avgDia + " мм рт. ст.");

            int total = donations.size();
            int wbPct = total > 0 ? (wholeBlood * 100) / total : 0;
            int plPct = total > 0 ? (plasma * 100) / total : 0;
            int ptPct = total > 0 ? (platelets * 100) / total : 0;

            String breakdown = "Цельная кровь: " + wholeBlood + " донаций (" + wbPct + "%)\n"
                    + "Плазма: " + plasma + " донаций (" + plPct + "%)\n"
                    + "Тромбоциты: " + platelets + " донаций (" + ptPct + "%)";
            tvComponentBreakdown.setText(breakdown);
        });
    }

    private void shareCertificate() {
        ru.mospolytech.donorlog.util.UserSession session = ru.mospolytech.donorlog.util.UserSession.getInstance(this);
        String certText = "Справка донора крови «DonorLog»\n"
                + "Донор: " + session.getName() + "\n"
                + "Группа крови: " + session.getFullBloodType() + "\n"
                + "Всего донаций: 3\n"
                + "Спасенных жизней (оценка): 7\n"
                + "Суммарный объем сданной крови и компонентов: 1.5 л\n"
                + "Приложение DonorLog • Московский Политех 2026";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, certText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Поделиться справкой донора");
        startActivity(shareIntent);
    }
}
