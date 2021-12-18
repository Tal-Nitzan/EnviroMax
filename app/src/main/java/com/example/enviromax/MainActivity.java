package com.example.enviromax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
//    private Fragment currentFragment;
//    private String currentFragmentTitle;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.removeStatusBar(this);
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Start in Fragment_Home view.
        showFragment(Fragment_Home.class, getResources().getString(R.string.home));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void showFragment(Class theFragment, String title) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) theFragment.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        setTitle(title);
    }

//    private void showFragment(Class theFragment, String title) {
//        if (isFragmentAlreadyRunning(theFragment, title))
//            return;
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = (Fragment)fragmentManager.findFragmentByTag(title);
//        if (fragment == null) { // Unknown fragment
//        try {
//                if (currentFragment != null) {
//                    fragmentManager.beginTransaction().detach(currentFragment).commit();
//                    fragmentManager.executePendingTransactions();
//                }
//                fragment = (Fragment) theFragment.newInstance();
//                fragmentManager.beginTransaction().add(R.id.flContent, fragment, title).commit();
//                fragmentManager.executePendingTransactions();
//                setTitle(title);
//                currentFragment = fragment;
//                currentFragmentTitle = title;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                fragmentManager.beginTransaction().detach(currentFragment).commit();
//                fragmentManager.executePendingTransactions();
//                fragmentManager.beginTransaction().attach(fragment).commit();
//                fragmentManager.executePendingTransactions();
//                setTitle(title);
//                currentFragment = fragment;
//                currentFragmentTitle = title;
//            }  catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private boolean isFragmentAlreadyRunning(Class theFragment, String title) {
//        Fragment myFragment = (Fragment)getSupportFragmentManager().findFragmentByTag(title);
//        if (myFragment != null && currentFragmentTitle != null &&  currentFragmentTitle.equals(title)) {
//            return true;
//        }
//        return false;
//    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) { // TODO save state
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        boolean logOut = false;
        switch(menuItem.getItemId()) {
            case R.id.nav_home_fragment:
                fragmentClass = Fragment_Home.class;
                break;
            case R.id.nav_map_fragment:
                fragmentClass = Fragment_Map.class;
                break;
            case R.id.nav_personalDetails_fragment:
                fragmentClass = Fragment_PersonalDetails.class;
                break;
            case R.id.nav_logOut:
                logOut = true;
                fragmentClass = Activity_Login.class;
                break;
            default:
                fragmentClass = Fragment_Map.class;
        }

        if (logOut) {
            Utils.logout(this);
        } else {
            showFragment(fragmentClass, (String)menuItem.getTitle());
            mDrawer.closeDrawers();
        }
    }
}