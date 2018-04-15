package com.procharvester.Logging;


public class LogValue {
    public LogValue(int value, long timeStamp) {
        this.timeStamp = timeStamp;
        this.value = value;
    }

    public final long timeStamp;
    public final int value;
}
