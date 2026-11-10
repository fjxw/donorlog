package ru.mospolytech.donorlog.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.mospolytech.donorlog.data.local.AppDatabase;
import ru.mospolytech.donorlog.data.local.BloodStationDao;
import ru.mospolytech.donorlog.data.local.DonationDao;
import ru.mospolytech.donorlog.data.model.BloodStationEntity;
import ru.mospolytech.donorlog.data.model.DonationEntity;
import ru.mospolytech.donorlog.data.remote.DonorApiClient;

public class DonorRepository {

    private static volatile DonorRepository INSTANCE;

    private final DonationDao donationDao;
    private final BloodStationDao bloodStationDao;
    private final DonorApiClient apiClient;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public interface RepoCallback<T> {
        void onComplete(T result);
    }

    private DonorRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.donationDao = db.donationDao();
        this.bloodStationDao = db.bloodStationDao();
        this.apiClient = new DonorApiClient();
        this.executor = Executors.newFixedThreadPool(3);
        this.mainHandler = new Handler(Looper.getMainLooper());

        seedInitialDataIfEmpty();
    }

    public static DonorRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (DonorRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DonorRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public void getAllDonations(RepoCallback<List<DonationEntity>> callback) {
        executor.execute(() -> {
            List<DonationEntity> list = donationDao.getAllDonations();
            mainHandler.post(() -> callback.onComplete(list));
        });
    }

    public void getDonationById(long id, RepoCallback<DonationEntity> callback) {
        executor.execute(() -> {
            DonationEntity item = donationDao.getById(id);
            mainHandler.post(() -> callback.onComplete(item));
        });
    }

    public void getLastDonation(RepoCallback<DonationEntity> callback) {
        executor.execute(() -> {
            DonationEntity item = donationDao.getLastDonation();
            mainHandler.post(() -> callback.onComplete(item));
        });
    }

    public void insertDonation(DonationEntity donation, RepoCallback<Long> callback) {
        executor.execute(() -> {
            long id = donationDao.insert(donation);
            mainHandler.post(() -> {
                if (callback != null) callback.onComplete(id);
            });
        });
    }

    public void updateDonation(DonationEntity donation, RepoCallback<Void> callback) {
        executor.execute(() -> {
            donationDao.update(donation);
            mainHandler.post(() -> {
                if (callback != null) callback.onComplete(null);
            });
        });
    }

    public void deleteDonation(long id, RepoCallback<Void> callback) {
        executor.execute(() -> {
            donationDao.deleteById(id);
            mainHandler.post(() -> {
                if (callback != null) callback.onComplete(null);
            });
        });
    }

    public void getAllStations(RepoCallback<List<BloodStationEntity>> callback) {
        executor.execute(() -> {
            List<BloodStationEntity> list = bloodStationDao.getAllStations();
            mainHandler.post(() -> callback.onComplete(list));
        });
    }

    public void syncWithBackend(RepoCallback<String> callback) {
        executor.execute(() -> {
            List<DonationEntity> localDonations = donationDao.getAllDonations();
            apiClient.syncDonations(localDonations, new DonorApiClient.ApiCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    // Refresh stations after sync
                    apiClient.fetchBloodStations(new DonorApiClient.ApiCallback<List<BloodStationEntity>>() {
                        @Override
                        public void onSuccess(List<BloodStationEntity> stations) {
                            executor.execute(() -> {
                                if (stations != null && !stations.isEmpty()) {
                                    bloodStationDao.deleteAll();
                                    bloodStationDao.insertAll(stations);
                                }
                                mainHandler.post(() -> callback.onComplete("Синхронизировано записей: " + result));
                            });
                        }

                        @Override
                        public void onError(String message) {
                            mainHandler.post(() -> callback.onComplete("Синхронизировано донаций: " + result + ", станции не обновлены"));
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    mainHandler.post(() -> callback.onComplete("Ошибка соединения: " + message));
                }
            });
        });
    }

    private void seedInitialDataIfEmpty() {
        executor.execute(() -> {
            if (donationDao.getTotalCount() == 0) {
                long now = System.currentTimeMillis();
                long sixtyFiveDaysAgo = now - TimeUnit.DAYS.toMillis(65);
                long oneHundredThirtyDaysAgo = now - TimeUnit.DAYS.toMillis(130);
                long twoHundredDaysAgo = now - TimeUnit.DAYS.toMillis(200);

                List<DonationEntity> seedDonations = new ArrayList<>();
                seedDonations.add(new DonationEntity(
                        "WHOLE_BLOOD",
                        sixtyFiveDaysAgo,
                        450,
                        "Центр крови им. О.К. Гаврилова (Поликарпова)",
                        142.0,
                        120,
                        80,
                        72,
                        true,
                        "Плановая безвозмездная донация цельной крови. Самочувствие хорошее."
                ));
                seedDonations.add(new DonationEntity(
                        "WHOLE_BLOOD",
                        oneHundredThirtyDaysAgo,
                        450,
                        "Центр крови им. О.К. Гаврилова (Царицыно)",
                        138.0,
                        118,
                        78,
                        70,
                        true,
                        "Донация цельной крови. Допуск терапевтом без замечаний."
                ));
                seedDonations.add(new DonationEntity(
                        "PLASMA",
                        twoHundredDaysAgo,
                        600,
                        "НМИЦ гематологии Минздрава России",
                        140.0,
                        122,
                        82,
                        74,
                        true,
                        "Плазмаферез аппаратный. Процедура прошла штатно."
                ));
                donationDao.insertAll(seedDonations);
            }

            if (bloodStationDao.getCount() == 0) {
                List<BloodStationEntity> seedStations = new ArrayList<>();
                seedStations.add(new BloodStationEntity(
                        "Центр крови имени О.К. Гаврилова (Поликарпова)",
                        "Москва",
                        "ул. Поликарпова, д. 14, корп. 2",
                        "+7 (495) 945-71-66",
                        "Пн-Вс: 08:00 - 13:30",
                        "YELLOW",
                        "GREEN", "RED", "YELLOW", "RED", "GREEN", "YELLOW", "GREEN", "GREEN"
                ));
                seedStations.add(new BloodStationEntity(
                        "Центр крови имени О.К. Гаврилова (Царицыно)",
                        "Москва",
                        "ул. Бакинская, д. 31",
                        "+7 (495) 326-98-36",
                        "Пн-Вс: 08:00 - 13:30",
                        "GREEN",
                        "GREEN", "YELLOW", "GREEN", "YELLOW", "GREEN", "GREEN", "GREEN", "GREEN"
                ));
                seedStations.add(new BloodStationEntity(
                        "НМИЦ гематологии Минздрава России",
                        "Москва",
                        "Новый Зыковский проезд, д. 4",
                        "+7 (495) 612-35-33",
                        "Пн-Пт: 09:00 - 14:00",
                        "RED",
                        "YELLOW", "RED", "RED", "RED", "YELLOW", "RED", "GREEN", "RED"
                ));
                seedStations.add(new BloodStationEntity(
                        "Центр крови ФМБА России",
                        "Москва",
                        "ул. Щукинская, д. 6, корп. 2",
                        "+7 (499) 190-60-25",
                        "Пн-Пт: 08:30 - 12:30",
                        "YELLOW",
                        "GREEN", "RED", "GREEN", "YELLOW", "RED", "YELLOW", "GREEN", "GREEN"
                ));
                seedStations.add(new BloodStationEntity(
                        "Городская станция переливания крови (СПб)",
                        "Санкт-Петербург",
                        "Московский пр-т, д. 104",
                        "+7 (812) 635-68-85",
                        "Пн-Пт: 09:00 - 13:00",
                        "GREEN",
                        "GREEN", "GREEN", "YELLOW", "YELLOW", "GREEN", "GREEN", "GREEN", "GREEN"
                ));
                bloodStationDao.insertAll(seedStations);
            }
        });
    }
}
