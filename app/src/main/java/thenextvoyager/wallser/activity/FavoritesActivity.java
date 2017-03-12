package thenextvoyager.wallser.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import thenextvoyager.wallser.Data.ImageContract;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.DataAdapter;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        RecyclerView view = (RecyclerView) findViewById(R.id.recyclerview);
        Cursor cursor = getContentResolver().query(ImageContract.ImageEntry.CONTENT_URI, null, null, null, null);
        DataAdapter adapter = new DataAdapter(getApplicationContext(), cursor, false);
    }
}
