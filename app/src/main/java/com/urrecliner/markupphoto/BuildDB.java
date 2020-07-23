package com.urrecliner.markupphoto;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.urrecliner.markupphoto.Vars.buildBitMap;
import static com.urrecliner.markupphoto.Vars.buildDB;
import static com.urrecliner.markupphoto.Vars.databaseIO;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.photoView;
import static com.urrecliner.markupphoto.Vars.photos;
import static com.urrecliner.markupphoto.Vars.squeezeDB;


class BuildDB {

    private static boolean isCanceled = false;
    private static Snackbar snackbar = null;
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

    void cancel() {
        isCanceled = true;
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }

    static class buildSumNailDB extends AsyncTask<String, String, String> {

        int count, totCount;
        TextView tvSnack;

        @Override
        protected void onPreExecute() {
            count = 0;
            totCount = photos.size();
            photoView.setBackgroundColor(Color.CYAN);
            String s = "Building SumNails for "+totCount+" photos";
            snackbar = Snackbar.make(mainLayout, s, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Hide", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                    snackbar = null;
                    Toast.makeText(mContext, "Status Bar hidden", Toast.LENGTH_LONG).show();
                }
            });
//            TextView stvAction = snackbar.getView().findViewById( android.support.v4.design.R.id.snackbar_action);
//            stvAction.setTextSize(12);
//            stvAction.setTransformationMethod(null);
//            stvAction.setTypeface(stvAction.getTypeface(), Typeface.BOLD);
//
//            tvSnack =  snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
//            tvSnack.setTextSize(16);
//            tvSnack.setMaxLines(3);
//            tvSnack.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            snackbar.show();
        }

        final String SAY_COUNT = "sc";
        @Override
        protected String doInBackground(String... inputParams) {

            for (int pos = 0; pos < photos.size(); pos++) {
                if (isCanceled)
                    break;
                try {
                    Photo photo = photos.get(pos);
                    if (photo.getBitmap() == null) {
                        photo = buildDB.getPhotoWithMap(photo);
                        photos.set(pos, photo);
                        publishProgress(SAY_COUNT);
                    }
                } catch (Exception e) {
                    break;
                }
                count++;
            }
            return "done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (snackbar != null) {
                String s = " "+count+" in "+totCount+" photos";
//                tvSnack.setText(s);
            }
        }

        @Override
        protected void onPostExecute(String doI) {

            if (snackbar != null) {
                snackbar.dismiss();
                snackbar = null;
            }
            photoView.setBackgroundColor(Color.WHITE);
            squeezeDB.run();
        }
    }

    Photo getPhotoWithMap(Photo photoIn) {

        Photo photo = databaseIO.retrievePhoto(photoIn);
        if (photo.getBitmap() == null) {
            photo = buildBitMap.makeSumNail(photoIn);
            return databaseIO.retrievePhoto(photo);
        }
        return photo;
    }
}

