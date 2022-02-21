package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.eventBitmaps;
import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolders;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.utils;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class EventFolderAdapter extends RecyclerView.Adapter<EventFolderAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return eventFolders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventTIme;
        ImageView image1;

        ViewHolder(final View itemView) {
            super(itemView);
            tvEventTIme = itemView.findViewById(R.id.eventTime);
            image1 = itemView.findViewById(R.id.photoImage1);
            itemView.setOnClickListener(view -> {
                currEventFolder = eventFolders.get(getAbsoluteAdapterPosition());
                Intent intent = new Intent(mContext, SnapSelect.class);
                mActivity.startActivity(intent);
            });

            itemView.setOnLongClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                currEventFolder = eventFolders.get(pos);
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Delete this event?");
                builder.setMessage(tvEventTIme.getText().toString());
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    utils.deleteFolder(currEventFolder);
                    eventFolders.remove(getAbsoluteAdapterPosition());
                    eventBitmaps.remove(getAbsoluteAdapterPosition());
                    eventFolderAdapter.notifyItemRemoved(pos);
                });
                builder.setNegativeButton("No", (dialog, which) -> { });
                showYesNoPopup(builder);
//                String deleteCmd = "rm -r \"" + currEventFolder.getAbsolutePath() + "\"";
//                Runtime runtime = Runtime.getRuntime();
//                try {
//                    runtime.exec(deleteCmd);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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

//    public static void deleteRecursive(File fileOrDirectory) {
//        if (fileOrDirectory.isDirectory())
//            deleteFolder(fileOrDirectory);
////            for (File child : fileOrDirectory.listFiles())
////                deleteRecursive(child);
//        else
//            fileOrDirectory.delete();
//    }

//    public static void deleteFolder(File file) {
//        String deleteCmd = "rm -r " + file.toString();
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            runtime.exec(deleteCmd);
//        } catch (IOException e) { }
//    }
//

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File oneEventFolder = eventFolders.get(position);
        String folderName = oneEventFolder.toString();
        String [] photoList = oneEventFolder.list();
        assert photoList != null;
        int photoSize = photoList.length;
        String showName = folderName.substring(38, 56) + " / "+photoSize;
        holder.tvEventTIme.setText(showName);
        if (eventBitmaps.get(position) ==  null && photoList.length > 30) {
            Bitmap bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize / 8]).copy(Bitmap.Config.RGB_565, false);
            int width = bitmap.getWidth() / 4;
            int height = bitmap.getHeight() / 4;
            int bigWidth = width * 22 / 10;
            int bigHeight = height * 22 / 10;
            int dWidth = bigWidth - width * 2;
            int dHeight = bigHeight - height * 2;
            Bitmap mergedBitmap = Bitmap.createBitmap(bigWidth, bigHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mergedBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(bitmap, 0f, 0f, null);
            bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize / 4]).copy(Bitmap.Config.RGB_565, false);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(bitmap, bigWidth-width-dWidth/2, dHeight/2, null);
            bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize / 2]).copy(Bitmap.Config.RGB_565, false);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(bitmap, dWidth/4, height+dHeight*3/4, null);
            bitmap = BitmapFactory.decodeFile(folderName + "/" + photoList[photoSize * 3 / 4]).copy(Bitmap.Config.RGB_565, false);
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            canvas.drawBitmap(bitmap, bigWidth-width, bigHeight-height, null);
            eventBitmaps.set(position,mergedBitmap);
        }
        if (eventBitmaps.get(position) != null)
            holder.image1.setImageBitmap(eventBitmaps.get(position));
    }
}