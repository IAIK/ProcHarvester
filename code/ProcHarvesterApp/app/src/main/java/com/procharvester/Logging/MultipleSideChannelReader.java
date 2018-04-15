package com.procharvester.Logging;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/** Optimized for performance when reading multiple side channels from the same file **/
public class MultipleSideChannelReader extends SideChannelReader {

    private static class SubTargetLogger {

        final EventLogger eventLogger_;
        final LogTarget.SubTarget subTarget_;

        public SubTargetLogger(EventLogger eventLogger_, LogTarget.SubTarget subTarget_) {
            this.eventLogger_ = eventLogger_;
            this.subTarget_ = subTarget_;
        }
    }

    @Override
    public void issueNewEvent(EventInfo info) {
        updateEventLoggers(info);
    }

    @Override
    public void updateCurrentEventData() {
        updateEventLoggers(null);
    }

    private final HashMap<Integer, SubTargetLogger> loggers_;

    public MultipleSideChannelReader(Context cx, LogTarget logTarget) {
        super(cx, logTarget);

        loggers_ = new HashMap<>();

        try {
            initEventLoggers(cx);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initEventLoggers(Context cx) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(logTarget_.path_));

        ArrayList<LogTarget.SubTarget> remainingSubTargets = new ArrayList<>(Arrays.asList(logTarget_.subTargets_));
        int lineIndex = 0;
        for (String line = reader.readLine(); line != null; line = reader.readLine(), lineIndex++) {

            for (Iterator<LogTarget.SubTarget> iterator = remainingSubTargets.iterator(); iterator.hasNext();) {
                LogTarget.SubTarget subTarget = iterator.next();
                if (line.contains(subTarget.attribute_)) {
                    EventLogger eventLogger = new EventLogger(cx, logTarget_.getLogFileName() + subTarget.getFileNameAppendix(), logTarget_);
                    SubTargetLogger subTargetLogger = new SubTargetLogger(eventLogger, subTarget);
                    loggers_.put(lineIndex, subTargetLogger);
                    iterator.remove();
                    break;
                }
            }
        }

        reader.close();

        if (!remainingSubTargets.isEmpty()) {
            Log.e(getClass().getSimpleName(), "The following sub targets were not found in " + logTarget_.path_ + ": " + Arrays.toString(remainingSubTargets.toArray()));
        }
    }

    private void sendDataToLogger(SubTargetLogger target, int value, EventInfo info) {

        long time = System.currentTimeMillis();
        LogValue logValue = new LogValue(value, time);
        if (info != null) {
            target.eventLogger_.issueNewEvent(logValue, info);
        } else {
            target.eventLogger_.updateCurrentEventData(logValue);
        }
    }

    private void extractSumOfColumnValues(SubTargetLogger current, String line, EventInfo info) {

        int summedValues = 0;
        // Profiler.startMeasurement("split String");
        String[] splitLine = fastLineSplit(line);
        // Profiler.stopMeasurement();

        int cnt = 0;
        for (String column : splitLine) {
            try {
                int value = (int)Long.parseLong(column);
                summedValues += value;
                cnt++;
                if (logTarget_.columnCntLimit_ != null && cnt >= logTarget_.columnCntLimit_) {
                    break;
                }
            } catch (NumberFormatException ignored) {}
        }
        // Log.e(MultipleSideChannelReader.class.getSimpleName(), "Resulting value for " + Arrays.toString(splitLine) + ": " + String.valueOf(summedValues));
        sendDataToLogger(current, summedValues, info);
    }

    private void extractSingleColumnValue(SubTargetLogger current, String line, EventInfo info) {
        String[] splitLine = fastLineSplit(line);
        int value = (int)Long.parseLong(splitLine[current.subTarget_.columnIndex_]);
        sendDataToLogger(current, value, info);
    }


    private void updateEventLoggers(EventInfo info) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(logTarget_.path_));

            int lineIndex = 0;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), lineIndex++) {
                if (!loggers_.containsKey(lineIndex)) {
                    continue;
                }
                SubTargetLogger logger = loggers_.get(lineIndex);
                if (logger.subTarget_.columnIndex_ == null) {
                    extractSumOfColumnValues(logger, line, info);
                } else {
                    extractSingleColumnValue(logger, line, info);
                }
            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
