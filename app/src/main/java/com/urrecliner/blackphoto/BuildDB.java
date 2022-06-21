package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolderFiles;
import static com.urrecliner.blackphoto.Vars.eventFolderFlag;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;


class BuildDB {

    private static boolean isCanceled = false;
    private static Snackbar snackBar = null;
    private static View mainLayout;

    void fillUp(View view) {

        isCanceled = false;
        mainLayout = view;
        try {
            new buildSumNailDB().execute("start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopSnackBar() {
        isCanceled = true;
        if (snackBar != null) {
            snackBar.dismiss();
            snackBar = null;
        }
    }

    class buildSumNailDB extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            mainLayout.setBackgroundColor(mActivity.getColor(R.color.folderMake));
            String s = "Building SumNails for "+ eventFolderFiles.size()+" events";
            snackBar = Snackbar.make(mainLayout, s, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction("Hide", v -> {
                snackBar.dismiss();
                snackBar = null;
                Toast.makeText(mContext, "Status Bar hidden", Toast.LENGTH_LONG).show();
            });
            snackBar.show();
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
                    Snackbar refreshingSnackBar = Snackbar
                            .make(mainLayout, "creating room database .."
                                    + (finalEvCnt+1) + " / " + eventFolderFiles.size() + " events", Snackbar.LENGTH_LONG);
                    refreshingSnackBar.show();
                });
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String doI) {

            stopSnackBar();
            mainLayout.setBackgroundColor(mActivity.getColor(R.color.folderDone));
            new SqueezeDB().run();
        }
    }

    void createSnapImage(String eventFolder, File f) {

        Bitmap bitmap = BitmapFactory.decodeFile(f.toString()).copy(Bitmap.Config.RGB_565, false);
        bitmap =  Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 5 / 40,
                bitmap.getHeight() * 5 / 40, false);
        SnapEntity snapOut = new SnapEntity(eventFolder, f.getName(), bitMapToString(bitmap));
        snapDao.insert(snapOut);
    }

    String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            utils.log("utils", " StringToBitMap Error "+e.toString());
            return null;
        }
    }

}