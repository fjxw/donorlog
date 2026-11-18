package ru.mospolytech.donorlog.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.ComponentType;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;

public class DonationEditorActivity extends AppCompatActivity {

    public static final String EXTRA_DONATION_ID = "extra_donation_id";

    private DonorRepository repository;
    private long editingDonationId = -1;
    private DonationEntity existingDonation;

    private com.google.android.material.appbar.MaterialToolbar toolbarEditor;
    private AutoCompleteTextView actvComponentType;
    private TextInputEditText etDate;
    private TextInputEditText etVolume;
    private TextInputEditText etStation;
    private TextInputEditText etHemoglobin;
    private TextInputEditText etSystolicBp;
    private TextInputEditText etDiastolicBp;
    private TextInputEditText etPulse;
    private MaterialCheckBox cbGratuitous;
    private TextInputEditText etNotes;
    private MaterialButton btnSaveDonation;
    private MaterialButton btnDeleteDonation;

    private final Calendar selectedCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_editor);

        toolbarEditor = findViewById(R.id.toolbarEditor);
        if (toolbarEditor != null) {
            toolbarEditor.setNavigationOnClickListener(v -> finish());
        }

        repository = DonorRepository.getInstance(this);
        editingDonationId = getIntent().getLongExtra(EXTRA_DONATION_ID, -1);

        initViews();
        setupDropdown();
        setupDatePicker();

        if (editingDonationId > 0) {
            loadExistingDonation();
        } else {
            etDate.setText(dateFormat.format(new Date()));
        }
    }

    private void initViews() {
        actvComponentType = findViewById(R.id.actvComponentType);
        etDate = findViewById(R.id.etDate);
        etVolume = findViewById(R.id.etVolume);
        etStation = findViewById(R.id.etStation);
        etHemoglobin = findViewById(R.id.etHemoglobin);
        etSystolicBp = findViewById(R.id.etSystolicBp);
        etDiastolicBp = findViewById(R.id.etDiastolicBp);
        etPulse = findViewById(R.id.etPulse);
        cbGratuitous = findViewById(R.id.cbGratuitous);
        etNotes = findViewById(R.id.etNotes);
        btnSaveDonation = findViewById(R.id.btnSaveDonation);
        btnDeleteDonation = findViewById(R.id.btnDeleteDonation);

        btnSaveDonation.setOnClickListener(v -> saveDonation());

        btnDeleteDonation.setOnClickListener(v -> {
            if (editingDonationId > 0) {
                repository.deleteDonation(editingDonationId, result -> {
                    Toast.makeText(DonationEditorActivity.this, "Запись удалена", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void setupDropdown() {
        String[] types = new String[]{
                ComponentType.WHOLE_BLOOD.getTitleRu(),
                ComponentType.PLASMA.getTitleRu(),
                ComponentType.PLATELETS.getTitleRu(),
                ComponentType.ERYTHROCYTES.getTitleRu()
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        actvComponentType.setAdapter(adapter);
        actvComponentType.setText(types[0], false);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    DonationEditorActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(dateFormat.format(selectedCalendar.getTime()));
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void loadExistingDonation() {
        if (toolbarEditor != null) {
            toolbarEditor.setTitle(R.string.title_activity_editor_edit);
        }
        btnDeleteDonation.setVisibility(View.VISIBLE);

        repository.getDonationById(editingDonationId, donation -> {
            if (donation != null) {
                existingDonation = donation;
                ComponentType ct = ComponentType.fromString(donation.getComponentType());
                actvComponentType.setText(ct.getTitleRu(), false);
                selectedCalendar.setTimeInMillis(donation.getDonationDate());
                etDate.setText(dateFormat.format(selectedCalendar.getTime()));
                etVolume.setText(String.valueOf(donation.getVolumeMl()));
                etStation.setText(donation.getStationName());
                etHemoglobin.setText(String.valueOf((int) donation.getHemoglobin()));
                etSystolicBp.setText(String.valueOf(donation.getSystolicBp()));
                etDiastolicBp.setText(String.valueOf(donation.getDiastolicBp()));
                etPulse.setText(String.valueOf(donation.getPulse()));
                cbGratuitous.setChecked(donation.isGratuitous());
                etNotes.setText(donation.getNotes() != null ? donation.getNotes() : "");
            }
        });
    }

    private void saveDonation() {
        String typeStr = actvComponentType.getText().toString();
        ComponentType ct = ComponentType.fromString(typeStr);

        String volumeStr = etVolume.getText() != null ? etVolume.getText().toString().trim() : "";
        String station = etStation.getText() != null ? etStation.getText().toString().trim() : "";
        String hbStr = etHemoglobin.getText() != null ? etHemoglobin.getText().toString().trim() : "";
        String sysStr = etSystolicBp.getText() != null ? etSystolicBp.getText().toString().trim() : "";
        String diaStr = etDiastolicBp.getText() != null ? etDiastolicBp.getText().toString().trim() : "";
        String pulseStr = etPulse.getText() != null ? etPulse.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        if (station.isEmpty()) {
            Toast.makeText(this, R.string.validation_error_station, Toast.LENGTH_SHORT).show();
            return;
        }

        int volume = 450;
        try {
            volume = Integer.parseInt(volumeStr);
        } catch (NumberFormatException ignored) {}

        double hb = 140.0;
        try {
            hb = Double.parseDouble(hbStr);
        } catch (NumberFormatException ignored) {}

        int sys = 120;
        try {
            sys = Integer.parseInt(sysStr);
        } catch (NumberFormatException ignored) {}

        int dia = 80;
        try {
            dia = Integer.parseInt(diaStr);
        } catch (NumberFormatException ignored) {}

        int pulse = 72;
        try {
            pulse = Integer.parseInt(pulseStr);
        } catch (NumberFormatException ignored) {}

        // Clinical warning check
        if (hb < 130.0) {
            Toast.makeText(this, R.string.validation_hemoglobin_warning, Toast.LENGTH_LONG).show();
        }

        if (existingDonation == null) {
            DonationEntity newDonation = new DonationEntity(
                    ct.name(),
                    selectedCalendar.getTimeInMillis(),
                    volume,
                    station,
                    hb,
                    sys,
                    dia,
                    pulse,
                    cbGratuitous.isChecked(),
                    notes
            );
            repository.insertDonation(newDonation, id -> {
                repository.syncWithBackend(null);
                Toast.makeText(DonationEditorActivity.this, "Донация успешно сохранена", Toast.LENGTH_SHORT).show();
                finish();
            });
        } else {
            existingDonation.setComponentType(ct.name());
            existingDonation.setDonationDate(selectedCalendar.getTimeInMillis());
            existingDonation.setVolumeMl(volume);
            existingDonation.setStationName(station);
            existingDonation.setHemoglobin(hb);
            existingDonation.setSystolicBp(sys);
            existingDonation.setDiastolicBp(dia);
            existingDonation.setPulse(pulse);
            existingDonation.setGratuitous(cbGratuitous.isChecked());
            existingDonation.setNotes(notes);
            existingDonation.setUpdatedAt(System.currentTimeMillis());

            repository.updateDonation(existingDonation, v -> {
                repository.syncWithBackend(null);
                Toast.makeText(DonationEditorActivity.this, "Запись обновлена", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
