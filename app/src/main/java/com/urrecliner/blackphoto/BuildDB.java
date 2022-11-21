package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolderFiles;
import static com.urrecliner.blackphoto.Vars.eventFolderFlag;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.utils;

import androidx.appcompat.app.ActionBar;
import androidx.exifinterface.media.ExifInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

    class buildSumNailDB extends AsyncTask<String, String, String> {

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
                if (fullFileList == null || fullFileList.length < 30) {
                    continue;
//                    utils.showToast( "No photos in " + thisEventFolder.getName());
                } else {
                    Arrays.sort(fullFileList);
                    File lastF = fullFileList[fullFileList.length-1];
                    String snapName = lastF.getName();
                    SnapEntity snapOut = snapDao.getByPhotoName(thisEventString, snapName);
                    if (snapOut == null) {  // this event folder not handled
                        final int nbrPhotos = fullFileList.length;
                        final String abTitle = "Black Photo ("+(evCnt+1)+"/"+eventFolderFiles.size()+")";
                        final String lastFName = thisEventFolder.getName();
                        mActivity.runOnUiThread(() -> actionBar.setTitle(abTitle));
                        actionBar.setSubtitle(lastFName+", "+nbrPhotos);
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
                    String s = ((finalEvCnt+1) == eventFolderFiles.size()) ? "All "+(finalEvCnt+1)+"  Folders Done"
                            : (finalEvCnt+1) + " / " + eventFolderFiles.size();
                    actionBar.setTitle("Black Photo DB ready");
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

    static Bitmap bitmap = null;
    static ExifInterface exif;
    void createSnapImage(String eventFolder, File f) {

        Bitmap bitmap = buildThumbNail(f);
        SnapEntity snapOut = new SnapEntity(eventFolder, f.getName(), bitMapToString(bitmap));
        snapDao.insert(snapOut);
    }

    Bitmap buildThumbNail(File f) {
        try {
            exif = new ExifInterface(f);
            byte[] imageData=exif.getThumbnail();
            if (imageData != null && imageData.length > 1) {
                Log.w("size "+imageData.length,f.toString());
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 20 / 40,
                        bitmap.getHeight() * 20 / 40, false);
            } else {
                Log.e("Size error ", f.toString());
                bitmap = null;
            }
        } catch (IOException e) {
            bitmap = null;
            Log.e("IOException", f.toString()+", "+e);
        }
        if (bitmap == null) {
            Log.w("buildThumBNail", f.toString()+" image");
            bitmap = BitmapFactory.decodeFile(f.toString()).copy(Bitmap.Config.RGB_565, false);
        }
        return bitmap;
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