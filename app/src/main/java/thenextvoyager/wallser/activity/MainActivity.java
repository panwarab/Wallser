package thenextvoyager.wallser.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import thenextvoyager.wallser.Data.Constants;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.SimpleFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants constants = new Constants(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setUpNavigationView();
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

    void selectedDrawerItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                
                break;
        }
    }
}
