import config
import logging_functions as lf

# Grant all protected permissions to target apps
# to achieve a seamless recording on test devices
# Do not use on private devices

PROTECTED_PERMISSIONS = [
    "READ_CALENDAR",
    "WRITE_CALENDAR",
    "CAMERA",
    "READ_CONTACTS",
    "WRITE_CONTACTS",
    "GET_ACCOUNTS",
    "ACCESS_FINE_LOCATION",
    "ACCESS_COARSE_LOCATION",
    "RECORD_AUDIO",
    "READ_PHONE_STATE",
    "CALL_PHONE",
    "READ_CALL_LOG",
    "WRITE_CALL_LOG",
    "USE_SIP",
    "PROCESS_OUTGOING_CALLS",
    "BODY_SENSORS",
    "SEND_SMS",
    "RECEIVE_SMS",
    "READ_SMS",
    "RECEIVE_WAP_PUSH",
    "RECEIVE_MMS",
    "READ_EXTERNAL_STORAGE",
    "WRITE_EXTERNAL_STORAGE",
]


def main():

    for app in config.TARGET_APPS:
        for permission in PROTECTED_PERMISSIONS:
            lf.adb("pm grant " + app + " android.permission." + permission)

main()
