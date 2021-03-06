package com.urrecliner.markupphoto.placeNearby;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.urrecliner.markupphoto.Vars.nowDownLoading;
import static com.urrecliner.markupphoto.Vars.placeInfos;

public class PlaceInfoBuild extends AsyncTask<String, String, String> {

    private String googlePlaceString;

    @Override
    protected String doInBackground(String... strings){
        String url = (String)strings[0];

        PlaceDownload placeDownload = new PlaceDownload();
        try {
            googlePlaceString = placeDownload.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceString;
    }

    public PlaceParser parser;

    @Override
    protected void onPostExecute(String s) {

        Log.w("onPostExecute", " getPlacesData");
        List<HashMap<String, String>> nearbyPlaceList;
        parser = new PlaceParser();
        nearbyPlaceList = parser.parse(s);

        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            HashMap<String, String> hashMap = nearbyPlaceList.get(i);
            placeInfos.add(new PlaceInfo(hashMap.get("name"), hashMap.get("vicinity"),
                    hashMap.get("icon"), hashMap.get("lat"), hashMap.get("lng")));
        }

        nowDownLoading = false;
    }
}
