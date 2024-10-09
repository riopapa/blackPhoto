package com.urrecliner.blackphoto;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity (primaryKeys = {"fullFolder","photoName"})
public class SnapEntity {

    @NonNull
    @ColumnInfo (name = "fullFolder")   // V22-02-13 08.59.47
    public String fullFolder;

    @NonNull
    @ColumnInfo (name = "photoName")    // D22-02-13 08.59.S456.jpg     "h" if header
    public String photoName;

    @ColumnInfo (name = "sumNailMap")
    public String sumNailMap;

    public boolean isChecked, isSent;

    public SnapEntity(@NonNull String fullFolder, @NonNull String photoName, String sumNailMap) {
        this.fullFolder = fullFolder;
        this.photoName = photoName;
        this.isChecked = false;
        this.isSent = false;
        this.sumNailMap = sumNailMap;
    }

}