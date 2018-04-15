import logging_functions as lf
import randomized_record
import time

RECORDS_PER_GESTURE = 10
REPEAT_TAPS = 2  # Press shift and regular key two times
TOUCH_EVENT_DURATION = 1.8 # In seconds, execute all touch events in constant time
SOFT_KEYBOARD_ACTIVITY = lf.LOGGING_APP + "/.Activities.TouchEventActivity"


class Pos:
    def __init__(self, x_coordinate, y_coordinate):
        self.x = x_coordinate
        self.y = y_coordinate

key_a = Pos(100, 1480)
key_shift = Pos(80, 1600)
key_special_characters = Pos(80, 1820)


def tap_event(pos):
    for cnt in range(0, REPEAT_TAPS):
        lf.adb("input tap " + str(pos.x) + " " + str(pos.y))


def long_swipe_event():
    lf.adb("input swipe 100 1450 850 1450 300")


def short_swipe_event():
    lf.adb("input swipe 100 1450 450 1450 75")


def long_press_event(pos):
    duration = 550 # milliseconds
    lf.adb("input swipe " + str(pos.x) + " " + str(pos.y) + " " + str(pos.x) + " " + str(pos.y) + " " + str(duration))


class EventAction:
    def __init__(self, handler, argument=None):
        self.handler = handler
        self.argument = argument


SOFT_KEYBOARD_ACTIONS = {
    "long_press_character": EventAction(long_press_event, key_a),
    "tap_regular_character": EventAction(tap_event, key_a),
    "tap_shift": EventAction(tap_event, key_shift),
    "short_swipe": EventAction(short_swipe_event),
    "long_swipe": EventAction(long_swipe_event),
}

SOFT_KEYBOARD_ACTIONS_LIST = []


def do_tap_gesture(action_code, trigger_event=True):

    if trigger_event:
        lf.trigger_new_event(action_code)

    current_time = time.time()
    end_time = current_time + TOUCH_EVENT_DURATION

    # Execute action based on action code lookup map
    print("Execute action code: " + action_code)
    event_action = SOFT_KEYBOARD_ACTIONS[action_code]
    if event_action.argument is not None:
        event_action.handler(event_action.argument)
    else:
        event_action.handler()

    # Avoid time bias by recording traces with constant time regardless of the touch gesture
    while current_time < end_time:
        current_time = time.time()


def main():

    for action_code in SOFT_KEYBOARD_ACTIONS.keys():
        SOFT_KEYBOARD_ACTIONS_LIST.append(action_code)
    print("Record tap gestures:", SOFT_KEYBOARD_ACTIONS_LIST, "\n")

    lf.kill_app(lf.LOGGING_APP)
    lf.adb("am start -n " + SOFT_KEYBOARD_ACTIVITY)
    time.sleep(1.0)

    lf.start_logging_app(clean_start=False)

    # First round without adb event trigger to enable automated detection of candidate side channels
    for gesture in SOFT_KEYBOARD_ACTIONS_LIST:
        do_tap_gesture(gesture, trigger_event=False)
        time.sleep(0.5)

    randomized_record.acquire_data_randomized(SOFT_KEYBOARD_ACTIONS_LIST, RECORDS_PER_GESTURE, do_tap_gesture)

    lf.stop_logging_app()


main()
