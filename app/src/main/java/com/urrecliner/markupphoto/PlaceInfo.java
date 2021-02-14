package com.urrecliner.markupphoto;

import static com.urrecliner.markupphoto.GPSTracker.oLatitude;
import static com.urrecliner.markupphoto.GPSTracker.oLongitude;

class PlaceInfo {
    String oName;
    String oAddress;
    String oIcon;
    String oLat;
    String oLng;
    String distance;
    Double lat, lng;    // derived from string

    public PlaceInfo(String oName, String oAddress, String oIcon, String oLat, String oLng) {
        this.oName = oName;
        this.oAddress = oAddress;
        this.oIcon = oIcon;
        this.oLat = oLat;
        this.oLng = oLng;
        lat = Double.parseDouble(oLat);
        lng = Double.parseDouble(oLng);
        distance = ((Math.sqrt((oLatitude-lat)*(oLatitude-lat)+(oLongitude-lng)*(oLongitude-lng))*1000L+1000L)+"");
    }

    public void setoName(String oName) {
        this.oName = oName;
    }
}
