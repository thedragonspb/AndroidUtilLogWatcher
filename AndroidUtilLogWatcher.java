package com.thedragonspb.mybraces;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 *    Need add 'android.permission.READ_LOGS' in manifest
 */
public class AndroidUtilLogWatcher extends Thread {

    private boolean watching = false;

    private final SimpleDateFormat logTimeFormat = new SimpleDateFormat("MM-dd hh:mm:ss.SS", Locale.UK);

    private LogWatcherListener logWatcherListener;

    @Override
    public void run() {
        super.run();

        Process logcat;
        BufferedReader br;

        String currentLog;
        String previousLog = "";
        Date previousLogTime = null;

        try {
            while (watching) {
                logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
                br = new BufferedReader(new InputStreamReader(logcat.getInputStream()));
                br.readLine();
                while ((currentLog = br.readLine()) != null) {
                    Date currentLogTime = logTimeFormat.parse(currentLog.substring(0, 18));
                    if (previousLogTime != null) {
                        if (currentLogTime.after(previousLogTime)
                        || currentLogTime.equals(previousLogTime)
                        ) {
                            if (!previousLog.equals(currentLog)) {
                                logWatcherListener.onListen(currentLog);
                            }
                            previousLog = currentLog;
                            previousLogTime = currentLogTime;
                        }
                    } else {
                        logWatcherListener.onListen(currentLog);
                        previousLogTime = currentLogTime;
                    }
                }
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logWatcherListener.onStop();
        }
    }

    public AndroidUtilLogWatcher setAndroidUtilLogWatcherListener(LogWatcherListener logWatcherListener) {
        this.logWatcherListener = logWatcherListener;
        return this;
    }

    public void startWatching() {
        if (logWatcherListener != null) {
            watching = true;
            start();
        }
    }

    public void stopWatching() {
        watching = false;
        if (logWatcherListener != null) {
            logWatcherListener.onStop();
        }
    }

    public interface LogWatcherListener {
        public void onListen(String log);
        public void onStop();
    }
}