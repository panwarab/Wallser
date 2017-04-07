package thenextvoyager.wallser.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.ImageAdapter;
import thenextvoyager.wallser.callback.OnResultFetchedCallback;
import thenextvoyager.wallser.data.Constants;
import thenextvoyager.wallser.data.DataModel;
import thenextvoyager.wallser.network.FetchImageVolley;
import thenextvoyager.wallser.utility.EndlessRecyclerViewScrollListener;

public class SearchableActivity extends AppCompatActivity implements OnResultFetchedCallback {


    private static final String TAG = SearchableActivity.class.getSimpleName();
    ProgressBar progressBar;
    private int page;
    private String query;
    private FetchImageVolley imageVolley;
    private RecyclerView recyclerView;
    private ArrayList<DataModel> model;
    private ImageAdapter imageAdapter;
    private GridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollLstener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_dialog);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            toolbar.setTitle(query);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            imageVolley = new FetchImageVolley(SearchableActivity.this);
            page = 1;
            imageVolley.loadDataForQuery(Constants.PER_PAGE, page, query);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        model = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.grid_recycler);
        imageAdapter = new ImageAdapter(SearchableActivity.this, model);
        layoutManager = new GridLayoutManager(SearchableActivity.this, 1);
        scrollLstener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (imageVolley != null) {
                    SearchableActivity.this.page = page;
                    imageVolley.loadDataForQuery(Constants.PER_PAGE, SearchableActivity.this.page, query);
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollLstener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void getData(ArrayList<DataModel> model) {
        Log.d(TAG, "Searchable Activity getData called for page number = " + page);
        imageAdapter.addNewData(model);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
