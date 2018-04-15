import random


def acquire_data_randomized(target_labels, records_per_label, event_function):
    start_cnts = {}
    for label in target_labels:
        start_cnts[label] = records_per_label

    while len(start_cnts) > 0:
        label = random.choice(list(start_cnts.keys()))
        start_cnts[label] -= 1

        print("\nLaunch " + label + ", " + str(start_cnts[label]) + " starts remaining")
        event_function(label)

        if start_cnts[label] == 0:
            start_cnts.pop(label)

    event_cnt = len(target_labels) * records_per_label
    print("\nFinished recording of " + str(event_cnt) + " events")
