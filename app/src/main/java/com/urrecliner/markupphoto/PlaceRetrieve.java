package com.urrecliner.markupphoto;

import android.widget.Toast;

import static com.urrecliner.markupphoto.Vars.NO_MORE_PAGE;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.nowDownLoading;
import static com.urrecliner.markupphoto.Vars.pageToken;
import static com.urrecliner.markupphoto.Vars.placeInfos;
import static com.urrecliner.markupphoto.Vars.placeType;

public class PlaceRetrieve {
    int shareRadius = 200;
    public void get(double lat, double lng) {
        nowDownLoading = true;

        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=").append(lat).append(",").append(lng);
        url.append("&radius=").append(shareRadius);
        url.append("&type=").append(placeType);
        url.append("&language=ko");
        if (!pageToken.equals(NO_MORE_PAGE)) {
            url.append("&pagetoken=").append(pageToken);
            Toast.makeText(mContext,"retrieving more places + "+placeInfos.size(),Toast.LENGTH_LONG).show();
        }
//        googlePlaceUrl.append("&fields=formatted_address,name,na");
        url.append("&key=").append(mContext.getString(R.string.maps_api_key));
        PlaceInfoBuild placeInfoBuild = new PlaceInfoBuild();
        placeInfoBuild.execute(url.toString());
    }

}
