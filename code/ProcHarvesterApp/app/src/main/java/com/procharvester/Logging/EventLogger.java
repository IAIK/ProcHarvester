package com.procharvester.Logging;

import android.content.Context;

import com.procharvester.util.CircularBuffer;

/**
 * This purpose of this class is to filter out the interesting events out
 * of the vast amount of input data. Several filtering mechanisms are applied.
 */
public class EventLogger {
    static final String TAG = EventLogger.class.getSimpleName();

    private final LogFile backingStorage;
    private final LogTarget logTarget;

    private final CircularBuffer<LogValue> preFetchBuffer;
    private Event currentEvent_;

    public EventLogger(Context cx, String logFileName, LogTarget logTarget) {
        this.backingStorage = new LogFile(cx, logFileName);
        this.logTarget = logTarget;
        preFetchBuffer = new CircularBuffer<>(Config.PREFETCH_BUFFER_SIZE);
    }

    private LogTarget getLogTarget() {
        return logTarget;
    }

    private void finishCurrentEvent() {
        currentEvent_ = null;
    }

    public void issueNewEvent(final LogValue logValue, EventInfo info) {

        /** Take care of the current event **/
        if (currentEvent_ != null && currentEvent_.freeSpaceAvailable()) {
            commitLogValue(logValue);
        }
        finishCurrentEvent();

        /** Create a new event with pre fetched values and current value in the right order**/
        currentEvent_ = new Event(logValue.timeStamp, info.targetLabel_);
        backingStorage.storeLine(LogParser.EVENT_START_MARKER);
        LogValue preFetchedValue;
        while ((preFetchedValue = preFetchBuffer.get()) != null) {
            final long delta = logValue.timeStamp - preFetchedValue.timeStamp;
            if (delta < Config.MAX_PREFETCH_TIME) { // throw value away if it was too long ago
                commitLogValue(preFetchedValue);
            }
        }
        backingStorage.storeLine(LogParser.createEventDescriptionLine(currentEvent_));
        resetDuplicateFiltering();
        updateCurrentEventData(logValue);
    }

    public void updateCurrentEventData(final LogValue logValue) {
        checkForEventTimeOut(logValue);
        if (!filterOutUnchangedValue(logValue)) {
            registerLogValue(logValue);
        }
    }

    private void checkForEventTimeOut(final LogValue logValue) {
        if (getLogTarget().isPermanentLoggingModeEnabled()) {
            return;
        }
        if (currentEvent_ == null) {
            return;
        }
        long delta = logValue.timeStamp - currentEvent_.time_;
        if (delta >= Config.EVENT_TIME_OUT) {
            registerLogValue(logValue);
            finishCurrentEvent();
        }
    }

    /**
     * We do not want to filter out all unchanged values:
     * Special handling is necessary to commit values that keep being
     * unchanged until to the point where a change happens.
     * Otherwhise there would be skewed
     * slopes in the resulting charts.
     */
    private LogValue lastValue = null;
    private boolean skippedLastValue = false;

    private void resetDuplicateFiltering() {
        lastValue = null;
        skippedLastValue = false;
    }

    private boolean filterOutUnchangedValue(LogValue value) {

        boolean ret = false;

        if (lastValue != null && value.value == lastValue.value) {
            skippedLastValue = true;
            ret = true;
        } else if (skippedLastValue) {
            skippedLastValue = false;
            registerLogValue(lastValue);
        }

        lastValue = value;
        return ret;
    }

    private void registerLogValue(LogValue logValue) {
        if (currentEvent_ != null && getLogTarget().isPermanentLoggingModeEnabled()) {
            commitLogValue(logValue);
        } else if (currentEvent_ != null && currentEvent_.freeSpaceAvailable()) {
            commitLogValue(logValue);
        } else {
            finishCurrentEvent();
            preFetchBuffer.add(logValue);
        }
    }

    private void commitLogValue(LogValue logValue) {
        currentEvent_.logValues_.add(logValue);
        backingStorage.storeLine(LogParser.createLogLine(logValue));
    }
}
