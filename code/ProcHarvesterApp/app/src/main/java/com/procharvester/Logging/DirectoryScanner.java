package com.procharvester.Logging;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;


public class DirectoryScanner {

    private static final String TAG = DirectoryScanner.class.getSimpleName();

    private final ArrayList<SideChannelReader> sideChannelReaders_;
    private final Context cx_;

    public DirectoryScanner(Context cx, ArrayList<SideChannelReader> readers) {
        sideChannelReaders_ = readers;
        cx_ = cx;
    }

    private int totalReadableFilesCnt_ = 0;
    private int permissionDeniedCnt_ = 0;

    public void scanDirRecursively(final String targetDir) {

        scanDirRecursively__(new File(targetDir));

        Log.e(TAG, "Read permission denied for " + permissionDeniedCnt_ + " files in target directory " + targetDir);
        Log.e(TAG, "Total number of readable files found: " + totalReadableFilesCnt_);
    }


    private void scanDirRecursively__(final File targetDir) {

        File[] files = scanTargetDirectory(targetDir);

        if (files == null)
            return;

        for (File f : files) {
            if (f.isDirectory()) {
                String dirName = f.getName();
                if (!isNumeric(dirName)) {
                    scanDirRecursively__(f);
                }
            }
        }
    }

    private boolean isNumeric(String str)
    {
        // return TextUtils.isDigitsOnly(str);
        try {
            Integer.valueOf(str);
        } catch (NumberFormatException ignored) {
            return false;
        }
        return true;
    }

    private boolean skipFile(String filePath) {
        for (String skipFile : Config.FILES_TO_SKIP) {
            if (filePath.equals(skipFile)) {
                Log.e(TAG, "Skip file " + filePath );
                return true;
            }
        }
        return false;
    }

    private File[] scanTargetDirectory(final File dir) {
        StringBuilder readableFiles = new StringBuilder();
        int readableFilesCnt = 0;

        File[] files = dir.listFiles();
        if (files == null)
            return null;

        for (File file : files) {
            if (file.isDirectory())
                continue;

            if (isNumeric(file.getName()))
                continue;

            String filePath = file.getPath();
            if (skipFile(filePath))
                continue;

            if (LogTarget.checkIfReadable(filePath)) {
                LogTarget target = new LogTarget(filePath, null, null);
                sideChannelReaders_.add(new WholeFileSideChannelReader(cx_, target));
                readableFiles.append("\n").append(filePath);
                totalReadableFilesCnt_++;
                readableFilesCnt++;
            } else {
                permissionDeniedCnt_++;
            }
        }

        if (readableFilesCnt > 0)
            Log.e(TAG, readableFilesCnt + " readable files found: " + readableFiles.toString());
        return files;
    }
}
