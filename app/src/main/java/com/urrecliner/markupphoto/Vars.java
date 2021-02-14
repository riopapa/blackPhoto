package com.urrecliner.markupphoto;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class Vars {
    static Context mContext;
    static Activity mainActivity;
    static Activity dirActivity;

    static PhotoAdapter photoAdapter;
    static DirectoryAdapter directoryAdapter;
    static Bitmap signatureMap;
    static RecyclerView photoView;

    static Utils utils;
    static SqueezeDB squeezeDB;
    static BuildDB buildDB;
    static MakeDirFolder makeDirFolder;
    static MarkUpOnePhoto markUpOnePhoto;
    static BuildBitMap buildBitMap;
    static ColorDraw colorDraw;

    static String shortFolder = null;
    static String longFolder = null;
    static int nowPos;
    static String nowPlace, nowAddress, nowLatLng;
    static int spanCount, spanWidth, sizeX;
    static String copyPasteText = null;
    static String copyPasteGPS;
    static boolean dirNotReady = true;

    static boolean multiMode = false;
    static List<Photo> photos = null;
    static ArrayList<DirectoryFolder> dirFolders = null;

    static SharedPreferences sharePrefer;
    static Menu mainMenu;

    static DatabaseIO databaseIO = null;
    static final String SUFFIX_JPG = "_ha.jpg";

    static ImageView colorPlate;
    static ImageView colorRange;
    static ImageView colorSpot;
    static SeekBar colorAlpha;
    static int colorRGB;
    static int markTextInColor;
    static int markTextOutColor;
    static TextView tvPlaceAddress;

    /* --- place select related variables --- */

    static String placeType = "";
    static String NO_MORE_PAGE = "<no>";
    static String pageToken = NO_MORE_PAGE;
    static boolean nowDownLoading = false;
    static ArrayList<PlaceInfo> placeInfos = null;
    static Activity selectActivity;
    static String [] iconNames = { "question",
            "airport", "amusement", "aquarium", "art_gallery", "atm", "baby",
            "bank_dollar", "bank_euro", "bank_pound", "bank_yen", "bar", "barber",
            "baseball", "beach", "bicycle", "bus", "cafe", "camping", "car_dealer",
            "car_repair", "casino", "civic_building", "convenience", "courthouse",
            "dentist", "doctor", "electronics", "fitness", "flower", "gas_station",
            "generic_business", "generic_recreational", "geocode", "golf", "government",
            "historic", "jewelry", "library", "lodging", "monument", "mountain",
            "movies", "museum", "pet", "police", "post_office", "repair", "restaurant",
            "school", "shopping", "ski", "stadium", "supermarket", "taxi", "tennis",
            "train", "travel_agent", "truck", "university", "wine", "worship_christian",
            "worship_general", "worship_hindu", "worship_islam", "worship_jewish", "zoo",
            "park", "bank","worship_dharma", "pharmacy"
    };
    static int [] iconRaws = { R.raw.question,
            R.raw.airport, R.raw.amusement, R.raw.aquarium, R.raw.art_gallery, R.raw.atm, R.raw.baby,
            R.raw.bank_dollar, R.raw.bank_euro, R.raw.bank_pound, R.raw.bank_yen, R.raw.bar, R.raw.barber,
            R.raw.baseball, R.raw.beach, R.raw.bicycle, R.raw.bus, R.raw.cafe, R.raw.camping, R.raw.car_dealer,
            R.raw.car_repair, R.raw.casino, R.raw.civic_building, R.raw.convenience, R.raw.courthouse,
            R.raw.dentist, R.raw.doctor, R.raw.electronics, R.raw.fitness, R.raw.flower, R.raw.gas_station,
            R.raw.generic_business, R.raw.generic_recreational, R.raw.geocode, R.raw.golf, R.raw.government,
            R.raw.historic, R.raw.jewelry, R.raw.library, R.raw.lodging, R.raw.monument, R.raw.mountain,
            R.raw.movies, R.raw.museum, R.raw.pet, R.raw.police, R.raw.post_office, R.raw.repair, R.raw.restaurant,
            R.raw.school, R.raw.shopping, R.raw.ski, R.raw.stadium, R.raw.supermarket, R.raw.taxi, R.raw.tennis,
            R.raw.train, R.raw.travel_agent, R.raw.truck, R.raw.university, R.raw.wine, R.raw.worship_christian,
            R.raw.worship_general, R.raw.worship_hindu, R.raw.worship_islam, R.raw.worship_jewish, R.raw.zoo,
            R.raw.park, R.raw.bank, R.raw.worship_dharma, R.raw.pharmacy
    };

}

