package thenextvoyager.wallser.network;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import thenextvoyager.wallser.callback.OnResultFetchedCallback;

import static thenextvoyager.wallser.Data.Constants.api_key;
import static thenextvoyager.wallser.fragment.PageFragment.order_By;

/**
 * Created by Abhiroj on 3/29/2017.
 */

public class FetchImageVolley {

    Context context;
    Dialog dialog;
    RequestQueue requestQueue;

    public FetchImageVolley(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void loadDataUsingVolley(int per_page, int page, String order_by, boolean shouldShowProgressDialog) {
        if (shouldShowProgressDialog) {
            dialog = ProgressDialog.show(context, "Wallser", "Loading");
        }
        String URL = "https://api.unsplash.com/photos/?page=" + page + "&client_id=" + api_key + "&per_page=" + per_page + "&order_by=" + order_by;
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                OnResultFetchedCallback fetchedCallback = (OnResultFetchedCallback) context;
                int len = array.length();
                ArrayList<DataModel> model = new ArrayList<>();
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

                if (dialog != null) {
                    dialog.dismiss();
                }
                if (model != null)
                    fetchedCallback.getData(model);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog != null) dialog.dismiss();
                if (context != null)
                    Toast.makeText(context, R.string.connissue, Toast.LENGTH_SHORT).show();
            }
        });
        objectRequest.setTag(order_By);
        requestQueue.add(objectRequest);
    }

    public void cancelRequest(String TAG) {
        if (dialog != null) dialog.cancel();
        if (TAG != null)
            requestQueue.cancelAll(TAG);
        requestQueue = null;
        context = null;
    }

    public void destroyRelyingObjects() {
        cancelRequest(null);
    }
}
