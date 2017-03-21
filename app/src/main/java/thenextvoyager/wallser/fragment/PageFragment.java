package thenextvoyager.wallser.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.ImageAdapter;
import thenextvoyager.wallser.callback.SortDialogCallback;
import thenextvoyager.wallser.utility.EndlessRecyclerViewScrollListener;

import static thenextvoyager.wallser.Data.Constants.HANDLER_DELAY_TIME;
import static thenextvoyager.wallser.Data.Constants.api_key;
import static thenextvoyager.wallser.utility.Utility.detectConnection;


/**
 * Created by Abhiroj on 3/3/2017.
 */

public class PageFragment extends Fragment implements SortDialogCallback {

    private static final String TAG = PageFragment.class.getSimpleName();
    /**
     * Unsplash API, By Default=10
     */
    private static final String per_page = "10";
    public static String order_By;
    /**
     * Unsplash API call parameter, By Default=latest
     * Change it in Pager Fragment, based on Tab tapped
     */
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    GridLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    FloatingActionButton actionButton;
    FrameLayout no_internet_container;
    Bundle savedInstanceState;
    // Attaching Handler to the main thread
    Handler handler = new Handler();
    boolean shouldHandlerRunAgain = true;
    private ArrayList<DataModel> model;
    /**
     * Handler is attached to the Main Thread and it's message queue, because it is the one who created it.
     * <p>
     * Handler is responsible for checking every second that are we connected to internet, and if we are, then :-
     * 1. Then we remove empty view
     * 2. Make the network call
     * 3. Stop handler from posting the code again using shouldHandlerRunAgain variable
     * 3.1 This is a kill switch otherwise handler will post the runnable again and again to the message queue, which will be executed as soon as it reaches the looper
     * <p>
     * Handler removeCallbacks is used to remove all the pending runnables  in the Message Queue
     */
    Runnable job = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Thread run " + job.hashCode());
            swapViews();
            if (shouldHandlerRunAgain)
                handler.postDelayed(job, HANDLER_DELAY_TIME);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ORDER_BY", order_By);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null)
        handler.post(job);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting Handler");
        layoutManager = new GridLayoutManager(getContext(), 2);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {

            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.w(TAG, "On load More Called with page number " + page);
                loadDataUsingVolley(page, order_By);
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(getContext(), "Async task", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "Invalid Options", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_page_fragment, menu);
    }


    private void swapViews() {
        if (detectConnection(getContext()) == false) {
            recyclerView.setVisibility(View.INVISIBLE);
            actionButton.setVisibility(View.INVISIBLE);
            no_internet_container.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Removing callbacks from handler and stopping it from posting");
            shouldHandlerRunAgain = false;
            handler.removeCallbacks(job, null);
            handler = null;
            recyclerView.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.VISIBLE);
            no_internet_container.setVisibility(View.INVISIBLE);
            if (savedInstanceState != null) {
                loadDataUsingVolley(1, savedInstanceState.getString("ORDER_BY"));
            } else {
                order_By = "latest";
                loadDataUsingVolley(1, order_By);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        actionButton = (FloatingActionButton) view.findViewById(R.id.sort_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortDialog sortDialog = new SortDialog();
                sortDialog.setTargetFragment(PageFragment.this, 911);
                sortDialog.show(getChildFragmentManager(), "sortfragment");
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        no_internet_container = (FrameLayout) view.findViewById(R.id.no_internet_container);

        return view;
    }

    void setUpRecyclerView() {
        if (imageAdapter == null)
            imageAdapter = new ImageAdapter(getContext(), model);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
    }

    void loadDataUsingVolley(int page, String order_by) {
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "Wallser", "Loading");
        dialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String URL = "https://api.unsplash.com/photos/?page=" + page + "&client_id=" + api_key + "&per_page=" + per_page + "&order_by=" + order_by;
        Log.d(TAG, URL);
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                int len = array.length();
                if (model == null)
                model = new ArrayList<>();
                for (int i = 0; i < len; i++) {
                    try {
                        JSONObject object = array.getJSONObject(i);
                        String id = object.getString("id");
                        JSONObject object1 = object.getJSONObject("urls");
                        String imageURL = object1.getString("regular");
                        JSONObject object2 = object.getJSONObject("links");
                        String downloadURL = object2.getString("download");
                        model.add(new DataModel(imageURL, downloadURL, id));
                        Log.d(TAG, downloadURL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
                Log.d(TAG, model.size() + "");
                setUpRecyclerView();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(objectRequest);
    }

    /**
     * marks a new network call to Unsplash API
     * Thus, set model array list to null, to start fresh.
     * as model is reset, ImageAdapter also needs to start fresh.
     *
     * @param order_by
     */
    @Override
    public void onDialogFinish(String order_by) {
        model = null;
        imageAdapter=null;
        order_By = order_by;
        loadDataUsingVolley(1, order_By);
    }
}
