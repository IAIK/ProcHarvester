import pandas as pd
import numpy as np
import config
import math
import distance_computation


def init_dist_matrices(file_contents):
    dist_matrices = []
    for fileContent in file_contents:
        dist_matrices.append(np.full((len(fileContent.records), len(fileContent.records)), np.nan))
    return dist_matrices


def predict_based_on_distance_matrix(file_contents, Y_train, test_index, train_index, dist_matrices):

    # predict based on k nearest distances majority vote
    # If there are multiple input files, then k gets multiplied by the number of files

    # Determine indices of k nearest neighbours
    k_nearest_indices_rows = []
    for file_cnt, fileContent in enumerate(file_contents):

        comp_cnt = 0
        dist_array = dist_matrices[file_cnt]

        for training_sample_cnt, row_cnt in enumerate(test_index):

            test_to_train_distances = np.zeros(len(train_index))

            for test_sample_cnt, col_cnt in enumerate(train_index):
                # lazy computation of distances
                dist = dist_array[row_cnt, col_cnt]
                if math.isnan(dist):
                    dist = distance_computation.dtw(fileContent.records[row_cnt], fileContent.records[col_cnt])
                    dist_array[row_cnt, col_cnt] = dist
                    dist_array[col_cnt, row_cnt] = dist
                    comp_cnt += 1
                test_to_train_distances[test_sample_cnt] = dist

            sorted_indices = np.argsort(test_to_train_distances)
            k_smallest_dist_indices = sorted_indices[0:config.K_NEAREST_CNT]

            if len(k_nearest_indices_rows) <= training_sample_cnt:
                k_nearest_indices_rows.append(k_smallest_dist_indices)
            else:
                extended = np.append(k_nearest_indices_rows[training_sample_cnt], k_smallest_dist_indices)
                k_nearest_indices_rows[training_sample_cnt] = extended

        sample_cnt = len(fileContent.records)
        print(fileContent.file_name + ": " + str(sample_cnt) + "x" + str(sample_cnt) + " distance matrix - " + str(comp_cnt) + " computations done")

    # Predict based on k nearest indices rows
    predictions = []
    for test_set_cnt in range(0, len(test_index)):

        k_smallest_dist_indices = k_nearest_indices_rows[test_set_cnt]
        k_nearest_labels = []

        for index in k_smallest_dist_indices:
            k_nearest_labels.append(Y_train.iloc[index])
        k_nearest_labels = pd.Series(k_nearest_labels)

        # label_cnts = tmp_lbls.groupby(tmp_lbls, sort=False).count()
        label_cnts = k_nearest_labels.value_counts(sort=False).reindex(pd.unique(k_nearest_labels))

        prediction = label_cnts.idxmax()
        predictions.append(prediction)

    return pd.Series(predictions)
