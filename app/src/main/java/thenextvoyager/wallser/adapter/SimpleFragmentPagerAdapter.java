package thenextvoyager.wallser.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import thenextvoyager.wallser.fragment.PageFragment;

/**
 * Created by Abhiroj on 3/3/2017.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = SimpleFragmentPagerAdapter.class.getSimpleName();
    Context context;
    private int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"Latest", "Popular"};


    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "Pager currently set on " + position);
        return PageFragment.newInstance(tabTitles[position]);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
