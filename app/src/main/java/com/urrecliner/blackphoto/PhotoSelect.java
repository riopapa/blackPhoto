package com.urrecliner.blackphoto;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.urrecliner.blackphoto.Vars.SPAN_COUNT;
import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.photosAdapter;
import static com.urrecliner.blackphoto.Vars.photos;

public class PhotoSelect extends AppCompatActivity {

    static RecyclerView photosView;
    String title;
    int cnt = 0;
    boolean asyncRunning = false;
    ActionBar actionBar;

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
        actionBar = this.getSupportActionBar();
        asyncRunning = true;
        new Handler().postDelayed(() -> new PhotoBitmap().execute(""), 20);
        photosView.setBackgroundColor(Color.YELLOW);
        title = currEventFolder.getName().substring(0, 18);
        showActionBar(0);
        photosView.scrollToPosition(photos.size()/3);
    }

    private void showActionBar(int cnt) {

        mActivity.runOnUiThread(() -> {
            actionBar.setTitle(title);
            String s = cnt+"/"+photos.size()+ " photos";
            actionBar.setSubtitle(s);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sending) {
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

    class PhotoBitmap extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            int oneThirdPos = photos.size() / 3;
            for (int i = 0; i < photos.size() * 2 / 3; i++) {
                if (!asyncRunning)
                    return null;
                int pos = oneThirdPos - i;
                if (pos >= 0)
                    setPhotoBitmap(pos);
                pos = oneThirdPos + i;
                if (pos < photos.size())
                    setPhotoBitmap(pos);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String doI) {
            photosView.setBackgroundColor(Color.WHITE);
            asyncRunning = false;
        }
    }

    private void setPhotoBitmap(int pos) {
        Photo photo;
        photo = photos.get(pos);
        if (photo.bitMap == null) {
            showActionBar(++cnt);
            photo.bitMap = PhotosAdapter.makeSumNail(photo.fullFileName);
            photos.set(pos, photo);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        asyncRunning = false;
    }
}