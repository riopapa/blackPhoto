package com.urrecliner.blackphoto;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
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

import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.photosAdapter;
import static com.urrecliner.blackphoto.Vars.photos;

public class PhotoSelect extends AppCompatActivity {

    RecyclerView photosView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        photosView = findViewById(R.id.sumNailView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        photosView.setLayoutManager(SGL);
        photosView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        photosView.setLayoutManager(SGL);
        photos =  new ArrayList<>();
        File[] fullFileList = currEventFolder.listFiles((dir, name) ->
                ((name.endsWith("jpg") || name.endsWith("JPG"))));
        if (fullFileList != null) {
            Arrays.sort(fullFileList);
            photos = new ArrayList<>();
            for (File f: fullFileList) {
                photos.add(new Photo(f));
            }
            new PhotoBitmap().execute("");
        } else {
            Toast.makeText(mContext,"No photos in "+currEventFolder.getName(), Toast.LENGTH_LONG).show();
            return;
        }
        photosAdapter = new PhotosAdapter();
        photosView.setAdapter(photosAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                photosView.scrollToPosition(50);
            }
        }, 200);
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

    private ArrayList<String> build_DeletePhoto() {
        ArrayList<String> arrayList = new ArrayList<>();

        for (Photo photo: photos) {
            if (photo.checked)
                arrayList.add(photo.shortName);
        }
        return arrayList;
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
    }
}
