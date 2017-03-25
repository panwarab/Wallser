package thenextvoyager.wallser.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import static thenextvoyager.wallser.Data.Constants.CHOICE_TAG;
import static thenextvoyager.wallser.Data.Constants.HANDLER_DELAY_TIME;
import static thenextvoyager.wallser.Data.Constants.api_key;
import static thenextvoyager.wallser.utility.Utility.detectConnection;


/**
 * Created by Abhiroj on 3/3/2017.
 */

public class PageFragment extends Fragment implements SortDialogCallback {

    /**
     * Unsplash API, By Default=10
     */
    private static final String per_page = "10";
    public static String order_By;
    ProgressDialog dialog = null;
    Context context;
    /**
     * Unsplash API call parameter, By Default=latest
     * Change it in Pager Fragment, based on Tab tapped
     */
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    GridLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    FrameLayout no_internet_container;
    Bundle savedInstanceState;
    // Attaching Handler to the main thread
    Handler handler = makeHandler();
    RequestQueue requestQueue;
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
            swapViews();
            if (shouldHandlerRunAgain)
                handler.postDelayed(job, HANDLER_DELAY_TIME);
        }
    };

    private Handler makeHandler() {
        return new Handler();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog != null) {
            dialog.cancel();
        }
        outState.putString(CHOICE_TAG, order_By);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler != null) {
            handler.post(job);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        requestQueue = Volley.newRequestQueue(getContext());
        layoutManager = new GridLayoutManager(getContext(), 2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                SortDialog sortDialog = new SortDialog();
                sortDialog.setTargetFragment(PageFragment.this, 911);
                sortDialog.show(getChildFragmentManager(), "sortfragment");
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_page_fragment, menu);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (handler == null && !detectConnection(getContext()))
            handler = makeHandler();
    }

    private void swapViews() {
        if (detectConnection(getContext()) == false) {
            recyclerView.setVisibility(View.INVISIBLE);
            setHasOptionsMenu(false);
            no_internet_container.setVisibility(View.VISIBLE);
        } else {
            shouldHandlerRunAgain = false;
            handler.removeCallbacks(job, null);
            handler = null;
            recyclerView.setVisibility(View.VISIBLE);
            setHasOptionsMenu(true);
            no_internet_container.setVisibility(View.INVISIBLE);
            if (savedInstanceState != null) {
                loadDataUsingVolley(1, savedInstanceState.getString(CHOICE_TAG), true);
            } else {
                order_By = "latest";
                loadDataUsingVolley(1, order_By, true);
            }
        }
    }

    /**
     * Call to this function attaches a new scroll listener
     */
    private void attachScrollListener() {
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {

            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadDataUsingVolley(page, order_By, false);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        no_internet_container = (FrameLayout) view.findViewById(R.id.no_internet_container);
        imageAdapter = new ImageAdapter(getContext(), (model == null) ? new ArrayList<DataModel>() : model);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(layoutManager);
        attachScrollListener();


        return view;
    }

    void loadDataUsingVolley(int page, String order_by, boolean shouldShowProgressDialog) {
        if (shouldShowProgressDialog) {
            dialog = ProgressDialog.show(getContext(), "Wallser", "Loading");
        }
        String URL = "https://api.unsplash.com/photos/?page=" + page + "&client_id=" + api_key + "&per_page=" + per_page + "&order_by=" + order_by;
        final ProgressDialog finalDialog = dialog;
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (finalDialog != null) {
                    finalDialog.dismiss();
                }
                if (model != null)
                imageAdapter.swapDataSet(model);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (finalDialog != null) finalDialog.dismiss();
                if (context != null)
                    Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        objectRequest.setTag(order_By);
        requestQueue.add(objectRequest);
    }

    /**
     * marks a new network call to Unsplash API
     * Thus, set model array list to 0, to start fresh.
     * @param order_by
     */
    @Override
    public void onDialogFinish(String order_by) {
        if(model!=null)
        model.clear();
        imageAdapter.swapDataSet(model);
        scrollListener.resetState();
        requestQueue.cancelAll(order_By);
        order_By = order_by;
        loadDataUsingVolley(1, order_By, true);
    }
}
