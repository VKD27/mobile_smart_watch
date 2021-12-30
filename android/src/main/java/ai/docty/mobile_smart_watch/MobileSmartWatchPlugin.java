package ai.docty.mobile_smart_watch;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yc.pedometer.sdk.BluetoothLeService;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

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


    private Context mContext;
    private Activity activity;
    private Application mApplication;
    private MobileConnect mobileConnect;

    private final int REQUEST_ENABLE_BT = 1212;

    // pedometer integration
    private BluetoothLeService mBluetoothLeService;
//  private WriteCommandToBLE mWriteCommand;
    // private Updates mUpdates;
    // private DataProcessing mDataProcessing;

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

        mobileConnect = new MobileConnect(applicationContext.getApplicationContext(), activity);
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
            case WatchConstants.STOP_DEVICE_SEARCH:
                String resultStatus = mobileConnect.stopDevicesScan();
                result.success(resultStatus);
                break;
            case WatchConstants.BIND_DEVICE:
                connectBluDevice(call, result);
                break;
            case WatchConstants.UNBIND_DEVICE:
                // disconnectBluDevice(call, result);
                break;
            case WatchConstants.GET_DEVICE_BATTERY_VERSION:
                //getDeviceBatteryNVersion(call, result);
                break;

            case WatchConstants.SET_USER_PARAMS:
                //setUserParams(call, result);
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

    private void searchForBTDevices( Result result) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (mobileConnect != null) {
                new Handler().postDelayed(() -> {
                    String resultStat = mobileConnect.stopDevicesScan();
                    Log.e("resultStat ", "deviceScanStop::" + resultStat);
                    List<BleDevices> bleDeviceList = mobileConnect.getDevicesList();
                    for (BleDevices device : bleDeviceList) {
                        Log.e("device_for ", "device::" + device.getName());
                    }
                    // mDevices = mLeDevices;
                    Type listType = new TypeToken<List<BleDevices>>() {
                    }.getType();
                    String jsonString = new Gson().toJson(bleDeviceList, listType);
                    try {
                        jsonObject.put("status", WatchConstants.SC_SUCCESS);
                        jsonObject.put("data", jsonString);
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
            //initBlueServices();
        }
        result.success(status);
    }
  /*private void setUserParams(MethodCall call, Result result) {
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
    if (gender.trim().equalsIgnoreCase("male")){
      isMale = true;
    }
    boolean isCelsius = false;
    assert isCel != null;
    if (isCel.equalsIgnoreCase("true")){
      isCelsius = true;
    }

    boolean isChinese = false;
    assert isChineseLang != null;
    if (isChineseLang.equalsIgnoreCase("true")){
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
      info.setBodyGender(isMale ? DeviceParametersInfo.switchStatusYes:DeviceParametersInfo.switchStatusNo);
      info.setCelsiusFahrenheitValue(isCelsius? DeviceParametersInfo.switchStatusYes :  DeviceParametersInfo.switchStatusNo);
      info.setOffScreenTime(setScreenOffTime);
      info.setOnlySupportEnCn(isChinese ? DeviceParametersInfo.switchStatusYes:  DeviceParametersInfo.switchStatusNo);  // no for english, yes for chinese

//            info.setRaisHandbrightScreenSwitch(DeviceParametersInfo.switchStatusYes);
//            info.setHighestRateAndSwitch(141, DeviceParametersInfo.switchStatusYes);
//            info.setDeviceLostSwitch(DeviceParametersInfo.switchStatusNo);

      mWriteCommand.sendDeviceParametersInfoToBLE(info);
    }
  }*/

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
