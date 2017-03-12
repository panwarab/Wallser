package thenextvoyager.wallser.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.io.File;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.DownloadAdapter;

public class DownloadsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Downloads");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        String path = Environment.getExternalStorageDirectory().toString() + "/Walser";
        File dir = new File(path);
        File[] files = dir.listFiles();
        DownloadAdapter downloadAdapter = new DownloadAdapter(getApplicationContext(), files);
        recyclerView.setAdapter(downloadAdapter);

    }

}
