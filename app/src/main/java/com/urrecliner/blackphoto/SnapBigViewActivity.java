package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.selectedJpgFolder;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.snapEntities;
import static com.urrecliner.blackphoto.Vars.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SnapBigViewActivity extends AppCompatActivity {

    SnapEntity sna;
    TextView tvPhotoName1, tvPhotoName2;
    ImageView ivPhoto, ivCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_big);

        tvPhotoName1 = findViewById(R.id.photoName1);
        tvPhotoName2 = findViewById(R.id.photoName2);
        ivPhoto = findViewById(R.id.photoImage);
        ivCheck = findViewById(R.id.checkState);
        showBigImage();
        ImageView ivLeft = findViewById(R.id.goLeft);
        ivLeft.setOnClickListener(view -> {
            if (--nowPos < 0) nowPos = 0;
            showBigImage();
        });
        ImageView ivRight = findViewById(R.id.goRight);
        ivRight.setOnClickListener(view -> {
            if (++nowPos >= snapEntities.size()) nowPos = snapEntities.size() - 1;
            showBigImage();
        });
        ImageView ivPrev = findViewById(R.id.go2Prv);
        ivPrev.setOnClickListener(view -> {
            if (--nowPos < 0) nowPos = 0;
            showBigImage();
        });
        ImageView ivNxt = findViewById(R.id.go2Nxt);
        ivNxt.setOnClickListener(view -> {
            if (++nowPos >= snapEntities.size()) nowPos = snapEntities.size() - 1;
            showBigImage();
        });
    }

    private void showBigImage() {
        sna = snapEntities.get(nowPos);
        tvPhotoName1.setText(sna.photoName);
        tvPhotoName2.setText(sna.photoName);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(new File(sna.fullFolder, sna.photoName).getAbsolutePath());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        ivPhoto.setImageBitmap(bitmap);
        PhotoViewAttacher pA;       // to enable zoom
        pA = new PhotoViewAttacher(ivPhoto);
        pA.update();
        ivCheck.setImageResource((sna.isChecked ? R.drawable.checked:R.drawable.unchecked));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.snap_big_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            item.setIcon(ContextCompat.getDrawable(mContext, R.mipmap.airplane_red_black));
            sna.isChecked = !sna.isChecked;
            snapEntities.set(nowPos, sna);
            snapImageAdaptor.notifyItemChanged(nowPos);
            File dest = new File (selectedJpgFolder, sna.photoName);
            try {
                Files.copy(new File(sna.fullFolder, sna.photoName).toPath(), dest.toPath());
                utils.showToast( sna.photoName+" copied");
            } catch (IOException e) {
    //            e.printStackTrace();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}