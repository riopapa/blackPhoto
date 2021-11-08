package com.urrecliner.blackphoto;

import android.app.Dialog;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        ViewHolder(final View itemView) {
            super(itemView);
            tvEventTIme = itemView.findViewById(R.id.eventTime);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_folder_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String folder = eventFolders.get(position).toString();
        folder = folder.substring(38, 56);
        holder.tvEventTIme.setText(folder);
    }
}