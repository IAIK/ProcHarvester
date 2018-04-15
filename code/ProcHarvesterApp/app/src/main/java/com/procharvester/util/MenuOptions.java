package com.procharvester.util;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.procharvester.Activities.CommandReceiveActivity;
import com.procharvester.R;
import com.procharvester.Services.RecordService;

public class MenuOptions {

    public static boolean onOptionsSharedItemSelected(Activity ac, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ac.finish();
                return true;
            case R.id.top_bar_start_logging:
                CommandReceiveActivity.launchInternalCommand(ac, CommandReceiveActivity.CMD_START_LOGGING, null);
                return true;
            default:
                return false;
        }
    }

    public static boolean onFileItemSelected(Context cx, MenuItem item, String fileName) {
        switch (item.getItemId()) {
            case R.id.topbar_export_matlab:
                DataExport.exportSingleFileForMatlab(cx, fileName);
                return true;
            case R.id.topbar_export_log_file:
                DataExport.exportRawFileContent(cx, fileName);
                return true;
            default:
                return false;
        }
    }

    public static void deleteAllFiles(Context cx) {
        for (File f : cx.getFilesDir().listFiles()) {
            if (!f.isDirectory()) {
                f.delete();
            }
        }
    }

    public static void stopLogging(Context cx) {
        RecordService service = RecordService.getInstance();
        if (service == null) {
            Boast.makeText(cx, "Service not running, can not stop").show();
        } else {
            service.stopLogging();
            Boast.makeText(cx, "Stopped logging").show();
        }
    }

    public static void deleteFileContent(Context cx, String fileName) {
        File temp = new File(cx.getFilesDir(), fileName);
        if (temp.exists()) {
            try {
                RandomAccessFile raf = new RandomAccessFile(temp, "rw");
                raf.setLength(0);
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
