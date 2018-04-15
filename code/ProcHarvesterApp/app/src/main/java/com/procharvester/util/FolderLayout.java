package com.procharvester.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.procharvester.R;

public class FolderLayout extends LinearLayout implements AdapterView.OnItemClickListener,
                                                AdapterView.OnItemLongClickListener {

    public interface IFolderItemListener {

        void OnCannotFileRead(File file);//implement what to do folder is Unreadable
        void OnFileClicked(File file);//What to do When a file is clicked
        void OnFileLongClicked(File file);
    }

    Context context;
    IFolderItemListener folderListener;
    private List<String> path = null;
    private ListView lstView;

    public FolderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.folderview, this);
        lstView = (ListView) view.findViewById(R.id.list);
    }

    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        this.folderListener = folderItemListener;
    }

    //Set Directory for view at anytime
    public int setDir(String dirPath){
        return getDir(dirPath);
    }


    private int getDir(String dirPath) {

        // myPath.setText("Location: " + dirPath);
        List<String> item = new ArrayList<>();
        path = new ArrayList<>();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        int fileCnt = 0;

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    path.add(file.getPath());
                    item.add(file.getName());
                    fileCnt++;
                }
            }

            setItemList(item);
        }

        return fileCnt;
    }

    //can manually set Item to display, if u want
    public void setItemList(List<String> item){
        ArrayAdapter<String> fileList = new ArrayAdapter<>(context,
                R.layout.row, item);

        lstView.setAdapter(fileList);
        lstView.setOnItemClickListener(this);
        lstView.setOnItemLongClickListener(this);
    }


    public void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(path.get(position));
        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path.get(position));
            else {
//what to do when folder is unreadable
                if (folderListener != null) {
                    folderListener.OnCannotFileRead(file);

                }

            }
        } else {

//what to do when file is clicked
//You can add more,like checking extension,and performing separate actions
            if (folderListener != null) {
                folderListener.OnFileClicked(file);
            }

        }
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        File file = new File(path.get(position));
        if (!file.isDirectory()) {
            if (folderListener != null) {
                folderListener.OnFileLongClicked(file);
                return true;
            }
        }
        return false;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        onListItemClick((ListView) arg0, arg0, arg2, arg3);
    }
}