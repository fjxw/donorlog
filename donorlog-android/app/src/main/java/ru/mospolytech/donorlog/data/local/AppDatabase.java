package ru.mospolytech.donorlog.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.mospolytech.donorlog.data.model.BloodStationEntity;
import ru.mospolytech.donorlog.data.model.DonationEntity;

@Database(entities = {DonationEntity.class, BloodStationEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "donorlog_db";
    private static volatile AppDatabase INSTANCE;

    public abstract DonationDao donationDao();
    public abstract BloodStationDao bloodStationDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
