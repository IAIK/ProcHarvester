import subprocess
import time
import config


# Do not change these constants without changing the logging app!
LOGGING_APP = "com.procharvester"
COMMAND_RECEIVE_ACTIVITY = LOGGING_APP + "/.Activities.CommandReceiveActivity"
COMMAND_KEY = "CMD"
ARG_KEY = "ARG"
CMD_START_LOGGING = "START_LOGGING"
CMD_STOP_LOGGING = "STOP_LOGGING"
CMD_TRIGGER_EVENT = "TRIGGER_EVENT"


def adb(command, get_output=False):
    try:
        if get_output:
            return subprocess.getoutput('adb shell \"' + command + '\"')
        else:
            subprocess.run(['adb', 'shell', command])
    except FileNotFoundError:
        raise ConnectionError("adb connection failed")


def return_to_home_screen():
    adb("am start -a android.intent.action.MAIN -c android.intent.category.HOME")


def start_logging_app(clean_start=True):
    if clean_start:
        # kill logging app first since we need to return to home screen after sending commands
        kill_app(LOGGING_APP)
    send_command(CMD_START_LOGGING)


def start_logging_procedure():
    start_logging_app()
    time.sleep(config.DELAY_AFTER_LAUNCH)
    return_to_home_screen()
    time.sleep(config.DELAY_AFTER_KILL)


def stop_logging_app():
    send_command(CMD_STOP_LOGGING)


def trigger_new_event(target_label):
    send_command(CMD_TRIGGER_EVENT, target_label)


def send_command(command, argument=None):
    command_string = "am start -n " + COMMAND_RECEIVE_ACTIVITY + " --es " + COMMAND_KEY + " " + command
    if argument is not None:
        command_string += " --es " + ARG_KEY + " " + argument
    adb(command_string)


def kill_app(package_name):
    adb("am force-stop " + package_name)


def launch_app(package_name, get_output=False, trigger_event=True):
    if trigger_event:
        trigger_new_event(package_name)
    return adb("monkey -p " + package_name + " -c android.intent.category.LAUNCHER 1", get_output)
