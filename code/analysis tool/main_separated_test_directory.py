import config
import features as ft
import pandas as pd
import app_classifier
import input_parser
import timing
from sklearn.metrics import classification_report

SEPARATED_TEST_RECORD_DIR = '/session_30_manually_started_apps'


def main():
    timing.start_measurement()
    classify_separated_test_directory()
    timing.stop_measurement()


def classify_separated_test_directory():

    print("\nDo classification with different training directory and test directory")
    print("\nTraining directory: " + config.get_record_dir())
    print("\nTest directory: " + SEPARATED_TEST_RECORD_DIR)

    file_contents, labels = input_parser.parse_input_files(config.get_record_dir(), combine_sc_vectors=True)
    test_data, test_labels = input_parser.parse_input_files(SEPARATED_TEST_RECORD_DIR, combine_sc_vectors=True)
    if len(file_contents) != len(test_data):
        raise ValueError("Different number of input files in training directory and test directory - must be equal")

    train_index = list(range(0, len(labels)))
    test_index = list(range(len(labels), len(labels) + len(test_labels)))

    # Append test data to training data
    for idx in range(0, len(file_contents)):
        training_file_content = file_contents[idx]
        test_file_content = test_data[idx]
        training_file_content.records.extend(test_file_content.records)
    labels.extend(test_labels)

    X = ft.extract_preconfigured_features(file_contents)
    Y = pd.Series(labels)

    Y_test, predictions, accuracy = app_classifier.do_classification(X, Y, train_index, test_index)
    print(classification_report(Y_test, predictions))

main()
