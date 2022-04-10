package com.example.enviromax;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Utils {

    public static void logout(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, Activity_Login.class);
                FirebaseAuth.getInstance().signOut();
                AuthUI.getInstance()
                        .signOut(activity)
                        .addOnCompleteListener(new OnCompleteListener<Void>(){

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                activity.startActivity(intent);
                activity.finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public static int[] translateIntensityToColors(FirebaseDB.IntensityEnum intensity) {
        switch (intensity) {
            case LOW:
                return new int[]{
                        MyColor.GREEN,
                        MyColor.GREEN,
                        MyColor.GREEN,
                        MyColor.GREEN,
                        MyColor.GREEN,
                        MyColor.GREEN
                };
            case LOW_MID:
                return new int[]{
                        MyColor.GREEN,
                        MyColor.GREEN,
                        MyColor.GREEN,
                        MyColor.YELLOW,
                        MyColor.YELLOW,
                        MyColor.Orange
                };
            case MID:
                return new int[]{
                        MyColor.GREEN,
                        MyColor.YELLOW,
                        MyColor.YELLOW,
                        MyColor.Orange,
                        MyColor.RED,
                        MyColor.RED
                };
            case MID_HIGH:
                return new int[]{
                        MyColor.GREEN,
                        MyColor.Orange,
                        MyColor.Orange,
                        MyColor.RED,
                        MyColor.RED,
                        MyColor.DarkOrchid,
                };
            case HIGH:
                return new int[]{
                        MyColor.Orange,
                        MyColor.RED,
                        MyColor.RED,
                        MyColor.DarkOrchid,
                        MyColor.DarkOrchid,
                        MyColor.Brown
                };
            case HIGHEST:
                return new int[]{
                        MyColor.Orange,
                        MyColor.RED,
                        MyColor.DarkOrchid,
                        MyColor.DarkOrchid,
                        MyColor.Brown,
                        MyColor.Brown
                };
            default:
                return new int[]{
                        MyColor.GREEN,
                        MyColor.YELLOW,
                        MyColor.YELLOW,
                        MyColor.Orange,
                        MyColor.Orange,
                        MyColor.RED
                };
        }
    }

    public static void fullScreenCall(Activity activity) {
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
    }
}

































