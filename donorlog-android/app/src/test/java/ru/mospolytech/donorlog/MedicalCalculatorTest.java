package ru.mospolytech.donorlog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.util.MedicalCalculator;

public class MedicalCalculatorTest {

    @Test
    public void testFirstDonationIsAlwaysEligible() {
        MedicalCalculator.IntervalStatus status = MedicalCalculator.calculateIntervalStatus(null, null, true);
        assertTrue("Донор без предшествующих кроводач должен быть допущен", status.isReady);
        assertEquals(0, status.daysRemaining);
    }

    @Test
    public void testWholeBloodIntervalLessThan60Days() {
        long now = System.currentTimeMillis();
        long fortyDaysAgo = now - TimeUnit.DAYS.toMillis(40);
        DonationEntity lastDonation = new DonationEntity("WHOLE_BLOOD", fortyDaysAgo, 450, "Центр крови", 140, 120, 80, 70, true, "");

        List<DonationEntity> history = new ArrayList<>();
        history.add(lastDonation);

        MedicalCalculator.IntervalStatus status = MedicalCalculator.calculateIntervalStatus(lastDonation, history, true);
        assertFalse("При интервале 40 дней кроводача не должна быть разрешена", status.isReady);
        assertEquals("Должно оставаться 20 дней", 20, status.daysRemaining);
    }

    @Test
    public void testWholeBloodInterval60DaysPassed() {
        long now = System.currentTimeMillis();
        long sixtyTwoDaysAgo = now - TimeUnit.DAYS.toMillis(62);
        DonationEntity lastDonation = new DonationEntity("WHOLE_BLOOD", sixtyTwoDaysAgo, 450, "Центр крови", 140, 120, 80, 70, true, "");

        List<DonationEntity> history = new ArrayList<>();
        history.add(lastDonation);

        MedicalCalculator.IntervalStatus status = MedicalCalculator.calculateIntervalStatus(lastDonation, history, true);
        assertTrue("После 62 дней интервал считается полностью выдержанным", status.isReady);
        assertEquals(0, status.daysRemaining);
    }

    @Test
    public void testPlasmaInterval14Days() {
        long now = System.currentTimeMillis();
        long tenDaysAgo = now - TimeUnit.DAYS.toMillis(10);
        DonationEntity lastDonation = new DonationEntity("PLASMA", tenDaysAgo, 600, "Центр крови", 140, 120, 80, 70, true, "");

        List<DonationEntity> history = new ArrayList<>();
        history.add(lastDonation);

        MedicalCalculator.IntervalStatus status = MedicalCalculator.calculateIntervalStatus(lastDonation, history, true);
        assertFalse("После 10 дней плазмафереза допуск закрыт", status.isReady);
        assertEquals(4, status.daysRemaining);

        long fifteenDaysAgo = now - TimeUnit.DAYS.toMillis(15);
        lastDonation.setDonationDate(fifteenDaysAgo);
        MedicalCalculator.IntervalStatus status2 = MedicalCalculator.calculateIntervalStatus(lastDonation, history, true);
        assertTrue("После 15 дней плазмафереза допуск открыт", status2.isReady);
    }

    @Test
    public void testAnnualLimitForMaleDonors() {
        long now = System.currentTimeMillis();
        List<DonationEntity> history = new ArrayList<>();

        // 5 whole blood donations in the last 300 days
        for (int i = 1; i <= 5; i++) {
            history.add(new DonationEntity("WHOLE_BLOOD", now - TimeUnit.DAYS.toMillis(65L * i), 450, "Центр крови", 140, 120, 80, 70, true, ""));
        }

        DonationEntity mostRecent = history.get(0); // 65 days ago, interval passed, but 5 donations in 365 days
        MedicalCalculator.IntervalStatus statusMale = MedicalCalculator.calculateIntervalStatus(mostRecent, history, true);
        assertFalse("Мужчинам разрешено не более 5 кроводач цельной крови в год", statusMale.isReady);
        assertTrue("Причина должна содержать превышение годового лимита", statusMale.reason.contains("лимит"));
    }

    @Test
    public void testAnnualLimitForFemaleDonors() {
        long now = System.currentTimeMillis();
        List<DonationEntity> history = new ArrayList<>();

        // 4 whole blood donations in the last 280 days
        for (int i = 1; i <= 4; i++) {
            history.add(new DonationEntity("WHOLE_BLOOD", now - TimeUnit.DAYS.toMillis(70L * i), 450, "Центр крови", 140, 120, 80, 70, true, ""));
        }

        DonationEntity mostRecent = history.get(0); // 70 days ago, interval passed, but 4 donations for female
        MedicalCalculator.IntervalStatus statusFemale = MedicalCalculator.calculateIntervalStatus(mostRecent, history, false);
        assertFalse("Женщинам разрешено не более 4 кроводач цельной крови в год", statusFemale.isReady);
    }

    @Test
    public void testHonoredDonorScheme1WholeBlood40() {
        MedicalCalculator.RoadmapProgress progress = MedicalCalculator.calculateBestRoadmap(40, 0);
        assertEquals("Схема 1: Кровь не менее 40 раз", progress.schemeTitle);
        assertEquals(40, progress.completedCount);
        assertEquals(40, progress.targetCount);
        assertEquals(100, progress.percent);
    }

    @Test
    public void testHonoredDonorScheme2Plasma60() {
        MedicalCalculator.RoadmapProgress progress = MedicalCalculator.calculateBestRoadmap(0, 60);
        assertEquals("Схема 2: Плазма не менее 60 раз", progress.schemeTitle);
        assertEquals(60, progress.completedCount);
        assertEquals(60, progress.targetCount);
        assertEquals(100, progress.percent);
    }

    @Test
    public void testHonoredDonorScheme3Combined40() {
        // 25 whole blood + 15 plasma = 40 (eligible under Scheme 3)
        MedicalCalculator.RoadmapProgress progress = MedicalCalculator.calculateBestRoadmap(25, 15);
        assertEquals("Схема 3: Кровь от 25 раз + плазма (до 40)", progress.schemeTitle);
        assertEquals(40, progress.completedCount);
        assertEquals(40, progress.targetCount);
        assertEquals(100, progress.percent);
    }

    @Test
    public void testHonoredDonorScheme4Combined60() {
        // 10 whole blood + 50 plasma = 60 (eligible under Scheme 4)
        MedicalCalculator.RoadmapProgress progress = MedicalCalculator.calculateBestRoadmap(10, 50);
        assertEquals("Схема 4: Кровь до 25 раз + плазма (до 60)", progress.schemeTitle);
        assertEquals(60, progress.completedCount);
        assertEquals(60, progress.targetCount);
        assertEquals(100, progress.percent);
    }

    @Test
    public void testHonoredDonorIntermediateProgress() {
        // 3 whole blood + 0 plasma
        MedicalCalculator.RoadmapProgress progress = MedicalCalculator.calculateBestRoadmap(3, 0);
        assertEquals(3, progress.completedCount);
        assertEquals(40, progress.targetCount);
        assertEquals(7, progress.percent); // 3 * 100 / 40 = 7%
    }

    @Test
    public void testHonoredDonorMoscowCriteria() {
        // Moscow Scheme 1: 20 whole blood
        MedicalCalculator.RoadmapProgress m1 = MedicalCalculator.calculateMoscowRoadmap(20, 0);
        assertEquals(20, m1.completedCount);
        assertEquals(20, m1.targetCount);
        assertEquals(100, m1.percent);
        assertTrue(m1.isCompleted);

        // Moscow Scheme 2: 30 plasma
        MedicalCalculator.RoadmapProgress m2 = MedicalCalculator.calculateMoscowRoadmap(0, 30);
        assertEquals(30, m2.completedCount);
        assertEquals(30, m2.targetCount);
        assertEquals(100, m2.percent);

        // Moscow Scheme 3: 13 blood + 7 plasma = 20 total
        MedicalCalculator.RoadmapProgress m3 = MedicalCalculator.calculateMoscowRoadmap(13, 7);
        assertEquals(20, m3.completedCount);
        assertEquals(20, m3.targetCount);
        assertEquals(100, m3.percent);

        // Moscow Scheme 4: 5 blood + 25 plasma = 30 total
        MedicalCalculator.RoadmapProgress m4 = MedicalCalculator.calculateMoscowRoadmap(5, 25);
        assertEquals(30, m4.completedCount);
        assertEquals(30, m4.targetCount);
        assertEquals(100, m4.percent);

        // Moscow partial: 3 blood
        MedicalCalculator.RoadmapProgress mPartial = MedicalCalculator.calculateMoscowRoadmap(3, 0);
        assertEquals(3, mPartial.completedCount);
        assertEquals(20, mPartial.targetCount);
        assertEquals(15, mPartial.percent); // 3 * 100 / 20 = 15%
    }
}
