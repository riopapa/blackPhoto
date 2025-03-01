package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.SPAN_COUNT;
import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.selectedJpgFolder;
import static com.urrecliner.blackphoto.Vars.snapEntities;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
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

public class SnapSelectActivity extends AppCompatActivity {

    static RecyclerView photosView;
    String title;
    Menu mainMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps_list);
        photosView = findViewById(R.id.snaps_view);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);

        photosView.setLayoutManager(SGL);
        photosView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        photosView.setLayoutManager(SGL);
        String[] shortNames = currEventFolder.list((dir, name) ->
                (name.endsWith("jpg")));
        if (shortNames == null) {
            utils.showToast(this, "No photos in " + currEventFolder.getName());
            return;
        }
        Arrays.sort(shortNames);
        snapEntities = new ArrayList<>();
        String currEventName = currEventFolder.toString();
        for (String s: shortNames) {
            snapEntities.add(new SnapEntity(currEventName, s, null));
        }
        snapImageAdaptor = new SnapImageAdaptor();
        photosView.setAdapter(snapImageAdaptor);
        title = currEventFolder.getName().substring(0, 18);
        photosView.scrollToPosition(snapEntities.size()*57/100);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        getMenuInflater().inflate(R.menu.snaps_list_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sending) {
            for (int i = 0; i < snapEntities.size(); i++) {
                SnapEntity snapEntity = snapEntities.get(i);
                if (snapEntity.isChecked) {
                    File dest = new File (selectedJpgFolder, snapEntity.photoName);
                    try {
                        Files.copy(new File(snapEntity.fullFolder, snapEntity.photoName).toPath(), dest.toPath());
                        utils.showToast(this, snapEntity.photoName+" copied");
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