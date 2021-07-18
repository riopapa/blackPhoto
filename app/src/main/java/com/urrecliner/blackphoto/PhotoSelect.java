package com.urrecliner.blackphoto;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.urrecliner.blackphoto.Vars.SPAN_COUNT;
import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.photosAdapter;
import static com.urrecliner.blackphoto.Vars.photos;

public class PhotoSelect extends AppCompatActivity {

    static RecyclerView photosView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        photosView = findViewById(R.id.sumNailView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        photosView.setLayoutManager(SGL);
        photosView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        photosView.setLayoutManager(SGL);
        photos =  new ArrayList<>();
        File[] fullFileList = currEventFolder.listFiles((dir, name) ->
                (name.endsWith("jpg")));
        if (fullFileList == null) {
            Toast.makeText(mContext, "No photos in " + currEventFolder.getName(), Toast.LENGTH_LONG).show();
            return;
        }
        Arrays.sort(fullFileList);
        photos = new ArrayList<>();
        for (File f: fullFileList) {
            photos.add(new Photo(f));
        }
        photosAdapter = new PhotosAdapter();
        photosView.setAdapter(photosAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new PhotoBitmap().execute("");
            }
        }, 200);
        photosView.setBackgroundColor(Color.YELLOW);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_sending:
                for (int i = 0; i < photos.size(); i++) {
                    Photo photo = photos.get(i);
                    if (photo.checked) {
                        new PhotoBigView().jpgCopy(photo.fullFileName);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class PhotoBitmap extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 0; i < photos.size(); i++) {
                Photo photo = photos.get(i);
                if (photo.bitMap == null ) {
                    photo.bitMap = PhotosAdapter.makeSumNail(photo.fullFileName);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String doI) {
            photosView.setBackgroundColor(Color.WHITE);
        }
    }
}
