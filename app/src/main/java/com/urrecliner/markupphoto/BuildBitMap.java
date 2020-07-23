package com.urrecliner.markupphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import androidx.exifinterface.media.ExifInterface;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.urrecliner.markupphoto.Vars.databaseIO;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.nowPlace;
import static com.urrecliner.markupphoto.Vars.nowPosition;
import static com.urrecliner.markupphoto.Vars.signatureMap;
import static com.urrecliner.markupphoto.Vars.sizeX;
import static com.urrecliner.markupphoto.Vars.utils;

class BuildBitMap {

    Bitmap bitmap = null;

    Photo makeSumNail(Photo photo) {
        ExifInterface exif;
        String fullFileName = photo.getFullFileName().toString();
        boolean landscape;
//        utils.log("sumnail","SumNail "+photo.getShortName());
        bitmap = BitmapFactory.decodeFile(fullFileName).copy(Bitmap.Config.RGB_565, false);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        try {
            exif = new ExifInterface(fullFileName);
        } catch (IOException e) {
            Toast.makeText(mContext,"No photo information on\n"+photo.getShortName(), Toast.LENGTH_LONG).show();
            return photo;
        }
        String Orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = Integer.parseInt(Orientation);
        if (orientation == 0)
            orientation = 1;
        if (orientation != 1) {
            Matrix matrix = new Matrix();
            if (orientation == 8)
                matrix.postRotate(-90);
            else if (orientation == 6)
                matrix.postRotate(90);
            else if (orientation == 3)
                matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }

        landscape = width > height;
        /* crop center image only */
        int sWidth;
        int sHeight;

        if (landscape) {
            sHeight = height * 11 / 16;
            sWidth = sHeight * 19 / 11;
            if (sWidth > width) {
                utils.logE("width",width+">" + sWidth+", "+height+">"+sHeight+", land=T, "+fullFileName);
                sWidth = width * 5 / 8;
            }
        }
        else {
            sWidth = width * 7 / 8;
            sHeight = sWidth * 9 / 6;
            if (sHeight > height) {
                utils.logE("height",width+">" + sWidth+", "+height+">"+sHeight+" land=F"+fullFileName);
                sHeight = height * 5 / 8;
            }
        }

        Bitmap sBitmap = Bitmap.createBitmap(bitmap, (width - sWidth)/2, (height - sHeight) /2, sWidth, sHeight);       // crop center
        int outWidth = sizeX * 5 / 18;   // smaller scale
        int outHeight = outWidth * sHeight / sWidth;
        sBitmap = Bitmap.createScaledBitmap(sBitmap, outWidth, outHeight, false);
        photo.setOrientation(orientation);
        photo.setBitmap(sBitmap);
        databaseIO.insert(photo);
//        newPhoto.setBitmap(null);   // nullify after insert db
        return photo;
    }

    Bitmap makeChecked(Bitmap photoMap) {

        int delta = 4;
        int delta2 = delta + delta;
        int width = photoMap.getWidth();
        int height = photoMap.getHeight();

        Bitmap outMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outMap);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 20;
        paint.setAntiAlias(true);
        paint.setColor(0x882B65EC);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Bitmap bitmap = Bitmap.createBitmap(photoMap, delta2, delta2, width-delta2-delta2, height-delta2-delta2);
        canvas.drawBitmap(bitmap, delta2, delta2, paint);
        return outMap;

    }

    Bitmap markDateLocSignature(Bitmap photoMap, long timeStamp) {
        final SimpleDateFormat sdfHourMin = new SimpleDateFormat("`yy/MM/dd(EEE) HH:mm", Locale.KOREA);
        int fontSize;
        int width = photoMap.getWidth();
        int height = photoMap.getHeight();
        Bitmap newMap = Bitmap.createBitmap(width, height, photoMap.getConfig());
        Canvas canvas = new Canvas(newMap);
        canvas.drawBitmap(photoMap, 0f, 0f, null);
        fontSize = (width>height) ? (width+height)/64 : (width+height)/56;  // date time
        String dateTime = sdfHourMin.format(timeStamp);
        int sigSize = (width + height) / 24;
        Bitmap sigMap = Bitmap.createScaledBitmap(signatureMap, sigSize, sigSize, false);
        if (nowPlace == null) { // datetime only at bottom
            int xPos = width / 4;
            int yPos = height - height / 18;
            drawTextOnCanvas(canvas, dateTime, fontSize, xPos, yPos);
            xPos = width - sigSize - width / 25;
            yPos = height - height / 18 - sigSize;
            canvas.drawBitmap(sigMap, xPos, yPos, null);
            return newMap;
        }
        int xPos = (width>height) ? width / 5: width / 4;
        int yPos = (width>height) ? height / 8: height / 9;
        drawTextOnCanvas(canvas, dateTime, fontSize, xPos, yPos);
        xPos = width - sigSize - sigSize / 2;
        yPos = sigSize/ 2;
        canvas.drawBitmap(sigMap, xPos, yPos, null);

        String [] split = nowPlace.split("\n");
        String place = split[0];
        String comment = "", address = "";
        if (split.length > 2) {
            comment = split[1].trim(); address = split[2].trim();
        }
        else
            address = split[1];
        if (place.length() == 0) place = " ";
        xPos = width / 2;
        fontSize = (height + width) / 72;  // gps
        yPos = height - fontSize - fontSize / 5;
        yPos = drawTextOnCanvas(canvas, nowPosition, fontSize, xPos, yPos);
        fontSize = fontSize * 13 / 10;  // address
        yPos -= fontSize + fontSize / 5;
        yPos = drawTextOnCanvas(canvas, address, fontSize, xPos, yPos);
        fontSize = fontSize * 14 / 10;  // Place
        yPos -= fontSize + fontSize / 5;
        if (!comment.equals("")) {
            yPos = drawTextOnCanvas(canvas, comment, fontSize, xPos, yPos);
            yPos -= fontSize + fontSize / 5;
        }
        drawTextOnCanvas(canvas, place, fontSize, xPos, yPos);
        return newMap;
    }

    int drawTextOnCanvas(Canvas canvas, String text, int fontSize, int xPos, int yPos) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(fontSize);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        int cWidth = canvas.getWidth() * 3 / 4;
        float tWidth = paint.measureText(text);
        int pos;
        if (tWidth > cWidth) {
//            utils.log("size","cWidth:"+cWidth+" tWidth:"+tWidth);
            int length = text.length() / 2;
            for (pos = length; pos < text.length(); pos++)
                if (text.substring(pos,pos+1).equals(" "))
                    break;
            String text1 = text.substring(pos);
            drawOutLinedText(canvas, text1, xPos, yPos, fontSize);
            yPos -= fontSize + fontSize / 4;
            text1 = text.substring(0, pos);
            drawOutLinedText(canvas, text1, xPos, yPos, fontSize);
            return yPos;
        }
        else
            drawOutLinedText(canvas, text, xPos, yPos, fontSize);
        return yPos;
    }

    void drawOutLinedText(Canvas canvas, String text, int xPos, int yPos, int textSize) {

        int color = ContextCompat.getColor(mContext, R.color.infoColor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth((int)(textSize/10));
        paint.setTypeface(mContext.getResources().getFont(R.font.nanumbarungothic));
        canvas.drawText(text, xPos, yPos, paint);

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, xPos, yPos, paint);
    }

    Bitmap buildSignatureMap() {
        Bitmap sigMap;
        File sigFile = new File(Environment.getExternalStorageDirectory(),"signature.png");
        if (sigFile.exists()) {
            sigMap = BitmapFactory.decodeFile(sigFile.toString(), null);
        }
        else
            sigMap = BitmapFactory.decodeResource(mContext.getResources(), R.raw.signature);
        Bitmap newBitmap = Bitmap.createBitmap(sigMap.getWidth(), sigMap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint alphaPaint = new Paint();
//        alphaPaint.setAlpha(120);
        canvas.drawBitmap(sigMap, 0, 0, alphaPaint);
        return newBitmap;
    }
}
