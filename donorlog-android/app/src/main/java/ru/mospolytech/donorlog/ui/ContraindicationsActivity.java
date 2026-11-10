package ru.mospolytech.donorlog.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.ui.adapter.ContraindicationAdapter;
import ru.mospolytech.donorlog.util.InsetsHelper;

public class ContraindicationsActivity extends AppCompatActivity {

    private AutoCompleteTextView actvContraType;
    private TextInputEditText etEventDate;
    private TextView tvCalcResult;
    private TextInputEditText etSearchContra;
    private RecyclerView rvContraindications;

    private ContraindicationAdapter adapter;
    private final Calendar selectedCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private final String[] presetTitles = new String[]{
            "Татуировки, пирсинг, татуаж (120 дн.)",
            "Хирургические операции (120 дн.)",
            "ОРВИ, грипп, ангина (30 дн.)",
            "Удаление зуба (10 дн.)",
            "Прием антибиотиков (14 дн.)",
            "Прием анальгетиков, салицилатов (3 дн.)",
            "Вакцинация инактивированная (10 дн.)",
            "Вакцинация живая (30 дн.)",
            "Контакт с больными гепатитом (120 дн.)"
    };

    private final int[] presetDays = new int[]{120, 120, 30, 10, 14, 3, 10, 30, 120};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contraindications);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbarContra);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        initViews();
        setupPresets();
        setupDatePicker();
        setupDirectoryList();
    }

    private void initViews() {
        actvContraType = findViewById(R.id.actvContraType);
        etEventDate = findViewById(R.id.etEventDate);
        tvCalcResult = findViewById(R.id.tvCalcResult);
        etSearchContra = findViewById(R.id.etSearchContra);
        rvContraindications = findViewById(R.id.rvContraindications);

        rvContraindications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContraindicationAdapter();
        rvContraindications.setAdapter(adapter);

        etSearchContra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPresets() {
        ArrayAdapter<String> dropAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, presetTitles);
        actvContraType.setAdapter(dropAdapter);
        actvContraType.setText(presetTitles[0], false);
        actvContraType.setOnItemClickListener((parent, view, position, id) -> calculateDeferral());
    }

    private void setupDatePicker() {
        // Set default to 10 days ago
        selectedCalendar.add(Calendar.DAY_OF_YEAR, -10);
        etEventDate.setText(dateFormat.format(selectedCalendar.getTime()));
        calculateDeferral();

        etEventDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    ContraindicationsActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etEventDate.setText(dateFormat.format(selectedCalendar.getTime()));
                        calculateDeferral();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void calculateDeferral() {
        String selected = actvContraType.getText().toString();
        int days = 120;
        for (int i = 0; i < presetTitles.length; i++) {
            if (presetTitles[i].equalsIgnoreCase(selected)) {
                days = presetDays[i];
                break;
            }
        }

        long eventMillis = selectedCalendar.getTimeInMillis();
        long eligibleMillis = eventMillis + TimeUnit.DAYS.toMillis(days);
        long now = System.currentTimeMillis();

        Date eligibleDate = new Date(eligibleMillis);
        String dateStr = dateFormat.format(eligibleDate);

        if (now >= eligibleMillis) {
            tvCalcResult.setText("Срок отвода " + days + " дн. истек (" + dateStr + "). Допуск к сдаче крови разрешен");
            tvCalcResult.setTextColor(getColor(R.color.status_green));
            tvCalcResult.setBackgroundResource(R.drawable.bg_badge_green);
        } else {
            long remaining = (long) Math.ceil((double) (eligibleMillis - now) / TimeUnit.DAYS.toMillis(1));
            tvCalcResult.setText("Срок отвода: допуск откроется " + dateStr + " (осталось " + remaining + " дн.)");
            tvCalcResult.setTextColor(getColor(R.color.status_yellow));
            tvCalcResult.setBackgroundResource(R.drawable.bg_badge_yellow);
        }
    }

    private void setupDirectoryList() {
        List<ContraindicationAdapter.ContraItem> list = new ArrayList<>();
        list.add(new ContraindicationAdapter.ContraItem("Татуировки, пирсинг, перманентный макияж", "120 дней", "Нанесение татуировок, татуаж или пирсинг любой локализации. Срок отвода отсчитывается с момента процедуры."));
        list.add(new ContraindicationAdapter.ContraItem("Хирургические операции", "120 дней", "Любые плановые или экстренные оперативные вмешательства при отсутствии осложнений."));
        list.add(new ContraindicationAdapter.ContraItem("ОРВИ, грипп, ангина, синусит", "30 дней", "Острые респираторные инфекции верхних и нижних дыхательных путей. Срок отвода после полного выздоровления."));
        list.add(new ContraindicationAdapter.ContraItem("Удаление зуба (экстракция)", "10 дней", "Хирургическая экстракция зуба при отсутствии воспалительного процесса лунки."));
        list.add(new ContraindicationAdapter.ContraItem("Курсовой прием антибиотиков", "14 дней", "Лечение антибактериальными препаратами любых групп. Отвод исчисляется после последней дозы."));
        list.add(new ContraindicationAdapter.ContraItem("Прием анальгетиков, салицилатов", "3 дня", "Прием аспирина, ибупрофена, парацетамола, анальгина (влияние на агрегацию тромбоцитов)."));
        list.add(new ContraindicationAdapter.ContraItem("Вакцинация инактивированная", "10 дней", "Прививки убитыми вакцинами (грипп, столбняк, гепатит B, дифтерия)."));
        list.add(new ContraindicationAdapter.ContraItem("Вакцинация живыми вакцинами", "30 дней", "Прививки живыми вакцинами (корь, краснуха, БЦЖ, желтая лихорадка)."));
        list.add(new ContraindicationAdapter.ContraItem("Контакт с больными гепатитами B, C", "120 дней", "Эпидемиологический контакт с подтвержденными носителями вирусных гепатитов."));
        list.add(new ContraindicationAdapter.ContraItem("Инфекция ВИЧ и СПИД", "Бессрочно", "Абсолютное противопоказание к донорству крови и ее компонентов."));
        list.add(new ContraindicationAdapter.ContraItem("Вирусные гепатиты B и C", "Бессрочно", "Абсолютное противопоказание независимо от клинической формы и давности."));
        list.add(new ContraindicationAdapter.ContraItem("Туберкулез всех форм", "Бессрочно", "Абсолютное противопоказание к донорству."));
        list.add(new ContraindicationAdapter.ContraItem("Злокачественные новообразования", "Бессрочно", "Абсолютное противопоказание независимо от стадии и лечения."));

        adapter.setItems(list);
    }
}
