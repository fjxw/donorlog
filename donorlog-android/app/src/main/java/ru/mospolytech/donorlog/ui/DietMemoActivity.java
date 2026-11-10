package ru.mospolytech.donorlog.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.util.NotificationHelper;

public class DietMemoActivity extends AppCompatActivity {

    private MaterialCheckBox cbCheckAlcohol;
    private MaterialCheckBox cbCheckAnalgesics;
    private MaterialCheckBox cbCheckFatty;
    private MaterialCheckBox cbCheckSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_memo);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbarDiet);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        MaterialButton btnSendDietNotification = findViewById(R.id.btnSendDietNotification);
        btnSendDietNotification.setOnClickListener(v -> {
            NotificationHelper.sendDietReminderNotification(DietMemoActivity.this);
            Toast.makeText(DietMemoActivity.this, "Памятка по диете отправлена в строку состояния", Toast.LENGTH_SHORT).show();
        });

        cbCheckAlcohol = findViewById(R.id.cbCheckAlcohol);
        cbCheckAnalgesics = findViewById(R.id.cbCheckAnalgesics);
        cbCheckFatty = findViewById(R.id.cbCheckFatty);
        cbCheckSleep = findViewById(R.id.cbCheckSleep);

        MaterialCheckBox.OnCheckedChangeListener listener = (buttonView, isChecked) -> verifyChecklist();
        cbCheckAlcohol.setOnCheckedChangeListener(listener);
        cbCheckAnalgesics.setOnCheckedChangeListener(listener);
        cbCheckFatty.setOnCheckedChangeListener(listener);
        cbCheckSleep.setOnCheckedChangeListener(listener);
    }

    private void verifyChecklist() {
        if (cbCheckAlcohol.isChecked() && cbCheckAnalgesics.isChecked() &&
                cbCheckFatty.isChecked() && cbCheckSleep.isChecked()) {
            Toast.makeText(this, "Все критерии подготовки к донации выполнены", Toast.LENGTH_SHORT).show();
        }
    }
}
