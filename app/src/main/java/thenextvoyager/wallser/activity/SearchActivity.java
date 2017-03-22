package thenextvoyager.wallser.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.utility.EndlessRecyclerViewScrollListener;

import static thenextvoyager.wallser.Data.Constants.SEARCH;
import static thenextvoyager.wallser.Data.Constants.api_key;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<DataModel> models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String search_query = getIntent().getExtras().getString(SEARCH);
        toolbar.setTitle(search_query);
        final LoadWallpaperTask wallpaperTask = new LoadWallpaperTask(search_query, SearchActivity.this, new ArrayList<DataModel>());
        wallpaperTask.execute();
        recyclerView = (RecyclerView) findViewById(R.id.search_grid);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!wallpaperTask.isCancelled())
                    wallpaperTask.cancel(true);
                wallpaperTask.execute(page);
            }
        };
        recyclerView.setOnScrollListener(scrollListener);
    }

    class LoadWallpaperTask extends AsyncTask<Integer, Integer, Void> {
        private final String TAG = LoadWallpaperTask.class.getSimpleName();
        int per_page = 20;
        String query;
        Context context;
        ArrayList<DataModel> models;
        ProgressDialog dialog;

        public LoadWallpaperTask(String search_query, Context context, ArrayList<DataModel> models) {
            super();
            query = search_query;
            this.context = context;
            this.models = models;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage("Fetching");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Void doInBackground(Integer... integers) {
            String URL = "https://api.unsplash.com/search/photos?page=" + integers[0] + "&client_id=" + api_key + "&per_page=" + per_page + "&query=" + query;
            try {
                URL url = new URL(URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                JsonReader jsonReader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    if (isCancelled()) break;
                    DataModel dataModel = new DataModel(null, null, null);
                    String name = jsonReader.nextName();
                    if (name.equals("results")) {
                        jsonReader.beginArray();
                        while (jsonReader.hasNext()) {
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
                                String token = jsonReader.nextName();
                                if (token.equals("id")) {
                                    String id = jsonReader.nextString();
                                    dataModel.name = id;
                                }
                                if (token.equals("urls")) {
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()) {
                                        String token1 = jsonReader.nextName();
                                        if (token1.equals("regular")) {
                                            String rawurl = jsonReader.nextString();
                                            dataModel.imageURL = rawurl;
                                        }
                                    }
                                    jsonReader.endObject();
                                }
                                if (token.equals("links")) {
                                    jsonReader.beginObject();
                                    while (jsonReader.hasNext()) {
                                        String token2 = jsonReader.nextName();
                                        if (token2.equals("download")) {
                                            String downloadurl = jsonReader.nextString();
                                            dataModel.downloadURL = downloadurl;
                                        }
                                    }
                                    jsonReader.endObject();
                                }

                            }
                            jsonReader.endObject();
                        }
                        jsonReader.endArray();
                    }
                    models.add(dataModel);
                }
                jsonReader.endObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
