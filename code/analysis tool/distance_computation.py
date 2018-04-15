from scipy.spatial.distance import euclidean
from fastdtw import fastdtw
import numpy as np
from pandas import DataFrame
from cdtw import pydtw


# Not used anymore, before lazy computation feature
def compute_distance_matrix(fc, distance_func):

    comp_cnt = 0

    rec_cnt = len(fc.records)
    print("Computing " + str(rec_cnt) + "x" + str(rec_cnt) + " dynamic time warping matrix")
    dwt_matrix = np.zeros((rec_cnt, rec_cnt))
    for i in range(0, rec_cnt):
        for j in range(0, i):
            dwt_matrix[i, j] = distance_func(fc.records[i], fc.records[j])
            comp_cnt += 1

    # Halve the number of computations since the distance matrix is always symmetric
    for i in range(0, rec_cnt):
        for j in range(0, i):
            dwt_matrix[j, i] = dwt_matrix[i, j]

    print("Finished " + str(comp_cnt) + " distance computations")
    return DataFrame(dwt_matrix)


def dtw(rec1, rec2):
    return fast_c_dtw_implementation(rec1, rec2)
    #return dtw_considering_time_steps(rec1, rec2)


def jaccard_distance(r1, r2):

    # Apply differential first to avoid comparing absolute values
    diff_s1 = np.diff(r1.np_values)
    diff_s2 = np.diff(r2.np_values)
    intersection_cardinality = len(set.intersection(*[set(diff_s1), set(diff_s2)]))
    union_cardinality = len(set.union(*[set(diff_s1), set(diff_s2)]))

    if union_cardinality > 0 and intersection_cardinality > 0:
        res = intersection_cardinality/float(union_cardinality)
    else:
        return 99999.0 # no match

    res = 1.0 / res # use reciprocal values since out knn classifier minimizes distances
    return res


def fast_c_dtw_implementation(rec1, rec2):

    s1 = rec1.np_values
    s2 = rec2.np_values

    d = pydtw.dtw(s1, s2, pydtw.Settings(step='p0sym',  # Sakoe-Chiba symmetric step with slope constraint p = 0
                                         window='palival',  # type of the window
                                         param=2.0,  # window parameter
                                         norm=True,  # normalization
                                         compute_path=False))
    distance = d.get_dist()
    if not np.isfinite(distance):
        distance = 9999999.

    return float(distance)

step_cnt = 0


def dtw_considering_time_steps(rec1, rec2):

    s1 = rec1.np_values
    s2 = rec2.np_values

    global step_cnt
    step_cnt += 1
    if step_cnt % 150 == 0:
        print(str(step_cnt) + " dtw computations finished")
    distance, _ = fastdtw(s1, s2, radius=1, dist=euclidean)
    return distance
