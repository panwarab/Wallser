package thenextvoyager.wallser.Data;

import android.content.Context;

import thenextvoyager.wallser.R;

/**
 * Created by Abhiroj on 3/12/2017.
 */

public class Constants {

    public static final String IMAGE_PASS_KEY = "image_one_pass";
    public static final int CURSOR_LOADER_MANAGER = 1;
    public static final String MODEL_TAG = "1";
    public static final String IMAGE_FRAGMENT_TAG = "2";
    public static final long HANDLER_DELAY_TIME = 1000;
    public static final String IMAGE_TAG = "IMAGE_PASS";
    public static final String SEARCH = "search";
    public static final String CHOICE_TAG="ORDER_BY";
    public static String api_key;
    Context context;

    public Constants(Context context) {
        this.context = context;
        api_key = context.getResources().getString(R.string.unsplash_api_key);
    }

}
