package howard2048.blescanner;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.util.Log;
import android.util.SparseArray;

import java.util.List;

public class MyScanCallback extends ScanCallback {

    private static final String TAG = MyScanCallback.class.getSimpleName();

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        Log.d(TAG, "[onScanResult]");
        super.onScanResult(callbackType, result);
        String macAddress = result.getDevice().getAddress();
        int rssi = result.getRssi();
        Log.d(TAG, "mac: " + macAddress + ", rssi: " + rssi);
        ScanRecord record = result.getScanRecord();
        if (record != null) {
            List<?> uuid = record.getServiceUuids();
            byte[] bytes = record.getBytes();

            SparseArray<byte[]> arr = record.getManufacturerSpecificData();
            for (int i = 0; i < arr.size(); i++) {
                int key = arr.keyAt(i);
                byte[] data = arr.get(key);
                Log.d(TAG, Utils.bytesToString(data));
            }

            byte[] specificData = record.getManufacturerSpecificData(0x0006);
            if (specificData != null && specificData.length != 0) {
                Log.d(TAG, "manufacturerSpecificData length: " + specificData.length);
                Log.i(TAG, Utils.bytesToString(specificData) + ", " + macAddress + ", RSSI: " + rssi);
            }
        }
    }
}
