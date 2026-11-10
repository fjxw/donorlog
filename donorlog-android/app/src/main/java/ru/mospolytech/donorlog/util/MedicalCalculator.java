package ru.mospolytech.donorlog.util;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.mospolytech.donorlog.data.model.ComponentType;
import ru.mospolytech.donorlog.data.model.DonationEntity;

public final class MedicalCalculator {

    private MedicalCalculator() {
    }

    public static class RoadmapProgress {
        public final int schemeIndex;
        public final String schemeTitle;
        public final int completedCount;
        public final int targetCount;
        public final int percent;
        public final boolean isCompleted;

        public RoadmapProgress(int schemeIndex, String schemeTitle, int completedCount,
                               int targetCount, int percent, boolean isCompleted) {
            this.schemeIndex = schemeIndex;
            this.schemeTitle = schemeTitle;
            this.completedCount = completedCount;
            this.targetCount = targetCount;
            this.percent = percent;
            this.isCompleted = isCompleted;
        }
    }

    public static class IntervalStatus {
        public final boolean isReady;
        public final int daysRemaining;
        public final long nextEligibleDateMillis;
        public final String reason;

        public IntervalStatus(boolean isReady, int daysRemaining, long nextEligibleDateMillis, String reason) {
            this.isReady = isReady;
            this.daysRemaining = daysRemaining;
            this.nextEligibleDateMillis = nextEligibleDateMillis;
            this.reason = reason;
        }
    }

    public static IntervalStatus calculateIntervalStatus(DonationEntity lastDonation, List<DonationEntity> allDonations, boolean isMale) {
        if (lastDonation == null) {
            return new IntervalStatus(true, 0, System.currentTimeMillis(), "История донаций пуста. Допуск открыт.");
        }

        // Annual limit check (past 365 days)
        long oneYearAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365);
        int wholeBloodLastYear = 0;
        if (allDonations != null) {
            for (DonationEntity d : allDonations) {
                if (d.getDonationDate() >= oneYearAgo) {
                    ComponentType ct = ComponentType.fromString(d.getComponentType());
                    if (ct == ComponentType.WHOLE_BLOOD) {
                        wholeBloodLastYear++;
                    }
                }
            }
        }

        int maxWholeBloodPerYear = isMale ? 5 : 4;
        if (wholeBloodLastYear >= maxWholeBloodPerYear) {
            return new IntervalStatus(false, 30, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30),
                    "Превышен ежегодный лимит кроводач (максимум " + maxWholeBloodPerYear + " в год). Рекомендуется плазма или пауза.");
        }

        ComponentType lastType = ComponentType.fromString(lastDonation.getComponentType());
        int requiredIntervalDays = lastType.getIntervalDays();

        long nextEligibleMillis = lastDonation.getDonationDate() + TimeUnit.DAYS.toMillis(requiredIntervalDays);
        long now = System.currentTimeMillis();

        if (now >= nextEligibleMillis) {
            return new IntervalStatus(true, 0, nextEligibleMillis, "Интервал " + requiredIntervalDays + " дн. выдержан. Допуск открыт.");
        } else {
            long diffMillis = nextEligibleMillis - now;
            int daysRemaining = (int) Math.ceil((double) diffMillis / TimeUnit.DAYS.toMillis(1));
            return new IntervalStatus(false, daysRemaining, nextEligibleMillis,
                    "До окончания восстановительного периода осталось " + daysRemaining + " дн.");
        }
    }

    public static RoadmapProgress calculateBestRoadmap(int wholeBloodCount, int plasmaCount) {
        // Scheme 1: Whole blood >= 40
        int s1Completed = Math.min(wholeBloodCount, 40);
        int s1Percent = (s1Completed * 100) / 40;

        // Scheme 2: Plasma >= 60
        int s2Completed = Math.min(plasmaCount, 60);
        int s2Percent = (s2Completed * 100) / 60;

        // Scheme 3: Whole blood >= 25 + plasma -> total 40
        int s3Completed = 0;
        int s3Percent = 0;
        if (wholeBloodCount >= 25) {
            s3Completed = Math.min(wholeBloodCount + plasmaCount, 40);
            s3Percent = (s3Completed * 100) / 40;
        }

        // Scheme 4: Whole blood < 25 + plasma -> total 60
        int s4Completed = Math.min(wholeBloodCount + plasmaCount, 60);
        int s4Percent = (s4Completed * 100) / 60;

        // Determine best trajectory
        int bestIndex = 1;
        String bestTitle = "Схема 1: Кровь не менее 40 раз";
        int bestComp = s1Completed;
        int bestTarget = 40;
        int bestPct = s1Percent;

        if (s3Percent > bestPct) {
            bestIndex = 3;
            bestTitle = "Схема 3: Кровь от 25 раз + плазма (до 40)";
            bestComp = s3Completed;
            bestTarget = 40;
            bestPct = s3Percent;
        }

        if (s2Percent > bestPct) {
            bestIndex = 2;
            bestTitle = "Схема 2: Плазма не менее 60 раз";
            bestComp = s2Completed;
            bestTarget = 60;
            bestPct = s2Percent;
        }

        if (s4Percent > bestPct) {
            bestIndex = 4;
            bestTitle = "Схема 4: Кровь до 25 раз + плазма (до 60)";
            bestComp = s4Completed;
            bestTarget = 60;
            bestPct = s4Percent;
        }

        return new RoadmapProgress(bestIndex, bestTitle, bestComp, bestTarget, Math.min(100, bestPct), bestPct >= 100);
    }

    /**
     * Расчет траектории к званию «Почетный донор Москвы»
     * Закон г. Москвы № 70, Постановление Правительства Москвы № 1282-ПП:
     * Схема 1: Кровь не менее 20 раз
     * Схема 2: Плазма не менее 30 раз
     * Схема 3: Кровь от 13 раз + плазма (суммарно 20)
     * Схема 4: Кровь менее 13 раз + плазма (суммарно 30)
     */
    public static RoadmapProgress calculateMoscowRoadmap(int wholeBloodCount, int plasmaCount) {
        // Схема 1: Кровь >= 20
        int m1Completed = Math.min(wholeBloodCount, 20);
        int m1Percent = (m1Completed * 100) / 20;

        // Схема 2: Плазма >= 30
        int m2Completed = Math.min(plasmaCount, 30);
        int m2Percent = (m2Completed * 100) / 30;

        // Схема 3: Кровь >= 13 + плазма -> всего 20
        int m3Completed = 0;
        int m3Percent = 0;
        if (wholeBloodCount >= 13) {
            m3Completed = Math.min(wholeBloodCount + plasmaCount, 20);
            m3Percent = (m3Completed * 100) / 20;
        }

        // Схема 4: Кровь < 13 + плазма -> всего 30
        int m4Completed = Math.min(wholeBloodCount + plasmaCount, 30);
        int m4Percent = (m4Completed * 100) / 30;

        // Выбираем оптимальную траекторию
        int bestIndex = 1;
        String bestTitle = "Схема 1: Кровь не менее 20 раз";
        int bestComp = m1Completed;
        int bestTarget = 20;
        int bestPct = m1Percent;

        if (m3Percent > bestPct) {
            bestIndex = 3;
            bestTitle = "Схема 3: Кровь от 13 раз + плазма (до 20)";
            bestComp = m3Completed;
            bestTarget = 20;
            bestPct = m3Percent;
        }

        if (m2Percent > bestPct) {
            bestIndex = 2;
            bestTitle = "Схема 2: Плазма не менее 30 раз";
            bestComp = m2Completed;
            bestTarget = 30;
            bestPct = m2Percent;
        }

        if (m4Percent > bestPct) {
            bestIndex = 4;
            bestTitle = "Схема 4: Кровь до 13 раз + плазма (до 30)";
            bestComp = m4Completed;
            bestTarget = 30;
            bestPct = m4Percent;
        }

        return new RoadmapProgress(bestIndex, bestTitle, bestComp, bestTarget, Math.min(100, bestPct), bestPct >= 100);
    }
}
