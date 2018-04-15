package com.procharvester.Logging;

import android.content.Context;

import java.io.FileReader;
import java.io.IOException;

public class LogTarget {

    final String path_;
    final String attribute_;
    final Integer columnIndex_;
    private boolean PERMANENT_LOGGING_MODE = false;
    SubTarget[] subTargets_ = null;
    Integer columnCntLimit_ = null;

    public LogTarget(String path_, String attribute_, Integer columnIndex_) {
        this.path_ = path_;
        this.attribute_ = attribute_;
        this.columnIndex_ = columnIndex_;
    }

    LogTarget setPermanentMode() {
        PERMANENT_LOGGING_MODE = true;
        return this;
    }

    LogTarget setColumnCntLimit(int columnCntLimit) {
        this.columnCntLimit_ = columnCntLimit;
        return this;
    }

    public static class SubTarget {
        final String attribute_;
        final Integer columnIndex_;

        public SubTarget(String attribute_, Integer columnIndex_) {
            this.attribute_ = attribute_;
            this.columnIndex_ = columnIndex_;
        }

        public SubTarget(String attribute_) {
            this.attribute_ = attribute_;
            this.columnIndex_ = null;
        }

        public String getFileNameAppendix() {
            StringBuilder appendix = new StringBuilder();
            appendix.append("_");
            if (attribute_ != null) {
                appendix.append("_" + attribute_);
            }
            if (columnIndex_ != null) {
                appendix.append("_c" + Integer.toString(columnIndex_));
            }
            return appendix.toString();
        }

        @Override
        public String toString() {
            return attribute_;
        }
    }

    LogTarget setMultipleSubTargets(SubTarget[] subTargets) {
        subTargets_ = subTargets;
        return this;
    }

    public static boolean checkIfReadable(String path) {
        try {
            new FileReader(path).close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public SideChannelReader createReaderObject(Context cx) {

        if (!checkIfReadable(path_)) {
            new LogFile(cx, "Access denied: " + getLogFileName());
            return null;
        }

        if (subTargets_ != null) {
            return new MultipleSideChannelReader(cx, this);
        } else if (attribute_ == null && columnIndex_ == null) {
            return new WholeFileSideChannelReader(cx, this);
        } else {
            return new SingleSideChannelReader(cx, this);
        }
    }

    public String getLogFileName() {
        String logFileName = path_.replace("/", "-");
        if (attribute_ != null) {
            logFileName += "__" + attribute_;
        }
        if (columnIndex_ != null){
            logFileName += "_c" + columnIndex_;
        }
        if (isPermanentLoggingModeEnabled()) {
            logFileName += "_PERMANENT_MODE";
        }
        return logFileName;
    }

    boolean isPermanentLoggingModeEnabled() {
        return PERMANENT_LOGGING_MODE;
    }
}

