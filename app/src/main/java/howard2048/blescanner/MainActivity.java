package howard2048.blescanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    private final String[] permissions = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE
    };

    private static final int REQUEST_CODE = 1;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        checkPermissions();
        Intent intent = new Intent(this, ScanService.class);
        startForegroundService(intent);
    }


    private void checkPermissions() {
        List<String> notGranted = new ArrayList<>();
        for (String permission : permissions) {
            Log.d(TAG, "checking permission: " + permission);
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "permission " + permission + " is NOT granted");
                notGranted.add(permission);
                break;
            }
        }
        if (!notGranted.isEmpty()) {
            Log.d(TAG, "requesting permissions: " + notGranted.size());
            requestPermissions(notGranted.toArray(new String[0]), MainActivity.REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> requestAgain = new ArrayList<>();
        String permission;
        int result;
        for (int i = 0; i < permissions.length; i++) {
            permission = permissions[i];
            result = grantResults[i];
            if (result == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    requestAgain.add(permission);
                } else {
                    Log.w(TAG, permission + " is denied twice");
                }
            }
        }
        if (!requestAgain.isEmpty()) {
            requestPermissions(requestAgain.toArray(new String[0]), MainActivity.REQUEST_CODE);
        }
    }
}
