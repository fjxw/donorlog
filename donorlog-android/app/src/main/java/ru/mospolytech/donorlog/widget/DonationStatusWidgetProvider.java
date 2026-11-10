package ru.mospolytech.donorlog.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.ui.MainActivity;
import ru.mospolytech.donorlog.util.MedicalCalculator;
import ru.mospolytech.donorlog.util.UserSession;

public class DonationStatusWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_donation_status);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent);

        UserSession session = UserSession.getInstance(context);
        views.setTextViewText(R.id.tvWidgetDonorInfo, session.getName() + " • " + session.getFullBloodType() + " • " + (session.isMoscowRegion() ? "г. Москва" : "РФ"));

        DonorRepository repo = DonorRepository.getInstance(context);
        repo.getAllDonations(donations -> {
            int count = (donations != null) ? donations.size() : 0;
            int totalMl = 0;
            int wholeCount = 0;
            int plasmaCount = 0;

            if (donations != null) {
                for (DonationEntity d : donations) {
                    totalMl += d.getVolumeMl();
                    if ("WHOLE_BLOOD".equalsIgnoreCase(d.getComponentType())) wholeCount++;
                    else if ("PLASMA".equalsIgnoreCase(d.getComponentType())) plasmaCount++;
                }
            }

            int livesSaved = wholeCount * 3 + plasmaCount;
            if (livesSaved == 0 && count > 0) livesSaved = count * 2;

            views.setTextViewText(R.id.tvWidgetStatDonations, count + " донации");
            views.setTextViewText(R.id.tvWidgetStatVolume, String.format(java.util.Locale.US, "%.1f л сдано", totalMl / 1000.0));
            views.setTextViewText(R.id.tvWidgetStatLives, livesSaved + " жизней");

            if (donations != null && !donations.isEmpty()) {
                DonationEntity last = donations.get(0);
                MedicalCalculator.IntervalStatus status = MedicalCalculator.calculateIntervalStatus(last, donations, true);

                if (status.isReady) {
                    views.setTextViewText(R.id.tvWidgetStatusBadge, "Допущен к сдаче");
                    views.setTextColor(R.id.tvWidgetStatusBadge, ContextCompat.getColor(context, R.color.status_green));
                    views.setTextViewText(R.id.tvWidgetIntervalText, "Интервал 60 дней выдержан • Допуск открыт");
                    views.setTextColor(R.id.tvWidgetIntervalText, ContextCompat.getColor(context, R.color.status_green));
                } else {
                    views.setTextViewText(R.id.tvWidgetStatusBadge, status.daysRemaining + " дн.");
                    views.setTextColor(R.id.tvWidgetStatusBadge, ContextCompat.getColor(context, R.color.status_yellow));
                    views.setTextViewText(R.id.tvWidgetIntervalText, "До допуска осталось: " + status.daysRemaining + " дн.");
                    views.setTextColor(R.id.tvWidgetIntervalText, ContextCompat.getColor(context, R.color.status_yellow));
                }

                if (session.isMoscowRegion()) {
                    MedicalCalculator.RoadmapProgress moscowRoad = MedicalCalculator.calculateMoscowRoadmap(wholeCount, plasmaCount);
                    views.setTextViewText(R.id.tvWidgetHonored, "Почетный донор Москвы: " + moscowRoad.completedCount + " / " + moscowRoad.targetCount + " (" + moscowRoad.percent + "%)");
                    views.setProgressBar(R.id.pbWidgetHonored, 100, moscowRoad.percent, false);
                } else {
                    MedicalCalculator.RoadmapProgress road = MedicalCalculator.calculateBestRoadmap(wholeCount, plasmaCount);
                    views.setTextViewText(R.id.tvWidgetHonored, "Почетный донор РФ: " + road.completedCount + " / " + road.targetCount + " (" + road.percent + "%)");
                    views.setProgressBar(R.id.pbWidgetHonored, 100, road.percent, false);
                }
            } else {
                views.setTextViewText(R.id.tvWidgetStatusBadge, "Допущен к сдаче");
                views.setTextColor(R.id.tvWidgetStatusBadge, ContextCompat.getColor(context, R.color.status_green));
                views.setTextViewText(R.id.tvWidgetIntervalText, "История чиста • Допуск к сдаче открыт");
                views.setTextColor(R.id.tvWidgetIntervalText, ContextCompat.getColor(context, R.color.status_green));
                views.setTextViewText(R.id.tvWidgetHonored, "Почетный донор: 0 / 40 (0%)");
                views.setProgressBar(R.id.pbWidgetHonored, 100, 0, false);
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        });
    }
}
