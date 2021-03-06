package com.urrecliner.markupphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;

import static com.urrecliner.markupphoto.Vars.SUFFIX_JPG;
import static com.urrecliner.markupphoto.Vars.buildBitMap;
import static com.urrecliner.markupphoto.Vars.nowPlace;
import static com.urrecliner.markupphoto.Vars.utils;

class MarkUpOnePhoto {

    static File insertGeoInfo(Photo photo) {

        File imgFile = photo.getFullFileName();
        long timeStamp = utils.getFileDate(imgFile);
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        int orientation = photo.getOrientation();
        if (orientation != 1) {
            Matrix matrix = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int degree = 0;
            if (orientation == 6)
                degree = 90;
            else if (orientation == 8)
                degree = -90;
            else if (orientation == 3)
                degree = 180;
            matrix.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        }
        bitmap = buildBitMap.markDateLocSignature(bitmap, timeStamp);
        String fileName = imgFile.toString();
        String outName = fileName.substring(0, fileName.length() - 4) + "_";

        if (nowPlace != null) {
            String[] pNames = nowPlace.split("\n");
            if (pNames.length > 2)
                outName += pNames[1] + "("+ pNames[0] + ")";    // [0] 음식명, [1] 장소명
            else
                outName += pNames[0];
        }
        outName += SUFFIX_JPG;
        utils.makeBitmapFile(imgFile, outName, bitmap, 1);
        return new File(outName);
    }

}
