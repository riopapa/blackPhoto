package com.urrecliner.blackphoto;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Vars {
    static Context mContext;
    static Activity mActivity;

    static PhotosAdapter photosAdapter;
    static RecyclerView eventFolderView;
    static File jpgFullFolder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/EventJpg");
    static File eventFullFolder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/event/eventJpg");
    static File logFullFolder =  new File(Environment.getExternalStorageDirectory(),
            "BlackBox/log");
    static Utils utils;

    static File currEventFolder = null;
    static int nowPos;
    static int spanWidth;

    static ArrayList<Photo> photos = null;
    static List<File> eventFolders = null;
    static EventFolderAdapter eventFolderAdapter;
    final static int SPAN_COUNT = 2;
}

