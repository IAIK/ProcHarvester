import logging_functions as lf
import time
import randomized_record
import config

def start_stop_target_app(package_name, trigger_event=True):
    lf.launch_app(package_name, trigger_event)
    time.sleep(config.DELAY_AFTER_LAUNCH)
    lf.kill_app(package_name)
    time.sleep(config.DELAY_AFTER_KILL)


def acquire_resume_data(labels, records_per_app):
    print("\nExecute app resume sequence")
    for target_label in labels:
        do_resume_sequence(target_label, records_per_app)


def do_resume_sequence(package_name, records_per_app):

    # Do not trigger event at first launch since this is a cold start
    lf.launch_app(package_name, trigger_event=False)
    time.sleep(config.DELAY_AFTER_LAUNCH)
    lf.return_to_home_screen()
    time.sleep(config.DELAY_AFTER_LAUNCH)

    for cnt in range(1, records_per_app + 1):
        print('\nResume Launch ' + str(cnt) + ': ' + package_name)
        lf.launch_app(package_name, trigger_event=True)
        time.sleep(config.DELAY_AFTER_LAUNCH)
        lf.return_to_home_screen()
        time.sleep(config.DELAY_AFTER_KILL)

    lf.kill_app(package_name)
    time.sleep(config.DELAY_AFTER_KILL)


def main():

    print("Record in mode: " + str(config.RECORD_MODE) + "\n")

    lf.start_logging_procedure()

    # Detect changes in proc files before actually triggering events
    start_stop_target_app("com.android.chrome", trigger_event=False)

    if config.RECORD_MODE == config.RecordMode.MIXED_MODE:
        # The first starts for resume launches do not qualify as actual app resumes
        COLD_START_RECORDS_PER_APP = 7
        RESUME_RECORDS_PER_APP = 9
        randomized_record.acquire_data_randomized(config.TARGET_APPS, COLD_START_RECORDS_PER_APP, start_stop_target_app)
        acquire_resume_data(config.TARGET_APPS, RESUME_RECORDS_PER_APP)
    elif config.RECORD_MODE == config.RecordMode.APP_RESUMES:
        acquire_resume_data(config.TARGET_APPS, config.records_per_app())
    elif config.RECORD_MODE == config.RecordMode.COLD_STARTS:
        randomized_record.acquire_data_randomized(config.TARGET_APPS, config.records_per_app(), start_stop_target_app)

    lf.stop_logging_app()


main()
