package com.urrecliner.markupphoto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.exifinterface.media.ExifInterface;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.urrecliner.markupphoto.Vars.copyPasteGPS;
import static com.urrecliner.markupphoto.Vars.longFolder;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.mainActivity;
import static com.urrecliner.markupphoto.Vars.shortFolder;
import static com.urrecliner.markupphoto.Vars.utils;

class Utils {

    final private String PREFIX = "log_";

    void log(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = traceName(traces[6].getMethodName()) + traceName(traces[5].getMethodName()) + traceName(traces[4].getMethodName()) + traceClassName(traces[3].getClassName())+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.w(tag , log);
        append2file(sdfDateTimeLog.format(new Date())+" " +log);
    }

    private String traceName (String s) {
        if (s.equals("performResume") || s.equals("performCreate") || s.equals("callActivityOnResume") || s.equals("access$1200")
                || s.equals("access$000") || s.equals("handleReceiver") || s.equals("handleMessage") || s.equals("dispatchMessage"))
            return "";
        else
            return s + "> ";
    }

    private final SimpleDateFormat sdfDateTimeLog = new SimpleDateFormat("MM-dd HH.mm.ss sss", Locale.US);
    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("yy-MM-dd", Locale.US);

    private String traceClassName(String s) {
        return s.substring(s.lastIndexOf(".")+1);
    }

    void logE(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = traceName(traces[5].getMethodName()) + traceName(traces[4].getMethodName()) + traceClassName(traces[3].getClassName())+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.e("<" + tag + ">" , log);
        append2file(sdfDateTimeLog.format(new Date())+" : " +log);
    }

    private void append2file(String textLine) {

        File directory = getPackageDirectory();
        BufferedWriter bw = null;
        FileWriter fw = null;
        String fullName = directory.toString() + "/" + PREFIX + sdfDate.format(new Date())+".txt";
        try {
            File file = new File(fullName);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    logE("createFile", " Error");
                }
            }
            String outText = "\n"+textLine+"\n";
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(outText);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) bw.close();
                if (fw != null) fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    File getPackageDirectory() {
        File directory = new File(Environment.getExternalStorageDirectory(), getAppLabel(mContext));
        try {
            if (!directory.exists()) {
                if(directory.mkdirs()) {
                    Log.e("mkdirs","Failed "+directory);
                }
            }
        } catch (Exception e) {
            Log.e("creating Directory error", directory.toString() + "_" + e.toString());
        }
        return directory;
    }

    ArrayList <File> getFilteredFileList(String fullPath) {
        File[] fullFileList = new File(fullPath).listFiles((dir, name) -> (name.endsWith("jpg") || name.endsWith("JPG")));
        ArrayList<File> sortedFileList = new ArrayList<>();
        if (fullFileList != null)
            sortedFileList.addAll(Arrays.asList(fullFileList));
        return sortedFileList;
    }

    private String getAppLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }

    long getFileDate(File file) {

        String dateTimeS = null;
        ExifInterface exif;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
            dateTimeS = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
//            return 0;
        }
        if (dateTimeS != null) {
            try {
                Date date = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).parse(dateTimeS);
                return date.getTime() - 5000;
            } catch (ParseException e) {
                Log.e("Parse","Exception dateTimeS "+dateTimeS);
                return System.currentTimeMillis();
            }
        }
        dateTimeS = file.getName();
        String prefixStr = dateTimeS.substring(0, 4);
        if (!prefixStr.equals("IMG_")){
            dateTimeS = dateTimeS.substring(4);
            Log.w("new datetime",dateTimeS);
//            if (dateTimeS.indexOf("2")> 0) {
//                dateTimeS = dateTimeS.substring(dateTimeS.indexOf("2"));
//                Log.w("prefix removed", dateTimeS);
//            } else {
//                return new Date(file.lastModified()).getTime();
//            }
        }

        String regex = "^\\d";
        String numbers = dateTimeS.replaceAll(regex, "");
        numbers = numbers.substring(0,14);
        try {
            Date date = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(numbers);
            return date.getTime() - 5000;
        } catch (ParseException e) {
            Log.e("Parse","Exception numbers "+numbers);
            return new Date(file.lastModified()).getTime();
        }
    }

    void makeBitmapFile(File imgFile, String outName, Bitmap bitmap, int orientation) {
        File file = new File(outName);
        if (file.exists())
            file.delete();
        bitMap2File(bitmap, file);
        copyExif(imgFile, file, orientation);
    }

    private void bitMap2File(Bitmap bitmap, File file) {
        FileOutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
            mainActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (IOException e) {
            utils.logE("ioException", e.toString());
            Toast.makeText(mainActivity, e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    static final private SimpleDateFormat sdfHourMinSec = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH);

    private void copyExif(File fileOrg, File fileNew, int orientation) {
        ExifInterface exifOrg, exifNew;
        String GPS, longitudeR = null, longitudeStr = null, latitudeR = null, latitudeStr = null, altitudeStr = null, altitudeR = null;
        try {
            exifOrg = new ExifInterface(fileOrg.getAbsolutePath());
            exifNew = new ExifInterface(fileNew.getAbsolutePath());

            GPS = exifOrg.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            if (GPS == null) {
                if (copyPasteGPS != null) {
                    String[] s = copyPasteGPS.split(";");
                    longitudeR = s[0]; longitudeStr = s[1];
                    latitudeR = s[2]; latitudeStr = s[3];
                    altitudeR = s[4]; altitudeStr = s[5];
                }
            }
            else {
                longitudeR = exifOrg.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                longitudeStr = exifOrg.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                latitudeR = exifOrg.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                latitudeStr = exifOrg.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                altitudeStr = exifOrg.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
                altitudeR = exifOrg.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
            }

            exifNew.setAttribute(ExifInterface.TAG_MAKE, exifOrg.getAttribute(ExifInterface.TAG_MAKE));
            exifNew.setAttribute(ExifInterface.TAG_MODEL, exifOrg.getAttribute(ExifInterface.TAG_MODEL));
            exifNew.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeR);
            exifNew.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
            exifNew.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeR);
            exifNew.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);
            exifNew.setAttribute(ExifInterface.TAG_ORIENTATION, ""+orientation);
            if (altitudeStr != null) {
                exifNew.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, altitudeR);
                exifNew.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, altitudeStr);

            }
            String dateTime = exifOrg.getAttribute(ExifInterface.TAG_DATETIME);
            if (dateTime == null) {
                dateTime = sdfHourMinSec.format(fileOrg.lastModified());
            }
            exifNew.setAttribute(ExifInterface.TAG_DATETIME, dateTime);
            exifNew.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "by riopapa");
            exifNew.saveAttributes();
            Date photoDate;
            try {
                photoDate = sdfHourMinSec.parse(dateTime);
            }
            catch (Exception e){
                photoDate = new Date(fileOrg.lastModified());
            }
            if (fileNew.setLastModified(photoDate.getTime()))
                Log.w("fileHa","Deleted");
        } catch (IOException e) {
            utils.log("1",e.toString());
            e.printStackTrace();
        }
    }

    void deleteOldLogFiles() {

        String oldDate = PREFIX + sdfDate.format(System.currentTimeMillis() - 2*24*60*60*1000L);
        File[] files = getPackageDirectory().listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            Collator myCollator = Collator.getInstance();
            for (File file : files) {
                String shortFileName = file.getName();
                if (myCollator.compare(shortFileName, oldDate) < 0) {
                    if (file.delete())
                        utils.log("delete old log",shortFileName);
                    else
                        Log.e("file", "Delete Error " + file);
                }
            }
        }
    }

    void deleteOldSAVFiles() {

        long oldDate = System.currentTimeMillis() - 5*24*60*60*1000L;        // 5 days before
        File[] files = new File(longFolder).listFiles((dir, name) -> name.endsWith("sav"));
        for (File file : files) {
            long lastModDate = new Date(file.lastModified()).getTime();
            if (lastModDate < oldDate && file.delete())
                utils.log("delete old SAV",file.getName());
            else
                    Log.e("file", "Delete " + file);
        }
    }

    String getUpperFolder(String fullPath, String lowerFolder) {
        String s = fullPath.replace("/"+lowerFolder+"/","");
        return s.substring(s.lastIndexOf("/")+1);
    }

    void showFolder (ActionBar actionBar) {
        String title = getUpperFolder(longFolder, shortFolder);
        if (title.equals("0")) {
            actionBar.setTitle(shortFolder);
        }
        else {
            actionBar.setTitle(title);
            actionBar.setSubtitle(shortFolder);
        }
    }
}
