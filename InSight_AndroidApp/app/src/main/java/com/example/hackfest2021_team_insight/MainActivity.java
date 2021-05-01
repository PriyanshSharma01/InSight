package com.example.hackfest2021_team_insight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private FragPagerAdapter tabPagerAdapter;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("InSight");
        toolbar.setTitleTextColor(Color.WHITE);

        NavigationView navigationView = findViewById(R.id.nav_view);

        tabLayout=findViewById(R.id.main_tab);
        viewPager=findViewById(R.id.tabPager);

        tabLayout.setupWithViewPager(viewPager);
        tabPagerAdapter= new FragPagerAdapter(getSupportFragmentManager());


        tabPagerAdapter.addFragment(new TextFragment("Read"));
        tabPagerAdapter.addFragment(new TextFragment("Explore"));
        tabPagerAdapter.addFragment(new TextFragment("Recognition"));

        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class FragPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public FragPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "Read";
                case 1:
                    return "Explore";
                case 2:
                    return "Recognition";
                default:
                    return null;
            }
        }
        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            //super.restoreState(state, loader);
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }


}