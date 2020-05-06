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

        List<String> logsBuffer = new LinkedList<>();

        String currentLog;
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
                            if (!logsBuffer.contains(currentLog)) {
                                logsBuffer.add(currentLog);                                
                                logWatcherListener.onListen(currentLog);
                            }
                            
                            if (logsBuffer.size() > 1000) {
                                logsBuffer.remove(0);
                            }
                            
                            previousLogTime = currentLogTime;
                        }
                    } else {
                        logWatcherListener.onListen(currentLog);
                        previousLogTime = currentLogTime;
                    }
                }
                logsBuffer.clear();
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopWatching();
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
