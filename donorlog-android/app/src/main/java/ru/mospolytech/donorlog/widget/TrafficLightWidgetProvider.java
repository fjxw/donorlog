package ru.mospolytech.donorlog.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.BloodStationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.ui.TrafficLightActivity;
import ru.mospolytech.donorlog.util.UserSession;

public class TrafficLightWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_traffic_light);

        Intent intent = new Intent(context, TrafficLightActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                10,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widgetTrafficRoot, pendingIntent);

        UserSession session = UserSession.getInstance(context);
        views.setTextViewText(R.id.tvWidgetBloodBadge, session.getFullBloodType());

        DonorRepository repo = DonorRepository.getInstance(context);
        repo.getAllStations(stations -> {
            if (stations != null && !stations.isEmpty()) {
                if (stations.size() > 0) {
                    BloodStationEntity s1 = stations.get(0);
                    views.setTextViewText(R.id.tvWidgetStation1Name, s1.getName());
                    bindStatus(views, R.id.tvWidgetStation1Status, s1.getAPosStatus(), context);
                }
                if (stations.size() > 1) {
                    BloodStationEntity s2 = stations.get(1);
                    views.setTextViewText(R.id.tvWidgetStation2Name, s2.getName());
                    bindStatus(views, R.id.tvWidgetStation2Status, s2.getAPosStatus(), context);
                }
                if (stations.size() > 2) {
                    BloodStationEntity s3 = stations.get(2);
                    views.setTextViewText(R.id.tvWidgetStation3Name, s3.getName());
                    bindStatus(views, R.id.tvWidgetStation3Status, s3.getAPosStatus(), context);
                }
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        });
    }

    private static void bindStatus(RemoteViews views, int viewId, String status, Context context) {
        if ("RED".equalsIgnoreCase(status)) {
            views.setTextViewText(viewId, "Критическая");
            views.setTextColor(viewId, ContextCompat.getColor(context, R.color.status_red));
            views.setInt(viewId, "setBackgroundResource", R.drawable.bg_badge_red);
        } else if ("YELLOW".equalsIgnoreCase(status)) {
            views.setTextViewText(viewId, "Повышенная");
            views.setTextColor(viewId, ContextCompat.getColor(context, R.color.status_yellow));
            views.setInt(viewId, "setBackgroundResource", R.drawable.bg_badge_yellow);
        } else {
            views.setTextViewText(viewId, "Достаточно");
            views.setTextColor(viewId, ContextCompat.getColor(context, R.color.status_green));
            views.setInt(viewId, "setBackgroundResource", R.drawable.bg_badge_green);
        }
    }
}
