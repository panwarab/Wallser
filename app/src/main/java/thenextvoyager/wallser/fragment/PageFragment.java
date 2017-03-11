package thenextvoyager.wallser.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import thenextvoyager.wallser.Data.Constants;
import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.ImageAdapter;
import thenextvoyager.wallser.utility.EndlessRecyclerViewScrollListener;

import static thenextvoyager.wallser.Data.DataModel.model;


/**
 * Created by Abhiroj on 3/3/2017.
 */

public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private static final String TAG = PageFragment.class.getSimpleName();
    /**
     * Unsplash API, By Default=10
     */
    private static final String per_page = "10";
    /**
     * Unsplash API call parameter, By Default=latest
     * Change it in Pager Fragment, based on Tab tapped
     */
    public static String order_By = "latest";

    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    boolean first_call = true;
    GridLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;

    public static Fragment newInstance(String callFor) {
        Log.d(TAG, "Instantiating " + callFor + " Fragment");
        Bundle args = new Bundle();
        args.putString(ARG_PAGE, callFor);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            order_By = ((savedInstanceState.getString(ARG_PAGE) == "Popular") ? "popular" : "latest");
            Log.d(TAG, "Ordering the results by " + order_By);
        }
        layoutManager = new GridLayoutManager(getContext(), 2);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.w(TAG, "On load More Called with page number " + page);
                loadDataUsingVolley(page);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        loadDataUsingVolley(1);
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        return view;
    }

    void setUpRecyclerView() {
        if (imageAdapter == null)
            imageAdapter = new ImageAdapter(getContext(), model);
        recyclerView.setAdapter(imageAdapter);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);

    }

    void loadDataUsingVolley(int page) {
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "Wallser", "Loading");
        dialog.show();
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        final String URL = "https://api.unsplash.com/photos/?page=" + page + "&client_id=" + Constants.api_key + "&per_page=" + per_page + "&order_by=" + order_By;
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
                if (model != null && first_call) {
                    setUpRecyclerView();
                    first_call = false;
                } else {
                    imageAdapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(objectRequest);
    }
}
