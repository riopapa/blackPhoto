package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.SPAN_COUNT;
import static com.urrecliner.blackphoto.Vars.buildDB;
import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolderFlag;
import static com.urrecliner.blackphoto.Vars.eventFolderView;
import static com.urrecliner.blackphoto.Vars.eventFolderFiles;
import static com.urrecliner.blackphoto.Vars.eventFolderBitmaps;
import static com.urrecliner.blackphoto.Vars.eventMP4Folder;
import static com.urrecliner.blackphoto.Vars.jpgFullFolder;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.selectedJpgFolder;
import static com.urrecliner.blackphoto.Vars.snapDB;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.spanWidth;
import static com.urrecliner.blackphoto.Vars.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mActivity = this;
        ActionBar actionBar = this.getSupportActionBar();
        askPermission();
        utils = new Utils();
        File[] eventFolderList = jpgFullFolder.listFiles();

//        File[] eventFolderList = jpgFullFolder.listFiles(file -> (file.getPath().startsWith("V2"))); // V2022-02-03 ...
        if (eventFolderList == null) {
            Toast.makeText(this,"No event Jpg Folders",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Arrays.sort(eventFolderList);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            Permission.ask(this, this, info);
        } catch (Exception e) {
            Log.e("Permission", "No Permission " + e);
        }
        // If you have access to the external storage, do whatever you need
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }


        eventFolderFiles = new ArrayList<>();
        eventFolderFiles.addAll(Arrays.asList(eventFolderList));
        eventFolderBitmaps = new ArrayList<>();
        eventFolderFlag = new ArrayList<>();
        for (int i = 0; i < eventFolderFiles.size(); i++) {
            eventFolderBitmaps.add(null);
            eventFolderFlag.add(false);
        }

        snapDB = Room.databaseBuilder(getApplicationContext(), SnapDataBase.class, "snapEntity-db")
                .fallbackToDestructiveMigration()   // schema changeable
                .allowMainThreadQueries()           // main thread 에서 IO
                .build();
        snapDao = snapDB.snapDao();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        spanWidth = (size.x / SPAN_COUNT) * 60 / 100;
        eventFolderView = findViewById(R.id.eventView);
        eventFolderAdapter = new EventFolderAdapter();
        eventFolderView.setAdapter(eventFolderAdapter);
        utils.readyPackageFolder(selectedJpgFolder);
        buildDB = new BuildDB();
        buildDB.fillUp(findViewById(R.id.main_layout), actionBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.erase) {
            confirm_delete();
        } else if (item.getItemId() == R.id.blackBox) {
            Intent sendIntent = getPackageManager().getLaunchIntentForPackage("com.urrecliner.blackbox");
            assert sendIntent != null;
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sendIntent);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
        return false;
    }

    public void confirm_delete() {
        View dialogView = getLayoutInflater().inflate(R.layout.confirm_delete, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(dialogView.getContext());
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button ok_btn = dialogView.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(v -> {
            alertDialog.dismiss();

            File[] mp4Files = eventMP4Folder.listFiles(file -> (file.getPath().endsWith("mp4")));
            String fNames = "";
            if (mp4Files != null && mp4Files.length > 0) {
                for (File mp4: mp4Files) {
                    mp4.delete();
                    fNames += mp4.getName()+", ";
                }
                Toast.makeText(mContext, fNames+ " files deleted ", Toast.LENGTH_SHORT).show();
            }
            File[] jpgFolders = jpgFullFolder.listFiles();
            if (jpgFolders != null) {
                for (File fJpg: jpgFolders) {
                    utils.deleteFolder(fJpg);
                    snapDao.deleteFolder(fJpg.toString());
                    Toast.makeText(mContext, fJpg.getName()+" event mp4 deleted ", Toast.LENGTH_SHORT).show();
                }
            }
            File[] jpgFiles = selectedJpgFolder.listFiles();
            if (jpgFiles != null && jpgFiles.length > 0) {
                for (File jpgFile: jpgFiles) {
                    jpgFile.delete();
                }
                Toast.makeText(mContext, jpgFiles.length+" selected Jpgs deleted ", Toast.LENGTH_SHORT).show();
            }
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        });

        Button cancle_btn = dialogView.findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(v -> alertDialog.dismiss());
    }

    // ↓ ↓ ↓ P E R M I S S I O N   RELATED /////// ↓ ↓ ↓ ↓  BEST CASE 20/09/27 with no lambda
    private final static int ALL_PERMISSIONS_RESULT = 101;
    ArrayList permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    String [] permissions;

    private void askPermission() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            permissions = info.requestedPermissions;//This array contain
        } catch (Exception e) {
            Log.e("Permission", "Not done", e);
        }

        permissionsToRequest = findUnAskedPermissions();
        if (permissionsToRequest.size() != 0) {
            requestPermissions((String[]) permissionsToRequest.toArray(new String[0]),
//            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    ALL_PERMISSIONS_RESULT);
        }
    }

    private ArrayList findUnAskedPermissions() {
        ArrayList <String> result = new ArrayList<>();
        for (String perm : permissions) if (hasPermission(perm)) result.add(perm);
        return result;
    }
    private boolean hasPermission(String permission) {
        return (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (Object perms : permissionsToRequest) {
                if (hasPermission((String) perms)) {
                    permissionsRejected.add((String) perms);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    String msg = "These permissions are mandatory for the application. Please allow access.";
                    showDialog(msg);
                }
            }
        }
    }

    private void showDialog(String msg) {
        showMessageOKCancel(msg,
                (dialog, which) -> MainActivity.this.requestPermissions(permissionsRejected.toArray(
                        new String[0]), ALL_PERMISSIONS_RESULT));
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

// ↑ ↑ ↑ ↑ P E R M I S S I O N    RELATED /////// ↑ ↑ ↑

}