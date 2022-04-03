package com.example.enviromax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private Fragment currentFragment;
    private String currentFragmentTitle;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.fullScreenCall(this);
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
        showFragment(new Fragment_Home(), getResources().getString(R.string.home));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }


    // TODO Fix this!
//    @Override
//    public void onBackPressed() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        int index = fragmentManager.getBackStackEntryCount() - 1;
//        String tag = null;
//        Fragment fragment = null;
//        try {
//            tag = fragmentManager.getBackStackEntryAt(index).getName();
//            fragment = fragmentManager.findFragmentByTag(tag);
//        } catch (Exception e) {};
//
//        if (fragment != null && tag != null)
//            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(tag).commit();
//        else
//            super.onBackPressed();
//    }
//
//    private void showFragment(Fragment theFragment, String title) {
//        if (currentFragmentTitle != null && currentFragmentTitle.equals(title)) {
//            return;
//        }
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentByTag(title);
//        if (fragment == null) {
//            try {
////                fragment = (Fragment) theFragment.newInstance();
//                fragmentManager.beginTransaction().replace(R.id.flContent, theFragment, title).addToBackStack(title).commit();
//                fragmentManager.executePendingTransactions();
//                currentFragment = theFragment;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, title).addToBackStack(title).commit();
//            currentFragment = fragment;
//        }
//        currentFragmentTitle = title;
//        setTitle(title);
//    }

    private void showFragment(Fragment theFragment, String title) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, theFragment, title).commit();
        fragmentManager.executePendingTransactions();
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
        boolean logOut = false;
        switch(menuItem.getItemId()) {
            case R.id.nav_home_fragment:
                fragment = new Fragment_Home();
                break;
            case R.id.nav_map_fragment:
                fragment = new Fragment_Map();
                break;
            case R.id.nav_personalDetails_fragment:
                fragment = new Fragment_PersonalDetails();
                break;
            case R.id.nav_logOut:
                logOut = true;
//                fragment = new Activity_Login();
                break;
            default:
                fragment = new Fragment_Map();
        }

        if (logOut) {
            Utils.logout(this);
        } else {
            showFragment(fragment, (String)menuItem.getTitle());
            mDrawer.closeDrawers();
        }
    }
}