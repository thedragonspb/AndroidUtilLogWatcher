### Java class for listening android.util.Log

##### Usage example

add 'android.permission.READ_LOGS' in manifest

        val androidUtilLogWatcher = AndroidUtilLogWatcher()
        
        androidUtilLogWatcher
            .setAndroidUtilLogWatcherListener(object : AndroidUtilLogWatcher.LogWatcherListener {
            
                override fun onListen(log: String) {
                    printWriter.write(log)
                    printWriter.write(separator)
                    printWriter.flush()
                }

                override fun onStop() {
                    printWriter.close()
                }
            })
            .startWatching()
            
        androidUtilLogWatcher.stopWatching()

