package com.urrecliner.markupphoto;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.urrecliner.markupphoto.placeNearby.PlaceRetrieve;

import java.util.Timer;
import java.util.TimerTask;

import static com.urrecliner.markupphoto.GPSTracker.hLatitude;
import static com.urrecliner.markupphoto.GPSTracker.hLongitude;
import static com.urrecliner.markupphoto.Vars.mContext;
import static com.urrecliner.markupphoto.Vars.nowDownLoading;
import static com.urrecliner.markupphoto.Vars.placeInfos;
import static com.urrecliner.markupphoto.Vars.placeType;
import static com.urrecliner.markupphoto.Vars.selectActivity;
import static com.urrecliner.markupphoto.Vars.sharedRadius;
import static com.urrecliner.markupphoto.Vars.utils;
import static com.urrecliner.markupphoto.placeNearby.PlaceParser.pageToken;
import static com.urrecliner.markupphoto.placeNearby.PlaceParser.NO_MORE_PAGE;

public class SelectActivity extends AppCompatActivity {

    static CountDownTimer waitTimer = null;
    RecyclerView placeRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        selectActivity = this;
        placeRecycleView = findViewById(R.id.place_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        placeRecycleView.setLayoutManager(mLinearLayoutManager);

        waitTimer = new CountDownTimer(20000, 200) {
            public void onTick(long millisUntilFinished) {
                if (!nowDownLoading) {
                    waitTimer.cancel();
                    if (!pageToken.equals(NO_MORE_PAGE)) {
                        new PlaceRetrieve(mContext, hLatitude, hLongitude, placeType, pageToken, placeInfos.size(), sharedRadius);
                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                waitTimer.start();
                            }
                        }, 2000);
                    } else {
                        placeInfos.sort((arg0, arg1) -> arg0.getDistance().compareTo(arg1.getDistance()));
//                        placeInfos.sort((arg0, arg1) -> arg0.oName.compareTo(arg1.oName));
                        String s = "Total "+placeInfos.size()+" places retrieved";
                        utils.log("LIST", s);
                        Toast.makeText(mContext,s, Toast.LENGTH_LONG).show();
                        PlaceAdapter placeAdapter = new PlaceAdapter();
                        placeRecycleView.setAdapter(placeAdapter);
                    }
                }
            }
            public void onFinish() { }
        }.start();
    }
}
