package com.example.android.bluetoothlegatt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;


// import androidx.appcompat.app.AlertDialog;
// import androidx.core.app.ActivityCompat;

public class BluetoothPermissionManager {
    private static final String TAG = "BluetoothPermission";
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final String[] BLUETOOTH_PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    public static boolean hasBluetoothPermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : BLUETOOTH_PERMISSIONS) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestBluetoothPermissions(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH_ADMIN)) {
            new AlertDialog.Builder(activity)
                    .setTitle("Bluetooth Permission Required")
                    .setMessage("This app requires access to Bluetooth in order to scan for devices.")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, BLUETOOTH_PERMISSIONS, REQUEST_BLUETOOTH_PERMISSIONS);
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
        ActivityCompat.requestPermissions(activity, BLUETOOTH_PERMISSIONS, REQUEST_BLUETOOTH_PERMISSIONS);

    }
}
