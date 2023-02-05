package com.urrecliner.blackphoto;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.urrecliner.blackphoto.Vars.logFullFolder;
import static com.urrecliner.blackphoto.Vars.mContext;

import org.apache.commons.io.FileUtils;

class Utils {

    void log(String tag, String text) {
        StackTraceElement[] traces;
        traces = Thread.currentThread().getStackTrace();
        String log = traceName(traces[6].getMethodName()) + traceName(traces[5].getMethodName()) + traceName(traces[4].getMethodName()) + traceClassName(traces[3].getClassName())+"> "+traces[3].getMethodName() + "#" + traces[3].getLineNumber() + " {"+ tag + "} " + text;
        Log.w(tag , log);
        append2file(sdfDateTimeLog.format(new Date())+" " +log);
    }

    private String traceName (String s) {
        if (s.equals("performResume") || s.equals("performCreate") || s.equals("callActivityOnResume") || s.equals("access$1200") || s.equals("access$000") || s.equals("handleReceiver") || s.equals("handleMessage") || s.equals("dispatchMessage"))
            return "";
        else
            return s + "> ";
    }

    private final SimpleDateFormat sdfDateTimeLog = new SimpleDateFormat("MM-dd HH.mm.ss sss", Locale.US);
    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("yy-MM-dd", Locale.US);

    private String traceClassName(String s) {
        return s.substring(s.lastIndexOf(".")+1);
    }

    private void append2file(String textLine) {

        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            String PREFIX = "log_";
            File file = new File(logFullFolder, PREFIX + sdfDate.format(new Date())+"Photo.txt");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.w("createFile", " Error");
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

    public void readyPackageFolder (File dir){
        try {
            if (!dir.exists() && !dir.mkdirs())
                Log.e("make dir", "Error");
        } catch (Exception e) {
            Log.e("creating Folder error", dir + "_" + e);
        }
    }

    void showToast(String text) {
        Toast toast = Toast.makeText(mContext,text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,200);
        toast.show();
    }

    void deleteFolder (File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}