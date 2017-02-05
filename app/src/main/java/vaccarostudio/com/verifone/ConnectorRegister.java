package vaccarostudio.com.verifone;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lucavaccaro on 25/02/14.
 */
public class ConnectorRegister extends BroadcastReceiver {
    public static final String ACTION_USB_PERMISSION = "vaccarostudio.com.sdk.USB_PERMISSION";
    private String TAG = "ConnectorRegister";

    public static ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
    private static boolean isConnected=false;

    private static ConnectorRegister connectorRegister = null;

    public static ConnectorRegister getInstance() {
        if (connectorRegister == null)
            connectorRegister = new ConnectorRegister();
        return connectorRegister;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = null;
        String action = intent.getAction();
        Log.d(TAG,action);
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            ;//Log.d(TAG, "Usb is plugged");

                // App is in Foreground
                service = new Intent(context, ConnectorIntent.class);
                service.setAction(ConnectorIntent.ACTION_USBPERMISSION);

        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            ;//Log.d(TAG, "Usb is unplugged");
            service = new Intent(context, ConnectorIntent.class);
            service.setAction(ConnectorIntent.ACTION_USBDETACH);
        } else if (ACTION_USB_PERMISSION.equals(action)) {
            // Usb detect attach/detach
            synchronized (this) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if (device != null) {
                        try {
                            SerialIO.Set((UsbManager) context.getSystemService(Context.USB_SERVICE), device);
                            ;//Log.d(TAG, "Usb is plugged");
                            service = new Intent(context, ConnectorIntent.class);
                            service.setAction(ConnectorIntent.ACTION_USBATTACH);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ;//Log.d(TAG, "Error: " + e.getMessage());
                            ;//Log.d(TAG, "Usb is unplugged");
                            service = new Intent(context, ConnectorIntent.class);
                            service.setAction(ConnectorIntent.ACTION_USBDETACH);
                        }
                    } else
                        ;//Log.d(TAG, "No connect");
                } else {
                    ;//Log.d(TAG, "permission denied for device " + device);
                }
            }
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Bluetooth start discover
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            ;//Log.d(TAG, "Bluetooth Device Find");
        } else if (BluetoothDevice.ACTION_UUID.equals(action)) {
            // Bluetooth uuid discover
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            if (uuidExtra == null)
                return;
            for (int i = 0; i < uuidExtra.length; i++) {
                ;//Log.d(TAG, "Bluetooth UUID Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
                btDeviceList.add(device);
                /*if (uuidExtra[i].toString().equals(mpayReqUuid.toString())){
                    // Add the name and address to an array adapter to show in a ListView
                    ;//Log.d(TAG,"Device " + device.getName() + " found");

                    btDeviceList.add(device);
                    mPairedList.add(device.getName() + "\n" + device.getAddress());
                    mPairedList.notifyDataSetChanged();
                }*/
            }
            service = new Intent(context, ConnectorIntent.class);
            service.setAction(ConnectorIntent.ACTION_UUID);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            ;//Log.d(TAG, "Bluetooth Discovery Started...");
            btDeviceList.clear();
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            ;//Log.d(TAG, "Bluetooth Discovery Finished");
            Iterator<BluetoothDevice> itr = btDeviceList.iterator();
            while (itr.hasNext()) {
                // Get Services for paired devices
                BluetoothDevice device = itr.next();
                device.getUuids();
                ;//Log.d(TAG, "\nGetting Services for " + device.getName() + ", " + device);
                if (!device.fetchUuidsWithSdp()) {
                    ;//Log.d(TAG, "\nSDP Failed for " + device.getName());
                } else {
                    //mPairedList.add(device.getName() + "\n" + device.getAddress());
                }
            }
            service = new Intent(context, ConnectorIntent.class);
            service.setAction(ConnectorIntent.ACTION_DISCOVER);
        }
        else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Device is now connected
            Log.d(TAG, "Bluetooth connected");
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
            Log.d(TAG, "Bluetooth about disconnected");
            //service = new Intent(context, ConnectorIntent.class);
            //service.setAction(ConnectorIntent.ACTION_BLUETOOTHDETACH);
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
            ;//Log.d(TAG, "Bluetooth disconnected");
            //service = new Intent(context, ConnectorIntent.class);
            //service.setAction(ConnectorIntent.ACTION_BLUETOOTHDETACH);
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d(TAG, "Bluetooth disconnected");
            if (device!=null
                    /*&& device.getName()!=null
                    && Settings.JuspBT.get()!=null
                    && Settings.JuspBT.get().getBluetoothDeviceName()!=null
                    && device.getName().equals(Settings.JuspBT.get().getBluetoothDeviceName())*/) {
                service = new Intent(context, ConnectorIntent.class);
                service.setAction(ConnectorIntent.ACTION_BLUETOOTHDETACH);
            }

        } else {
            // internet
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // Wifi enabled
                    if (isConnected==false) {
                        ;//Log.d(TAG, "Internet wifi enable");
                        service = new Intent(context, ConnectorIntent.class);
                        service.setAction(ConnectorIntent.ACTION_SYNCHRONIZE);
                        isConnected=true;
                    }
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // Mobile data enabled
                    if (isConnected==false) {
                        ;//Log.d(TAG, "Internet Mobile enable");
                        service = new Intent(context, ConnectorIntent.class);
                        service.setAction(ConnectorIntent.ACTION_SYNCHRONIZE);
                        isConnected=true;
                    }
                } else {
                    //Not connected to Internet
                    ;//Log.d(TAG, "Internet disable");
                    isConnected=false;
                }
            }else {
                //Not connected to Internet
                ;//Log.d(TAG, "Internet disable");
                isConnected=false;
            }
        }
        if (service!=null)
            context.startService(service);
    }



    public boolean isAppForground(Context mContext) {

        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }

        return true;
    }

}
