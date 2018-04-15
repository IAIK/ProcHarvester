import input_parser
import pandas as pd
import app_classifier
import config
import timing


class ClassificationResult:
    def __init__(self, accuracy, file_name):
        self.accuracy = accuracy
        self.file_name = file_name
    def __str__(self):
        return "Total accuracy: " + str(self.accuracy) + " for " + self.file_name


def explorative_classification():

    file_contents, label_list = input_parser.parse_input_files(config.get_record_dir(), combine_sc_vectors=False)
    results = []

    for idx, fc in enumerate(file_contents):
        labels = label_list[idx]

        print("\nEvaluate ", fc.file_name)
        X = [fc]
        Y = pd.Series(labels)

        total_accuracy = app_classifier.do_kfold_cross_validation(X, Y, verbose=False)
        results.append(ClassificationResult(total_accuracy, fc.file_name))

    results.sort(key = lambda classificationResult: classificationResult.accuracy, reverse=True)

    print("\nSummary for files in " + config.get_record_dir() + ":\n")
    for r in results:
        print(r)


def main():
    timing.start_measurement()
    print("Do explorative classification with separate input files")
    explorative_classification()
    timing.stop_measurement()


main()
