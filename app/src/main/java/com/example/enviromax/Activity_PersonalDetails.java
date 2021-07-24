package com.example.enviromax;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

public class Activity_PersonalDetails extends AppCompatActivity implements myDrawerInterface {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.removeStatusBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__personal_details);

        findViews();
        initViews();
    }

    private void findViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
    }

    private void initViews() {
    }

    @Override
    public void ClickMap(View view) {
        Utils.redirectActivity(this, MainActivity.class);
    }

    @Override
    public void ClickLogo(View view) { DrawerUtils.closeDrawer(drawerLayout); }

    @Override
    public void ClickPersonalDetails(View view) { DrawerUtils.closeDrawer(drawerLayout); }

    @Override
    public void ClickLogOff(View view) { Utils.logout(this); }

    @Override
    public void ClickMenu(View view) { DrawerUtils.openDrawer(drawerLayout); }
}