package com.urrecliner.markupphoto;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import static com.urrecliner.markupphoto.Vars.sharedAutoLoad;
import static com.urrecliner.markupphoto.Vars.sharedRadius;
import static com.urrecliner.markupphoto.Vars.sharedSort;
import static com.urrecliner.markupphoto.Vars.sharedSpan;
import static com.urrecliner.markupphoto.Vars.utils;

public class SettingsActivity extends AppCompatActivity  {

    static Preference pRadius, pSort, pAutoLoad, pSpan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        final String RADIUS = "radius", SORT = "sort", AUTO_LOAD = "autoLoad", SPAN = "span";
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.set_preferences, rootKey);

            final String fRadius = "반경 %s m내의 장소를 검색합니다";
            pRadius = findPreference(RADIUS);
            pRadius.setSummary(String.format(fRadius, sharedRadius));
            pRadius.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedRadius = newValue.toString();
                pRadius.setSummary(String.format(fRadius, sharedRadius));
                return true;
            });
            pSort = findPreference(SORT);
            pSort.setSummary((sharedSort.equals("none")) ? "정열하지 않고 보여 줍니다":sharedSort + " 순으로 보여 줍니다");
            pSort.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedSort = newValue.toString();
                pSort.setSummary((sharedSort.equals("none")) ? "정열하지 않고 보여 줍니다":sharedSort + " 순으로 보여 줍니다");
                return true;
            });

            pAutoLoad = findPreference(AUTO_LOAD);
            pAutoLoad.setSummary(sharedAutoLoad ? "자동 로드 안 함" : "사진 선택하면 자동 로드");
            pAutoLoad.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedAutoLoad = Boolean.parseBoolean(newValue.toString());
                pAutoLoad.setSummary(sharedAutoLoad ? "자동 로드 안 함" : "사진 선택하면 자동 로드");
                return true;
            });

            pSpan = findPreference(SPAN);
            pSpan.setSummary("한 줄에 "+(sharedSpan.equals("2") ? "두": "세")+" 사진을 보여줍니다");
            pSpan.setOnPreferenceChangeListener((preference, newValue) -> {
                sharedSpan = newValue.toString();
                pSpan.setSummary("한 줄에 "+(sharedSpan.equals("2") ? "두": "세")+" 사진을 보여줍니다");
                return true;
            });
        }

    }

    @Override
    protected void onDestroy() {
        utils.getPreference();
        MainActivity.prepareCards();
        super.onDestroy();
    }

}