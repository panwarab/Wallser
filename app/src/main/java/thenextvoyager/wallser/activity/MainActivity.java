    package thenextvoyager.wallser.activity;

    import android.app.SearchManager;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Typeface;
    import android.os.Bundle;
    import android.support.annotation.NonNull;
    import android.support.design.widget.NavigationView;
    import android.support.design.widget.TabLayout;
    import android.support.v4.view.GravityCompat;
    import android.support.v4.view.ViewPager;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.SearchView;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.android.gms.ads.AdRequest;
    import com.google.android.gms.ads.AdView;

    import java.util.ArrayList;

    import thenextvoyager.wallser.R;
    import thenextvoyager.wallser.adapter.SimpleFragmentPagerAdapter;
    import thenextvoyager.wallser.callback.OnResultFetchedCallback;
    import thenextvoyager.wallser.data.Constants;
    import thenextvoyager.wallser.data.DataModel;
    import thenextvoyager.wallser.fragment.PageFragment;

    import static thenextvoyager.wallser.data.Constants.PAGEFRAG;
    import static thenextvoyager.wallser.data.Constants.TagToFrag;

    public class MainActivity extends AppCompatActivity implements OnResultFetchedCallback {

        private static String TAG = MainActivity.class.getSimpleName();

        AdView adView;

        DrawerLayout drawerLayout;
        NavigationView navigationView;
        ViewPager viewPager;
        TabLayout tabLayout;

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.search_menu, menu);

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);

            return true;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Constants constants = new Constants(getApplicationContext());
            if (savedInstanceState != null) {
                TagToFrag.put(PAGEFRAG, getSupportFragmentManager().getFragment(savedInstanceState, PAGEFRAG));
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.app_name);
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            setSupportActionBar(toolbar);
            setUpViewPager();

            adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            setUpNavigationView();
        }

        private void setUpViewPager() {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
            viewPager.setCurrentItem(0);
            tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        }

        private void setUpNavigationView() {
            View view = navigationView.inflateHeaderView(R.layout.nav_header);
            TextView header = (TextView) view.findViewById(R.id.app_name);
            header.setText(R.string.app_name);
            header.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf"));
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedDrawerItem(item);
                    return false;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId())
            {
                case android.R.id.home:
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        void selectedDrawerItem(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.feedback:
                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"abhiroj95@gmail.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wallser app");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text goes here");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    break;
            }
            drawerLayout.closeDrawers();

        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            getSupportFragmentManager().putFragment(outState, PAGEFRAG, TagToFrag.get(PAGEFRAG));
        }

        @Override
        public void getData(ArrayList<DataModel> model) {
            Log.d(TAG, "OnResultFetchedCallback Called with data size" + model.size());
            PageFragment pagefragment = (PageFragment) TagToFrag.get(PAGEFRAG);
            if (pagefragment != null)
                pagefragment.addNewData(model);
            else
                Toast.makeText(MainActivity.this, TagToFrag.get(PAGEFRAG).toString(), Toast.LENGTH_SHORT).show();
        }
    }
