package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.SPAN_COUNT;
import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.selectedJpgFolder;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.snapImages;
import static com.urrecliner.blackphoto.Vars.utils;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class SnapSelect extends AppCompatActivity {

    static RecyclerView photosView;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        photosView = findViewById(R.id.sumNailView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);

        photosView.setLayoutManager(SGL);
        photosView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        photosView.setLayoutManager(SGL);
        File[] fullFileList = currEventFolder.listFiles((dir, name) ->
                (name.endsWith("jpg")));
        if (fullFileList == null) {
            utils.showToast( "No photos in " + currEventFolder.getName());
            return;
        }
        Arrays.sort(fullFileList);
        snapImages = new ArrayList<>();
        String currEventName = currEventFolder.toString();
        for (File f: fullFileList) {
            snapImages.add(new SnapImage(currEventName, f.getName(), null));
        }
        snapImageAdaptor = new SnapImageAdaptor();
        photosView.setAdapter(snapImageAdaptor);
//        photosView.setBackgroundColor(Color.YELLOW);
        title = currEventFolder.getName().substring(0, 18);
        photosView.scrollToPosition(snapImages.size()*4/10);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sending) {
            for (int i = 0; i < snapImages.size(); i++) {
                SnapImage snapImage = snapImages.get(i);
                if (snapImage.isChecked) {
                    File dest = new File (selectedJpgFolder, snapImage.photoName);
                    try {
                        Files.copy(new File(snapImage.fullFolder, snapImage.photoName).toPath(), dest.toPath());
                        utils.showToast( snapImage.photoName+" copied");
                    } catch (IOException e) {
//            e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}