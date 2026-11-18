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
import ru.mospolytech.donorlog.data.remote.DonorApiClient;
import ru.mospolytech.donorlog.util.UserSession;

public class AuthActivity extends AppCompatActivity {

    private MaterialButtonToggleGroup toggleGroupAuth;
    private MaterialButton btnModeLogin;
    private MaterialButton btnModeRegister;
    private TextInputLayout tilFullName;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private LinearLayout layoutMedicalProfile;
    private ChipGroup chipGroupBlood;
    private ChipGroup chipGroupRh;
    private ChipGroup chipGroupRegion;
    private MaterialButton btnSubmitAuth;
    private MaterialButton btnLogout;
    private MaterialButton btnGuestMode;
    private MaterialButton btnBackToMain;
    private TextView tvAuthSubtitle;
    private com.google.android.material.appbar.MaterialToolbar toolbarAuth;

    private boolean isRegisterMode = false;
    private UserSession session;
    private DonorApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        session = UserSession.getInstance(this);
        apiClient = new DonorApiClient();

        initViews();
        setupListeners();
        updateUiState();
    }

    private void initViews() {
        toggleGroupAuth = findViewById(R.id.toggleGroupAuth);
        btnModeLogin = findViewById(R.id.btnModeLogin);
        btnModeRegister = findViewById(R.id.btnModeRegister);
        tilFullName = findViewById(R.id.tilFullName);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        layoutMedicalProfile = findViewById(R.id.layoutMedicalProfile);
        chipGroupBlood = findViewById(R.id.chipGroupBlood);
        chipGroupRh = findViewById(R.id.chipGroupRh);
        chipGroupRegion = findViewById(R.id.chipGroupRegion);
        btnSubmitAuth = findViewById(R.id.btnSubmitAuth);
        btnLogout = findViewById(R.id.btnLogout);
        btnGuestMode = findViewById(R.id.btnGuestMode);
        btnBackToMain = findViewById(R.id.btnBackToMain);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);
        toolbarAuth = findViewById(R.id.toolbarAuth);
    }

    private void updateUiState() {
        if (session.isLoggedIn()) {
            // Личный кабинет авторизованного пользователя
            if (toolbarAuth != null) toolbarAuth.setTitle("Личный кабинет");
            tvAuthSubtitle.setText("Личный кабинет донора · Активный аккаунт");
            btnModeLogin.setText("Личный профиль");
            btnModeRegister.setText("Сменить аккаунт");
            toggleGroupAuth.check(R.id.btnModeLogin);

            tilFullName.setVisibility(View.VISIBLE);
            layoutMedicalProfile.setVisibility(View.VISIBLE);
            btnSubmitAuth.setText("Сохранить изменения профиля");

            if (btnLogout != null) btnLogout.setVisibility(View.VISIBLE);
            if (btnGuestMode != null) btnGuestMode.setVisibility(View.GONE);

            populateExistingData();
        } else {
            // Экран первичного входа / регистрации
            if (toolbarAuth != null) toolbarAuth.setTitle("Авторизация");
            btnModeLogin.setText("Вход");
            btnModeRegister.setText("Регистрация");
            toggleGroupAuth.check(R.id.btnModeLogin);

            if (btnLogout != null) btnLogout.setVisibility(View.GONE);
            if (btnGuestMode != null) btnGuestMode.setVisibility(View.VISIBLE);

            setRegisterMode(false);
        }
    }

    private void setupListeners() {
        if (toolbarAuth != null) {
            toolbarAuth.setNavigationOnClickListener(v -> finish());
        }

        if (btnBackToMain != null) {
            btnBackToMain.setOnClickListener(v -> finish());
        }

        toggleGroupAuth.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (session.isLoggedIn()) {
                    if (checkedId == R.id.btnModeRegister) {
                        // Режим смены аккаунта
                        logoutAndSwitchToAuth();
                    } else {
                        updateUiState();
                    }
                } else {
                    if (checkedId == R.id.btnModeRegister) {
                        setRegisterMode(true);
                    } else {
                        setRegisterMode(false);
                    }
                }
            }
        });

        btnSubmitAuth.setOnClickListener(v -> processSubmit());

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logoutAndSwitchToAuth());
        }

        btnGuestMode.setOnClickListener(v -> proceedToMain());
    }

    private void logoutAndSwitchToAuth() {
        session.logout();
        Toast.makeText(this, "Вы вышли из учетной записи", Toast.LENGTH_SHORT).show();
        etFullName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        updateUiState();
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

        String bg = session.getBloodGroup();
        if ("O(I)".equals(bg)) chipGroupBlood.check(R.id.chipBloodO);
        else if ("B(III)".equals(bg)) chipGroupBlood.check(R.id.chipBloodB);
        else if ("AB(IV)".equals(bg)) chipGroupBlood.check(R.id.chipBloodAB);
        else chipGroupBlood.check(R.id.chipBloodA);

        String rh = session.getRhFactor();
        if ("Rh-".equals(rh)) chipGroupRh.check(R.id.chipRhMinus);
        else chipGroupRh.check(R.id.chipRhPlus);
    }

    private void processSubmit() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Введите адрес электронной почты");
            return;
        }

        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        final String selectedRegion = chipGroupRegion.getCheckedChipId() == R.id.chipRegionMoscow
                ? UserSession.REGION_MOSCOW
                : UserSession.REGION_OTHER;

        int bloodId = chipGroupBlood.getCheckedChipId();
        final String bloodGroup;
        if (bloodId == R.id.chipBloodO) bloodGroup = "O(I)";
        else if (bloodId == R.id.chipBloodB) bloodGroup = "B(III)";
        else if (bloodId == R.id.chipBloodAB) bloodGroup = "AB(IV)";
        else bloodGroup = "A(II)";

        final String rh = chipGroupRh.getCheckedChipId() == R.id.chipRhMinus ? "Rh-" : "Rh+";

        if (session.isLoggedIn()) {
            // Режим редактирования существующего профиля
            String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            if (TextUtils.isEmpty(name)) {
                etFullName.setError("Укажите ваше ФИО");
                return;
            }
            session.saveProfile(name, email, bloodGroup, rh, selectedRegion, true);
            apiClient.registerUser(email, name, password, bloodGroup, rh, selectedRegion, null);
            apiClient.syncProfile(name, email, bloodGroup, rh, null);
            Toast.makeText(this, "Данные профиля успешно обновлены", Toast.LENGTH_SHORT).show();
            proceedToMain();
        } else if (isRegisterMode) {
            // Режим регистрации нового донора
            String name = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            if (TextUtils.isEmpty(name)) {
                etFullName.setError("Укажите ваше ФИО");
                return;
            }
            btnSubmitAuth.setEnabled(false);
            btnSubmitAuth.setText("Регистрация...");

            apiClient.registerUser(email, name, password, bloodGroup, rh, selectedRegion, new DonorApiClient.ApiCallback<java.util.Map<String, Object>>() {
                @Override
                public void onSuccess(java.util.Map<String, Object> data) {
                    session.saveProfile(name, email, bloodGroup, rh, selectedRegion, true);
                    Toast.makeText(AuthActivity.this, "Регистрация успешна! Добро пожаловать, " + name, Toast.LENGTH_SHORT).show();
                    proceedToMain();
                }

                @Override
                public void onError(String errorMessage) {
                    session.saveProfile(name, email, bloodGroup, rh, selectedRegion, true);
                    Toast.makeText(AuthActivity.this, "Сохранено локально (Сервер: " + errorMessage + ")", Toast.LENGTH_LONG).show();
                    proceedToMain();
                }
            });
        } else {
            // Режим входа
            btnSubmitAuth.setEnabled(false);
            btnSubmitAuth.setText("Вход...");

            apiClient.loginUser(email, password, new DonorApiClient.ApiCallback<java.util.Map<String, Object>>() {
                @Override
                public void onSuccess(java.util.Map<String, Object> data) {
                    String serverFullName = null;
                    String serverBlood = bloodGroup;
                    String serverRh = rh;
                    String serverRegion = selectedRegion;

                    if (data != null) {
                        if (data.get("fullName") != null) serverFullName = String.valueOf(data.get("fullName"));
                        if (data.get("bloodGroup") != null) serverBlood = String.valueOf(data.get("bloodGroup"));
                        if (data.get("rhFactor") != null) serverRh = String.valueOf(data.get("rhFactor"));
                        if (data.get("region") != null) serverRegion = String.valueOf(data.get("region"));
                    }

                    if (TextUtils.isEmpty(serverFullName) || "null".equals(serverFullName)) {
                        String prefix = email.split("@")[0];
                        if (prefix.length() > 1) {
                            serverFullName = Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1) + " Донор";
                        } else {
                            serverFullName = prefix;
                        }
                    }

                    session.saveProfile(serverFullName, email, serverBlood, serverRh, serverRegion, true);
                    Toast.makeText(AuthActivity.this, "Добро пожаловать, " + serverFullName, Toast.LENGTH_SHORT).show();
                    proceedToMain();
                }

                @Override
                public void onError(String errorMessage) {
                    String prefix = email.split("@")[0];
                    String fallbackName = Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1) + " Донор";
                    session.saveProfile(fallbackName, email, bloodGroup, rh, selectedRegion, true);
                    Toast.makeText(AuthActivity.this, "Вход выполнен: " + fallbackName, Toast.LENGTH_SHORT).show();
                    proceedToMain();
                }
            });
        }
    }

    private void proceedToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
