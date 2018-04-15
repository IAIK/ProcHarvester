package com.procharvester.util;

import android.util.Log;

import java.util.ArrayDeque;

public class Profiler {

    public static final boolean PROFILING_ENABLED = false;

    private static final String TAG = Profiler.class.getSimpleName();
    private ArrayDeque<ProfileEntry> profileEntries_;

    private void startMeasurement_(String functionName) {
        ProfileEntry profileEntry = new ProfileEntry(functionName);
        profileEntries_.addLast(profileEntry);
        profileEntry.startTime_ = System.nanoTime();
    }

    private void stopMeasurement_() {
        long current = System.nanoTime();
        ProfileEntry profileEntry = profileEntries_.removeLast();
        long delta = current - profileEntry.startTime_;
        Log.w(TAG, profileEntry.functionName_ + " took " + (delta / 1000) + " microseconds");
    }

    private Profiler() {
        profileEntries_ = new ArrayDeque<>();
    }

    /** ----------- Singleton handling code ------------ **/

    public static void startMeasurement(String functionName) {
        if (PROFILING_ENABLED) {
            Profiler.getInstance().startMeasurement_(functionName);
        }
    }

    public static void stopMeasurement() {
        if (PROFILING_ENABLED) {
            Profiler.getInstance().stopMeasurement_();
        }
    }
    private static class ProfileEntry {
        long startTime_;
        final String functionName_;
        ProfileEntry(String functionName) {
            functionName_ = functionName;
        }
    }

    private static Profiler instance_;

    private static Profiler getInstance() {
        if (instance_ == null) {
            instance_ = new Profiler();
        }
        return instance_;
    }
}
