package com.urrecliner.blackphoto;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class Vars {
    static Context mContext;
    static Activity mActivity;

    static PhotosAdapter photosAdapter;
    static RecyclerView eventFolderView;
    static String jpgFolder = "BlackBox/EventJpg";
    static String eventFolder = "BlackBox/event/eventJpg";
    static File jpgFullFolder =  new File(Environment.getExternalStorageDirectory(), jpgFolder);
    static File eventFullFolder =  new File(Environment.getExternalStorageDirectory(), eventFolder);
    static Utils utils;

    static File currEventFolder = null;
    static int nowPos;
    static int spanWidth, sizeX;

    static boolean multiMode = false;
    static ArrayList<Photo> photos = null;
    static ArrayList<File> eventFolders = null;
}

