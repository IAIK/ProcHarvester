package com.procharvester.Logging;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/** Optimized for reading all potential side channels of a file to explore new side channels **/
public class WholeFileSideChannelReader extends SideChannelReader {

    private static final String TAG = WholeFileSideChannelReader.class.getSimpleName();

    private static final int MIN_NUMBER_OF_CHANGES_UNTIL_FIRST_EVENT = 10;

    private static class ChangeRate {
        int prevValue_;
        int numberOfChanges_;
        ChangeRate(int prevValue_) {
            this.prevValue_ = prevValue_;
            this.numberOfChanges_ = 0;
        }
    }

    private Map<ValuePosition, ChangeRate> changeTrackMap_;
    private Map<ValuePosition, EventLogger> eventLoggers_;
    private HashSet<Integer> trackedLines_;

    private boolean explorationMode_ = true;
    private boolean loggingDisabled_ = false;

    public WholeFileSideChannelReader(Context cx, LogTarget logTarget) {
        super(cx, logTarget);
        changeTrackMap_ = new HashMap<>();
        eventLoggers_ = new HashMap<>();
        trackedLines_ = new HashSet<>();
    }

    @Override
    public void issueNewEvent(EventInfo info) {
        explorationMode_ = false;
        updateEventLoggers(info);
    }

    @Override
    public void updateCurrentEventData() {
        updateEventLoggers(null);
    }

    private void insertNewEventLogger(ValuePosition pos, String[] splitLine) {

        final String valueName = guessAttributeName(splitLine);
        String logFileName = logTarget_.getLogFileName() + ValuePosition.createLogFileAppendix(pos, valueName);
        eventLoggers_.put(pos, new EventLogger(cx_, logFileName, logTarget_));
        changeTrackMap_.remove(pos);
        trackedLines_.add(pos.lineIndex);
    }

    private void checkIfValueShouldBeLogged(ValuePosition pos, int newValue, String[] splitLine) {
        ChangeRate changeRate = changeTrackMap_.get(pos);
        if (newValue != changeRate.prevValue_) {
            changeRate.numberOfChanges_++;
        }
        changeRate.prevValue_ = newValue;
        if (changeRate.numberOfChanges_ >= MIN_NUMBER_OF_CHANGES_UNTIL_FIRST_EVENT) {
            Log.e(TAG, "Inserted new event logger for " + logTarget_.getLogFileName());
            insertNewEventLogger(pos, splitLine);
        }
    }

    private void recordLogValue(ValuePosition pos, LogValue logValue, EventInfo info) {
        EventLogger eventLogger = eventLoggers_.get(pos);
        if (info != null) {
            eventLogger.issueNewEvent(logValue, info);
        } else {
            eventLogger.updateCurrentEventData(logValue);
        }
    }

    private void updateEventLoggers(EventInfo info) {

        if (loggingDisabled_)
            return;
        if (!explorationMode_ && eventLoggers_.isEmpty())
            return;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(logTarget_.path_));

            int lineIndex = 0;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), lineIndex++) {

                if (!explorationMode_ && !trackedLines_.contains(lineIndex)) {
                    continue; // optimization, splitting and parsing lines is very expensive
                }

                final String[] splitLine = fastLineSplit(line);

                int columnIndex = 0;
                for (String chunk : splitLine) {
                    try {
                        final int value = (int)Long.parseLong(chunk);
                        final long time = System.currentTimeMillis();
                        final ValuePosition pos = new ValuePosition(lineIndex, columnIndex);

                        if (eventLoggers_.containsKey(pos)) {
                            recordLogValue(pos, new LogValue(value, time), info);
                        } else if (explorationMode_ && changeTrackMap_.containsKey(pos)) {
                            checkIfValueShouldBeLogged(pos, value, splitLine);
                        } else if (explorationMode_) {
                            changeTrackMap_.put(pos, new ChangeRate(value));
                        }

                    } catch (NumberFormatException ignored) {}
                    columnIndex++;
                }
            }

            reader.close();

        } catch (IOException e) {
            loggingDisabled_ = true;
            e.printStackTrace();
        }
    }

    /** Assign meaningful log file names **/
    private String guessAttributeName(String[] splitLine) {

        String attribute = splitLine[0];

        for (int idx = 0; idx < splitLine.length; idx++) {

            String column = splitLine[idx];
            if (column.trim().length() == 0) {
                continue;
            }

            if (!isNumber(column)) {
                attribute = "";
                for (int i = idx; i >= 0; i--) {
                    if (isNumber(splitLine[i]))
                        continue;
                    attribute = splitLine[i] + " " + attribute;
                }
            }
        }

        // Log.e(TAG, "Attribute name choice for " + Arrays.toString(splitLine) + ": " + attribute);
        return attribute;
    }

    private boolean isNumber(String str) {
        try {
            long dummy = Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static class ValuePosition {

        ValuePosition(int lineIndex, int columnIndex) {
            this.lineIndex = lineIndex;
            this.columnIndex = columnIndex;
        }

        final int lineIndex;
        final int columnIndex;

        public static String createLogFileAppendix(ValuePosition pos, String attributeName) {
            return "__" + attributeName.replace("/", "-") + "_l" + pos.lineIndex + "_c" + pos.columnIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ValuePosition)) {
                return false;
            }

            ValuePosition valuePosition = (ValuePosition)o;
            return (this.lineIndex == valuePosition.lineIndex && this.columnIndex == valuePosition.columnIndex);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lineIndex, columnIndex);
        }
    }
}
