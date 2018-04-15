package com.procharvester.Logging;

import android.content.Context;

import java.util.ArrayList;

import com.procharvester.util.StringUtils;

/** Responsible for parsing log data in both directions,
 * from event object instances to text files and from text files to event object instances
 */
public class LogParser {

    public static final String EVENT_START_MARKER = "Event Start";
    public static final String TARGET_LABEL_MARKER = "Label:";

    public static String createEventDescriptionLine(Event event) {
        return String.valueOf(event.getTime()) + " " + StringUtils.formatTimeStamp(event.getTime()) + " " + TARGET_LABEL_MARKER + " " + event.targetLabel_;
    }

    public static String createLogLine(LogValue logValue) {
        return logValue.timeStamp + " " + logValue.value;
    }

    public static ArrayList<Event> readEventsFromFile(Context cx, String fileName) {

        ArrayList<Event> events = new ArrayList<>();
        Event current = new Event();
        LogFile.LineReaderFile readerFile = new LogFile.LineReaderFile(cx, fileName);

        String line;
        while ((line = readerFile.readLine()) != null) {
            if (line.equals(EVENT_START_MARKER)) {
                if (!current.logValues_.isEmpty()) {
                    events.add(current);
                }
                current = new Event();
            } else if (line.contains(TARGET_LABEL_MARKER)) {
                String[] freeChunkedLine = line.split(" ");
                current.time_ = Long.valueOf(freeChunkedLine[0]);
                current.targetLabel_ = freeChunkedLine[3];
            } else {
                String[] freeChunkedLine = line.split(" ");
                long time = Long.valueOf(freeChunkedLine[0]);
                int value = Integer.valueOf(freeChunkedLine[1]);
                LogValue logValue = new LogValue(value, time);
                current.logValues_.add(logValue);
            }
        }

        if (!current.logValues_.isEmpty()) {
            events.add(current);
        }
        return events;
    }

    /** Go in the opposite direction: Save file based on in-memory event data **/
    public static void storeEventDataInFile(Context cx, String fileName, ArrayList<Event> events) {
        LogFile logFile = new LogFile(cx, fileName);
        for (int cnt = 0; cnt < events.size(); cnt++) {
            Event e = events.get(cnt);
            logFile.storeLine(EVENT_START_MARKER);
            for (LogValue v : e.logValues_) {
                if (v.timeStamp == e.getTime()) {
                    logFile.storeLine(createEventDescriptionLine(e));
                }
                logFile.storeLine(createLogLine(v));
            }
        }
    }
}
