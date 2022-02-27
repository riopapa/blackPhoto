package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.eventFolders;
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
            mainLayout.setBackgroundColor(Color.CYAN);
            String s = "Building SumNails for "+eventFolders.size()+" events";
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

            for (int evCnt = 0; evCnt < eventFolders.size(); evCnt++) {
                File thisEventFolder = eventFolders.get(evCnt);
                String thisEventString = thisEventFolder.toString();
                File[] fullFileList = thisEventFolder.listFiles((dir, name) ->
                        (name.endsWith("jpg")));
                if (fullFileList == null) {
                    utils.showToast( "No photos in " + thisEventFolder.getName());
                } else {
                    Arrays.sort(fullFileList);
                    File lastF = fullFileList[fullFileList.length-1];
                    String snapName = lastF.getName();
                    SnapImage snapOut = snapDao.getByPhotoName(thisEventString, snapName);
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
                int finalEvCnt = evCnt + 1;
                mActivity.runOnUiThread(() -> {
                    Snackbar refreshingSnackBar = Snackbar
                            .make(mainLayout, "creating room database .."
                                    + finalEvCnt + " / " + eventFolders.size() + " events", Snackbar.LENGTH_LONG);
                    refreshingSnackBar.show();
                });
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String doI) {

            stopSnackBar();
            mainLayout.setBackgroundColor(Color.WHITE);
//            new SqueezeDB().run();
        }
    }

    void createSnapImage(String eventFolder, File f) {

        Bitmap bitmap = BitmapFactory.decodeFile(f.toString()).copy(Bitmap.Config.RGB_565, false);
        bitmap =  Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 5 / 40,
                bitmap.getHeight() * 5 / 40, false);
        SnapImage snapOut = new SnapImage(eventFolder, f.getName(), bitMapToString(bitmap));
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