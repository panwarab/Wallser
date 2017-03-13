package thenextvoyager.wallser.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.fragment.ImageFragment;

/**
 * Created by Abhiroj on 3/7/2017.
 */

public class ImagePagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = ImagePagerAdapter.class.getSimpleName();
    Context context;
    ArrayList<DataModel> model;

    public ImagePagerAdapter(FragmentManager fm, ArrayList<DataModel> model, Context context) {
        super(fm);
        this.context = context;
        this.model = model;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, position + " position in getItem");
        return ImageFragment.newInstance(model.get(position), null);
    }


    @Override
    public int getCount() {
        return model.size();
    }

}
