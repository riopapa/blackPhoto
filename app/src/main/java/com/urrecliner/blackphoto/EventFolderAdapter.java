package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.buildDB;
import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolderBitmaps;
import static com.urrecliner.blackphoto.Vars.eventFolderFiles;
import static com.urrecliner.blackphoto.Vars.eventFolderFlag;
import static com.urrecliner.blackphoto.Vars.header;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.snapDao;
import static com.urrecliner.blackphoto.Vars.utils;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class EventFolderAdapter extends RecyclerView.Adapter<EventFolderAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return eventFolderFiles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventTIme;
        ImageView image1;
        LinearLayout linearLayout;

        ViewHolder(final View itemView) {
            super(itemView);
            tvEventTIme = itemView.findViewById(R.id.eventTime);
            image1 = itemView.findViewById(R.id.photoImage1);
            linearLayout = itemView.findViewById(R.id.folderLayout);
            itemView.setOnClickListener(view -> {
                currEventFolder = eventFolderFiles.get(getAbsoluteAdapterPosition());
                Intent intent = new Intent(mContext, SnapSelectActivity.class);
                mActivity.startActivity(intent);
            });

            itemView.setOnLongClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                currEventFolder = eventFolderFiles.get(pos);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Delete this event?");
                builder.setMessage(tvEventTIme.getText().toString());
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    utils.deleteFolder(currEventFolder);
                    eventFolderFiles.remove(getAbsoluteAdapterPosition());
                    eventFolderBitmaps.remove(getAbsoluteAdapterPosition());
                    eventFolderAdapter.notifyItemRemoved(pos);
                    snapDao.deleteFolder(currEventFolder.toString());
                });
                builder.setNegativeButton("No", (dialog, which) -> { });
                showYesNoPopup(builder);
                return true;
            });
        }
    }
    static void showYesNoPopup(AlertDialog.Builder builder) {
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

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File oneEventFolder = eventFolderFiles.get(position);
        String folderName = oneEventFolder.toString();
        String [] photoList = oneEventFolder.list();
        assert photoList != null;
        String showName = folderName.substring(38, 56) + " / "+photoList.length;
        holder.tvEventTIme.setText(showName);

        if (eventFolderBitmaps.get(position) ==  null) {
            SnapEntity snapHead = snapDao.getByPhotoName(folderName, header);
            if (snapHead == null) {
                snapHead = new SnapEntity(folderName, header, "");
                Bitmap mergedBitmap = makeBitmap(folderName, photoList);
                snapHead.sumNailMap = buildDB.bitMapToString(mergedBitmap);
                snapDao.insert(snapHead);
                eventFolderBitmaps.set(position,mergedBitmap);
            } else
                eventFolderBitmaps.set(position,buildDB.stringToBitMap(snapHead.sumNailMap));
        }
        holder.image1.setImageBitmap(eventFolderBitmaps.get(position));
        holder.linearLayout.setBackgroundColor((eventFolderFlag.get(position) ?
                mActivity.getColor(R.color.folderDone) : mActivity.getColor(R.color.folderMake)));
    }

    private Bitmap makeBitmap(String folderName, String[] photoList) {
        int photoSize = photoList.length;
        if (photoSize < 30) {
            return BitmapFactory.decodeFile(folderName + "/" + photoList[1]).copy(Bitmap.Config.RGB_565, false);
        }
        Bitmap bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize *2/12]).copy(Bitmap.Config.RGB_565, false);
        int width = bitmap.getWidth() / 12;
        int height = bitmap.getHeight() / 12;
        int bigWidth = width * 32 / 10;
        int bigHeight = height * 22 / 10;
        int dWidth = (bigWidth - width * 3) / 7;
        int dHeight = (bigHeight - height * 2) / 5;
        Bitmap mergedBitmap = Bitmap.createBitmap(bigWidth, bigHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mergedBitmap);
        Paint paint = new Paint();
        paint.setColor(mActivity.getColor(R.color.folderDone));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, dWidth, dHeight, null);    // x--
        bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize *4/12]).copy(Bitmap.Config.RGB_565, false);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, width+dWidth*2, dHeight*2, null);   // -x-
        bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize*6/12]).copy(Bitmap.Config.RGB_565, false);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, width*2+dWidth*3, dHeight*3, null);  // --x
        bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize*7/12]).copy(Bitmap.Config.RGB_565, false);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, dWidth+dWidth, height+dHeight*2, null);    // y--
        bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize*8/12]).copy(Bitmap.Config.RGB_565, false);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, width+dWidth*3, height+dHeight*3, null);  // -y-
        bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize *10/12]).copy(Bitmap.Config.RGB_565, false);
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        canvas.drawBitmap(bitmap, width*2+dWidth*4, height+dHeight*4, null);  // --y
        return mergedBitmap;
    }
}