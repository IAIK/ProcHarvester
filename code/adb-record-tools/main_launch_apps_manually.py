import time
import randomized_record
import config

# Use app names instead of package names for manual recording
MANUAL_TARGET_SET = [

    # From IEEE interrupts 2016 paper
    "Bilibili",
    "SC Hong Kong",
    "SHAREit",
    "Little Alchemy",
    "SILVRR",
    "TED",

    # financial
    "ELBA (Raiffeisen)",
    "PayPal",

    # utility
    "Play Music",
    "Chrome",
    "Fotos (Google Photos)",
    "Gmail",
    "Play Store",
    "Random Gallery",
    "Dropbox",

    # social
    "Messenger (Facebook Messenger)",
    "WhatsApp",
    "Snapchat",
    "Telegram",
    "Instagram",
]


def ask_for_user_action(command):
    input(command + " - press [ENTER] to continue...")
    print("...")


def instruct_manual_launch(package_name):
    ask_for_user_action("\nStart " + package_name)
    time.sleep(config.DELAY_AFTER_LAUNCH)
    ask_for_user_action("Kill foreground app")
    time.sleep(config.DELAY_AFTER_LAUNCH)


def main():

    print("Record app launches manually\n")
    ask_for_user_action("Start logging procedure\n")

    randomized_record.acquire_data_randomized(MANUAL_TARGET_SET, config.records_per_app(), instruct_manual_launch)

    ask_for_user_action("\nStop logging app")

main()
