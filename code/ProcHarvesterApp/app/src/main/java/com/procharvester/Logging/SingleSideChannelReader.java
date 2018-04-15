package com.procharvester.Logging;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SingleSideChannelReader extends SideChannelReader {

    private static final String TAG = SingleSideChannelReader.class.getSimpleName();

    private final EventLogger eventLogger_;
    private final Integer lineIndex_;

    public SingleSideChannelReader(Context cx, LogTarget logTarget) {
        super(cx, logTarget);
        lineIndex_ = findLineIndex();
        eventLogger_ = new EventLogger(cx, logTarget.getLogFileName(), logTarget);
    }

    @Override
    public void issueNewEvent(EventInfo info) {
        eventLogger_.issueNewEvent(readLogValue(), info);
    }

    @Override
    public void updateCurrentEventData() {
        eventLogger_.updateCurrentEventData(readLogValue());
    }

    private LogValue readLogValue() {
        final long time = System.currentTimeMillis();
        int value = extractColumnValue(logTarget_.columnIndex_, readLineAtPosition(lineIndex_));
        return new LogValue(value, time);
    }

    private Integer findLineIndex() {
        if (logTarget_.attribute_ == null) {
            return 0;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logTarget_.path_));
            int index = 0;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), index++) {
                if (line.contains(logTarget_.attribute_)) {
                    reader.close();
                    return index;
                }
            }
            Log.e(TAG, "Unable to find attribute " + logTarget_.attribute_ + " in file " + logTarget_.path_);
            throw new NullPointerException("readFile failed");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readLineAtPosition(int lineCnt) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logTarget_.path_));
            int index = 0;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), index++) {
                if (index == lineCnt) {
                    reader.close();
                    return line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer extractColumnValue(int columnIndex, String content) {
        if (content == null) {
            return null;
        }
        String[] freeChunkedLine = fastLineSplit(content);
        String valueStr = freeChunkedLine[columnIndex];
        try {
            return (int)Long.parseLong(valueStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error when trying to read " + logTarget_.getLogFileName());
            e.printStackTrace();
            return null;
        }
    }
}

