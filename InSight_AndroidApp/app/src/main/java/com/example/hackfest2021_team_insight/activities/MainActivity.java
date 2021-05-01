package com.example.hackfest2021_team_insight.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.hackfest2021_team_insight.PageViewAdapter;
import com.example.hackfest2021_team_insight.R;
import com.example.hackfest2021_team_insight.fragments.ExploreFragment;
import com.example.hackfest2021_team_insight.fragments.RecognisationFragment;
import com.example.hackfest2021_team_insight.fragments.TextFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("InSight");
        toolbar.setTitleTextColor(Color.WHITE);

        NavigationView navigationView = findViewById(R.id.nav_view);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

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

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public void setUpViewPager(ViewPager viewPager) {
        PageViewAdapter pageViewAdapter = new PageViewAdapter(getSupportFragmentManager(), 0);
        TextFragment readFragTab = new TextFragment();
        ExploreFragment exploreFragTab = new ExploreFragment();
        RecognisationFragment recognitionFragTab = new RecognisationFragment();

        pageViewAdapter.addFragment(readFragTab, "Read");
        pageViewAdapter.addFragment(exploreFragTab, "Explore");
        pageViewAdapter.addFragment(recognitionFragTab, "Recognition");
        viewPager.setAdapter(pageViewAdapter);
    }


}