package com.urrecliner.blackphoto;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import static com.urrecliner.blackphoto.Vars.mContext;

class BuildBitMap {

    Activity activity;
    Context context;

    public void init(Activity activity, Context context) {
        this.activity = activity;this.context = context;
    }

    Bitmap makeChecked(Bitmap photoMap) {

        int delta = 2;
        int delta2 = delta + delta;
        int width = photoMap.getWidth();
        int height = photoMap.getHeight();

        Bitmap outMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outMap);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        final float roundPx = 10;
        paint.setAntiAlias(true);
        paint.setColor(mContext.getColor(R.color.xorColor));
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        Bitmap bitmap = Bitmap.createBitmap(photoMap, delta2, delta2, width-delta2-delta2, height-delta2-delta2);
        canvas.drawBitmap(bitmap, delta2, delta2, paint);
        return outMap;
    }
}
