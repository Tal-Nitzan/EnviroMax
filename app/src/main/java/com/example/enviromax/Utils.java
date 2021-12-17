package com.example.enviromax;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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

    public static User mainUser;

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

    public static void removeStatusBar(Activity activity) {
        // Remove status bar
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void initDevices() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://enviromax-8ead5-default-rtdb.europe-west1.firebasedatabase.app");

        DatabaseReference myRef = database.getReference().child("Devices");
        Log.d("xxxxxxx", "here");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("xxxxxxx", "...");
                for (DataSnapshot dsp : snapshot.getChildren()) {
                    Log.d("xxxxxxx", dsp.toString());
                }
                MainActivity.setNumOfActiveDevices((int)snapshot.getChildrenCount());
                Log.d("xxxxxxx", "" + MainActivity.getNumOfActiveDevices());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("xxxxxxx", error.getDetails());
            }
        });
    }

    public static int[] translateIntensityToColors(FirebaseDB.IntensityEnum intensity) {
        switch (intensity) {
            case LOW:
                return new int[]{
                        Color.GREEN,
                        Color.GREEN,
                        Color.GREEN,
                        Color.GREEN,
                        Color.GREEN,
                        Color.GREEN
                };
            case LOW_MID:
                return new int[]{
                        Color.GREEN,
                        Color.GREEN,
                        Color.GREEN,
                        Color.YELLOW,
                        Color.YELLOW,
                        Color.rgb(255, 165, 0)// orange
                };
            case MID:
                return new int[]{
                        Color.GREEN,
                        Color.YELLOW,
                        Color.YELLOW,
                        Color.rgb(255, 165, 0),// orange
                        Color.RED,
                        Color.RED
                };
            case MID_HIGH:
                return new int[]{
                        Color.GREEN,
                        Color.rgb(255, 165, 0), // orange
                        Color.rgb(255, 165, 0), // orange
                        Color.RED,
                        Color.RED,
                        Color.rgb(153, 50, 204) //dark orchid
                };
            case HIGH:
                return new int[]{
                        Color.rgb(255, 165, 0), // orange
                        Color.RED,
                        Color.RED,
                        Color.rgb(153, 50, 204), //dark orchid
                        Color.rgb(153, 50, 204), //dark orchid
                        Color.rgb(165, 42, 42) //brown(301-500)
                };
            case HIGHEST:
                return new int[]{
                        Color.rgb(255, 165, 0), // orange
                        Color.RED,
                        Color.rgb(153, 50, 204), //dark orchid
                        Color.rgb(153, 50, 204), //dark orchid
                        Color.rgb(165, 42, 42), //brown(301-500)
                        Color.rgb(165, 42, 42) //brown(301-500)
                };
            default:
                return new int[]{
                        Color.GREEN,
                        Color.YELLOW,
                        Color.YELLOW,
                        Color.rgb(255, 165, 0),// orange
                        Color.RED,
                        Color.RED
                };
        }
    }
}

































