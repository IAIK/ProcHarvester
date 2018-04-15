import logging_functions as lf
import config
import time

WAIT_TIME_AFTER_START = 2.0

# Checks whether the apps on the target smartphone are installed and able to launch properly
def main():

    print("Check configuration for " + str(len(config.TARGET_APPS)) + " target apps\n")

    lf.start_logging_app()
    time.sleep(1)

    failed_starts = ""
    failed_starts_cnt = 0

    for app in config.TARGET_APPS:
        print("\nAttempt to launch " + app)
        output = lf.launch_app(app, get_output=True)
        print(output)

        if "No activities found to run" in output or "CRASH:" in output:
            failed_starts += "\n" + app + ": " + output
            failed_starts_cnt += 1
        else:
            time.sleep(WAIT_TIME_AFTER_START)

    lf.return_to_home_screen()
    lf.stop_logging_app()

    print("\nThere have been " + str(failed_starts_cnt) + " failed starts")
    print(failed_starts)

main()
