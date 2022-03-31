package com.urrecliner.blackphoto;

import static com.urrecliner.blackphoto.Vars.snapDao;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class SqueezeDB {

    private List<SnapEntity> snapFolders;
    private List<SnapEntity> delSnaps;
    Timer timer = null;

    void run() {

        snapFolders = snapDao.getFolderList();
        if (snapFolders.size() == 0)
            return;
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                for (SnapEntity delFolder: snapFolders) {
                    File file = new File(delFolder.fullFolder);
                    if (!file.exists()) {
                        delSnaps = snapDao.getWithinFolder(delFolder.fullFolder);
                        for (SnapEntity sna: delSnaps) {
                            snapDao.delete(sna);
                        }
                    }
                }
                timer.cancel();
            }
        };
        timer.schedule(timerTask, 100, 100);
    }

}