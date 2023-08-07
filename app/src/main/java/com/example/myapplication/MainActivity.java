package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private BroadcastReceiver packageChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);

        // Register the BroadcastReceiver to listen for package changes
        packageChangeReceiver = new PackageChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(packageChangeReceiver, filter);

        // Display the last 5 recently installed non-system apps on the screen
        updateResultTextView();
    }

    // Get the installation time of the app
    private long getInstallationTime(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get the app name
    private String getAppName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Save the installation time and app name to the database
    private void saveInstallationTimeAndAppName(Context context, String packageName, long installationTime, String appName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.saveInstallationTimeAndAppName(packageName, installationTime, appName);
    }

    // Update the result TextView with the last 5 recently installed non-system apps
    private void updateResultTextView() {
        List<AppInfo> recentlyInstalledApps = getRecentlyInstalledNonSystemApps(getApplicationContext());

        // Display the result on the screen
        StringBuilder resultBuilder = new StringBuilder();
        for (AppInfo appInfo : recentlyInstalledApps) {
            String result = "App Name: " + appInfo.appName + "\nInstallation Time: " + appInfo.installationTime + "\n\n";
            resultBuilder.append(result);
        }

        resultTextView.setText(resultBuilder.toString());
    }

    // Get the last 5 recently installed non-system apps
    private List<AppInfo> getRecentlyInstalledNonSystemApps(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        Collections.reverse(packageInfoList); // Reverse the list to get the most recent installations first

        int count = 0;
        for (PackageInfo packageInfo : packageInfoList) {
            String packageName = packageInfo.packageName;
            ApplicationInfo appInfo = packageInfo.applicationInfo;

            // Skip system apps and the current app's information (your own app)
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !packageName.equals(context.getPackageName())) {
                long installationTime = packageInfo.firstInstallTime;
                String appName = getAppName(context, packageName);

                saveInstallationTimeAndAppName(context, packageName, installationTime, appName);
                appInfoList.add(new AppInfo(appName, installationTime));
                count++;

                if (count == 5) {
                    break; // Limit the list to the last 5 recently installed non-system apps
                }
            }
        }

        return appInfoList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterReceiver(packageChangeReceiver);
    }

    // BroadcastReceiver to listen for package changes
    private class PackageChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                String packageName = intent.getDataString().substring(8); // Remove "package:" prefix

                // If a package is added or removed, update the result TextView
                if (Intent.ACTION_PACKAGE_ADDED.equals(action) || Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                    updateResultTextView();
                }
            }
        }
    }

    // AppInfo class to store app information (app name and installation time)
    private static class AppInfo {
        String appName;
        long installationTime;

        AppInfo(String appName, long installationTime) {
            this.appName = appName;
            this.installationTime = installationTime;
        }
    }
}
