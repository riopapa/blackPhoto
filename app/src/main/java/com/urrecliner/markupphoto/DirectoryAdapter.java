package com.urrecliner.markupphoto;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.urrecliner.markupphoto.Vars.buildDB;
import static com.urrecliner.markupphoto.Vars.dirActivity;
import static com.urrecliner.markupphoto.Vars.dirNotReady;
import static com.urrecliner.markupphoto.Vars.dirFolders;
import static com.urrecliner.markupphoto.Vars.longFolder;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.multiMode;
import static com.urrecliner.markupphoto.Vars.sPref;
import static com.urrecliner.markupphoto.Vars.shortFolder;
import static com.urrecliner.markupphoto.Vars.squeezeDB;
import static com.urrecliner.markupphoto.Vars.utils;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    @Override
    public int getItemCount() {
        return dirFolders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iVImage;
        TextView tVInfo;

        ViewHolder(final View itemView) {
            super(itemView);
            tVInfo = itemView.findViewById(R.id.dirName);
            iVImage = itemView.findViewById(R.id.dirImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    SharedPreferences.Editor editor = sPref.edit();
                    shortFolder = dirFolders.get(pos).getShortFolder();
                    editor.putString("shortFolder", shortFolder);
                    longFolder = dirFolders.get(pos).getLongFolder();
                    editor.putString("longFolder", longFolder);
                    editor.apply();
                    editor.commit();
                    dirActivity.finish();
                    buildDB.cancel();
                    squeezeDB.cancel();
                    multiMode = false;
                    dirNotReady = false;
                    Intent intent = new Intent(mContext, MainActivity.class);
                    dirActivity.startActivity(intent);
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DirectoryFolder df = dirFolders.get(position);
        String shortFolder = df.getShortFolder();
        String longFolder = df.getLongFolder();
        String s = utils.getUpperFolder(longFolder, shortFolder);
        if (s.equals("0"))
            s = shortFolder + "("+df.getNumberOfPics()+")";
        else
            s += " /\n"+shortFolder + "("+df.getNumberOfPics()+")";
        holder.tVInfo.setText(s);
        holder.iVImage.setImageBitmap(df.getImageBitmap());
    }
}
