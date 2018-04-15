import config


def get_used_side_channels(file_dict):

    used_side_channels = []
    if config.USE_TARGETED_SIDECHANNELS:
        print("\nUse targeted side channels in target directory " + config.get_record_dir() + ": ", config.TARGETED_SIDECHANNELS)
        for side_channel in config.TARGETED_SIDECHANNELS:
            used_side_channels.append(get_file(file_dict, side_channel))
    else:
        print("\nUse " + str(len(file_dict)) + " side channels in target directory " + config.get_record_dir())
        for file_name, file_content in file_dict.items():
            print("Use side channel " + file_name)
            used_side_channels.append(file_content)

    return used_side_channels


def extract_preconfigured_features(file_contents):
    fcs = {}
    for fc in file_contents:
        fcs[fc.file_name] = fc

    side_channels = get_used_side_channels(fcs)
    return side_channels # Do lazy computation, compute dtw later on


def get_file(dict, key_substring):
    for key, value in dict.items():
        if key_substring in key:
            print("Use side channel " + key)
            return value
    raise ValueError("No file record found that matches to " + key_substring)


def merge_features_to_one_frame(X):
    X_merged = X[0]
    for cnt in range(1, len(X)):
        X_merged.merge(X[cnt])
        #X_merged = pd.concat([X_merged, X[cnt]], axis=1)
    return X_merged
