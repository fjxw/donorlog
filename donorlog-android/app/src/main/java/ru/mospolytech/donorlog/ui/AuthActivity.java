package ru.mospolytech.donorlog.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.util.UserSession;

public class AuthActivity extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroupAuth;
    private TextInputLayout tilFullName;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private LinearLayout layoutMedicalProfile;
    private ChipGroup chipGroupBlood;
    private ChipGroup chipGroupRh;
    private ChipGroup chipGroupRegion;
    private MaterialButton btnSubmitAuth;
    private MaterialButton btnGuestMode;
    private TextView tvAuthSubtitle;

    private boolean isRegisterMode = false;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        session = UserSession.getInstance(this);

        initViews();
        setupListeners();
        populateExistingData();
    }

    private void initViews() {
        toggleGroupAuth = findViewById(R.id.toggleGroupAuth);
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        layoutMedicalProfile = findViewById(R.id.layoutMedicalProfile);
        chipGroupBlood = findViewById(R.id.chipGroupBlood);
        chipGroupRh = findViewById(R.id.chipGroupRh);
        chipGroupRegion = findViewById(R.id.chipGroupRegion);
        btnSubmitAuth = findViewById(R.id.btnSubmitAuth);
        btnGuestMode = findViewById(R.id.btnGuestMode);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);

        toggleGroupAuth.check(R.id.btnModeLogin);
    }

    private void setupListeners() {
        toggleGroupAuth.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnModeRegister) {
                    setRegisterMode(true);
                } else {
                    setRegisterMode(false);
                }
            }
        });

        btnSubmitAuth.setOnClickListener(v -> processSubmit());
        btnGuestMode.setOnClickListener(v -> proceedToMain());
    }

    private void setRegisterMode(boolean register) {
        isRegisterMode = register;
        if (register) {
            tilFullName.setVisibility(View.VISIBLE);
            layoutMedicalProfile.setVisibility(View.VISIBLE);
            btnSubmitAuth.setText("Зарегистрироваться");
            tvAuthSubtitle.setText("Создание нового профиля донора крови");
        } else {
            tilFullName.setVisibility(View.GONE);
            layoutMedicalProfile.setVisibility(View.GONE);
            btnSubmitAuth.setText("Войти в личный кабинет");
            tvAuthSubtitle.setText("Персональный дневник и траектория донора");
        }
    }

    private void populateExistingData() {
        etEmail.setText(session.getEmail());
        etFullName.setText(session.getName());

        if (session.isMoscowRegion()) {
            chipGroupRegion.check(R.id.chipRegionMoscow);
        } else {
            chipGroupRegion.check(R.id.chipRegionOther);
        }
    }

    private void processSubmit() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите адрес электронной почты");
            return;
        }

        String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        if (isRegisterMode && TextUtils.isEmpty(name)) {
            etFullName.setError("Укажите ваше ФИО");
            return;
        }

        String selectedRegion = chipGroupRegion.getCheckedChipId() == R.id.chipRegionMoscow
                ? UserSession.REGION_MOSCOW
                : UserSession.REGION_OTHER;

        String bloodGroup = "A(II)";
        int bloodId = chipGroupBlood.getCheckedChipId();
        if (bloodId == R.id.chipBloodO) bloodGroup = "O(I)";
        else if (bloodId == R.id.chipBloodB) bloodGroup = "B(III)";
        else if (bloodId == R.id.chipBloodAB) bloodGroup = "AB(IV)";

        String rh = chipGroupRh.getCheckedChipId() == R.id.chipRhMinus ? "Rh-" : "Rh+";

        if (isRegisterMode) {
            session.saveProfile(name, email, bloodGroup, rh, selectedRegion, true);
            Toast.makeText(this, "Профиль донора успешно сохранен", Toast.LENGTH_SHORT).show();
        } else {
            session.setEmail(email);
            session.setRegion(selectedRegion);
            session.setLoggedIn(true);
        }

        proceedToMain();
    }

    private void proceedToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
