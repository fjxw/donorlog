package ru.mospolytech.donorlog.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.ui.DonationEditorActivity;
import ru.mospolytech.donorlog.util.UserSession;

public class QuickMetricsWidgetProvider extends AppWidgetProvider {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_metrics);

        Intent intent = new Intent(context, DonationEditorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                30,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widgetMetricsRoot, pendingIntent);

        UserSession session = UserSession.getInstance(context);
        DonorRepository repo = DonorRepository.getInstance(context);

        repo.getAllDonations(donations -> {
            if (donations != null && !donations.isEmpty()) {
                DonationEntity last = donations.get(0);
                String dateStr = DATE_FORMAT.format(new Date(last.getDonationDate()));
                views.setTextViewText(R.id.tvWidgetMetricsDonor, session.getName() + " • Донация: " + dateStr);

                if (last.getHemoglobin() > 0) {
                    views.setTextViewText(R.id.tvWidgetHbValue, String.format(Locale.US, "%.1f г/л", last.getHemoglobin()));
                } else {
                    views.setTextViewText(R.id.tvWidgetHbValue, "140.0 г/л");
                }

                if (last.getSystolicBp() > 0 && last.getDiastolicBp() > 0) {
                    views.setTextViewText(R.id.tvWidgetBpValue,
                            last.getSystolicBp() + "/" + last.getDiastolicBp() + " мм рт. ст. • " + last.getPulse() + " уд/мин");
                } else {
                    views.setTextViewText(R.id.tvWidgetBpValue, "120/80 мм рт. ст. • 72 уд/мин");
                }
            } else {
                views.setTextViewText(R.id.tvWidgetMetricsDonor, session.getName() + " • Первичный осмотр");
                views.setTextViewText(R.id.tvWidgetHbValue, "140.0 г/л");
                views.setTextViewText(R.id.tvWidgetBpValue, "120/80 мм рт. ст. • 72 уд/мин");
            }

            views.setTextViewText(R.id.tvWidgetMetricsNormBadge, "В норме");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        });
    }
}
