package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolderFiles;
import static com.urrecliner.blackphoto.Vars.eventFolderFlag;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.utils;

import androidx.appcompat.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

class BuildDB {

    static View mainLayout;
    static ActionBar actionBar;

    void fillUp(View view, ActionBar actionBar) {

        mainLayout = view;
        this.actionBar = actionBar;
        try {
            new buildSumNailDB().execute("start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class buildSumNailDB extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            actionBar.setTitle("Black Photo");
            actionBar.setSubtitle("0 / "+eventFolderFiles.size());
            mainLayout.setBackgroundColor(mActivity.getColor(R.color.folderMake));
        }
        @Override
        protected String doInBackground(String... inputParams) {

            for (int evCnt = 0; evCnt < eventFolderFiles.size(); evCnt++) {
                File thisEventFolder = eventFolderFiles.get(evCnt);
                String thisEventString = thisEventFolder.toString();
                File[] fullFileList = thisEventFolder.listFiles((dir, name) ->
                        (name.endsWith("jpg")));
                if (fullFileList == null) {
                    utils.showToast( "No photos in " + thisEventFolder.getName());
                } else {
                    Arrays.sort(fullFileList);
                    File lastF = fullFileList[fullFileList.length-1];
                    String snapName = lastF.getName();
                    SnapEntity snapOut = snapDao.getByPhotoName(thisEventString, snapName);
                    if (snapOut == null) {
                        for (File f : fullFileList) {
                            snapName = f.getName();
                            snapOut = snapDao.getByPhotoName(thisEventString, snapName);
                            if (snapOut == null) {
                                createSnapImage(thisEventFolder.toString(), f);
                            }
                        }
                    }
                }
                eventFolderFlag.set(evCnt, true);   // this folder image is ready
                int finalEvCnt = evCnt;
                mActivity.runOnUiThread(() -> {
                    eventFolderAdapter.notifyItemChanged(finalEvCnt);
                    String s = ((finalEvCnt+1) == eventFolderFiles.size()) ? "All "+(finalEvCnt+1)+" Done"
                            : (finalEvCnt+1) + " / " + eventFolderFiles.size();
                    actionBar.setSubtitle(s);
                });
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String doI) {

            mainLayout.setBackgroundColor(mActivity.getColor(R.color.folderDone));
            new SqueezeDB().run();
        }
    }

    static void createSnapImage(String eventFolder, File f) {

        Bitmap bitmap = BitmapFactory.decodeFile(f.toString()).copy(Bitmap.Config.RGB_565, false);
        bitmap =  Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 5 / 40,
                bitmap.getHeight() * 5 / 40, false);
        SnapEntity snapOut = new SnapEntity(eventFolder, f.getName(), bitMapToString(bitmap));
        snapDao.insert(snapOut);
    }

    static String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream bOut= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, bOut);
        byte [] b=bOut.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    static Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            utils.log("utils", " StringToBitMap Error "+e);
            return null;
        }
    }
}