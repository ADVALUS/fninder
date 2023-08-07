package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);

        // Retrieve the installation time and app name
        long installationTime = getInstallationTime(getApplicationContext());
        String appName = getAppName(getApplicationContext());

        // Save the installation time and app name to the database
        saveInstallationTimeAndAppName(getApplicationContext(), installationTime, appName);

        // Display the result on the screen
        String result = "App Name: " + appName + "\nInstallation Time: " + installationTime;
        resultTextView.setText(result);
    }

    // Get the installation time of the app
    private long getInstallationTime(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get the app name
    private String getAppName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Save the installation time and app name to the database
    private void saveInstallationTimeAndAppName(Context context, long installationTime, String appName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.saveInstallationTimeAndAppName(installationTime, appName);
    }
}
