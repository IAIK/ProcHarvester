import input_parser
import pandas as pd
import features as ft
import app_classifier
import config
import timing


def main():
    timing.start_measurement()

    print("Do combined classification using all input files")
    file_contents, labels = input_parser.parse_input_files(config.get_record_dir(), combine_sc_vectors=True)
    X = ft.extract_preconfigured_features(file_contents)
    Y = pd.Series(labels)
    app_classifier.do_kfold_cross_validation(X, Y)

    timing.stop_measurement()


main()
