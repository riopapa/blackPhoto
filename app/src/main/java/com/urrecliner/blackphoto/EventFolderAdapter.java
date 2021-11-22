package com.urrecliner.blackphoto;

import android.app.Dialog;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
                Intent intent = new Intent(mContext, PhotoSelect.class);
                mActivity.startActivity(intent);
            });

            itemView.setOnLongClickListener(view -> {
                currEventFolder = eventFolders.get(getAbsoluteAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Delete this event?");
                builder.setMessage(tvEventTIme.getText().toString());
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    deleteRecursive(currEventFolder);
                    eventFolders.remove(getAbsoluteAdapterPosition());
                    eventFolderAdapter.notifyDataSetChanged();
                });
                builder.setNegativeButton("No", (dialog, which) -> { });
                showPopup(builder);
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
    static void showPopup(AlertDialog.Builder builder) {
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

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folders_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String folder = eventFolders.get(position).toString();
        String s = folder.substring(38, 56);
        File files = eventFolders.get(position);
        File [] photoList = files.listFiles();
        assert photoList != null;
        int photoSize = photoList.length;
        s += " / "+photoSize;
        holder.tvEventTIme.setText(s);
        if (photoList.length > 50) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoList[photoSize/8].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image1.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeFile(photoList[photoSize/4].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image2.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeFile(photoList[photoSize/2].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image3.setImageBitmap(bitmap);
            bitmap = BitmapFactory.decodeFile(photoList[photoSize*3/4].toString()).copy(Bitmap.Config.RGB_565, false);
            holder.image4.setImageBitmap(bitmap);
        }
    }
}