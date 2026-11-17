package ru.mospolytech.donorlog;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.util.NotificationHelper;

public class DonorLogApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        NotificationHelper.createNotificationChannels(this);
        DonorRepository.getInstance(this);
    }
}
