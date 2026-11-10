package ru.mospolytech.donorlog.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.ui.adapter.DonationAdapter;
import ru.mospolytech.donorlog.util.MedicalCalculator;
import ru.mospolytech.donorlog.util.UserSession;
import ru.mospolytech.donorlog.widget.DonationStatusWidgetProvider;
import ru.mospolytech.donorlog.widget.HonoredProgressWidgetProvider;
import ru.mospolytech.donorlog.widget.QuickMetricsWidgetProvider;
import ru.mospolytech.donorlog.widget.TrafficLightWidgetProvider;

public class MainActivity extends AppCompatActivity {

    private DonorRepository repository;
    private UserSession session;
    private DonationAdapter adapter;

    private SwipeRefreshLayout swipeRefresh;
    private TextView tvDonorName;
    private TextView tvBloodGroup;
    private TextView tvDonorRegionBadge;
    private View cardDonorProfile;

    private TextView tvIntervalBadge;
    private TextView tvIntervalDescription;

    private LinearLayout layoutHonoredMoscowBanner;
    private TextView tvHonoredMoscowShort;
    private LinearProgressIndicator pbHonoredMoscowProgress;

    private TextView tvHonoredShort;
    private LinearProgressIndicator pbHonoredProgress;

    private TextView tvEmptyDonations;
    private TextView tvDonationsCountBadge;
    private TextView tvStatDonationsCount;
    private TextView tvStatVolumeCount;
    private TextView tvStatLivesCount;
    private TextView tvTierBadge;
    private RecyclerView rvDonations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = DonorRepository.getInstance(this);
        session = UserSession.getInstance(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileViews();
        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            performSync(true);
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(this, AuthActivity.class));
            return true;
        } else if (id == R.id.action_pin_widgets) {
            pinWidgetsToHomeScreen();
            return true;
        } else if (id == R.id.action_test_notification) {
            ru.mospolytech.donorlog.util.NotificationHelper.sendReadinessNotification(this);
            Toast.makeText(this, "Тестовое уведомление отправлено в строку состояния", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        swipeRefresh = findViewById(R.id.swipeRefresh);
        cardDonorProfile = findViewById(R.id.cardDonorProfile);
        tvDonorName = findViewById(R.id.tvDonorName);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvDonorRegionBadge = findViewById(R.id.tvDonorRegionBadge);

        tvIntervalBadge = findViewById(R.id.tvIntervalBadge);
        tvIntervalDescription = findViewById(R.id.tvIntervalDescription);

        layoutHonoredMoscowBanner = findViewById(R.id.layoutHonoredMoscowBanner);
        tvHonoredMoscowShort = findViewById(R.id.tvHonoredMoscowShort);
        pbHonoredMoscowProgress = findViewById(R.id.pbHonoredMoscowProgress);

        tvHonoredShort = findViewById(R.id.tvHonoredShort);
        pbHonoredProgress = findViewById(R.id.pbHonoredProgress);

        tvEmptyDonations = findViewById(R.id.tvEmptyDonations);
        tvDonationsCountBadge = findViewById(R.id.tvDonationsCountBadge);
        tvStatDonationsCount = findViewById(R.id.tvStatDonationsCount);
        tvStatVolumeCount = findViewById(R.id.tvStatVolumeCount);
        tvStatLivesCount = findViewById(R.id.tvStatLivesCount);
        tvTierBadge = findViewById(R.id.tvTierBadge);
        rvDonations = findViewById(R.id.rvDonations);

        if (cardDonorProfile != null) {
            cardDonorProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AuthActivity.class)));
        }

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.md_theme_light_primary);
            swipeRefresh.setOnRefreshListener(() -> performSync(true));
        }

        rvDonations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonationAdapter();
        adapter.setOnItemClickListener(donation -> {
            Intent intent = new Intent(MainActivity.this, DonationEditorActivity.class);
            intent.putExtra(DonationEditorActivity.EXTRA_DONATION_ID, donation.getId());
            startActivity(intent);
        });
        rvDonations.setAdapter(adapter);

        // Achievements / Gamification Click Listeners
        View cardBadge1 = findViewById(R.id.cardBadge1);
        if (cardBadge1 != null) {
            cardBadge1.setOnClickListener(v -> showAchievementDialog(
                    "Первый шаг",
                    "Награда за первую успешную кроводачу.\n\nКритерий: 1 донация любого типа (цельная кровь, плазма, тромбоциты).\nСтатус: Выполнено (1 из 1)."
            ));
        }

        View cardBadge2 = findViewById(R.id.cardBadge2);
        if (cardBadge2 != null) {
            cardBadge2.setOnClickListener(v -> showAchievementDialog(
                    "Регулярный донор",
                    "Награда за регулярную помощь здравоохранению.\n\nКритерий: Не менее 3 донаций за один календарный год.\nСтатус: Выполнено (3 из 3)."
            ));
        }

        View cardBadge3 = findViewById(R.id.cardBadge3);
        if (cardBadge3 != null) {
            cardBadge3.setOnClickListener(v -> showAchievementDialog(
                    "Спасатель жизней",
                    "Каждая донация цельной крови спасает до 3 жизней пациентов.\n\nКритерий: Спасение более 5 жизней.\nСтатус: Выполнено (7 спасенных жизней)."
            ));
        }

        View cardBadge4 = findViewById(R.id.cardBadge4);
        if (cardBadge4 != null) {
            cardBadge4.setOnClickListener(v -> showAchievementDialog(
                    "Марафонец",
                    "Знак признания за преданность донорскому движению.\n\nКритерий: Совершение 10 донаций крови или ее компонентов.\nСтатус: В процессе (3 из 10, осталось 7)."
            ));
        }

        View cardBadge5 = findViewById(R.id.cardBadge5);
        if (cardBadge5 != null) {
            cardBadge5.setOnClickListener(v -> showAchievementDialog(
                    "Почетный путь",
                    "Движение к государственной награде «Почетный донор».\n\nКритерий: Достижение 15% прогресса по любой из законодательных схем.\nСтатус: В процессе (15% Москва, 8% РФ)."
            ));
        }

        // Service navigation cards
        View cardNavRoadmap = findViewById(R.id.cardNavRoadmap);
        if (cardNavRoadmap != null) {
            cardNavRoadmap.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HonoredDonorActivity.class)));
        }

        View cardNavAnalytics = findViewById(R.id.cardNavAnalytics);
        if (cardNavAnalytics != null) {
            cardNavAnalytics.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AnalyticsActivity.class)));
        }

        View cardNavDiet = findViewById(R.id.cardNavDiet);
        if (cardNavDiet != null) {
            cardNavDiet.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DietMemoActivity.class)));
        }

        View cardNavTraffic = findViewById(R.id.cardNavTraffic);
        if (cardNavTraffic != null) {
            cardNavTraffic.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TrafficLightActivity.class)));
        }

        View cardNavContra = findViewById(R.id.cardNavContra);
        if (cardNavContra != null) {
            cardNavContra.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ContraindicationsActivity.class)));
        }

        ExtendedFloatingActionButton fabAddDonation = findViewById(R.id.fabAddDonation);
        if (fabAddDonation != null) {
            fabAddDonation.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DonationEditorActivity.class)));
        }
    }

    private void showAchievementDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Понятно", null)
                .show();
    }

    private void updateProfileViews() {
        if (tvDonorName != null) tvDonorName.setText(session.getName());
        if (tvBloodGroup != null) tvBloodGroup.setText(session.getFullBloodType());
        if (tvDonorRegionBadge != null) tvDonorRegionBadge.setText(session.getRegion());

        if (layoutHonoredMoscowBanner != null) {
            layoutHonoredMoscowBanner.setVisibility(session.isMoscowRegion() ? View.VISIBLE : View.GONE);
        }
    }

    private void performSync(boolean showUserFeedback) {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
        repository.syncWithBackend(result -> {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            if (showUserFeedback) {
                Toast.makeText(MainActivity.this, "Данные обновлены", Toast.LENGTH_SHORT).show();
            }
            loadData();
        });
    }

    private void setupUserData() {
        updateProfileViews();
        performSync(false);
    }

    private void loadData() {
        repository.getAllDonations(donations -> {
            if (donations == null || donations.isEmpty()) {
                tvEmptyDonations.setVisibility(View.VISIBLE);
                rvDonations.setVisibility(View.GONE);
                tvDonationsCountBadge.setText("0");
                if (tvStatDonationsCount != null) tvStatDonationsCount.setText("0");
                if (tvStatVolumeCount != null) tvStatVolumeCount.setText("0 л");
                if (tvStatLivesCount != null) tvStatLivesCount.setText("0");

                tvIntervalBadge.setText("Допущен к сдаче");
                tvIntervalBadge.setBackgroundResource(R.drawable.bg_badge_green);
                tvIntervalBadge.setTextColor(ContextCompat.getColor(this, R.color.status_green));
                tvIntervalDescription.setText("История донаций пуста. Вы можете сдать кровь в любое удобное время.");
                updateHonoredProgress(0, 0);
            } else {
                tvEmptyDonations.setVisibility(View.GONE);
                rvDonations.setVisibility(View.VISIBLE);
                adapter.setItems(donations);
                tvDonationsCountBadge.setText(String.valueOf(donations.size()));

                int totalVolume = 0;
                int wholeCount = 0;
                int plasmaCount = 0;

                for (DonationEntity d : donations) {
                    totalVolume += d.getVolumeMl();
                    if ("WHOLE_BLOOD".equalsIgnoreCase(d.getComponentType())) {
                        wholeCount++;
                    } else if ("PLASMA".equalsIgnoreCase(d.getComponentType())) {
                        plasmaCount++;
                    }
                }

                if (tvStatDonationsCount != null) {
                    tvStatDonationsCount.setText(String.valueOf(donations.size()));
                }
                if (tvStatVolumeCount != null) {
                    double liters = totalVolume / 1000.0;
                    tvStatVolumeCount.setText(String.format(java.util.Locale.US, "%.1f л", liters));
                }
                if (tvStatLivesCount != null) {
                    int estimatedLives = wholeCount * 3 + plasmaCount;
                    if (estimatedLives == 0 && !donations.isEmpty()) estimatedLives = donations.size() * 2;
                    tvStatLivesCount.setText(String.valueOf(estimatedLives));
                }

                DonationEntity lastDonation = donations.get(0);
                updateIntervalBadge(lastDonation, donations);
                updateHonoredProgress(wholeCount, plasmaCount);
            }
            updateWidgets();
        });
    }

    private void updateIntervalBadge(DonationEntity lastDonation, List<DonationEntity> allDonations) {
        MedicalCalculator.IntervalStatus status = MedicalCalculator.calculateIntervalStatus(lastDonation, allDonations, session.isMale());
        if (status.isReady) {
            tvIntervalBadge.setText("Допущен к сдаче");
            tvIntervalBadge.setBackgroundResource(R.drawable.bg_badge_green);
            tvIntervalBadge.setTextColor(ContextCompat.getColor(this, R.color.status_green));
            tvIntervalDescription.setText(status.reason);
        } else {
            tvIntervalBadge.setText(status.daysRemaining + " дн.");
            tvIntervalBadge.setBackgroundResource(R.drawable.bg_badge_yellow);
            tvIntervalBadge.setTextColor(ContextCompat.getColor(this, R.color.status_yellow));
            tvIntervalDescription.setText(status.reason);
        }
    }

    private void updateHonoredProgress(int wholeBloodCount, int plasmaCount) {
        // Federal
        MedicalCalculator.RoadmapProgress federal = MedicalCalculator.calculateBestRoadmap(wholeBloodCount, plasmaCount);
        tvHonoredShort.setText(federal.schemeTitle + ": " + federal.completedCount + " из " + federal.targetCount + " (" + federal.percent + "%)");
        pbHonoredProgress.setProgress(federal.percent);

        // Moscow (if region is Moscow)
        if (session.isMoscowRegion()) {
            if (layoutHonoredMoscowBanner != null) layoutHonoredMoscowBanner.setVisibility(View.VISIBLE);
            MedicalCalculator.RoadmapProgress moscow = MedicalCalculator.calculateMoscowRoadmap(wholeBloodCount, plasmaCount);
            if (tvHonoredMoscowShort != null) {
                tvHonoredMoscowShort.setText("Почетный донор Москвы: " + moscow.completedCount + " из " + moscow.targetCount + " (" + moscow.percent + "%)");
            }
            if (pbHonoredMoscowProgress != null) {
                pbHonoredMoscowProgress.setProgress(moscow.percent);
            }
        } else {
            if (layoutHonoredMoscowBanner != null) layoutHonoredMoscowBanner.setVisibility(View.GONE);
        }
    }

    private void pinWidgetsToHomeScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppWidgetManager manager = getSystemService(AppWidgetManager.class);
            if (manager != null && manager.isRequestPinAppWidgetSupported()) {
                String[] items = {
                    "1. Статус донора и готовность (4×2)",
                    "2. Траектория Почетного донора (круговая диаграмма) (4×2)",
                    "3. Оперативный светофор станций крови (4×2)",
                    "4. Витальные биомаркеры и аналитика (4×2)"
                };
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Добавить виджет на рабочий стол")
                        .setItems(items, (dialog, which) -> {
                            if (which == 0) {
                                ComponentName comp1 = new ComponentName(this, DonationStatusWidgetProvider.class);
                                manager.requestPinAppWidget(comp1, null, null);
                            } else if (which == 1) {
                                ComponentName compHonored = new ComponentName(this, HonoredProgressWidgetProvider.class);
                                manager.requestPinAppWidget(compHonored, null, null);
                            } else if (which == 2) {
                                ComponentName comp2 = new ComponentName(this, TrafficLightWidgetProvider.class);
                                manager.requestPinAppWidget(comp2, null, null);
                            } else {
                                ComponentName compMetrics = new ComponentName(this, QuickMetricsWidgetProvider.class);
                                manager.requestPinAppWidget(compMetrics, null, null);
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            } else {
                Toast.makeText(this, "Закрепление виджетов не поддерживается текущим лаунчером", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateWidgets() {
        Intent intent1 = new Intent(this, DonationStatusWidgetProvider.class);
        intent1.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids1 = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), DonationStatusWidgetProvider.class));
        intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids1);
        sendBroadcast(intent1);

        Intent intentHonored = new Intent(this, HonoredProgressWidgetProvider.class);
        intentHonored.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idsHonored = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), HonoredProgressWidgetProvider.class));
        intentHonored.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idsHonored);
        sendBroadcast(intentHonored);

        Intent intent2 = new Intent(this, TrafficLightWidgetProvider.class);
        intent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids2 = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), TrafficLightWidgetProvider.class));
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids2);
        sendBroadcast(intent2);

        Intent intentMetrics = new Intent(this, QuickMetricsWidgetProvider.class);
        intentMetrics.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idsMetrics = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), QuickMetricsWidgetProvider.class));
        intentMetrics.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idsMetrics);
        sendBroadcast(intentMetrics);
    }
}
