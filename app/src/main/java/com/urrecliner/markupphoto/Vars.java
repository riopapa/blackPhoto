package com.urrecliner.markupphoto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

class Vars {
    static Context mContext;
    static Activity mainActivity;
    static Activity dirActivity;
    static PhotoAdapter photoAdapter;
    static DirectoryAdapter directoryAdapter;
    static Bitmap signatureMap;
    static RecyclerView photoView;

    static Utils utils;
    static SqueezeDB squeezeDB;
    static BuildDB buildDB;
    static MakeDirFolder makeDirFolder;
    static MarkUpOnePhoto markUpOnePhoto;
    static BuildBitMap buildBitMap;
    static ColorDraw colorDraw;

    static String shortFolder = null;
    static String longFolder = null;
    static int nowPos;
    static String nowPlace,nowPosition, nowComment;
    static int spanCount, spanWidth, sizeX;
    static String copyPasteText = "\n\n";
    static String copyPasteGPS;
    static boolean dirNotReady = true;

    static boolean multiMode = false;
    static List<Photo> photos = null;
    static ArrayList<DirectoryFolder> dirFolders = null;

    static SharedPreferences sharePrefer;
    static Menu mainMenu;

    static DatabaseIO databaseIO = null;
    static final String SUFFIX_JPG = "_ha.jpg";

    static ImageView colorPlate;
    static ImageView colorRange;
    static ImageView colorSpot;
    static SeekBar colorAlpha;
    static int colorRGB;
    static int markTextInColor;
    static int markTextOutColor;

}

