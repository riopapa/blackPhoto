package com.urrecliner.markupphoto;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static com.urrecliner.markupphoto.Vars.buildBitMap;
import static com.urrecliner.markupphoto.Vars.buildDB;
import static com.urrecliner.markupphoto.Vars.databaseIO;
import static com.urrecliner.markupphoto.Vars.dirNotReady;
import static com.urrecliner.markupphoto.Vars.longFolder;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.mainActivity;
import static com.urrecliner.markupphoto.Vars.mainMenu;
import static com.urrecliner.markupphoto.Vars.makeDirFolder;
import static com.urrecliner.markupphoto.Vars.markTextInColor;
import static com.urrecliner.markupphoto.Vars.markTextOutColor;
import static com.urrecliner.markupphoto.Vars.markUpOnePhoto;
import static com.urrecliner.markupphoto.Vars.multiMode;
import static com.urrecliner.markupphoto.Vars.nowPlace;
import static com.urrecliner.markupphoto.Vars.photoAdapter;
import static com.urrecliner.markupphoto.Vars.photoView;
import static com.urrecliner.markupphoto.Vars.photos;
import static com.urrecliner.markupphoto.Vars.sharePrefer;
import static com.urrecliner.markupphoto.Vars.shortFolder;
import static com.urrecliner.markupphoto.Vars.signatureMap;
import static com.urrecliner.markupphoto.Vars.sizeX;
import static com.urrecliner.markupphoto.Vars.spanCount;
import static com.urrecliner.markupphoto.Vars.spanWidth;
import static com.urrecliner.markupphoto.Vars.squeezeDB;
import static com.urrecliner.markupphoto.Vars.utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mainActivity = this;
        utils = new Utils();
        buildBitMap = new BuildBitMap();
        askPermission();
        squeezeDB = new SqueezeDB();
        buildDB = new BuildDB();
        markUpOnePhoto = new MarkUpOnePhoto();

        utils.log("markup", "Start--");
        if (databaseIO == null) databaseIO = new DatabaseIO();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sizeX = size.x;

        photos = new ArrayList<>();
        sharePrefer = getApplicationContext().getSharedPreferences("myPhoto", MODE_PRIVATE);
        spanCount = sharePrefer.getInt("spanCount", 3);
        shortFolder = sharePrefer.getString("shortFolder", "DCIM/Camera");
        longFolder = sharePrefer.getString("longFolder", new File(Environment.getExternalStorageDirectory(), shortFolder).toString());
        markTextInColor = sharePrefer.getInt("markTextInColor", ContextCompat.getColor(mContext, R.color.markInColor));
        markTextOutColor = sharePrefer.getInt("markTextOutColor", ContextCompat.getColor(mContext, R.color.markOutColor));
        signatureMap = buildBitMap.buildSignatureMap();
        ArrayList<File> photoFiles = utils.getFilteredFileList(longFolder);
        if (photoFiles.size() == 0) {
            Toast.makeText(getApplicationContext(), "No jpg files in " + shortFolder + " folder\nSelect Folder", Toast.LENGTH_LONG).show();
//            finish();
//            Intent intent = new Intent(this, DirectoryActivity.class);
//            startActivity(intent);
//            return;
//            shortFolder = "DCIM/Camera";
//            longFolder = new File(Environment.getExternalStorageDirectory(), shortFolder).toString();
        }
        Collections.sort(photoFiles, Collections.<File>reverseOrder());

        prepareCards();
        for (File photoFile : photoFiles) {
            photos.add(new Photo(photoFile));
        }

        photoAdapter = new PhotoAdapter();
        photoView.setAdapter(photoAdapter);
        utils.showFolder(this.getSupportActionBar());
        utils.deleteOldLogFiles();
        final View view = findViewById(R.id.main_layout);
        view.post(new Runnable() {
            @Override
            public void run() {
                buildDB.fillUp(view);
            }
        });

        if (dirNotReady) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MenuItem item = mainMenu.findItem(R.id.action_Directory);
                            item.setEnabled(true);
                            item.getIcon().setAlpha(255);
                        }
                    }, 100);
                    makeDirFolder = new MakeDirFolder();
                }
            }, 1000);
        }
    }

    private void prepareCards() {
        photoView = findViewById(R.id.photoView);
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        photoView.setLayoutManager(SGL);
        photoView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        photoView.setLayoutManager(SGL);
        photoView.setBackgroundColor(0x88000000 + Color.GRAY);
        spanWidth = (sizeX / spanCount) * 96 / 100;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mainMenu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_TwoThree);
        item.setIcon((spanCount == 3) ? R.drawable.icon_show_two:R.drawable.icon_show_three);

        if (dirNotReady) {
            item = mainMenu.findItem(R.id.action_Directory);
            item.setEnabled(false);
            item.getIcon().setAlpha(35);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_setting:
                finish();
                intent = new Intent(this, ColorActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_MarkUpMulti:
                nowPlace = null;
                final MarkUpMulti markUpMulti = new MarkUpMulti();
                markUpMulti.markUp();
                return true;

            case R.id.action_Directory:
                finish();
                intent = new Intent(this, DirectoryActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_unSelect:
                multiMode = false;
                int totCount = photos.size();
                for (int i = 0; i < totCount; i++) {
                    Photo photo = photos.get(i);
                    if (photo.isChecked()) {
                        photo.setChecked(false);
                        photos.set(i, photo);
                        photoAdapter.notifyItemChanged(i, photo);
                    }
                }
                photoAdapter.notifyDataSetChanged();
                return true;

            case R.id.action_Delete:
                final ArrayList<String> toDelete;
                toDelete = build_DeletePhoto();
                if (toDelete.size()> 0) {
                    StringBuilder msg = new StringBuilder();
                    for (String s : toDelete) msg.append("\n").append(s);
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle("Delete multiple photos ?");
                    builder.setMessage(msg.toString());
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DeleteMulti.run();
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    showPopup(builder);
                } else {
                    Toast.makeText(mContext,"Photo selection is required to delete",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.action_TwoThree:
                spanCount = (spanCount == 2) ? 3:2;
                SharedPreferences.Editor editor = sharePrefer.edit();
                editor.putInt("spanCount", spanCount);
                editor.apply();
                editor.commit();
                prepareCards();
//                getMenuInflater().inflate(R.menuPlace.main_menu, menuThis);
                MenuItem item23 = mainMenu.findItem(R.id.action_TwoThree);
                item23.setIcon((spanCount == 3) ? R.drawable.icon_show_two:R.drawable.icon_show_three);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static void showPopup(AlertDialog.Builder builder) {
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btn = dialog.getButton(Dialog.BUTTON_POSITIVE);
        btn.setTextSize(16);
        btn.setAllCaps(false);
        btn = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        btn.setTextSize(24);
        btn.setAllCaps(false);
        btn.setFocusable(true);
        btn.setFocusableInTouchMode(true);
        btn.requestFocus();
    }

    private ArrayList<String> build_DeletePhoto() {
        ArrayList<String> arrayList = new ArrayList<>();

        for (Photo photo: photos) {
            if (photo.isChecked())
                arrayList.add(photo.getShortName());
        }
        return arrayList;
    }

    final long BACK_DELAY = 4000;
    long backKeyPressedTime;
    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis()<backKeyPressedTime+BACK_DELAY){
            buildDB.cancel();
            squeezeDB.cancel();
            finish();
            new Timer().schedule(new TimerTask() {
                public void run() {
                    finishAffinity();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }, 500);
        }
        Toast.makeText(this, "Press BackKey again to quit",Toast.LENGTH_SHORT).show();
        backKeyPressedTime = System.currentTimeMillis();
    }

    // ↓ ↓ ↓ P E R M I S S I O N    RELATED /////// ↓ ↓ ↓ ↓
    ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    private void askPermission() {
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.VIBRATE);
        permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        permissions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (permissionsToRequest.size() != 0) {
            requestPermissions(permissionsToRequest.toArray(new String[0]),
//            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList findUnAskedPermissions(@NonNull ArrayList<String> wanted) {
        ArrayList <String> result = new ArrayList<String>();
        for (String perm : wanted) if (hasPermission(perm)) result.add(perm);
        return result;
    }
    private boolean hasPermission(@NonNull String permission) {
        return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }

//    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perms : permissionsToRequest) {
                if (hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
            }
            else
                Toast.makeText(mContext, "Permissions not granted.", Toast.LENGTH_LONG).show();
        }
    }
    private void showDialog(String msg) {
        showMessageOKCancel(msg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsRejected.toArray(
                                new String[0]), ALL_PERMISSIONS_RESULT);
                    }
                });
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(mainActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
// ↑ ↑ ↑ ↑ P E R M I S S I O N    RELATED /////// ↑ ↑ ↑

}