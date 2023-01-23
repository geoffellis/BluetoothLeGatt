package com.example.android.bluetoothlegatt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import android.app.AlertDialog;
import android.support.v4.app.ActivityCompat;

// import androidx.appcompat.app.AlertDialog;
// import androidx.core.app.ActivityCompat;

public class LocationPermissionManager {
    private static final String TAG = "LocationPermission";
    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    private static final String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static boolean hasLocationPermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : LOCATION_PERMISSIONS) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void setRequestLocationPermissions(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(activity)
                    .setTitle("Location Permission Required")
                    .setMessage("This app requires access to Location in order to scan for devices.")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);

    }
}
