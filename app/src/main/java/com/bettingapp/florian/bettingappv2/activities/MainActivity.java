package com.bettingapp.florian.bettingappv2.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bettingapp.florian.bettingappv2.R;
import com.bettingapp.florian.bettingappv2.adapters.BetAdapter;
import com.bettingapp.florian.bettingappv2.fragments.BetsFragment;
import com.bettingapp.florian.bettingappv2.fragments.NewBetFragment;
import com.bettingapp.florian.bettingappv2.fragments.OnFragmentInteractionListener;
import com.bettingapp.florian.bettingappv2.fragments.ProfileFragment;
import com.bettingapp.florian.bettingappv2.model.Bet;
import com.bettingapp.florian.bettingappv2.repo.LoadedListener;
import com.bettingapp.florian.bettingappv2.repo.Repository;
import com.bettingapp.florian.bettingappv2.session.UserSessionManager;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener{


    UserSessionManager session;

    private NavigationView navView;

    private Fragment betsFragment;
    private Fragment newBetFragment;
    private Fragment profileFragment;

    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new UserSessionManager(getApplicationContext());
        if (!session.checkLogin()) {
            finish();
            return;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        createDrawerMenu();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //toggleTranslateFAB(slideOffset);

                //mDrawerList.bringToFront();
                //drawerLayout.requestLayout();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.drawable.betslogo);

        betsFragment = new BetsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentpane, betsFragment).commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            if (profileFragment == null || !profileFragment.isVisible()) {
                profileFragment = new ProfileFragment();
                transaction.replace(R.id.fragmentpane, profileFragment);
                transaction.commit();
            }
        } else if (id == R.id.nav_allbets) {
            if (betsFragment == null || !betsFragment.isVisible()) {
                betsFragment = new BetsFragment();
                transaction.replace(R.id.fragmentpane, betsFragment);
                transaction.commit();
            }

        } else if (id == R.id.nav_newbet) {
            if (newBetFragment == null || !newBetFragment.isVisible()) {
                newBetFragment = new NewBetFragment();
                transaction.replace(R.id.fragmentpane, newBetFragment);
                transaction.commit();
            }

        } else if (id == R.id.nav_logout) {
            Repository.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(OnFragmentInteractionListener.fragment fragment, int id) {
        if(fragment== OnFragmentInteractionListener.fragment.betsfragment) {
            navView = (NavigationView) findViewById(R.id.nav_view);
            navView.setCheckedItem(R.id.nav_allbets);
            onNavigationItemSelected(navView.getMenu().findItem(R.id.nav_allbets));
        }
    }

    public void toggleTranslateFAB(float slideOffset) {
        mFAB.setTranslationX(slideOffset * 200);
    }

    public void createDrawerMenu() {

        try {
            navView = (NavigationView) findViewById(R.id.nav_view);

            navView.setNavigationItemSelectedListener(this);
            View headerView = navView.getHeaderView(0);

            LinearLayout drawerHeader = (LinearLayout) headerView.findViewById(R.id.drawer_header);
            drawerHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navView.setCheckedItem(R.id.nav_profile);
                    onNavigationItemSelected(navView.getMenu().findItem(R.id.nav_profile));
                }
            });


            ImageView profilephoto = (ImageView) headerView.findViewById(R.id.user_profile_photo);
            TextView profilename = (TextView) headerView.findViewById(R.id.username);

            if(Repository.getCurrentUser().getProfilePictureUrl().equals("")){
                Glide.with(this).load(R.drawable.profile).centerCrop().into(profilephoto);
            }else {
                Glide.with(this).load(Repository.getCurrentUser().getProfilePictureUrl()).centerCrop().into(profilephoto);
            }
            profilename.setText(Repository.getCurrentUser().getUsername());

        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }


}
