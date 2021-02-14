package com.urrecliner.markupphoto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.urrecliner.markupphoto.Vars.mainActivity;
import static com.urrecliner.markupphoto.Vars.nowAddress;
import static com.urrecliner.markupphoto.Vars.nowPlace;
import static com.urrecliner.markupphoto.Vars.selectActivity;
import static com.urrecliner.markupphoto.GPSTracker.hLatitude;
import static com.urrecliner.markupphoto.GPSTracker.hLongitude;
import static com.urrecliner.markupphoto.Vars.iconNames;
import static com.urrecliner.markupphoto.Vars.iconRaws;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.placeInfos;
import static com.urrecliner.markupphoto.Vars.tvPlaceAddress;
import static com.urrecliner.markupphoto.Vars.utils;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder>  {

    static class PlaceHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress;
        ImageView ivIcon;
        View viewLine;

        PlaceHolder(View view) {
            super(view);
            this.viewLine = itemView.findViewById(R.id.recycler_layout);
            this.tvName = itemView.findViewById(R.id.recycler_PlaceName);
            this.tvAddress = itemView.findViewById(R.id.recycler_PlaceAddress);
            this.ivIcon = itemView.findViewById(R.id.recycler_icon);
            this.viewLine.setOnClickListener(view1 -> {
                int idx = getAdapterPosition();
                nowPlace = placeInfos.get(idx).oName;
                nowAddress = placeInfos.get(idx).oAddress;
                hLatitude = Double.parseDouble(placeInfos.get(idx).oLat);
                hLongitude = Double.parseDouble(placeInfos.get(idx).oLng);
                mainActivity.runOnUiThread(() -> {
                    String s = " \n"+nowPlace+"\n"+nowAddress;
                    tvPlaceAddress.setText(s);
                });
                selectActivity.finish();
            });
        }
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false);
        return new PlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder viewHolder, int position) {

        int icon = getIconRaw(placeInfos.get(position).oIcon);
        if (icon == -1) {
            String s = placeInfos.get(position).oIcon;
            utils.log("icon error "+s,"https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/"+s+"-71.png");
            Toast.makeText(mContext,"UnKnown Icon ["+s+"]",Toast.LENGTH_LONG).show();
            icon = iconRaws[0];
            placeInfos.get(position).setoName(placeInfos.get(position).oName+" "+s);
        }
        viewHolder.tvName.setText(placeInfos.get(position).oName);
        viewHolder.tvAddress.setText(placeInfos.get(position).oAddress);
        viewHolder.ivIcon.setImageResource(icon);
    }

    private int getIconRaw(String s) {
        for (int i = 0; i < iconNames.length; i++) {
            if (s.equals(iconNames[i]))
                return iconRaws[i];
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return (placeInfos == null) ? 0 : placeInfos.size();
    }

}
