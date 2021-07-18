package com.urrecliner.blackphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.urrecliner.blackphoto.Vars.eventFullFolder;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.photos;
import static com.urrecliner.blackphoto.Vars.photosAdapter;

public class PhotoBigView extends AppCompatActivity {

    Photo photo;
    TextView tvPhotoName;
    ImageView ivPhoto, ivCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        tvPhotoName = findViewById(R.id.photoName);
        ivPhoto = findViewById(R.id.photoImage);
        ivCheck = findViewById(R.id.checkState);
        showBigImage();
        ImageView ivLeft = findViewById(R.id.goLeft);
        ivLeft.setOnClickListener(view -> {
            nowPos--;
            if (nowPos < 0) nowPos = 0;
            showBigImage();
        });
        ImageView ivRight = findViewById(R.id.goRight);
        ivRight.setOnClickListener(view -> {
            nowPos++;
            if (nowPos >= photos.size()) nowPos = photos.size() - 1;
            showBigImage();
        });
    }

    private void showBigImage() {
        photo = photos.get(nowPos);
        tvPhotoName.setText(photo.fullFileName.getName());
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(photo.fullFileName.getAbsolutePath());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        ivPhoto.setImageBitmap(bitmap);
        PhotoViewAttacher pA;       // to enable zoom
        pA = new PhotoViewAttacher(ivPhoto);
        pA.update();
        ivCheck.setImageResource((photo.checked ? R.mipmap.checked:R.mipmap.unchecked));
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
                photo.checked = !photo.checked;
                photos.set(nowPos, photo);
                photosAdapter.notifyItemChanged(nowPos);
                nowPos++;
                if (nowPos >= photos.size()) nowPos = photos.size() - 1;
                showBigImage();
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
