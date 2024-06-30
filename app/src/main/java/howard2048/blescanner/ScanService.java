package howard2048.blescanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

public class ScanService extends Service {
    private static final String TAG = ScanService.class.getSimpleName();

    private static final String CHANNEL_ID = "SS";
    private static final String CHANNEL_NAME = "ScanningService";

    private BluetoothLeScanner scanner = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "[onStartCommand]");
        PackageManager pm = getPackageManager();
        boolean hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (hasBluetooth) {
            BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager != null) {
                BluetoothAdapter adapter = manager.getAdapter();
                if (adapter != null) {
                    if (adapter.isEnabled()) {
                        scanner = adapter.getBluetoothLeScanner();
                        Log.i(TAG, "scanner: " + (scanner != null));
                    }
                } else {
                    Log.w(TAG, "bluetoothAdapter is null");
                }
            } else {
                Log.w(TAG, "bluetoothManager is null");
            }
        } else {
            Log.w(TAG, "Does NOT support bluetooth");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.i(TAG, "***");
            startScan();
        }
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void startScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "no permission: " + Manifest.permission.BLUETOOTH_SCAN);
            return;
        }

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().build());
        ScanSettings settings = new ScanSettings.Builder()
                .build();

        Context context = getApplicationContext();
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.setAction(BluetoothDevice.ACTION_FOUND);

        int requestCode = 1;
        int flags = PendingIntent.FLAG_MUTABLE;

        PendingIntent callbackIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);
        Log.i(TAG, "---------> starting to scan");

        boolean useCallback = false;
        if (useCallback) {
            scanner.startScan(null, settings, new MyScanCallback());
        } else {
            int result = scanner.startScan(null, null, callbackIntent);
            Log.i(TAG, "---------> result: " + result);
        }
    }
}
