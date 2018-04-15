package com.procharvester.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.procharvester.Logging.LogFile;
import com.procharvester.R;
import com.procharvester.util.MenuOptions;

public class LogResultFragment extends Fragment {

    public LogResultFragment() {
    }

    TextView fileView_;
    String fileName_;
    public static final String TAG = LogResultFragment.class.getSimpleName();
    static final String PARAM_FILE_TO_SHOW = "file_to_show";

    public static LogResultFragment newInstance(String fileName) {
        LogResultFragment fragment = new LogResultFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_FILE_TO_SHOW, fileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log_result_and_chart_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topbar_delete_file_content:
                MenuOptions.deleteFileContent(getActivity(), fileName_);
                reloadData();
                return true;
            default:
                return MenuOptions.onFileItemSelected(getActivity(), item, fileName_) ||
                        super.onOptionsItemSelected(item);
        }
    }

    void reloadData() {
        fileView_.setText(LogFile.readFileContent(getActivity(), fileName_));
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_result, container, false);

        fileName_ = getArguments().getString(PARAM_FILE_TO_SHOW);

        TextView tv = (TextView) view.findViewById(R.id.feature_name);
        tv.setText(fileName_);

        fileView_ = (TextView)view.findViewById(R.id.file_content_view);
        fileView_.setSingleLine(false);
        return view;
    }
}
