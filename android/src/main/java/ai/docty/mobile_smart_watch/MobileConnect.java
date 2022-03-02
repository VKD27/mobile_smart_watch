package ai.docty.mobile_smart_watch;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.yc.pedometer.sdk.BLEServiceOperate;
import com.yc.pedometer.sdk.BluetoothLeService;
import com.yc.pedometer.sdk.DeviceScanInterfacer;
import com.yc.pedometer.utils.SPUtil;

import java.util.ArrayList;

import ai.docty.mobile_smart_watch.model.BleDevices;
import ai.docty.mobile_smart_watch.util.WatchConstants;
import no.nordicsemi.android.dfu.DfuServiceInitiator;


public class MobileConnect {

    public static final String CONNECTED_DEVICE_CHANNEL = "connected_device_channel";
    public static final String FILE_SAVED_CHANNEL = "file_saved_channel";
    public static final String PROXIMITY_WARNINGS_CHANNEL = "proximity_warnings_channel";

    private  Context context;
    private  Activity activity;

    private final BLEServiceOperate mBLEServiceOperate;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<BleDevices> mLeDevices;

    private Handler mHandler;
    private boolean mScanning;

//    private final long SCAN_PERIOD = 10000;
//    private final int REQUEST_ENABLE_BT = 1122;

    public MobileConnect(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.mBLEServiceOperate =  BLEServiceOperate.getInstance(context);
        /*mBLEServiceOperate.setServiceStatusCallback(new ServiceStatusCallback() {
            @Override
            public void OnServiceStatuslt(int i) {
                if (i == ICallbackStatus.BLE_SERVICE_START_OK) {
                    Log.e("inside_service_result", ""+mBluetoothLeService);
                    //Service startup is complete, get the service object
                    mBluetoothLeService = mBLEServiceOperate.getBleService();
                }
            }
        });*/
        //if (mBLEServiceOperate!=null){
         //   this.mBluetoothLeService = mBLEServiceOperate.getBleService();
       // }

        initializeDfuService(this.context);
    }

    private void initializeDfuService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context);
            //activity.
            final NotificationChannel channel = new NotificationChannel(CONNECTED_DEVICE_CHANNEL, context.getString(R.string.channel_connected_devices_title), NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(context.getString(R.string.channel_connected_devices_description));
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationChannel fileChannel = new NotificationChannel(FILE_SAVED_CHANNEL, context.getString(R.string.channel_files_title), NotificationManager.IMPORTANCE_LOW);
            fileChannel.setDescription(context.getString(R.string.channel_files_description));
            fileChannel.setShowBadge(false);
            fileChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            final NotificationChannel proximityChannel = new NotificationChannel(PROXIMITY_WARNINGS_CHANNEL, context.getString(R.string.channel_proximity_warnings_title), NotificationManager.IMPORTANCE_LOW);
            proximityChannel.setDescription(context.getString(R.string.channel_proximity_warnings_description));
            proximityChannel.setShowBadge(false);
            proximityChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(fileChannel);
            notificationManager.createNotificationChannel(proximityChannel);
        }
    }

    public BLEServiceOperate getBLEServiceOperate(){
        return this.mBLEServiceOperate;
    }

    public void setBluetoothLeService(BluetoothLeService mBluetoothLeService) {
        this.mBluetoothLeService = mBluetoothLeService;
    }

    public BluetoothLeService getBluetoothLeService(){
        return this.mBluetoothLeService;
    }
    
    public boolean isBleEnabled() {
       // this. mBLEServiceOperate = BLEServiceOperate.getInstance(context);
        // mBLEServiceOperate.setDeviceScanListener(this);
        return this.mBLEServiceOperate.isBleEnabled();
    }

    public boolean checkBlu4() {
        return this.mBLEServiceOperate.isSupportBle4_0();
    }

    public String startListeners() {
       /* this.mBLEServiceOperate.setServiceStatusCallback(new ServiceStatusCallback() {
            @Override
            public void OnServiceStatuslt(int i) {
                if (i == ICallbackStatus.BLE_SERVICE_START_OK) {
                    //Service startup is complete, get the service object
                    mBluetoothLeService = mBLEServiceOperate.getBleService();
                }
            }
        });*/
        
        this.mBLEServiceOperate.setDeviceScanListener(new DeviceScanInterfacer() {
            @Override
            public void LeScanCallback(BluetoothDevice device, int rssi, byte[] bytes) {
                // Log.e("inside_scan ", "LeScanCallback");
                //Log.e("inside_scan ", "LeScanCallback::"+rssi);
                if (mLeDevices == null) {
                    mLeDevices = new ArrayList<>();
                }
                if (device != null && device.getName() != null) {
                    //Log.e("inside_device", "" + device.getName());

                    if (!TextUtils.isEmpty(device.getName())) {
                        BleDevices mBleDevices;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            mBleDevices = new BleDevices(device.getName(), device.getAddress(), rssi, device.getType(), device.getBondState(), device.getAlias());
                        } else {
                            mBleDevices = new BleDevices(device.getName(), device.getAddress(), rssi, device.getType(), device.getBondState(), "");
                        }
                        addDevice(mBleDevices);
                        //Log.e("afterAddDevice", "" + mLeDevices);
                    }
                }/* else {
                    Log.e("ble_scan_exp: ", "LeScanCallback::" + rssi);
                }*/
            }
        });
        //return "initiated";
        return WatchConstants.SC_INIT;
    }

    public String startDevicesScan() {
        clearDeviceList();
        Log.e("mBLEServiceOperate",""+this.mBLEServiceOperate);
        //mScanning = false;
        this.mBLEServiceOperate.startLeScan();
        return "success";
    }

    public String stopDevicesScan() {
        //mScanning = false;
        this.mBLEServiceOperate.stopLeScan();
        return "success";
    }

    public boolean connectDevice(String macAddress) {
        Log.e("mBLEServiceOperate: ",""+mBLEServiceOperate);
        Log.e("getBleService: ",""+mBLEServiceOperate.getBleService());
        boolean status = this.mBLEServiceOperate.connect(macAddress);
        Log.e("ble_service_operate: ",""+status);
        /*if (mBluetoothLeService!= null)
        {
            boolean bleServiceStatus = this.mBluetoothLeService.connect(macAddress);
            Log.e("bleServiceStatus: ",""+bleServiceStatus);
        }*/
        SPUtil.getInstance(context).setLastConnectDeviceAddress(macAddress);
        //mScanning = false;
     //  return this.mBLEServiceOperate.connect(macAddress);
       /* this.mBLEServiceOperate.setServiceStatusCallback(new ServiceStatusCallback() {
            @Override
            public void OnServiceStatuslt(int i) {
                if(i== ICallbackStatus.CONNECTED_STATUS){

                }
            }
        });*/

       //if (status){
//       }else{
//           if (this.mBLEServiceOperate!=null){
//               this.mBLEServiceOperate.disConnect();
//               if (this.mBluetoothLeService != null) {
//                   this.mBluetoothLeService.disconnect();
//               }
//               connectDevice(macAddress);
//           }
//       }
       return status;
      //  return "success";
    }

    public boolean disconnectDevice() {
        if (mBLEServiceOperate != null) {
            this.mBLEServiceOperate.disConnect();
            if (this.mBluetoothLeService != null) {
               // this.mBluetoothLeService.disconnect();
                this.mBluetoothLeService.ClearGattForDisConnect();
              //  this.mBLEServiceOperate.unBindService();
            }
            return true;
        }else{
            return false;
        }
    }

    public boolean clearGattDisconnect() {
        if (this.mBluetoothLeService != null) {
            this.mBluetoothLeService.ClearGattForDisConnect();
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<BleDevices> getDevicesList() {
        if (this.mLeDevices == null) {
            return new ArrayList<>();
        }else{
            Log.e("getDevicesList ", "BleDevices::" + this.mLeDevices.size());
            return this.mLeDevices;
        }
    }

    private void addDevice(BleDevices device) {
        Log.e("addDevice ", "BleDevices::" + device.getAddress());
        boolean repeat = false;
        for (int i = 0; i < this.mLeDevices.size(); i++) {
            if (this.mLeDevices.get(i).getAddress().equals(device.getAddress())) {
                this.mLeDevices.remove(i);
                repeat = true;
                //break;
                device.setIndex(i);
                this.mLeDevices.add(i, device);
            }
        }
        if (!repeat) {
            this.mLeDevices.add(device);
        }
    }

    public BleDevices getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clearDeviceList() {
        if (mLeDevices != null) {
            mLeDevices.clear();
        } else {
            mLeDevices = new ArrayList<>();
        }

    }



     /* public String initBlueConnect() {
        mBLEServiceOperate = BLEServiceOperate.getInstance(context);
        if (!mBLEServiceOperate.isBleEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (!mBLEServiceOperate.isSupportBle4_0()) {
            return "not supported";
        }
        mBLEServiceOperate.setDeviceScanListener(this);
        return "initiated";
       *//* mBLEServiceOperate.setDeviceScanListener(new DeviceScanInterfacer() {
            @Override
            public void LeScanCallback(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

            }
        });*//*
        // Checks if Bluetooth is supported on the device.
       *//* if (!mBLEServiceOperate.isSupportBle4_0()) {
            Toast.makeText(this, R.string.not_support_ble, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }*//*
    }*/

    /*public String startDevicesScan() {
        ArrayList<BleDevices> mDevices = new ArrayList<>();
        clearDeviceList();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                mBLEServiceOperate.stopLeScan();
                // mDevices = mLeDevices;
            }
        }, SCAN_PERIOD);
        mScanning = true;
        mBLEServiceOperate.startLeScan();
        // return list of devices list json
        return "";
    }*/

 /* private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEServiceOperate.stopLeScan();
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBLEServiceOperate.startLeScan();
            LogUtils.i(TAG,"startLeScan");
        } else {
            mScanning = false;
            mBLEServiceOperate.stopLeScan();
        }
        invalidateOptionsMenu();
    }*/
   /* @Override
    public void LeScanCallback(BluetoothDevice device, int rssi, byte[] bytes) {

        activity.runOnUiThread( new Runnable(){
            @Override
            public void run() {
                Log.e("inside_scan ", "LeScanCallback");
                if (mLeDevices == null) {
                    mLeDevices = new ArrayList<BleDevices>();
                }
                if (device != null) {
                    Log.e("inside_scan_device", device.getName());

                    if (!TextUtils.isEmpty(device.getName())) {
                        BleDevices mBleDevices = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            mBleDevices = new BleDevices(device.getName(), device.getAddress(), rssi, device.getType(), device.getBondState(), device.getAlias());
                        }else{
                            mBleDevices = new BleDevices(device.getName(), device.getAddress(), rssi, device.getType(), device.getBondState(), "");
                        }
                        addDevice(mBleDevices);
                    }
                }
            }
        });

    }*/

}
