package com.urrecliner.blackphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.FileUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.urrecliner.blackphoto.Vars.eventFullFolder;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.photos;
import static com.urrecliner.blackphoto.Vars.photosAdapter;

public class PhotoBigView extends AppCompatActivity {

    Photo photo;
    TextView tv;
    ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        photo = photos.get(nowPos);
        tv = findViewById(R.id.photoName);
        iv = findViewById(R.id.photoImage);
        tv.setText(photo.fullFileName.getName());
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(photo.fullFileName.getAbsolutePath());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        iv.setImageBitmap(bitmap);
        PhotoViewAttacher pA;       // to enable zoom
        pA = new PhotoViewAttacher(iv);
        pA.update();
        ImageView sv = findViewById(R.id.sendImage);
        sv.setOnClickListener(view -> {
            photos.set(nowPos, photo);
            jpgCopy(photo.fullFileName);
            photosAdapter.notifyItemChanged(nowPos);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.selectPhoto:
                photo.checked = true;
                photos.set(nowPos, photo);
                photosAdapter.notifyItemChanged(nowPos);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    static void jpgCopy (File sourcepath) {

        File dest = new File (eventFullFolder, sourcepath.getName());
        try {
            Files.copy(sourcepath.toPath(), dest.toPath());
            Toast.makeText(mContext, sourcepath.getName()+" copied",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}
