package com.procharvester.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.procharvester.Logging.Event;
import com.procharvester.Logging.LogFile;
import com.procharvester.Logging.LogParser;
import com.procharvester.Logging.LogValue;

public class DataExport {
    private static final String TAG = DataExport.class.getSimpleName();

    private static final String DELIMITER = ", ";
    private static final String TIME_STAMP_SEPERATOR = "|";

    private static File saveMatlabTable(Context cx, String fileName) {


        ArrayList<Event> events = LogParser.readEventsFromFile(cx, fileName);

        StringBuilder matLabTable = new StringBuilder();

        for (Event event : events) {
            if (event.logValues_.isEmpty()) {
                continue;
            }
            final long firstTimeStamp = event.logValues_.get(0).timeStamp;

            for (LogValue logValue : event.logValues_) {
                final long relativeTimeStamp = logValue.timeStamp - firstTimeStamp + 1; // prevent zeros in matlab
                matLabTable.append(logValue.value + TIME_STAMP_SEPERATOR + relativeTimeStamp + DELIMITER);
            }
            matLabTable.append(event.getTargetLabel() + DELIMITER);
            matLabTable.append("\n");
        }
        return storeAsTemporaryFile(cx, fileName, matLabTable.toString());
    }

    public static void exportSingleFileForMatlab(Context cx, String fileName) {

        File file = saveMatlabTable(cx, fileName);

        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri fileProviderUri = FileProvider.getUriForFile(
                cx, cx.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(Intent.EXTRA_STREAM, fileProviderUri);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cx.startActivity(intent);
    }

    public static void exportAllFilesForMatlab(Context cx) {

        String dirPath = cx.getFilesDir().getAbsolutePath();
        File fDir = new File(dirPath);
        File[] files = fDir.listFiles();

        ArrayList<Uri> exportUris = new ArrayList<>();
        for (File f : files) {
            if (!f.isDirectory()) {
                File exportFile = saveMatlabTable(cx, f.getName());
                Uri fileProviderUri = FileProvider.getUriForFile(
                        cx, cx.getApplicationContext().getPackageName() + ".provider", exportFile);
                exportUris.add(fileProviderUri);
            }
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Export all record files");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, exportUris);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cx.startActivity(intent);
    }

    private static File storeAsTemporaryFile(Context cx, String fileName, String content) {
        File file = new File(cx.getCacheDir(), fileName + ".txt");
        saveAndOverwriteFile(cx, file, content);
        return file;
    }

    private static void saveAndOverwriteFile(Context cx, File file, String content) {
        if (file.exists ()) {
            file.delete ();
        }
        try {
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.append(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Toast.makeText(cx, "Failed to save file " + file, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void exportRawFileContent(Context cx, String fileName) {
        sendStringWithIntent(cx, LogFile.readFileContent(cx, fileName));
    }

    private static void sendStringWithIntent(Context cx, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        cx.startActivity(sendIntent);
    }
}
