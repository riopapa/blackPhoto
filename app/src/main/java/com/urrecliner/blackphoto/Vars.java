package com.urrecliner.blackphoto;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.util.List;

public class Vars {
    static Context mContext;
    static Activity mActivity;

    static SnapImageAdaptor snapImageAdaptor;
    static RecyclerView eventFolderView;
    static File jpgFullFolder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/EventJpg");
    static File selectedJpgFolder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/event/selectedJpg");
    static File eventMP4Folder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/event");
    static File logFullFolder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/log");
    static Utils utils;

    static File currEventFolder = null;
    static int nowPos;
    static int spanWidth;

    static List<File> eventFolders = null;
    static List<Bitmap> eventBitmaps = null;
    static EventFolderAdapter eventFolderAdapter;
    final static int SPAN_COUNT = 1;

    static SnapDataBase snapDB;
    static SnapDao snapDao;
    static List<SnapImage> snapImages = null;
    static BuildDB buildDB;


}