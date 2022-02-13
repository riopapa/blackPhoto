package com.urrecliner.blackphoto;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity (primaryKeys = {"fullFolder","photoName"})
public class SnapImage {

    @NonNull
    @ColumnInfo (name = "fullFolder")   // V22-02-13 08.59.47
    public String fullFolder;

    @NonNull
    @ColumnInfo (name = "photoName")    // D22-02-13 08.59.S456.jpg
    public String photoName;

    public boolean isChecked;
    @ColumnInfo (name = "sumNailMap")
    public String sumNailMap;

    public SnapImage(@NonNull String fullFolder, @NonNull String photoName, String sumNailMap) {
        this.fullFolder = fullFolder;
        this.photoName = photoName;
        this.isChecked = false;
        this.sumNailMap = sumNailMap;
    }

}