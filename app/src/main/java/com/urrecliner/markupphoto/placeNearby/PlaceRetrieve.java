package com.urrecliner.markupphoto.placeNearby;

import android.content.Context;
import android.widget.Toast;

import com.urrecliner.markupphoto.R;


public class PlaceRetrieve {

    public PlaceRetrieve(Context mContext, double latitude, double longitude, String placeType, String pageToken, int size, int sharedRadius) {

        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location=").append(latitude).append(",").append(longitude);
        url.append("&radius=").append(sharedRadius);
        url.append("&fields=formatted_address,name,geometry");
        if (!placeType.equals("all"))
            url.append("&type=").append(placeType);
        url.append("&language=ko");
        if (pageToken != null && !pageToken.equals("no more")) {
            url.append("&pagetoken=").append(pageToken);
            Toast.makeText(mContext,"retrieving more places "+size,Toast.LENGTH_LONG).show();
        }
//        googlePlaceUrl.append("&fields=formatted_address,name,na");
        url.append("&key=").append(mContext.getString(R.string.maps_api_key));
        PlaceInfoBuild placeInfoBuild = new PlaceInfoBuild();
        placeInfoBuild.execute(url.toString());
    }

}
