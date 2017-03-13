package thenextvoyager.wallser.Data;

import android.content.Context;

import thenextvoyager.wallser.R;

/**
 * Created by Abhiroj on 3/12/2017.
 */

public class Constants {

    public static final String IMAGE_PASS_KEY = "image_one_pass";
    public static String api_key;
    Context context;

    public Constants(Context context) {
        this.context = context;
        api_key = context.getResources().getString(R.string.unsplash_api_key);
    }

}
