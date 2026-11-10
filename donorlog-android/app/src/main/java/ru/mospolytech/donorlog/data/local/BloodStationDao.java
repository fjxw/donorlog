package ru.mospolytech.donorlog.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ru.mospolytech.donorlog.data.model.BloodStationEntity;

@Dao
public interface BloodStationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BloodStationEntity> stations);

    @Query("SELECT * FROM stations ORDER BY name ASC")
    List<BloodStationEntity> getAllStations();

    @Query("DELETE FROM stations")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM stations")
    int getCount();
}
