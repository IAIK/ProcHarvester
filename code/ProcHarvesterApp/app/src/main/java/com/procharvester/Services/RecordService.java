package com.procharvester.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.procharvester.Logging.Config;
import com.procharvester.Logging.DirectoryScanner;
import com.procharvester.Logging.EventInfo;
import com.procharvester.Logging.LogTarget;
import com.procharvester.Logging.SideChannelReader;
import com.procharvester.util.Profiler;

import java.io.File;
import java.util.ArrayList;

public class RecordService extends IntentService {

    public static final String TAG = RecordService.class.getSimpleName();

    private static RecordService instance;

    public static RecordService getInstance() {
        return instance;
    }

    private volatile boolean shouldSelfKill_ = false;
    private volatile String targetLabel_ = null;

    public void stopLogging() {
        shouldSelfKill_ = true;
        instance = null;
    }

    public void triggerNewEvent(String targetLabel) {
        targetLabel_ = targetLabel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private ArrayList<SideChannelReader> sideChannelReaders;

    public RecordService() {
        super(RecordService.class.getSimpleName());
    }

    public static void startProcRecordService(Context context) {
        Intent intent = new Intent(context, RecordService.class);
        context.startService(intent);
    }

    void addSideChannelReaders(LogTarget[] logTargets) {
        for (LogTarget logTarget : logTargets) {
            SideChannelReader reader = logTarget.createReaderObject(this);
            if (reader != null)
                sideChannelReaders.add(reader);
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        instance = this;

        sideChannelReaders = new ArrayList<>();
        addSideChannelReaders(Config.LOG_TARGETS);
        addSideChannelReaders(Config.WHOLE_FILE_TARGETS);

        DirectoryScanner directoryScanner = new DirectoryScanner(this, sideChannelReaders);
        for (String target : Config.RECURSIVE_TARGETS) {
                directoryScanner.scanDirRecursively(target);
        }

        // deleteOutdatedFiles(); we do not want to do this for now

        // Main loop
        while (true) {
            if (shouldSelfKill_) {
                return;
            }
            Profiler.startMeasurement("pollSideChannelVectors");
            pollSideChannelVectors();
            Profiler.stopMeasurement();
        }
    }

    boolean isValidFileName(String fileName) {
        for (SideChannelReader sideChannelReader : sideChannelReaders) {
            String logFileName = sideChannelReader.getLogTarget().getLogFileName();
            if (logFileName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    void deleteOutdatedFiles() {
        for (File f : getFilesDir().listFiles()) {
            if (!f.isDirectory() && !isValidFileName(f.getName())) {
                f.delete();
            }
        }
    }

    void pollSideChannelVectors() {

        String newTargetLabel = targetLabel_;
        if (newTargetLabel != null) {
            targetLabel_ = null;
        }

        for (SideChannelReader sideChannelReader : sideChannelReaders) {

            // Profiler.startMeasurement("Read side channels from " + sideChannelReader.getLogTarget().getLogFileName());
            if (newTargetLabel != null) {
                sideChannelReader.issueNewEvent(new EventInfo(newTargetLabel));
            } else {
                sideChannelReader.updateCurrentEventData();
            }
            // Profiler.stopMeasurement();

        }
    }
}
