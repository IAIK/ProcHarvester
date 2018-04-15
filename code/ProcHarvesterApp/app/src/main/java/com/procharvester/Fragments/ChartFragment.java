package com.procharvester.Fragments;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.procharvester.Logging.Event;
import com.procharvester.Logging.LogParser;
import com.procharvester.R;
import com.procharvester.util.Boast;
import com.procharvester.util.ChartAdapter;
import com.procharvester.util.MenuOptions;


public class ChartFragment extends Fragment implements View.OnLongClickListener {

    static final String ARG_FILE_NAME = "file_name";

    String fileName_;
    ListView chartListView_;
    Button btnLogFile_;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance(String fileName) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FILE_NAME, fileName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileName_ = getArguments().getString(ARG_FILE_NAME);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chartListView_ = (ListView) view.findViewById(R.id.chart_list_view);

        TextView featureName = (TextView)view.findViewById(R.id.fragment_chart_feature_name);
        featureName.setText(fileName_);

        btnLogFile_ = (Button)view.findViewById(R.id.btn_show_log_file);
        btnLogFile_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.act_file_record_root_layout, LogResultFragment.newInstance(fileName_))
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    void reloadData() {

        ArrayList<Event> events = LogParser.readEventsFromFile(getActivity(), fileName_);
        if (events == null) {
            Boast.makeText(getActivity(), "Parsing of logfile failed, invalid format", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<Event> reverseOrderEvents = new ArrayList<>();
        if (events.isEmpty()) {
            Boast.makeText(getActivity(), "Logfile empty", Toast.LENGTH_LONG).show();
        } else {
            /** Switch the order to show the most current events in the first charts **/
            for (int cnt = events.size() - 1; cnt >= 0; cnt--) {
                reverseOrderEvents.add(events.get(cnt));
            }
        }

        ChartAdapter adapter = (ChartAdapter)chartListView_.getAdapter();
        if (adapter == null) {
            chartListView_.setAdapter(new ChartAdapter(reverseOrderEvents, getActivity(), this));
        } else {
            adapter.changeEventData(reverseOrderEvents);
        }
        btnLogFile_.setText("VIEW LOG FILE (" + events.size() + " events)");
    }



    @Override
    public boolean onLongClick(View v) {
        final Event eventToDelete = (Event) v.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete event " + eventToDelete + "?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (deleteEvent(eventToDelete)) {
                    Boast.makeText(getActivity(), "Deleted event " + eventToDelete, Toast.LENGTH_LONG).show();
                } else {
                    Boast.makeText(getActivity(), "Failed to delete event " + eventToDelete, Toast.LENGTH_LONG).show();
                }
                reloadData();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
        return true;
    }

    boolean deleteEvent(Event eventToDelete) {
        ArrayList<Event> events = LogParser.readEventsFromFile(getActivity(), fileName_);
        for (int cnt = 0; cnt < events.size(); cnt++) {
            Event e = events.get(cnt);
            if (e.getTime() == eventToDelete.getTime()) {

                events.remove(cnt);
                /** This is a theoretically possible race condition with the background service writing to the file,
                 * but we have to do it this way to keep the code simple and the background service running */
                MenuOptions.deleteFileContent(getActivity(), fileName_);
                LogParser.storeEventDataInFile(getActivity(), fileName_, events);
                return true;
            }
        }
        return false; // event not found
    }
}
