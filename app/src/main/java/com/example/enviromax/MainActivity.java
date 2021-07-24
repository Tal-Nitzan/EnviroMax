package com.example.enviromax;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements myDrawerInterface {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.removeStatusBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
    }

    public void findViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void initViews() {
    }

    @Override
    public void ClickMap(View view) { DrawerUtils.closeDrawer(drawerLayout); }

    public void ClickLogo(View view) {
        DrawerUtils.closeDrawer(drawerLayout);
    }

    public void ClickPersonalDetails(View view) {
        Utils.redirectActivity(this, Activity_PersonalDetails.class);
    }

    public void ClickLogOff(View view) { Utils.logout(this); }

    public void ClickMenu(View view) { DrawerUtils.openDrawer(drawerLayout); }
}