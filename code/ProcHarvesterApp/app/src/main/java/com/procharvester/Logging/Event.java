package com.procharvester.Logging;


import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class Event {

    boolean freeSpaceAvailable() {
        return logValues_.size() < Config.MAX_EVENT_SIZE;
    }

    public final ArrayList<LogValue> logValues_;
    String targetLabel_;
    long time_;

    Event(long time, String targetLabel) {
        this.targetLabel_ = targetLabel;
        time_ = time;
        logValues_ = new ArrayList<>();
    }

    Event() {
        logValues_ = new ArrayList<>();
    }

    public ArrayList<Entry> getChartEntries() {
        ArrayList<Entry> chartEntries = new ArrayList<>();
        if (!logValues_.isEmpty()) {
            LogValue firstValue = logValues_.get(0);
            for (LogValue value : logValues_) {
                chartEntries.add(new Entry(value.timeStamp - firstValue.timeStamp, value.value));
            }
        }
        return chartEntries;
    }

    public long getRelativeTime() {
        LogValue firstValue = logValues_.get(0);
        return time_ - firstValue.timeStamp;
    }

    public String getTargetLabel() {
        return targetLabel_;
    }

    public long getTime() {
        return time_;
    }

    @Override
    public String toString() {
        return LogParser.createEventDescriptionLine(this);
    }
}
