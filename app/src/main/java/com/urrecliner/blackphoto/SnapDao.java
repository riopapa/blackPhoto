package com.urrecliner.blackphoto;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SnapDao {

    /* query by unique full path name */
    @Query("SELECT * FROM SnapEntity WHERE fullFolder LIKE :fullFolder AND "
            + "photoName LIKE :photoName LIMIT 1 ")
    SnapEntity getByPhotoName(String fullFolder, String photoName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SnapEntity snapEntity);

    /* query by unique full path name */
    @Query("SELECT * FROM SnapEntity GROUP BY fullFolder ")
    List<SnapEntity> getFolderList();

    /* query within one folder */
    @Query("SELECT * FROM SnapEntity WHERE fullFolder LIKE :fullFolder ")
    List<SnapEntity> getWithinFolder(String fullFolder);

    @Delete
    void delete(SnapEntity snapEntity);

    @Query("DELETE FROM SnapEntity WHERE fullFolder LIKE :fullFolder ")
    void deleteFolder(String fullFolder);

}