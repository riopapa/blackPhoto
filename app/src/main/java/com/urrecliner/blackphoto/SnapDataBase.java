package com.urrecliner.blackphoto;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SnapImage.class}, version = 1)
public abstract class SnapDataBase extends RoomDatabase {

	public abstract SnapDao snapDao();

}