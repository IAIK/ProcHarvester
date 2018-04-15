import logging_functions as lf
import time
import randomized_record
import config

CHECK_CONFIGURATION = False

TARGET_ACTIVITIES = [

    "com.google.android.gm/.ConversationListActivityGmail",
    "com.google.android.gm/.EmlViewerActivityGmail",
    "com.google.android.gm/.GmailActivity",
    "com.google.android.gm/com.android.mail.ui.settings.PublicPreferenceActivity",
    "com.google.android.gm/.ui.MailboxSelectionActivityGmail",
    "com.google.android.gm/.CreateShortcutActivityGmail",
    "com.google.android.gm/.CreateShortcutActivityGoogleMail",
    "com.google.android.gm/com.android.mail.ui.MailActivity",
    "com.google.android.gm/.ComposeActivityGmailExternal",
    "com.google.android.gm/.ui.MailActivityGmail",
    "com.google.android.gm/.ui.MailActivityGmail",
    "com.google.android.gm/.ComposeActivityGmailExternal",

    #"com.amazon.mShop.android.shopping/com.amazon.mShop.details.ProductDetailsActivity",
    #"com.amazon.mShop.android.shopping/com.amazon.mShop.android.home.PublicUrlActivity",
    #"com.amazon.mShop.android.shopping/com.amazon.mShop.search.RetailSearchFragmentActivity",
    #"com.amazon.mShop.android.shopping/com.amazon.mShop.search.SearchActivity",
]


def launch_activity(activity_target):
    return lf.adb("am start -n " + activity_target, get_output=True)


def start_stop_activity(activity_target):

    package_name = activity_target.split("/")[0]
    print("Package name of " + activity_target + ": " + package_name)

    lf.trigger_new_event(activity_target)
    launch_activity(activity_target)

    time.sleep(config.DELAY_AFTER_LAUNCH)
    lf.kill_app(package_name)
    time.sleep(config.DELAY_AFTER_KILL)


def check_config():

    failed_launches = []

    for activity in TARGET_ACTIVITIES:
        print("\nAttempt to launch " + activity)
        output = launch_activity(activity)
        print(output)
        if "SecurityException" in output:
            failed_launches.append(activity)
        time.sleep(1)

    print("\n" + str(len(failed_launches)) + " failed launches occured:", failed_launches, "\n")


def main():

    print("Record activity launches\n")

    lf.start_logging_procedure()

    if not CHECK_CONFIGURATION:
        randomized_record.acquire_data_randomized(TARGET_ACTIVITIES, config.records_per_app(), start_stop_activity)
    else:
        check_config()

    lf.stop_logging_app()

main()
