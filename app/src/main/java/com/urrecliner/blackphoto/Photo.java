package com.urrecliner.blackphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

class Photo {
    public File fullFileName;
    public String shortName;
    public boolean checked;
    public Bitmap bitMap;

    Photo(File fullFileName) {
        this.fullFileName = fullFileName;
        this.shortName = fullFileName.getName();
        this.checked = false;
        this.bitMap = null;
    }
}
