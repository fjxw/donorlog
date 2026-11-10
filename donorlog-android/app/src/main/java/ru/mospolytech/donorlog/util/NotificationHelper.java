package ru.mospolytech.donorlog.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.ui.DietMemoActivity;
import ru.mospolytech.donorlog.ui.DonationEditorActivity;
import ru.mospolytech.donorlog.ui.MainActivity;

public final class NotificationHelper {

    public static final String CHANNEL_READINESS_ID = "channel_donation_readiness";
    public static final String CHANNEL_DIET_ID = "channel_donation_diet";

    public static final int NOTIFICATION_ID_READINESS = 1001;
    public static final int NOTIFICATION_ID_DIET = 1002;

    private NotificationHelper() {
    }

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                // Channel 1: Donation readiness
                NotificationChannel readinessChannel = new NotificationChannel(
                        CHANNEL_READINESS_ID,
                        context.getString(R.string.notif_channel_readiness),
                        NotificationManager.IMPORTANCE_HIGH
                );
                readinessChannel.setDescription("Уведомления о завершении восстановительного периода и готовности к донации");
                readinessChannel.enableVibration(true);
                manager.createNotificationChannel(readinessChannel);

                // Channel 2: Diet reminder
                NotificationChannel dietChannel = new NotificationChannel(
                        CHANNEL_DIET_ID,
                        context.getString(R.string.notif_channel_diet),
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                dietChannel.setDescription("Напоминания о правилах диеты за 48 и 24 часа до планируемой кроводачи");
                manager.createNotificationChannel(dietChannel);
            }
        }
    }

    public static void sendReadinessNotification(Context context) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent addDonationIntent = new Intent(context, DonationEditorActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                context,
                1,
                addDonationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_READINESS_ID)
                .setSmallIcon(R.drawable.ic_blood_drop)
                .setContentTitle(context.getString(R.string.notif_readiness_title))
                .setContentText(context.getString(R.string.notif_readiness_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_readiness_body)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_calendar, "Записаться", actionPendingIntent);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_READINESS, builder.build());
        } catch (SecurityException ignored) {
            // Handled when notification permission is denied
        }
    }

    public static void sendDietReminderNotification(Context context) {
        Intent dietIntent = new Intent(context, DietMemoActivity.class);
        dietIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                2,
                dietIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DIET_ID)
                .setSmallIcon(R.drawable.ic_restaurant)
                .setContentTitle(context.getString(R.string.notif_diet_title))
                .setContentText(context.getString(R.string.notif_diet_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notif_diet_body)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_restaurant, "Диета", contentPendingIntent);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DIET, builder.build());
        } catch (SecurityException ignored) {
            // Handled when notification permission is denied
        }
    }
}
