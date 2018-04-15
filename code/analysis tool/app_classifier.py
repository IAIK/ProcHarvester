from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import accuracy_score
from sklearn.metrics import classification_report
import plot_confusion_matrix
import numpy as np
import pandas as pd
import precomputed_knn_selector

def do_classification(X, Y, train_index, test_index, dist_matrices=None):

    Y_train, Y_test = Y[train_index], Y[test_index]

    if dist_matrices is None:
        dist_matrices = precomputed_knn_selector.init_dist_matrices(X)
    predictions = precomputed_knn_selector.predict_based_on_distance_matrix(X, Y_train, test_index, train_index, dist_matrices)

    accuracy = accuracy_score(Y_test, predictions)
    return Y_test, predictions, accuracy


def do_kfold_cross_validation(X, Y, verbose=True):

    folds = 8
    printv("\nSelecting rows for " + str(folds) + "-fold validation", verbose)
    kf = StratifiedKFold(n_splits=folds, shuffle=True)
    kf.get_n_splits()

    # Initialize classification performance measures
    unique_labels = Y.unique()
    cnf_mat = pd.DataFrame(np.zeros((len(unique_labels), len(unique_labels))), columns=unique_labels)
    cnf_mat.set_index(keys=unique_labels, inplace=True)
    Y_test_all_folds = []
    predictions_all_folds = []
    summed_accuracy = 0

    fold_cnt = 1
    firstFileContent = X[0]
    split_var = firstFileContent.records
    dist_matrices = precomputed_knn_selector.init_dist_matrices(X)

    for train_index, test_index in kf.split(split_var, Y):

        printv("\nFold: " + str(fold_cnt), verbose)

        Y_test, predictions, accuracy = do_classification(X, Y, train_index, test_index, dist_matrices)

        if verbose:
            for idx, pred in enumerate(predictions):
                cnf_mat.ix[Y_test.iloc[idx], pred] += 1

        printv("Accuracy:" + str(accuracy), verbose)
        summed_accuracy += accuracy
        fold_cnt += 1

        Y_test_all_folds.extend(Y_test.values.tolist())
        predictions_all_folds.extend(predictions.values.tolist())


    total_accuracy = summed_accuracy / folds
    printv(classification_report(Y_test_all_folds, predictions_all_folds), verbose)
    print("\nTotal accuracy over all folds: " + str(total_accuracy))

    if verbose:
        plot_confusion_matrix.show_confusion_matrix(cnf_mat.values.astype(int), unique_labels)

    return total_accuracy


def printv(str, verbose):
    if verbose:
        print(str)
