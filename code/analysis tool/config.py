import preprocessing


RECORD_DIR = 'session_30_manually_started_apps/'

PREPROCESS_FUNCTION = preprocessing.custom_left_neighbour_interpolate
SKIP_FIRST_MILLISECONDS = 0

# Use this to check whether excluding certain side channels improves the results
EXCLUDE_SIDECHANNELS = False
EXCLUDED_SIDECHANNELS = [
    #"pgalloc_dma",
    "qcom,smd-rpm",
    "Function call interrupts",
    "wlan_pci",
    "ufshcd",
]

# Warning: The file order of the side channels is relevant for the knn implementation,
# since it is used as criterion for even majority vote situations
USE_TARGETED_SIDECHANNELS = False # Use all files in target directory when this option is disabled
TARGETED_SIDECHANNELS = [
    "intr_c67", # Single function call interrupts
    "intr_c22",
    "nr_dirty_threshold",
    "nr_mapped",
    "pgalloc_dma",
    "intr_c117",
    "pgfault_c1",
    "nr_anon_pages",
    "intr_c1",
    "wlan0_c10",
    "nr_free_pages",
    "wlan0_c11",
    #"softirq_c5",
    "wlan0_c3",
    #"softirq_c4",
    "cpu_c1",
    "intr_c306",
    "nr_shmem",
    ]

MIN_EVENT_NUMBER = 50


def get_record_dir():
    return RECORD_DIR


K_NEAREST_CNT = 2
FILE_ENDINGS = '*.txt'
RECORD_BASE_DIR = 'record_files/'
