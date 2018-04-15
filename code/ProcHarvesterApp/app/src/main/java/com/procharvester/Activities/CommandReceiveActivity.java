package com.procharvester.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.procharvester.Services.RecordService;
import com.procharvester.util.Boast;
import com.procharvester.util.MenuOptions;

public class CommandReceiveActivity extends AppCompatActivity {

    public static void launchInternalCommand(Activity ac, String cmd, String arg) {
        Intent intent = new Intent(ac, CommandReceiveActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(COMMAND_KEY, cmd);
        if (arg != null) {
            bundle.putString(ARG_KEY, arg);
        }
        intent.putExtras(bundle);
        ac.startActivity(intent);
    }

    /** Do not change these constants without changing the python adb scripts! **/
    public static final String COMMAND_KEY = "CMD";
    public static final String ARG_KEY = "ARG";
    public static final String CMD_START_LOGGING = "START_LOGGING";
    public static final String CMD_STOP_LOGGING = "STOP_LOGGING";
    public static final String CMD_TRIGGER_EVENT = "TRIGGER_EVENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String command = null;
        String arg = null;
        if (bundle != null) {
            command = bundle.getString(COMMAND_KEY);
            arg = bundle.getString(ARG_KEY);
        }

        if (command == null) {
            Boast.makeText(this, "Error: " + CommandReceiveActivity.class.getSimpleName() + " started without any command").show();
        } else {
            handleCommand(command, arg);
        }
    }

    private void handleCommand(String command, String arg) {

        switch (command) {
            case CMD_START_LOGGING:
                startLogging();
                break;
            case CMD_STOP_LOGGING:
                MenuOptions.stopLogging(this);
                break;
            case CMD_TRIGGER_EVENT:
                triggerNewEvent(arg);
                break;
            default:
                Boast.makeText(this, "Error: Received unknown command " + command + " and argument " + arg).show();
        }
        finish();
    }

    void triggerNewEvent(String newTargetLabel) {
        RecordService recordService = RecordService.getInstance();
        if (recordService != null) {
            recordService.triggerNewEvent(newTargetLabel);
        }
    }

    void startLogging() {
        if (RecordService.getInstance() != null) {
            Boast.makeText(this, "Logging already active", Toast.LENGTH_LONG).show();
        } else {
            RecordService.startProcRecordService(this);
            Boast.makeText(this, "Started logging", Toast.LENGTH_LONG).show();
        }
    }
}
