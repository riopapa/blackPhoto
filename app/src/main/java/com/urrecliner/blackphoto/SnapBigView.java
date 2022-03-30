package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.nowPos;
import static com.urrecliner.blackphoto.Vars.snapImageAdaptor;
import static com.urrecliner.blackphoto.Vars.snapImages;

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

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SnapBigView extends AppCompatActivity {

    SnapImage sna;
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
            if (--nowPos < 0) nowPos = 0;
            showBigImage();
        });
        ImageView ivRight = findViewById(R.id.goRight);
        ivRight.setOnClickListener(view -> {
            if (++nowPos >= snapImages.size()) nowPos = snapImages.size() - 1;
            showBigImage();
        });
        ImageView ivPrev = findViewById(R.id.go2Prv);
        ivPrev.setOnClickListener(view -> {
            if (--nowPos < 0) nowPos = 0;
            showBigImage();
        });
        ImageView ivNxt = findViewById(R.id.go2Nxt);
        ivNxt.setOnClickListener(view -> {
            if (++nowPos >= snapImages.size()) nowPos = snapImages.size() - 1;
            showBigImage();
        });
    }

    private void showBigImage() {
        sna = snapImages.get(nowPos);
        tvPhotoName.setText(sna.photoName);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap bitmap = BitmapFactory.decodeFile(new File(sna.fullFolder, sna.photoName).getAbsolutePath());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        ivPhoto.setImageBitmap(bitmap);
        PhotoViewAttacher pA;       // to enable zoom
        pA = new PhotoViewAttacher(ivPhoto);
        pA.update();
        ivCheck.setImageResource((sna.isChecked ? R.mipmap.checked:R.mipmap.unchecked));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.selectPhoto) {
            sna.isChecked = !sna.isChecked;
            snapImages.set(nowPos, sna);
            snapImageAdaptor.notifyItemChanged(nowPos);
            nowPos++;
            if (nowPos >= snapImages.size()) nowPos = snapImages.size() - 1;
            showBigImage();
        }
        return super.onOptionsItemSelected(item);
    }

}