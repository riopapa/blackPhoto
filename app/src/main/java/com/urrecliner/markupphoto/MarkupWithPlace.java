package com.urrecliner.markupphoto;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.urrecliner.markupphoto.placeNearby.PlaceRetrieve;

import androidx.exifinterface.media.ExifInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.urrecliner.markupphoto.Vars.SUFFIX_JPG;
import static com.urrecliner.markupphoto.Vars.buildDB;
import static com.urrecliner.markupphoto.Vars.byPlaceName;
import static com.urrecliner.markupphoto.Vars.copyPasteGPS;
import static com.urrecliner.markupphoto.Vars.copyPasteText;
import static com.urrecliner.markupphoto.Vars.databaseIO;
import static com.urrecliner.markupphoto.Vars.longFolder;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.mActivity;
import static com.urrecliner.markupphoto.Vars.markUpOnePhoto;
import static com.urrecliner.markupphoto.Vars.nowDownLoading;
import static com.urrecliner.markupphoto.Vars.nowPlace;
import static com.urrecliner.markupphoto.Vars.nowPos;
import static com.urrecliner.markupphoto.Vars.nowLatLng;
import static com.urrecliner.markupphoto.Vars.photoAdapter;
import static com.urrecliner.markupphoto.Vars.photoView;
import static com.urrecliner.markupphoto.Vars.photos;
import static com.urrecliner.markupphoto.Vars.placeActivity;
import static com.urrecliner.markupphoto.Vars.placeInfos;
import static com.urrecliner.markupphoto.Vars.placeRetrieve;
import static com.urrecliner.markupphoto.Vars.placeType;
import static com.urrecliner.markupphoto.Vars.sharedRadius;
import static com.urrecliner.markupphoto.Vars.tvPlaceAddress;
import static com.urrecliner.markupphoto.Vars.typeAdapter;
import static com.urrecliner.markupphoto.Vars.typeIcons;
import static com.urrecliner.markupphoto.Vars.typeInfos;
import static com.urrecliner.markupphoto.Vars.typeNames;
import static com.urrecliner.markupphoto.Vars.typeNumber;
import static com.urrecliner.markupphoto.Vars.utils;
import static com.urrecliner.markupphoto.placeNearby.PlaceParser.pageToken;

public class MarkupWithPlace extends AppCompatActivity {

    ExifInterface exif = null;
    String strAddress = null, strPlace = null, strPosition = null;
    String dateTimeColon, dateTimeFileName = null;
    Date photoDate;
    String maker, model, Orientation, longitudeR, longitudeStr, latitudeR, latitudeStr, altitudeStr, altitudeR;
    double latitude, longitude, altitude;
    File fileFullName;
    int orientation;
    Photo photo;
    Bitmap bitmap;
    Menu menuPlace;

    static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US);
    static final SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markup);
        placeActivity = this;
        tvPlaceAddress = findViewById(R.id.placeAddress);
        typeInfos = new ArrayList<>();
        for (int i = 0; i < typeNames.length; i++) {
            typeInfos.add(new TypeInfo(typeNames[i], typeIcons[i]));
        }

        RecyclerView typeRecyclerView = findViewById(R.id.type_recycler);
//        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager mLinearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        typeRecyclerView.setLayoutManager(mLinearLayoutManager);
        typeAdapter = new TypeAdapter(typeInfos);
        typeRecyclerView.setAdapter(typeAdapter);

        photo = photos.get(nowPos);
        photo.setChecked(false);
        photos.set(nowPos, photo);
        photoAdapter.notifyItemChanged(nowPos, photo);
        utils.showFolder(this.getSupportActionBar());

        buildPhotoScreen();
    }

    void buildPhotoScreen() {
        photo = photos.get(nowPos);
        fileFullName = photo.getFullFileName();
        if (!fileFullName.exists())
            return;
        ImageView iv = findViewById(R.id.image);
        bitmap = BitmapFactory.decodeFile(fileFullName.getAbsolutePath());
        getPhotoExif(fileFullName);
        orientation = Integer.parseInt(Orientation);
        photo.setOrientation(orientation);
        if (orientation != 1) {
            if (orientation == 6) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                orientation = 1;
            }
        }
        iv.setImageBitmap(bitmap);
        PhotoViewAttacher pA;       // to enable zoom
        pA = new PhotoViewAttacher(iv);
        pA.update();
        getLocationInfo();
        TextView tv = findViewById(R.id.photoName);
        tv.setText(fileFullName.getName());
        ImageView iVPlace = findViewById(R.id.getLocation);
        iVPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeInfos = new ArrayList<>();
                getPlaceByLatLng();
            }
        });
//        iVPlace.setImageBitmap(utils.maskedIcon(typeIcons[typeNumber]));
        iVPlace.setImageResource(typeIcons[typeNumber]);

        ImageView iVInfo = findViewById(R.id.getInformation);
        iVInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, buildLongInfo(), Toast.LENGTH_LONG).show();
            }
        });
        FloatingActionButton fab = findViewById(R.id.rotate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItem item = menuPlace.findItem(R.id.saveRotate);
                item.setEnabled(true);
                item.getIcon().setAlpha(255);
                ImageView iv = findViewById(R.id.image);
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                iv.setImageBitmap(bitmap);
                PhotoViewAttacher pA;       // to enable zoom
                pA = new PhotoViewAttacher(iv);
                pA.update();
            }
        });

        final ImageView ivL = findViewById(R.id.imageL);
        if (nowPos > 0) {
            ivL.setImageBitmap(photos.get(nowPos-1).getBitmap());
            ivL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nowPos--;
                    buildPhotoScreen();
                }
            });
            ivL.setVisibility(View.VISIBLE);
        } else
            ivL.setVisibility(View.INVISIBLE);

        final ImageView ivR = findViewById(R.id.imageR);
        if (nowPos < photos.size()-1) {
            ivR.setImageBitmap(photos.get(nowPos+1).getBitmap());
            ivR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nowPos++;
                    buildPhotoScreen();
                }
            });
            ivR.setVisibility(View.VISIBLE);
        }
        else
            ivR.setVisibility(View.INVISIBLE);
        utils.deleteOldSAVFiles();

    }

    private void getLocationInfo() {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        latitude = convertDMS(latitudeStr, latitudeR);
        longitude = convertDMS(longitudeStr, longitudeR);
        altitude = convertALT(altitudeStr, altitudeR);
        strPlace = "";
        nowLatLng = String.format(Locale.ENGLISH, "%.5f ; %.5f ; %.1f", latitude, longitude, altitude);
        strAddress = GPS2Address.get(geocoder, latitude, longitude);
        EditText et = findViewById(R.id.placeAddress);
        String text = "\n"+strAddress;
        et.setText(text);
        et.setSelection(text.indexOf("\n"));
    }

//    final int REQUEST_PLACE_PICKER = 11;
    private void getPlaceByLatLng() {
        nowDownLoading = true;
        EditText et = findViewById(R.id.placeAddress);
        String placeName = et.getText().toString();
        if (placeName != null && placeName.startsWith("?")) {
            String[] placeNames = placeName.split("\n");
            byPlaceName = placeNames[0].substring(1);
        } else
            byPlaceName = "";
        new PlaceRetrieve(mContext, latitude, longitude, placeType, pageToken, sharedRadius, byPlaceName);
        new Timer().schedule(new TimerTask() {
            public void run() {
                selectPlace();
            }
        }, 2000);
    }

    private void selectPlace() {
        Intent intent = new Intent(mContext, SelectActivity.class);
        startActivity(intent);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == RESULT_OK)) {
//            NearByPlacePicker.Companion companion = NearByPlacePicker.Companion;
//            com.google.android.libraries.places.api.model.Place place = companion.getPlace(data);
//            if (place != null) {
//                strPlace = place.getName();
//                String text = place.getAddress();
//                if (text.length() > 5)
//                    strAddress = text.replace("대한민국 ", "");
//                EditText et = findViewById(R.id.placeAddress);
//                text = strPlace + "\n\n" + strAddress;
//                et.setText(text);
//                et.setSelection(text.indexOf("\n") + 1);
//                latitude = place.getLatLng().latitude;
//                longitude = place.getLatLng().longitude;
//            }
//        }
//    }

    private void getPhotoExif(File fileFullName) {
        try {
            exif = new ExifInterface(fileFullName.getAbsolutePath());
        } catch (Exception e) {
            utils.log("1",e.toString());
            e.printStackTrace();
        }
        maker = exif.getAttribute(ExifInterface.TAG_MAKE);
        model = exif.getAttribute(ExifInterface.TAG_MODEL);
        Orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        longitudeR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        longitudeStr = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        latitudeR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        latitudeStr = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        altitudeR = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
        altitudeStr = exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
        if (altitudeStr == null) {
            altitudeStr = ""; altitudeR = "";
        }
        dateTimeColon = exif.getAttribute(ExifInterface.TAG_DATETIME);
        if (dateTimeColon != null) {
            try {
                photoDate = sdfDate.parse(dateTimeColon);
            } catch (Exception e) {
                Log.e("Exception", "on date "+ dateTimeColon);
                photoDate = new Date(fileFullName.lastModified());
            }
        }
        else {
            photoDate = new Date(fileFullName.lastModified());
        }
        dateTimeFileName = sdfFile.format(photoDate.getTime());
    }

    private String buildLongInfo() {

        return "Directory : "+longFolder+"\nFile Name : "+fileFullName.getName()+
                "\nDevice: "+maker+" - "+model+"\nOrientation: "+orientation+
                "\nLocation: "+latitudeStr+latitudeR+", "+longitudeStr+longitudeR+", "+(altitudeR.equals("0")? "":"-")+altitudeStr+
                "\nDate Time: "+dateTimeFileName+
                "\nSize: "+bitmap.getWidth()+" x "+bitmap.getHeight();
    }

//    @Override
//    protected void oxnActivityResult(int requestCode, int resultCode, Intent data) {
//
//        utils.log("A","requestCode="+requestCode+" result="+resultCode);
//        if (resultCode == RESULT_OK) {  // user picked up place within the google map list
//            Place place = PlacePicker.getPlace(this, data);
//            strPlace = place.getName().toString();
//            String text = place.getAddress().toString();
//            if (text.length() > 5)
//                strAddress = text.replace("대한민국 ", "");
//            EditText et = findViewById(R.id.placeAddress);
//            text = strPlace + "\n\n" + strAddress;
//            et.setText(text);
//            et.setSelection(text.indexOf("\n") + 1);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    double convertDMS (String dmsString, String NEWS) {
        if (dmsString != null) {
            String[] dms = dmsString.split(",");
            if (dms.length == 3) {
                double degree = Double.parseDouble(dms[0].substring(0, dms[0].length() - 2));
                double min = Double.parseDouble(dms[1].substring(0, dms[1].length() - 2));
                double sec = Double.parseDouble(dms[2].substring(0, dms[2].indexOf("/")));
                double secDiv = Double.parseDouble(dms[2].substring(dms[2].indexOf("/") + 1));
                sec /= secDiv;
                double result = degree + min / 60f + sec / 3600f;
                if (NEWS.equals("S") || NEWS.equals("W"))
                    result *= -1;
                return result;
            } else
                return 0;
        }
        else
            return 0;
    }

    double convertALT(String altString, String UpDown) {
        if (altString.length() > 1) {
            double val = Double.parseDouble(altString.substring(0, altString.indexOf("/"))) /
                    Double.parseDouble(altString.substring(altString.indexOf("/")+1));
            return (UpDown == null || UpDown.equals("0")) ? val: -val;
        }
        else
            return 0;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuPlace = menu;
        MenuItem item;
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        String fileName = photos.get(nowPos).getShortName();
        if (fileName.substring(fileName.length()-7).equals(SUFFIX_JPG)) {
            item = menu.findItem(R.id.markWithPlace);
            item.setEnabled(false);
            item.getIcon().setAlpha(40);
        }
        else {
            item = menu.findItem(R.id.markWithPlace);
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        }
        item = menu.findItem(R.id.saveRotate);
        item.setEnabled(false);
        item.getIcon().setAlpha(40);
        item = menu.findItem(R.id.pasteText);
//        Log.w("text",copyPasteText+" "+copyPasteGPS);
//        item.setVisible((copyPasteText == null));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        EditText etPlace = findViewById(R.id.placeAddress);
        Photo photo = photos.get(nowPos);
        File orgFileName, tgtFileName;

        switch (item.getItemId()) {

            case R.id.saveRotate:
                orgFileName = photo.getFullFileName();
                tgtFileName = new File (orgFileName.toString().replace(photo.getShortName(),""), dateTimeFileName +".jpg.sav");
                tgtFileName.delete();
                orgFileName.renameTo(tgtFileName);
                databaseIO.delete(orgFileName);
                String outName = orgFileName.toString();
                orientation = 1; // (bitmap.getWidth() > bitmap.getHeight()) ? 1:6;
                utils.makeBitmapFile(orgFileName, outName, bitmap, orientation);
                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(orgFileName)));
                photo.setOrientation(orientation);
                photo.setChecked(false);
                photo.setBitmap(null);
                photos.set(nowPos, photo);
                photoAdapter.notifyItemChanged(nowPos, photo);
                finish();
                return true;

            case R.id.copyText:
                copyPasteText = etPlace.getText().toString();
                copyPasteGPS = longitudeR+";"+longitudeStr+";"+latitudeR+";"+latitudeStr+";"+altitudeR+";"+altitudeStr;
                Toast.makeText(mContext, "Text Copied\n"+copyPasteText,Toast.LENGTH_SHORT).show();
                MenuItem itemP = menuPlace.findItem(R.id.pasteText);
                itemP.setTitle("Paste <"+copyPasteText+">");
                return true;

            case R.id.pasteText:
                etPlace.setText(copyPasteText);
                return true;

            case R.id.markWithPlace:
                nowPlace = etPlace.getText().toString();
                if (nowPlace.length() > 5) {
                    Photo nPhoto = new Photo(markUpOnePhoto.insertGeoInfo(photo));
                    String nFileName = nPhoto.getFullFileName().toString();
                    if (photos.get(nowPos-1).getFullFileName().toString().equals(nFileName)) {
                        removeItemView(nowPos-1);
                        databaseIO.delete(nPhoto.getFullFileName());
                        nowPos--;
                    };

                    nPhoto.setBitmap(null);
                    nPhoto.setOrientation(photo.getOrientation());
                    nPhoto = buildDB.getPhotoWithMap(nPhoto);
                    photos.add(nowPos, nPhoto);
                    photoAdapter.notifyItemInserted(nowPos);
                    photoAdapter.notifyItemChanged(nowPos, nPhoto);
                    photoAdapter.notifyItemChanged(nowPos+1);
                    finish();
                }
                return true;

            case R.id.markDelete:
                nowPlace = null;
                deleteOnConfirm(nowPos);
                finish();
                return true;

            case R.id.renameClock:
                photo.setChecked(false);
                orgFileName = photo.getFullFileName();
                String newName = orgFileName.toString().replace(photo.getShortName(),"");
                int C = 67; // 'C'
                do {
                    tgtFileName = new File(newName, dateTimeFileName + (char)C + ".jpg");
                    if (!tgtFileName.exists())
                        break;
                    C++;
                } while (C < 84);
                orgFileName.renameTo(tgtFileName);
                photo.setFullFileName(tgtFileName);
                photos.set(nowPos, photo);
                photoAdapter.notifyItemChanged(nowPos, photo);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void deleteOnConfirm(int position) {
        final int pos = position;
        final Photo photo = photos.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Delete photo ?");
        builder.setMessage(photo.getShortName());
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        File file = photo.getFullFileName();
                        if (file.delete()) {
                            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                            removeItemView(pos);
                        }
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        MainActivity.showPopup(builder);
    }

    void removeItemView(int position) {
        photos.remove(position);
        photoAdapter.notifyItemRemoved(position);
        photoAdapter.notifyItemRangeChanged(position, photos.size());
        photoAdapter.notifyItemChanged(position);
        SystemClock.sleep(100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        int pos = (nowPos > 3) ? nowPos-3:0;
        photoView.scrollToPosition(pos);
    }
}
