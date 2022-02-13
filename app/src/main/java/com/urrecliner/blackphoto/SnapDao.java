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
    @Query("SELECT * FROM snapImage WHERE fullFolder LIKE :fullFolder AND "
            + "photoName LIKE :photoName LIMIT 1 ")
    SnapImage getByPhotoName(String fullFolder, String photoName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SnapImage snapImage);

    /* query by unique full path name */
    @Query("SELECT * FROM snapImage GROUP BY fullFolder ")
    List<SnapImage> getFolderList();

    /* query within one folder */
    @Query("SELECT * FROM snapImage WHERE fullFolder LIKE :fullFolder ")
    List<SnapImage> getWithinFolder(String fullFolder);

    @Delete
    void delete(SnapImage snapImage);

}