package com.procharvester.Logging;

/** Wrapper class to pass event specific data throughout the call chain to the target loggers **/
public class EventInfo {

    public final String targetLabel_;

    public EventInfo(String targetLabel_) {
        this.targetLabel_ = targetLabel_;
    }
}
