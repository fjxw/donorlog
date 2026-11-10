package ru.mospolytech.donorlog;

import android.app.Application;

import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.util.NotificationHelper;

public class DonorLogApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannels(this);
        DonorRepository.getInstance(this);
    }
}
