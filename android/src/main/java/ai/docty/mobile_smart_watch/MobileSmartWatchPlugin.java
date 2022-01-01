package ai.docty.mobile_smart_watch;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.yc.pedometer.info.CustomTestStatusInfo;
import com.yc.pedometer.info.DeviceParametersInfo;
import com.yc.pedometer.info.HeartRateHeadsetSportModeInfo;
import com.yc.pedometer.info.SportsModesInfo;
import com.yc.pedometer.sdk.BluetoothLeService;
import com.yc.pedometer.sdk.DataProcessing;
import com.yc.pedometer.sdk.ICallback;
import com.yc.pedometer.sdk.ICallbackStatus;
import com.yc.pedometer.sdk.WriteCommandToBLE;
import com.yc.pedometer.utils.GetFunctionList;
import com.yc.pedometer.utils.GlobalVariable;
import com.yc.pedometer.utils.SPUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.docty.mobile_smart_watch.model.BleDevices;
import ai.docty.mobile_smart_watch.util.WatchConstants;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


/**
 * MobileSmartWatchPlugin
 */
public class MobileSmartWatchPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware{

    /// PluginRegistry.ActivityResultListener
    ///FlutterPluginRegistry

    private FlutterPluginBinding flutterPluginBinding;
    private ActivityPluginBinding activityPluginBinding;

    private MethodChannel methodChannel;
    private MethodChannel mCallbackChannel;

    // Callbacks
    final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    // private Map<String, Runnable> callbackById = new HashMap<>();
    private Map<String, Map<String, Object>> mCallbacks = new HashMap<>();

    private Context mContext;
    private Activity activity;
    private Application mApplication;
    private MobileConnect mobileConnect;

    private final int REQUEST_ENABLE_BT = 1212;
    private Boolean validateDeviceListCallback = false;

    // pedometer integration
    private BluetoothLeService mBluetoothLeService;
    private WriteCommandToBLE mWriteCommand;
    // private Updates mUpdates;
    private DataProcessing mDataProcessing;


    MethodCallHandler callbacksHandler = new MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, Result result) {
            final String method = call.method;
            Log.e("calling_method", "callbacksHandler++ "+method);
            //WatchConstants.START_LISTENING.equalsIgnoreCase(method)
            //if ("startListening".equals(method)) {
            if (WatchConstants.START_LISTENING.equalsIgnoreCase(method)) {
                startListening(call.arguments, result);
            } else {
                result.notImplemented();
            }
        }
    };

    //sdk return results
    private Result flutterInitResultBlu;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding;
        this.mContext = flutterPluginBinding.getApplicationContext();
        setUpEngine(this, flutterPluginBinding.getBinaryMessenger(), flutterPluginBinding.getApplicationContext());

    }

    private void setUpEngine(MobileSmartWatchPlugin mobileSmartWatchPlugin, BinaryMessenger binaryMessenger, Context applicationContext) {
        methodChannel = new MethodChannel(binaryMessenger, WatchConstants.SMART_METHOD_CHANNEL); // "mobile_smart_watch"
        methodChannel.setMethodCallHandler(mobileSmartWatchPlugin);

        mCallbackChannel = new MethodChannel(binaryMessenger, WatchConstants.SMART_CALLBACK);
        mCallbackChannel.setMethodCallHandler(callbacksHandler);

        mobileConnect = new MobileConnect(applicationContext.getApplicationContext(), activity);
        mWriteCommand = WriteCommandToBLE.getInstance(applicationContext.getApplicationContext());
        mDataProcessing = DataProcessing.getInstance(applicationContext.getApplicationContext());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        String method = call.method;
        switch (method) {
            case WatchConstants.DEVICE_INITIALIZE:
                initDeviceConnection(result);
                break;
            case WatchConstants.START_DEVICE_SEARCH:
                searchForBTDevices( result);
                break;
           /* case WatchConstants.STOP_DEVICE_SEARCH:
                String resultStatus = mobileConnect.stopDevicesScan();
                result.success(resultStatus);
                break;*/
            case WatchConstants.BIND_DEVICE:
                connectBluDevice(call, result);
                break;
            case WatchConstants.UNBIND_DEVICE:
                 disconnectBluDevice(result);
                break;
            case WatchConstants.SET_USER_PARAMS:
                setUserParams(call, result);
                break;

            case WatchConstants.GET_DEVICE_BATTERY_VERSION:
                getDeviceBatteryNVersion(result);
                break;

            case WatchConstants.GET_SYNC_STEPS:
                // syncAllStepsData(call, result);
                break;
            case WatchConstants.GET_SYNC_RATE:
                //syncRateData(call, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    /*if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }*/
    }

    private void initDeviceConnection(Result result) {
        this.flutterInitResultBlu = result;
        if (mobileConnect != null) {
            boolean enable = mobileConnect.isBleEnabled();
            boolean blu4 = mobileConnect.checkBlu4();
            String resultStatus = mobileConnect.startListeners();
            Log.e("device_enable:", "" + enable);
            Log.e("device_blu4e:", "" + blu4);
            try {
                if (enable) {
                    if (blu4) {
                        //String resultStatus = mobileConnect.startListeners();
                        Log.e("init_res_status", "" + resultStatus);
                        result.success(resultStatus);
                    } else {
                        result.success(WatchConstants.BLE_NOT_SUPPORTED);
                    }
                } else {

                    //String resultStatus = mobileConnect.startListeners();
                    Log.e("else_res_status", "" + resultStatus);
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    result.success(resultStatus);
                }
            } catch (Exception exp) {
                Log.e("initDeviceExp::", "" + exp.getMessage());
            }
        } else {
            Log.e("device_connect_err:", "device_connect not initiated..");
        }
    }

    private void searchForBTDevices(Result result) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (mobileConnect != null) {
                new Handler().postDelayed(() -> {
                    String resultStat = mobileConnect.stopDevicesScan();
                    Log.e("resultStat ", "deviceScanStop::" + resultStat);
                    ArrayList<BleDevices> bleDeviceList = mobileConnect.getDevicesList();
                    JSONArray jsonArray = new JSONArray();

                    for (BleDevices device : bleDeviceList) {
                        Log.e("device_for ", "device::" + device.getName());
                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("name",device.getName());
                            jsonObj.put("address",device.getAddress());
                            jsonObj.put("rssi",device.getRssi());
                            jsonObj.put("deviceType",device.getDeviceType());
                            jsonObj.put("bondState",device.getBondState());
                            jsonObj.put("alias",device.getAlias());
                            jsonArray.put(jsonObj);

                        } catch (Exception e) {
                          //  e.printStackTrace();
                            Log.e("jsonExp::", "jsonParse::" + e.getMessage());
                        }
                    }
                    // mDevices = mLeDevices;
//                    Type listType = new TypeToken<ArrayList<BleDevices>>() {
//                    }.getType();
//                    String jsonString = new Gson().toJson(bleDeviceList, listType);
//                    JsonArray jsonArray2 = new Gson().toJsonTree(bleDeviceList, listType).getAsJsonArray();
                    Log.e("jsonString ", "jsonString::" + jsonArray.toString());

                    try {
                        jsonObject.put("status", WatchConstants.SC_SUCCESS);
                        jsonObject.put("data", jsonArray);
                        Log.e("jsonStringList", jsonObject.toString());
                        result.success(jsonObject.toString());
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Log.e("searchForBTExp2::", e.getMessage());
                    }

//       Gson gson = new Gson();
//       String jsonOutput = "Your JSON String";
//       Type listType = new TypeToken<List<Post>>(){}.getType();
//       List<Post> posts = gson.fromJson(jsonOutput, listType);
                    // convert into the json and send back as a response to flutter sdk

                }, 10000);
                String resultStatus = mobileConnect.startDevicesScan();
                Log.e("startStatus", resultStatus);
            } else {
                jsonObject.put("status", WatchConstants.SC_CANCELED); // not connected
                jsonObject.put("data", "[]");
                result.success(jsonObject.toString());
            }
        } catch (Exception exp) {
            Log.e("searchForBTExp1::", exp.getMessage());
        }

    }

    private void connectBluDevice(MethodCall call, Result result) {
        String index = (String) call.argument("index");
        String name = (String) call.argument("name");
        String alias = (String) call.argument("alias");
        String address = (String) call.argument("address");
        String deviceType = (String) call.argument("deviceType");
        String rssi = (String) call.argument("rssi");
        String bondState = (String) call.argument("bondState");
        boolean status = mobileConnect.connectDevice(address);
        this.mBluetoothLeService = this.mobileConnect.getBluetoothLeService();
        if (this.mBluetoothLeService != null) {
            Log.e("mBluetoothLeService::", "inside not null call the listeners");
            initBlueServices();
        }
        result.success(status);
    }

    private void initBlueServices() {
        mBluetoothLeService.setICallback(new ICallback() {
            @Override
            public void OnResult(boolean status, int result) {
                Log.e("onResult:", "status>> " + status + " resultValue>> " + result);
                try {
                    JSONObject jsonObject = new JSONObject();
                    switch (result) {
                        case ICallbackStatus.GET_BLE_VERSION_OK:
                            String deviceVersion = SPUtil.getInstance(mContext).getImgLocalVersion();
                            Log.e("deviceVersion::", deviceVersion);
                            jsonObject.put("deviceVersion", deviceVersion);
                            runOnUIThread(new JSONObject(), WatchConstants.DEVICE_VERSION, WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.GET_BLE_BATTERY_OK:
                            String deviceVer = SPUtil.getInstance(mContext).getImgLocalVersion();
                            String batteryStatus = "" + SPUtil.getInstance(mContext).getBleBatteryValue();
                            Log.e("batteryStatus::", batteryStatus);
                            jsonObject.put("deviceVersion", deviceVer);
                            jsonObject.put("batteryStatus", batteryStatus);
                           // runOnUIThread(jsonObject, WatchConstants.BATTERY_VERSION, WatchConstants.SC_SUCCESS);
                            runOnUIThread(jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            break;
                        // while connecting a device
                        case ICallbackStatus.READ_CHAR_SUCCESS: // 137
                            break;

                        case ICallbackStatus.WRITE_COMMAND_TO_BLE_SUCCESS: // 148
                            break;
                        case ICallbackStatus.SYNC_TIME_OK: // 6
                            //sync time ok
                            break;
                        case ICallbackStatus.CONNECTED_STATUS: // 20
                            // connected successfully
                            runOnUIThread(new JSONObject(), WatchConstants.DEVICE_CONNECTED, WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.DISCONNECT_STATUS: // 19
                            // disconnected successfully
                            runOnUIThread(new JSONObject(), WatchConstants.DEVICE_DISCONNECTED, WatchConstants.SC_SUCCESS);
                            break;
                    }
                } catch (Exception exp) {
                    Log.e("ble_service_exp:", exp.getMessage());
                    runOnUIThread(new JSONObject(), WatchConstants.SERVICE_LISTENING, WatchConstants.SC_FAILURE);
                }
            }

            @Override
            public void OnDataResult(boolean status, int i, byte[] bytes) {
                Log.e("OnDataResult:", "status>> " + status + "resultValue>> " + i);
            }

            @Override
            public void onCharacteristicWriteCallback(int i) {
                Log.e("onCharWriteCallback:", "status>> " + i);
            }

            @Override
            public void onIbeaconWriteCallback(boolean b, int i, int i1, String s) {

            }

            @Override
            public void onQueryDialModeCallback(boolean b, int i, int i1, int i2) {

            }

            @Override
            public void onControlDialCallback(boolean b, int i, int i1) {

            }

            @Override
            public void onSportsTimeCallback(boolean b, String s, int i, int i1) {

            }

            @Override
            public void OnResultSportsModes(boolean b, int i, int i1, int i2, SportsModesInfo sportsModesInfo) {

            }

            @Override
            public void OnResultHeartRateHeadset(boolean b, int i, int i1, int i2, HeartRateHeadsetSportModeInfo heartRateHeadsetSportModeInfo) {

            }

            @Override
            public void OnResultCustomTestStatus(boolean b, int i, CustomTestStatusInfo customTestStatusInfo) {

            }
        });
    }

    private void disconnectBluDevice(Result result) {
        boolean status = mobileConnect.disconnectDevice();
        result.success(status);
    }

    private void setUserParams(MethodCall call, Result result) {
        String age = (String) call.argument("age");
        String height = (String) call.argument("height");
        String weight = (String) call.argument("weight");
        String gender = ((String) call.argument("gender"));
        String steps = (String) call.argument("steps");
        String isCel = (String) call.argument("isCelsius");
        String screenOffTime = (String) call.argument("screenOffTime");
        String isChineseLang = (String) call.argument("isChineseLang");


        assert age != null;
        int bodyAge = Integer.parseInt(age);
        assert height != null;
        int bodyHeight = Integer.parseInt(height);
        assert weight != null;
        int bodyWeight = Integer.parseInt(weight);
        assert steps != null;
        int bodySteps = Integer.parseInt(steps);

        assert screenOffTime != null;
        int setScreenOffTime = Integer.parseInt(screenOffTime);

        boolean isMale = false;
        assert gender != null;
        if (gender.trim().equalsIgnoreCase("male")) {
            isMale = true;
        }
        boolean isCelsius = false;
        assert isCel != null;
        if (isCel.equalsIgnoreCase("true")) {
            isCelsius = true;
        }

        boolean isChinese = false;
        assert isChineseLang != null;
        if (isChineseLang.equalsIgnoreCase("true")) {
            isChinese = true;
        }

        boolean isSupported = GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_NEW_PARAMETER_SETTINGS_FUNCTION);
        Log.e("isSupported::", "isSupported>>" + isSupported);

        if (isSupported) {
            DeviceParametersInfo info = new DeviceParametersInfo();
            info.setBodyAge(bodyAge);
            info.setBodyHeight(bodyHeight);
            info.setBodyWeight(bodyWeight);
            info.setStepTask(bodySteps);
            info.setBodyGender(isMale ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);
            info.setCelsiusFahrenheitValue(isCelsius ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);
            info.setOffScreenTime(setScreenOffTime);
            info.setOnlySupportEnCn(isChinese ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);  // no for english, yes for chinese

//    info.setRaisHandbrightScreenSwitch(DeviceParametersInfo.switchStatusYes);
//    info.setHighestRateAndSwitch(141, DeviceParametersInfo.switchStatusYes);
//     info.setDeviceLostSwitch(DeviceParametersInfo.switchStatusNo);
            if (mWriteCommand != null) {
                mWriteCommand.sendDeviceParametersInfoToBLE(info);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            result.success(WatchConstants.SC_NOT_SUPPORTED);
        }
    }

    private void getDeviceBatteryNVersion(Result result) {
        if (mWriteCommand!=null){
            mWriteCommand.sendToReadBLEBattery();
            mWriteCommand.sendToReadBLEVersion();
            result.success(WatchConstants.SC_INIT);
        }else{
            result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void syncAllStepsData(Result result) {
        Log.e("steps_status", "" + SPUtil.getInstance(mContext).getBleConnectStatus());
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand!=null){
                mWriteCommand.syncAllStepData();
                result.success(WatchConstants.SC_INIT);
            }else{
                result.success(WatchConstants.SC_FAILURE);
            }
        }else{
            //device disconnected
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = (FlutterActivity) binding.getActivity();
        Log.e("onAttachedToActivity", "inside_attached");
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        Log.e("onAttachedToActivity", "onReattachedToActivityForConfigChanges");
        this.activity = (FlutterActivity) binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        Log.e("onDetachedFromActivity", "onDetachedFromActivityForConfigChanges");
    }

    @Override
    public void onDetachedFromActivity() {
        Log.e("onDetachedFromActivity", "onDetachedFromActivity");
    }

    private void startListening(Object arguments, Result rawResult) {
        // Get callback id
        String callbackName = (String) arguments;

        Log.e("callbackName", "start_listener "+ callbackName);

        if (callbackName.equals(WatchConstants.SMART_CALLBACK)) {
            validateDeviceListCallback = true;
        }

        Map<String, Object> args = new HashMap<>();
        args.put("id", callbackName);
        mCallbacks.put(callbackName, args);

        rawResult.success(null);
    }
    private void cancelListening(Object args, MethodChannel.Result result) {
        // Get callback id
      //  int currentListenerId = (int) args;
        String callbackName = (String) args;
        Log.e("callbackName", "cancel_listener "+callbackName);
        // Remove callback
        mCallbacks.remove(callbackName);
        // Do additional stuff if required to cancel the listener
        result.success(null);
    }

    private void runOnUIThread(final JSONObject data, final String callbackName, final String status) {
        uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("runOnUIThread", "Calling runOnUIThread with: " + data);

                        try {
                            JSONObject args = new JSONObject();
                            args.put("id", callbackName);
                            args.put("status", status);
                            args.put("data", data);
                            mCallbackChannel.invokeMethod(WatchConstants.CALL_LISTENER, args.toString());

                        } catch (Exception e) {
                            // e.printStackTrace();
                            Log.e("data_run_exp:", e.getMessage());
                        }
                    }
                }
        );
    }

/*    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("act_resultCode", "" + resultCode);
        Log.e("act_requestCode", "" + requestCode);
        try {
            if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
                this.flutterInitResultBlu.success(WatchConstants.SC_CANCELED);
            } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
                // do the statement
                String resultStatus = this.mobileConnect.startListeners();
                Log.e("act_res_status", "" + resultStatus);

                // String result = deviceConnect.startDevicesScan();
                this.flutterInitResultBlu.success(resultStatus);
            } else {
                Log.e("inside_else", "" + "nothing to do here");
                // do nothing
            }
           // return false;
        } catch (Exception exp) {
            Log.e("act_result_exp:", "" + exp.getMessage());
            //return false;
        }
        return false;
    }*/
}
