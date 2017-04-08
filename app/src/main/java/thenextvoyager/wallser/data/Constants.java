package thenextvoyager.wallser.data;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.HashMap;

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
    public static final String DATA_TAG = "DATA";
    public static final int PER_PAGE = 10;
    public static final String PAGEFRAG = "PAGEFRAGMENT";
    public static final String TAG_TO_FRAG = "TagToFrag";
    public static final String PAGE_NO = "pagenumber";
    private static final String TAG = Constants.class.getSimpleName();
    public static String api_key;
    public static HashMap<String, Fragment> TagToFrag;
    private static String unsplashUrl = "https://unsplash.com/@";
    Context context;

    public Constants(Context context) {
        this.context = context;
        api_key = context.getResources().getString(R.string.unsplash_api_key);
        TagToFrag = new HashMap<>();
    }

    public static String makeUserURL(String username) {
        return new StringBuffer(unsplashUrl).append(username).toString();
    }

}
