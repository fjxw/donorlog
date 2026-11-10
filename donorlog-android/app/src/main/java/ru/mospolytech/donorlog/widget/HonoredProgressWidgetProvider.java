package ru.mospolytech.donorlog.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.RemoteViews;

import ru.mospolytech.donorlog.R;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.repository.DonorRepository;
import ru.mospolytech.donorlog.ui.HonoredDonorActivity;
import ru.mospolytech.donorlog.util.MedicalCalculator;
import ru.mospolytech.donorlog.util.UserSession;

public class HonoredProgressWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_honored_progress);

        Intent intent = new Intent(context, HonoredDonorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                20,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widgetHonoredRoot, pendingIntent);

        UserSession session = UserSession.getInstance(context);
        DonorRepository repo = DonorRepository.getInstance(context);

        repo.getAllDonations(donations -> {
            int wholeCount = 0;
            int plasmaCount = 0;

            if (donations != null) {
                for (DonationEntity d : donations) {
                    if ("WHOLE_BLOOD".equalsIgnoreCase(d.getComponentType())) wholeCount++;
                    else if ("PLASMA".equalsIgnoreCase(d.getComponentType())) plasmaCount++;
                }
            }

            MedicalCalculator.RoadmapProgress moscow = MedicalCalculator.calculateMoscowRoadmap(wholeCount, plasmaCount);
            MedicalCalculator.RoadmapProgress russia = MedicalCalculator.calculateBestRoadmap(wholeCount, plasmaCount);

            int mainPercent = session.isMoscowRegion() ? moscow.percent : russia.percent;

            // Generate circular diagram bitmap
            Bitmap gaugeBitmap = renderCircularGauge(context, mainPercent, 74);
            views.setImageViewBitmap(R.id.ivCircularHonoredWidget, gaugeBitmap);

            // Set tier badge
            String tierTitle;
            if (wholeCount >= 40 || (wholeCount >= 20 && session.isMoscowRegion())) {
                tierTitle = "Почетный донор";
            } else if (wholeCount >= 10) {
                tierTitle = "Марафонец";
            } else if (wholeCount >= 5) {
                tierTitle = "Спасатель жизней";
            } else if (wholeCount >= 2) {
                tierTitle = "Регулярный донор";
            } else if (wholeCount >= 1) {
                tierTitle = "Первый шаг";
            } else {
                tierTitle = "Кандидат";
            }
            views.setTextViewText(R.id.tvWidgetTierBadge, tierTitle);

            views.setTextViewText(R.id.tvWidgetHonoredDetail1,
                    "г. Москва: " + moscow.completedCount + " из " + moscow.targetCount + " (осталось " + (moscow.targetCount - moscow.completedCount) + ")");
            views.setTextViewText(R.id.tvWidgetHonoredDetail2,
                    "Россия (125-ФЗ): " + russia.completedCount + " из " + russia.targetCount + " (осталось " + (russia.targetCount - russia.completedCount) + ")");
            views.setTextViewText(R.id.tvWidgetHonoredScheme, "Рекомендуемый путь: " + russia.schemeTitle);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        });
    }

    private static Bitmap renderCircularGauge(Context context, int percent, int sizeDp) {
        float density = context.getResources().getDisplayMetrics().density;
        int px = Math.max(1, (int) (sizeDp * density));
        Bitmap bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        float strokeWidth = 8f * density;
        float pad = strokeWidth / 2f + 2f * density;
        RectF oval = new RectF(pad, pad, px - pad, px - pad);

        // Background track circle
        Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeWidth);
        trackPaint.setColor(Color.parseColor("#F4DDDD"));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(oval, 0f, 360f, false, trackPaint);

        // Progress arc
        Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(Color.parseColor("#B3261E"));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        float sweep = (Math.min(100, Math.max(0, percent)) / 100f) * 360f;
        if (sweep > 0) {
            canvas.drawArc(oval, -90f, sweep, false, progressPaint);
        }

        // Percentage text inside
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#B3261E"));
        textPaint.setTextSize(15f * density);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        String text = percent + "%";
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        float y = (px / 2f) - textBounds.exactCenterY();
        canvas.drawText(text, px / 2f, y, textPaint);

        return bitmap;
    }
}
