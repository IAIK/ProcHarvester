package com.procharvester.Logging;


import android.content.Context;

import java.util.regex.Pattern;

public abstract class SideChannelReader {

    private Pattern pattern_;

    protected SideChannelReader(Context cx, LogTarget logTarget) {
        this.logTarget_ = logTarget;
        this.cx_ = cx;
        pattern_ = Pattern.compile("\\s+");
    }

    protected String[] fastLineSplit(String line) {
        return pattern_.split(line);
    }

    protected final LogTarget logTarget_;
    protected Context cx_;

    public abstract void issueNewEvent(EventInfo info);
    public abstract void updateCurrentEventData();

    public LogTarget getLogTarget() {
        return logTarget_;
    }
}
