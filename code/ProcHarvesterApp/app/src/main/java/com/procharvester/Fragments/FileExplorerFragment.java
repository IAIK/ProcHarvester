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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.procharvester.R;
import com.procharvester.Services.RecordService;
import com.procharvester.util.Boast;
import com.procharvester.util.DataExport;
import com.procharvester.util.FolderLayout;
import com.procharvester.util.MenuOptions;

public class FileExplorerFragment extends Fragment implements FolderLayout.IFolderItemListener {

    FolderLayout localFolders;
    TextView tvCurrentState_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_file_explorer_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topbar_export_all_files_to_matlab:
                reloadUI();
                DataExport.exportAllFilesForMatlab(getActivity());
                return true;
            case R.id.top_bar_stop_logging:
                MenuOptions.stopLogging(getActivity());
                reloadUI();
                return true;
            case R.id.topbar_action_delete_all_files:
                deleteAllFiles();
                reloadUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void OnCannotFileRead(File file) {
        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("[" + file.getName() + "] folder can't be read!")
                .show();
    }

    public void OnFileClicked(File file) {

        getFragmentManager().beginTransaction()
                .replace(R.id.act_file_record_root_layout, ChartFragment.newInstance(file.getName()))
                .addToBackStack(null)
                .commit();
    }

    void deleteAllFiles() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete all files?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MenuOptions.stopLogging(getActivity());
                MenuOptions.deleteAllFiles(FileExplorerFragment.this.getActivity());
                Boast.makeText(getActivity(), "Files removed", Toast.LENGTH_SHORT).show();
                reloadUI();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void OnFileLongClicked(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete file " + file.getName() + "?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (file.delete()) {
                    Boast.makeText(getActivity(), "Deleted file", Toast.LENGTH_SHORT).show();
                } else {
                    Boast.makeText(getActivity(), "Deleting file failed", Toast.LENGTH_SHORT).show();
                }
                reloadUI();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public FileExplorerFragment() {
    }

    public static FileExplorerFragment newInstance() {
        return new FileExplorerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folders, container, false);

        localFolders = (FolderLayout) view.findViewById(R.id.localfolders);
        localFolders.setIFolderItemListener(this);
        tvCurrentState_ = (TextView) view.findViewById(R.id.current_spy_target);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadUI();
    }


    void reloadUI() {
        int fileCnt = localFolders.setDir(getActivity().getFilesDir().getAbsolutePath()); // show files in internal storage
        if (RecordService.getInstance() != null ) {
            tvCurrentState_.setText("Logging active - " + Integer.toString(fileCnt) + " log files");
        } else {
            tvCurrentState_.setText("Logging inactive - " + Integer.toString(fileCnt) + " log files");
        }
    }
}
