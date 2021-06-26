package com.urrecliner.blackphoto;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.urrecliner.blackphoto.Vars.currEventFolder;
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
                currEventFolder = eventFolders.get(getAdapterPosition());
                Toast.makeText(mContext,currEventFolder.getName().substring(0, 18)+" selected", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, PhotoSelect.class);
                mActivity.startActivity(intent);
            });
        }
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
