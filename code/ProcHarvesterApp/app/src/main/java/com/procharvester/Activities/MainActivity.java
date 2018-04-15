package com.procharvester.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.procharvester.Fragments.FileExplorerFragment;
import com.procharvester.R;
import com.procharvester.util.MenuOptions;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (MenuOptions.onOptionsSharedItemSelected(this, item) || super.onOptionsItemSelected(item));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_record);
        startApp();
    }

    void startApp() {
        getFragmentManager().beginTransaction()
                .replace(R.id.act_file_record_root_layout, FileExplorerFragment.newInstance())
                .commit();
    }
}

