package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.SPAN_COUNT;
import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.selectedJpgFolder;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.snapImages;
import static com.urrecliner.blackphoto.Vars.utils;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class SnapSelect extends AppCompatActivity {

    static RecyclerView photosView;
    String title;
    Menu mainMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        photosView = findViewById(R.id.sumNailView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);

        photosView.setLayoutManager(SGL);
        photosView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        photosView.setLayoutManager(SGL);
        String[] shortNames = currEventFolder.list((dir, name) ->
                (name.endsWith("jpg")));
        if (shortNames == null) {
            utils.showToast( "No photos in " + currEventFolder.getName());
            return;
        }
        Arrays.sort(shortNames);
        snapImages = new ArrayList<>();
        String currEventName = currEventFolder.toString();
        for (String s: shortNames) {
            snapImages.add(new SnapImage(currEventName, s, null));
        }
        snapImageAdaptor = new SnapImageAdaptor();
        photosView.setAdapter(snapImageAdaptor);
        title = currEventFolder.getName().substring(0, 18);
        photosView.scrollToPosition(snapImages.size()*4/10);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        getMenuInflater().inflate(R.menu.photos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sending) {
            item.setIcon(ContextCompat.getDrawable(mContext, R.mipmap.airplane_red_black));
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
            new Timer().schedule(new TimerTask() {
                public void run() {
                    runOnUiThread(() -> item.setIcon(ContextCompat.getDrawable(mContext, R.mipmap.airplane_black)));
                }
            }, 2000);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}