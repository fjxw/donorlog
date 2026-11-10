package ru.mospolytech.donorlog.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.mospolytech.donorlog.data.model.DonationEntity;

@Dao
public interface DonationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DonationEntity donation);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DonationEntity> donations);

    @Update
    void update(DonationEntity donation);

    @Delete
    void delete(DonationEntity donation);

    @Query("DELETE FROM donations WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM donations ORDER BY donationDate DESC")
    List<DonationEntity> getAllDonations();

    @Query("SELECT * FROM donations WHERE id = :id LIMIT 1")
    DonationEntity getById(long id);

    @Query("SELECT * FROM donations ORDER BY donationDate DESC LIMIT 1")
    DonationEntity getLastDonation();

    @Query("SELECT COUNT(*) FROM donations")
    int getTotalCount();

    @Query("SELECT COUNT(*) FROM donations WHERE componentType = 'WHOLE_BLOOD'")
    int getWholeBloodCount();

    @Query("SELECT COUNT(*) FROM donations WHERE componentType = 'PLASMA'")
    int getPlasmaCount();

    @Query("SELECT COUNT(*) FROM donations WHERE componentType = 'PLATELETS'")
    int getPlateletsCount();

    @Query("SELECT * FROM donations WHERE donationDate >= :sinceMillis ORDER BY donationDate DESC")
    List<DonationEntity> getDonationsSince(long sinceMillis);

    @Query("SELECT * FROM donations WHERE isSynced = 0")
    List<DonationEntity> getUnsyncedDonations();
}
