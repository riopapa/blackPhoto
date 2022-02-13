package com.urrecliner.blackphoto;

import android.app.Dialog;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import static com.urrecliner.blackphoto.Vars.currEventFolder;
import static com.urrecliner.blackphoto.Vars.eventFolderAdapter;
import static com.urrecliner.blackphoto.Vars.eventFolders;
import static com.urrecliner.blackphoto.Vars.mActivity;
import static com.urrecliner.blackphoto.Vars.mContext;
import static com.urrecliner.blackphoto.Vars.utils;

public class EventFolderAdapter extends RecyclerView.Adapter<EventFolderAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return eventFolders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventTIme;
        ImageView image1, image2, image3, image4;

        ViewHolder(final View itemView) {
            super(itemView);
            tvEventTIme = itemView.findViewById(R.id.eventTime);
            image1 = itemView.findViewById(R.id.photoImage1);
            image2 = itemView.findViewById(R.id.photoImage2);
            image3 = itemView.findViewById(R.id.photoImage3);
            image4 = itemView.findViewById(R.id.photoImage4);
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
        if (photoList.length > 50) {
            Bitmap bitmap = BitmapFactory.decodeFile(folderName+"/"+photoList[photoSize/8].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image1.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeFile(folderName+"/"+photoList[photoSize/4].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image2.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeFile(folderName+"/"+photoList[photoSize/2].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image3.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeFile(folderName+"/"+photoList[photoSize*3/4].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image4.setImageBitmap(bitmap);
        }
    }
}