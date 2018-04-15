package com.procharvester.Logging;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LogFile {
    private static final String TAG = LogFile.class.getSimpleName();

    private BufferedWriter writer_;

    public LogFile(Context cx, String fileName) {
        try {
            writer_ = new BufferedWriter(new OutputStreamWriter(cx.openFileOutput(fileName, Context.MODE_APPEND)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeLine(String logLine) {
        try {
            writer_.append(logLine);
            writer_.newLine();
            writer_.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileContent(Context cx, String fileName) {
        BufferedReader bufferedReader = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(cx.openFileInput(fileName));
            bufferedReader = new BufferedReader(inputStreamReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder fileContent = new StringBuilder();
        String receiveString = "";
        try {
            while ((receiveString = bufferedReader.readLine()) != null) {
                fileContent.append(receiveString);
                fileContent.append("\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Read content of " + fileName);
        return fileContent.toString();
    }

    public static class LineReaderFile {

        private BufferedReader reader_;

        public LineReaderFile(Context cx, String fileName) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(cx.openFileInput(fileName));
                reader_ = new BufferedReader(inputStreamReader);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public String readLine() {
            String receiveString = null;
            try {
                receiveString = reader_.readLine();
                if (receiveString == null) {
                    reader_.close(); // no more line
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveString;
        }
    }
}
