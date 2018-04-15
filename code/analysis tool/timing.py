import time
import plot_confusion_matrix


START_TIME = None
STOPPED = False

def start_measurement():
    global START_TIME
    START_TIME = time.time()


def stop_measurement():
    global STOPPED, START_TIME
    if not STOPPED:
        elapsed_seconds = time.time() - START_TIME
        m, s = divmod(elapsed_seconds, 60)
        h, m = divmod(m, 60)
        print("\nElapsed time: %d:%02d:%02d" % (h, m, s))
    STOPPED = True

    plot_confusion_matrix.show_blocking()