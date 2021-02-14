package com.urrecliner.markupphoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.urrecliner.markupphoto.Vars.NO_MORE_PAGE;
import static com.urrecliner.markupphoto.Vars.pageToken;


public class PlaceParser {

    private HashMap<String, String> getOnePlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlaceList = new HashMap<>();
        String placeName = "--NA--";
        String vicinity= "--NA--";

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
                vicinity = vicinity.replace("KR","")
                        .replace("대한민국","").replace("서울특별시","");

            }

            String latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            String icon=googlePlaceJson.getString("icon");
            String []icons = icon.split("/");
            icon = icons[icons.length-1];
            icons = icon.split("-");
            icon = icons[0];

            googlePlaceList.put("name", placeName);
            googlePlaceList.put("vicinity", vicinity);
            googlePlaceList.put("lat", latitude);
            googlePlaceList.put("lng", longitude);
            googlePlaceList.put("icon", icon);

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceList;

    }
    private List<HashMap<String, String>> getAllPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String, String>> placelist = new ArrayList<>();
        HashMap<String, String> placeMap;

        for(int i = 0; i<count;i++)
        {
            try {
                placeMap = getOnePlace((JSONObject) jsonArray.get(i));
                placelist.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placelist;
    }

    public List<HashMap<String, String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
//        utils.log("json data", jsonData);

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            pageToken = (jsonObject.isNull("next_page_token")) ?
                    NO_MORE_PAGE :jsonObject.getString("next_page_token");
        } catch (JSONException e) {
            pageToken = NO_MORE_PAGE;
            e.printStackTrace();
        }
        assert jsonArray != null;
        return getAllPlaces(jsonArray);
    }
}
