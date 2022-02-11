package ai.docty.mobile_smart_watch;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yc.pedometer.info.BPVOneDayInfo;
import com.yc.pedometer.info.BreatheInfo;
import com.yc.pedometer.info.CustomTestStatusInfo;
import com.yc.pedometer.info.DeviceParametersInfo;
import com.yc.pedometer.info.HeartRateHeadsetSportModeInfo;
import com.yc.pedometer.info.OxygenInfo;
import com.yc.pedometer.info.Rate24HourDayInfo;
import com.yc.pedometer.info.RateOneDayInfo;
import com.yc.pedometer.info.SevenDayWeatherInfo;
import com.yc.pedometer.info.SleepInfo;
import com.yc.pedometer.info.SleepTimeInfo;
import com.yc.pedometer.info.SportsModesInfo;
import com.yc.pedometer.info.StepOneDayAllInfo;
import com.yc.pedometer.info.StepOneHourInfo;
import com.yc.pedometer.info.TemperatureInfo;
import com.yc.pedometer.listener.BreatheRealListener;
import com.yc.pedometer.listener.OxygenRealListener;
import com.yc.pedometer.listener.RateCalibrationListener;
import com.yc.pedometer.listener.TemperatureListener;
import com.yc.pedometer.listener.TurnWristCalibrationListener;
import com.yc.pedometer.sdk.BLEServiceOperate;
import com.yc.pedometer.sdk.BloodPressureChangeListener;
import com.yc.pedometer.sdk.BluetoothLeService;
import com.yc.pedometer.sdk.DataProcessing;
import com.yc.pedometer.sdk.ICallback;
import com.yc.pedometer.sdk.ICallbackStatus;
import com.yc.pedometer.sdk.RateChangeListener;
import com.yc.pedometer.sdk.RateOf24HourRealTimeListener;
import com.yc.pedometer.sdk.ServiceStatusCallback;
import com.yc.pedometer.sdk.SleepChangeListener;
import com.yc.pedometer.sdk.StepChangeListener;
import com.yc.pedometer.sdk.UTESQLOperate;
import com.yc.pedometer.sdk.WriteCommandToBLE;
import com.yc.pedometer.utils.CalendarUtils;
import com.yc.pedometer.utils.GetFunctionList;
import com.yc.pedometer.utils.GlobalVariable;
import com.yc.pedometer.utils.SPUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.docty.mobile_smart_watch.handler.SmartStreamHandler;
import ai.docty.mobile_smart_watch.model.BleDevices;
import ai.docty.mobile_smart_watch.util.GlobalMethods;
import ai.docty.mobile_smart_watch.util.WatchConstants;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


/**
 * MobileSmartWatchPlugin
 */
public class MobileSmartWatchPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    // ServiceStatusCallback, ICallback,
    // RateCalibrationListener, TurnWristCalibrationListener, TemperatureListener, OxygenRealListener, BreatheRealListener
    /// PluginRegistry.ActivityResultListener
    ///FlutterPluginRegistry

    private FlutterPluginBinding flutterPluginBinding;
    private ActivityPluginBinding activityPluginBinding;

    private MethodChannel methodChannel;
    private EventChannel eventChannel, bpEventChannel, tempEventChannel;
    private MethodChannel mCallbackChannel;

    // Callbacks
    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());
    // private Map<String, Runnable> callbackById = new HashMap<>();
    Map<String, Map<String, Object>> mCallbacks = new HashMap<>();

    private Context mContext;
    private Activity activity;
    private Application mApplication;
    private MobileConnect mobileConnect;

    private final int REQUEST_ENABLE_BT = 1212;
    private final int REQUEST_BLE_ENABLE = 1213;
    private Boolean validateDeviceListCallback = false;

    // pedometer integration
    private BluetoothLeService mBluetoothLeService;
    private BLEServiceOperate bleServiceOperate;
    private WriteCommandToBLE mWriteCommand;
    private UTESQLOperate mUTESQLOperate;
    // private Updates mUpdates;
    private DataProcessing mDataProcessing;


    /*MethodCallHandler callbacksHandler = new MethodCallHandler() {
        @Override
        public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
            try{
                updateCallBackHandler(call,result);
            }catch (Exception exp){
                Log.e("callbacksHandlerExp:",""+exp.getMessage());
            }
        }
    };*/

    /*private void updateCallBackHandler(MethodCall call, Result result) {
        final String method = call.method;
        Log.e("calling_method", "callbacksHandler++ " + method); // startListening
        //WatchConstants.START_LISTENING.equalsIgnoreCase(method)
        //if ("startListening".equals(method)) {
        if (WatchConstants.START_LISTENING.equalsIgnoreCase(method)) {
            startListening(call.arguments, result);
        } else {
            result.notImplemented();
        }
    }*/

    //sdk return results
    //private Result flutterInitResultBlu;
    private Result flutterResultBluConnect;
    private Result deviceBatteryResult;
    private Result deviceVersionIDResult;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding;
        this.mContext = flutterPluginBinding.getApplicationContext();
        setUpEngine(this, flutterPluginBinding.getBinaryMessenger(), flutterPluginBinding.getApplicationContext());
    }

    private void setUpEngine(MobileSmartWatchPlugin mobileSmartWatchPlugin, BinaryMessenger binaryMessenger, Context applicationContext) {
        methodChannel = new MethodChannel(binaryMessenger, WatchConstants.SMART_METHOD_CHANNEL); // "mobile_smart_watch"
        methodChannel.setMethodCallHandler(mobileSmartWatchPlugin);

        eventChannel = new EventChannel(binaryMessenger, WatchConstants.SMART_EVENT_CHANNEL);
        bpEventChannel = new EventChannel(binaryMessenger, WatchConstants.SMART_BP_TEST_CHANNEL);
        tempEventChannel = new EventChannel(binaryMessenger, WatchConstants.SMART_TEMP_TEST_CHANNEL);

        mCallbackChannel = new MethodChannel(binaryMessenger, WatchConstants.SMART_CALLBACK);
        // mCallbackChannel.setMethodCallHandler(callbacksHandler);
        mCallbackChannel.setMethodCallHandler(mobileSmartWatchPlugin);
        eventChannel.setStreamHandler(new SmartStreamHandler(applicationContext));
        bpEventChannel.setStreamHandler(new SmartStreamHandler(applicationContext));
        tempEventChannel.setStreamHandler(new SmartStreamHandler(applicationContext));

        try {
            mUTESQLOperate = UTESQLOperate.getInstance(applicationContext.getApplicationContext());
            mobileConnect = new MobileConnect(applicationContext.getApplicationContext(), activity);
            bleServiceOperate = mobileConnect.getBLEServiceOperate();
            bleServiceOperate.setServiceStatusCallback(new ServiceStatusCallback() {
                @Override
                public void OnServiceStatuslt(int status) {
                    if (status == ICallbackStatus.BLE_SERVICE_START_OK) {
                        Log.e("inside_service_result", "" + mBluetoothLeService);
                        if (mBluetoothLeService == null) {
                            startListeningCallback(true);
                        }
                    }
                }
            });

            mBluetoothLeService = bleServiceOperate.getBleService();
            if (mBluetoothLeService != null) {
                startListeningCallback(false);
            }

            mWriteCommand = WriteCommandToBLE.getInstance(applicationContext.getApplicationContext());
            mDataProcessing = DataProcessing.getInstance(applicationContext.getApplicationContext());

            startListeningDataProcessing();


        } catch (Exception exp) {
            Log.e("setUpEngineExp:", exp.getMessage());
        }
    }

   private boolean initializeData(){
        mobileConnect = new MobileConnect(this.mContext.getApplicationContext(), activity);
        bleServiceOperate = mobileConnect.getBLEServiceOperate();
        /*bleServiceOperate.setServiceStatusCallback(new ServiceStatusCallback() {
            @Override
            public void OnServiceStatuslt(int status) {
                if (status == ICallbackStatus.BLE_SERVICE_START_OK) {
                    Log.e("inside_service_result", "" + mBluetoothLeService);
                    if (mBluetoothLeService == null) {
                        startListeningCallback(true);
                    }
                }
            }
        });*/
        mBluetoothLeService = bleServiceOperate.getBleService();
        if (mBluetoothLeService != null) {
            startListeningCallback(false);
        }
        return true;
    }

   /* private void setUpDataEngine( BinaryMessenger binaryMessenger){
        try{
            Log.e("inside_set_up_engine", "binaryMessenger");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                Log.e("inside_set_up_engine", "");
//                mCallbackChannel = new MethodChannel(binaryMessenger, WatchConstants.SMART_CALLBACK);
//                mCallbackChannel.setMethodCallHandler(callbacksHandler);
                }
            });
        }catch (Exception exp){
            Log.e("set_up_engine_exp", exp.getMessage());
        }
    }*/

    private void startListeningDataProcessing() {
        mDataProcessing.setOnStepChangeListener(new StepChangeListener() {
            @Override
            public void onStepChange(StepOneDayAllInfo info) {
                if (info != null) {
                    //Log.e("onStepChange1", "calendar: " + info.getCalendar());
                    Log.e("onStepChange2", "mSteps: " + info.getStep() + ", mDistance: " + info.getDistance() + ", mCalories=" + info.getCalories());
                    Log.e("onStepChange3", "mRunSteps: " + info.getRunSteps() + ", mRunDistance: " + info.getRunDistance() + ", mRunCalories=" + info.getRunCalories() + ", mRunDurationTime=" + info.getRunDurationTime());
                    Log.e("onStepChange4", "mWalkSteps: " + info.getWalkSteps() + ", mWalkDistance: " + info.getWalkDistance() + ", mWalkCalories=" + info.getWalkCalories() + ", mWalkDurationTime=" + info.getWalkDurationTime());
                    Log.e("onStepChange5", "getStepOneHourArrayInfo: " + info.getStepOneHourArrayInfo() + ", getStepRunHourArrayInfo: " + info.getStepRunHourArrayInfo() + ", getStepWalkHourArrayInfo=" + info.getStepWalkHourArrayInfo());

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("steps", "" + info.getStep());
                        //   jsonObject.put("distance", ""+info.getDistance());
                        //  jsonObject.put("calories", ""+info.getCalories());
                        jsonObject.put("distance", "" + GlobalMethods.convertDoubleToStringWithDecimal(info.getDistance()));
                        jsonObject.put("calories", "" + GlobalMethods.convertDoubleToStringWithDecimal(info.getCalories()));
                        // runOnUIThread(WatchConstants.STEPS_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                        pushEventCallBack(WatchConstants.STEPS_REAL_TIME, jsonObject, WatchConstants.SC_SUCCESS);
                    } catch (Exception e) {
                        // e.printStackTrace();
                        Log.e("onStepJSONExp::", e.getMessage());
                    }

                }
            }
        });
        mDataProcessing.setOnRateListener(new RateChangeListener() {
            @Override
            public void onRateChange(int rate, int status) {
                Log.e("onRateListener", "rate: " + rate + ", status: " + status);
                updateContinuousHeartRate(rate, status);
                /*try {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("hr", "" + rate);
                            } catch (Exception e) {
                                // e.printStackTrace();
                                Log.e("onRateJSONExp: ", e.getMessage());
                            }
                            runOnUIThread(WatchConstants.HR_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                        }
                    });
                } catch (Exception exp) {
                    Log.e("onRateExp: ", exp.getMessage());
                }*/
            }
        });
        mDataProcessing.setOnSleepChangeListener(new SleepChangeListener() {
            @Override
            public void onSleepChange() {
                Log.e("onSleepChangeCalender", CalendarUtils.getCalendar(0));
                SleepTimeInfo sleepTimeInfo = UTESQLOperate.getInstance(mContext).querySleepInfo(CalendarUtils.getCalendar(0));
                int deepTime, lightTime, awakeCount, sleepTotalTime;
                if (sleepTimeInfo != null) {
                    deepTime = sleepTimeInfo.getDeepTime();
                    lightTime = sleepTimeInfo.getLightTime();
                    awakeCount = sleepTimeInfo.getAwakeCount();
                    sleepTotalTime = sleepTimeInfo.getSleepTotalTime();
                    Log.e("sleepTimeInfo", "deepTime: " + deepTime + ", lightTime: " + lightTime + ", awakeCount=" + awakeCount + ", sleepTotalTime=" + sleepTotalTime);
                }
            }
        });
        mDataProcessing.setOnBloodPressureListener(new BloodPressureChangeListener() {
            @Override
            public void onBloodPressureChange(int highPressure, int lowPressure, int status) {
                Log.e("onBloodPressureChange", "highPressure: " + highPressure + ", lowPressure: " + lowPressure + ", status=" + status);
                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("high", "" + highPressure);
                        jsonObject.put("low", "" + lowPressure);
                        jsonObject.put("status", "" + status);
                        // runOnUIThread(WatchConstants.BP_RESULT, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                        //pushEventCallBack(WatchConstants.BP_RESULT, jsonObject, WatchConstants.SC_SUCCESS);
                        pushBPEventCallBack(WatchConstants.BP_RESULT, jsonObject, WatchConstants.SC_SUCCESS);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Log.e("bpChangeJSONExp::", e.getMessage());
                    }

                } catch (Exception exp) {
                    Log.e("bpChangeExp::", exp.getMessage());
                }
            }
        });
        mDataProcessing.setOnRateOf24HourListenerRate(new RateOf24HourRealTimeListener() {
            @Override
            public void onRateOf24HourChange(int maxHeartRateValue, int minHeartRateValue, int averageHeartRateValue, boolean isRealTimeValue) {
                Log.e("onRateOf24Hour", "maxHeartRateValue: " + maxHeartRateValue + ", minHeartRateValue: " + minHeartRateValue + ", averageHeartRateValue=" + averageHeartRateValue + ", isRealTimeValue=" + isRealTimeValue);

                try {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("maxHr", "" + maxHeartRateValue);
                        jsonObject.put("minHr", "" + minHeartRateValue);
                        jsonObject.put("avgHr", "" + averageHeartRateValue);
                        jsonObject.put("rtValue", isRealTimeValue);
                        // runOnUIThread(WatchConstants.BP_RESULT, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                        pushEventCallBack(WatchConstants.HR_24_REAL_RESULT, jsonObject, WatchConstants.SC_SUCCESS);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Log.e("onRateOf24JSONExp::", e.getMessage());
                    }

                } catch (Exception exp) {
                    Log.e("onRate24HourExp::", exp.getMessage());
                }
            }
        });
    }

    private void updateContinuousHeartRate(int rate, int status) {
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("hr", "" + rate);
            } catch (Exception e) {
                // e.printStackTrace();
                Log.e("onRateJSONExp: ", e.getMessage());
            }
            // runOnUIThread(WatchConstants.HR_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
            //sendEventToDart(jsonObject, WatchConstants.SMART_EVENT_CHANNEL);
            pushEventCallBack(WatchConstants.HR_REAL_TIME, jsonObject, WatchConstants.SC_SUCCESS);
        } catch (Exception exp) {
            Log.e("onRateExp: ", exp.getMessage());
        }
    }

    private void startListeningCallback(boolean initial) {
        if (initial) {
            mBluetoothLeService = mobileConnect.getBLEServiceOperate().getBleService();
        }
        mobileConnect.setBluetoothLeService(mBluetoothLeService);
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
                            // deviceVersionIDResult.success(jsonObject.toString());
                            // runOnUIThread(new JSONObject(), WatchConstants.DEVICE_VERSION, WatchConstants.SC_SUCCESS);
                            //  runOnUIThread(WatchConstants.DEVICE_VERSION, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.DEVICE_VERSION, jsonObject, WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.GET_BLE_BATTERY_OK:
                            //String deviceVer = SPUtil.getInstance(mContext).getImgLocalVersion();
                            String batteryStatus = "" + SPUtil.getInstance(mContext).getBleBatteryValue();
                            Log.e("batteryStatus::", batteryStatus);
                            //jsonObject.put("deviceVersion", deviceVer);
                            jsonObject.put("batteryStatus", batteryStatus);
                            // runOnUIThread(jsonObject, WatchConstants.BATTERY_VERSION, WatchConstants.SC_SUCCESS);
                            //deviceBatteryResult.success(jsonObject.toString());
                            // runOnUIThread(WatchConstants.BATTERY_STATUS, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.BATTERY_STATUS, jsonObject, WatchConstants.SC_SUCCESS);
                            break;
                        // while connecting a device
                        case ICallbackStatus.READ_CHAR_SUCCESS: // 137
                            break;
                        case ICallbackStatus.WRITE_COMMAND_TO_BLE_SUCCESS: // 148
                            break;
                        case ICallbackStatus.SYNC_TIME_OK: // 6
                            //sync time ok
                            break;


                        case ICallbackStatus.OFFLINE_STEP_SYNC_OK: // 2
                            //steps sync done
                            pushEventCallBack(WatchConstants.SYNC_STEPS_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.OFFLINE_SLEEP_SYNC_OK: // 6
                            //sleep sync done
                            pushEventCallBack(WatchConstants.SYNC_SLEEP_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.OFFLINE_SLEEP_SYNC_TIMEOUT: // 93
                            //sleep sync done
                            pushEventCallBack(WatchConstants.SYNC_SLEEP_TIME_OUT, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.SYNC_TEMPERATURE_COMMAND_FINISH_CRC_FAIL:
                            pushEventCallBack(WatchConstants.SYNC_TEMPERATURE_TIME_OUT, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.OFFLINE_BLOOD_PRESSURE_SYNC_OK: // 47
                            //bp sync done
                            pushEventCallBack(WatchConstants.SYNC_BP_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.OFFLINE_RATE_SYNC_OK: // 23
                            pushEventCallBack(WatchConstants.SYNC_RATE_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.OFFLINE_24_HOUR_RATE_SYNC_OK: // 82
                            pushEventCallBack(WatchConstants.SYNC_24_HOUR_RATE_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.SYNC_TEMPERATURE_COMMAND_OK: // 82
                            pushEventCallBack(WatchConstants.SYNC_TEMPERATURE_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.ECG_DATA_SYNC_OK: // 165
                            pushEventCallBack(WatchConstants.SYNC_ECG_DATA_FINISH, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;


                        case ICallbackStatus.SET_STEPLEN_WEIGHT_OK: // 8
                            //runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.UPDATE_DEVICE_PARAMS, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.OFFLINE_BLOOD_PRESSURE_SYNCING: // 46
                            // runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            break;


                        case ICallbackStatus.BLOOD_PRESSURE_TEST_START: // 50
                            pushBPEventCallBack(WatchConstants.BP_TEST_STARTED, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.BLOOD_PRESSURE_TEST_END: // 91
                            pushBPEventCallBack(WatchConstants.BP_TEST_FINISHED, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.BLOOD_PRESSURE_TEST_TIME_OUT: // 48
                            pushBPEventCallBack(WatchConstants.BP_TEST_TIME_OUT, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.BLOOD_PRESSURE_TEST_ERROR: // 49
                            pushBPEventCallBack(WatchConstants.BP_TEST_ERROR, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.QUERY_CURRENT_TEMPERATURE_COMMAND_OK: // 49
                            pushTemperatureEventCallBack(WatchConstants.TEMP_TEST_OK, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.RATE_TEST_START: // 79
                            // runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.HR_TEST_STARTED, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.RATE_TEST_STOP: // 80
                            // runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.HR_TEST_FINISHED, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;

                            //after connections
                        case ICallbackStatus.SYNC_STATUS_24_HOUR_RATE_OPEN:  //89
                            // sync 24 hrs heart rate status
                            jsonObject.put("status", status);
                            pushEventCallBack(WatchConstants.SYNC_STATUS_24_HOUR_RATE_OPEN, jsonObject, WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.SYNC_TEMPERATURE_AUTOMATICTEST_INTERVAL_COMMAND_OK: // 107
                            jsonObject.put("status", status);
                            pushEventCallBack(WatchConstants.SYNC_TEMPERATURE_24_HOUR_AUTOMATIC, jsonObject, WatchConstants.SC_SUCCESS);
                            break;

                       /* case ICallbackStatus.TWO_DAY_WEATHER_SYNC_SUCCESS: // 73
                            jsonObject.put("status", status);
                            pushEventCallBack(WatchConstants.SYNC_WEATHER_SUCCESS, jsonObject, WatchConstants.SC_SUCCESS);
                            break;*/

                        case ICallbackStatus.SEVEN_DAY_WEATHER_SYNC_SUCCESS: // 51
                            jsonObject.put("status", status);
                            pushEventCallBack(WatchConstants.SYNC_WEATHER_SUCCESS, jsonObject, WatchConstants.SC_SUCCESS);
                            break;

                        case ICallbackStatus.CONNECTED_STATUS: // 20
                            // connected successfully
                            //runOnUIThread(new JSONObject(), WatchConstants.DEVICE_CONNECTED, WatchConstants.SC_SUCCESS);
                            //flutterResultBluConnect.success(connectionStatus);
                            // updateConnectionStatus(true);
                            //updateConnectionStatus2(true);
                            //updateConnectionStatus3(true);
                           // mobileConnect.getBluetoothLeService().setRssiHandler(mHandlerMessage);
                            updateReadRSSIThread();
                            updatePasswordStatus();
                            // runOnUIThread(WatchConstants.DEVICE_CONNECTED, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.DEVICE_CONNECTED, new JSONObject(), WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.DISCONNECT_STATUS: // 19
                            String lastConnectAddress = SPUtil.getInstance(mContext).getLastConnectDeviceAddress();
                          //  boolean connectResult = mobileConnect.getBLEServiceOperate().connect(lastConnectAddress);
                           // jsonObject.put("status", connectResult);
                            // disconnected successfully
                            // mobileConnect.disconnectDevice();
                            // updateConnectionStatus(false);
                            // runOnUIThread(WatchConstants.DEVICE_DISCONNECTED, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            pushEventCallBack(WatchConstants.DEVICE_DISCONNECTED, jsonObject, WatchConstants.SC_SUCCESS);
                            // runOnUIThread(new JSONObject(), WatchConstants.DEVICE_DISCONNECTED, WatchConstants.SC_SUCCESS);
                            break;
                    }
                } catch (Exception exp) {
                    Log.e("ble_service_exp:", exp.getMessage());
                    // runOnUIThread(WatchConstants.CALLBACK_EXCEPTION, new JSONObject(), WatchConstants.SERVICE_LISTENING, WatchConstants.SC_FAILURE);
                    pushEventCallBack(WatchConstants.CALLBACK_EXCEPTION, new JSONObject(), WatchConstants.SC_FAILURE);
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
        mBluetoothLeService.setTemperatureListener(new TemperatureListener() {
            @Override
            public void onTestResult(TemperatureInfo temperatureInfo) {
                Log.e("temperatureListener", "temperature: " + temperatureInfo.getBodyTemperature() + ", type: " + temperatureInfo.getType());
                try {
                    if (temperatureInfo!=null){
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("calender", temperatureInfo.getCalendar());
                        jsonObject.put("type", "" + temperatureInfo.getType());
                        jsonObject.put("inCelsius", "" + GlobalMethods.convertDoubleToCelsiusWithDecimal(temperatureInfo.getBodyTemperature()));
                        jsonObject.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                        jsonObject.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                        jsonObject.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));
                        Log.e("onTestResult", "object: " + jsonObject.toString());
                        //pushEventCallBack(WatchConstants.TEMP_RESULT, jsonObject, WatchConstants.SC_SUCCESS);
                        Log.e("onTestResult", "mUTESQLOperate: " + mUTESQLOperate.toString());
                        if (mUTESQLOperate!=null){
                            mUTESQLOperate.saveTemperature(temperatureInfo);
                            Log.e("onTestResult", "mUTESQLOperate: After save saveTemperature");
                        }
                        pushTemperatureEventCallBack(WatchConstants.TEMP_RESULT, jsonObject, WatchConstants.SC_SUCCESS);
                    }else{
                        pushTemperatureEventCallBack(WatchConstants.TEMP_TEST_TIME_OUT, new JSONObject(), WatchConstants.SC_SUCCESS);
                    }
                } catch (Exception exp) {
                    Log.e("onTestResultExp:", exp.getMessage());
                }
            }

            @Override
            public void onSamplingResult(TemperatureInfo temperatureInfo) {

            }
        });//Setting up a temperature test，Sampling data callback

        mBluetoothLeService.setRateCalibrationListener(new RateCalibrationListener() {
            @Override
            public void onRateCalibrationStatus(int i) {

            }
        });//Set up heart rate calibration monitor

        mBluetoothLeService.setTurnWristCalibrationListener(new TurnWristCalibrationListener() {
            @Override
            public void onTurnWristCalibrationStatus(int i) {

            }
        });//Set up wrist watch calibration

        mBluetoothLeService.setOxygenListener(new OxygenRealListener() {
            @Override
            public void onTestResult(int status, OxygenInfo oxygenInfo) {
                Log.e("oxygenRealListener", "value: " + oxygenInfo.getOxygenValue() + ", status: " + status);
            }
        });//Oxygen Listener
                /*if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST)) {
                    mBluetoothLeService.setTemperatureListener(this);
                }
                if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN)) {
                    mBluetoothLeService.setOxygenListener(this);
                }*/
        mBluetoothLeService.setBreatheRealListener(new BreatheRealListener() {
            @Override
            public void onBreatheResult(int status, BreatheInfo breatheInfo) {
                Log.e("setBreatheRealListener", "value: " + breatheInfo.getBreatheValue() + ", status: " + status);
            }
        });//Breathe Listener

        Log.e("inside_service_result", "listeners" + mBluetoothLeService);
    }

    private void updatePasswordStatus(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e( "mWriteCommand", mWriteCommand.toString());
                if (mWriteCommand!=null){
                    mWriteCommand.sendToQueryPasswardStatus();
                }
            }
        }, 600);
    }
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        try {
            handleMethodCall(call, result);
            //this.flutterResultBluConnect = result;
        } catch (Exception exp) {
            Log.e("onMethodCallExp::", exp.getMessage());
        }
    }

    private Context getApplicationContext() {
        return this.mContext.getApplicationContext();
    }

    private void handleMethodCall(MethodCall call, Result result) {
        String method = call.method;
        switch (method) {
            case WatchConstants.START_LISTENING:
                //initDeviceConnection(result);
                startListening(call.arguments, result);
                break;
            case WatchConstants.BLE_RE_INITIALIZE:
                bleReInitialize(result);
                break;
            case WatchConstants.DEVICE_RE_INITIATE:
                initReInitialize(result);
                break;
            case WatchConstants.DEVICE_INITIALIZE:
                initDeviceConnection(result);
                break;
            case WatchConstants.START_DEVICE_SEARCH:
                searchForBTDevices(result);
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
            case WatchConstants.SET_24_HEART_RATE:
                set24HeartRate(call, result);
                break;
            case WatchConstants.SET_24_TEMPERATURE_TEST:
                set24HrTemperatureTest(call, result);
                break;
            case WatchConstants.SET_WEATHER_INFO:
                setSevenDaysWeatherInfo(call, result);
                break;

            case WatchConstants.GET_DEVICE_VERSION:
                getDeviceVersion(result);
                break;

            case WatchConstants.GET_DEVICE_BATTERY_STATUS:
                getDeviceBatteryStatus(result);
                break;

            case WatchConstants.CHECK_CONNECTION_STATUS:
                getCheckConnectionStatus(result);
                break;

            // sync all the data,from watch to the local (android SDK)
            case WatchConstants.SYNC_ALL_JUDGE:
                fetchAllJudgement(call, result);
                break;

            case WatchConstants.GET_SYNC_STEPS:
                syncAllStepsData(result);
                break;
            case WatchConstants.GET_SYNC_SLEEP:
                syncAllSleepData(result);
                break;
            case WatchConstants.GET_SYNC_RATE:
                syncRateData(result);
                break;
            case WatchConstants.GET_SYNC_BP:
                syncBloodPressure(result);
                break;
            case WatchConstants.GET_SYNC_OXYGEN:
                syncOxygenSaturation(result);
                break;
            case WatchConstants.GET_SYNC_TEMPERATURE:
                syncBodyTemperature(result);
                break;
            //start doing test here
            case WatchConstants.START_BP_TEST:
                startBloodPressure(result);
                break;
            case WatchConstants.STOP_BP_TEST:
                stopBloodPressure(result);
                break;
            case WatchConstants.START_HR_TEST:
                startHeartRate(result);
                break;
            case WatchConstants.STOP_HR_TEST:
                stopHeartRate(result);
                break;
            case WatchConstants.START_OXYGEN_TEST:
                startOxygenSaturation(result);
                break;
            case WatchConstants.STOP_OXYGEN_TEST:
                stopOxygenSaturation(result);
                break;

            case WatchConstants.START_TEST_TEMP:
                startTempTest(result);
                break;

            //fetch individual data
            case WatchConstants.FETCH_OVERALL_BY_DATE:
                fetchOverAllBySelectedDate(call, result);
                break;

            case WatchConstants.FETCH_STEPS_BY_DATE:
                fetchStepsBySelectedDate(call, result);
                break;
            case WatchConstants.FETCH_SLEEP_BY_DATE:
                fetchSleepByDate(call, result);
                break;
            case WatchConstants.FETCH_BP_BY_DATE:
                fetchBPByDate(call, result);
                break;
            case WatchConstants.FETCH_HR_BY_DATE:
                fetchHRByDate(call, result);
                break;
            case WatchConstants.FETCH_24_HOUR_HR_BY_DATE:
                fetch24HourHRDateByDate(call, result);
                break;
            case WatchConstants.FETCH_TEMP_BY_DATE:
                fetchTemperatureByDate(call, result);
                break;

            // fetch all the data
            case WatchConstants.FETCH_OVERALL_DEVICE_DATA:
                fetchOverAllDeviceData(result);
                break;
            case WatchConstants.FETCH_ALL_STEPS_DATA:
                fetchAllStepsData(result);
                break;
            case WatchConstants.FETCH_ALL_SLEEP_DATA:
                fetchAllSleepData(result);
                break;
            case WatchConstants.FETCH_ALL_BP_DATA:
                fetchAllBPData(result);
                break;
            case WatchConstants.FETCH_ALL_TEMP_DATA:
                fetchAllTemperatureData(result);
                break;
            case WatchConstants.FETCH_ALL_HR_24_DATA:
                fetchAllHeartRate24Data(result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    private void bleReInitialize(Result result){
        try {
        new Handler().postDelayed(() -> {
            mobileConnect.getBluetoothLeService().initialize();
          result.success(WatchConstants.SC_BLE_RE_INIT);
          }, 1000);

        } catch (Exception exp) {
            Log.e("bleReInitializeExp::", "" + exp.getMessage());
        }
    }
    private void initReInitialize(Result result){
        try {
            //mUTESQLOperate = UTESQLOperate.getInstance(mContext.getApplicationContext());
            mobileConnect = new MobileConnect(mContext.getApplicationContext(), activity);
            bleServiceOperate = mobileConnect.getBLEServiceOperate();
            bleServiceOperate.setServiceStatusCallback(new ServiceStatusCallback() {
                @Override
                public void OnServiceStatuslt(int status) {
                    if (status == ICallbackStatus.BLE_SERVICE_START_OK) {
                        Log.e("inside_service_result", "" + mBluetoothLeService);
                        if (mBluetoothLeService == null) {
                            startListeningCallback(true);
                        }
                    }
                }
            });

            mBluetoothLeService = bleServiceOperate.getBleService();
            if (mBluetoothLeService != null) {
                startListeningCallback(false);
            }

//            mWriteCommand = WriteCommandToBLE.getInstance(mContext.getApplicationContext());
//            mDataProcessing = DataProcessing.getInstance(mContext.getApplicationContext());

            startListeningDataProcessing();
            new Handler().postDelayed(() -> {
                result.success(WatchConstants.SC_RE_INIT);
            }, 1000);

        } catch (Exception exp) {
            Log.e("initReInitialize::", "" + exp.getMessage());
        }
    }
    private void initDeviceConnection(Result result) {
        // this.flutterInitResultBlu = result;
        try {
            if (mobileConnect != null) {
                //  boolean initStatus = initializeData();
                //mobileConnect = new MobileConnect(this.mContext.getApplicationContext(), activity);
                boolean blu4Enabled = mobileConnect.checkBlu4();
                Log.e("blu4Enabled:", "" + blu4Enabled);
                if (blu4Enabled){
                    String resultStatus = mobileConnect.startListeners();
                    boolean enable = mobileConnect.isBleEnabled();
                    Log.e("device_enable:", "" + enable);
                    if (enable){
                        boolean connectionStatus = SPUtil.getInstance(mContext).getBleConnectStatus();
                        Log.e("connectionStatus:", "" + connectionStatus);
                        //result.success(resultStatus);
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter != null) {
                            Log.e("blueAdapter_status:", ""+bluetoothAdapter.isEnabled());
                                /*if (!bluetoothAdapter.isEnabled()) {
                                    bluetoothAdapter.enable();
                                }*/
                        }
                        new Handler().postDelayed(() -> {
                            result.success(resultStatus);
                        }, 1000);
                    }else{
                        // turn on bluetooth
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            String[] multiplePermission = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE};
                            if (!checkPermissionEnabled(Manifest.permission.BLUETOOTH_SCAN)){
                                //permissionLauncher.launch(multiplePermission);
                                ActivityCompat.requestPermissions(activity, multiplePermission, REQUEST_BLE_ENABLE);
                            }
                        }else{
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                        new Handler().postDelayed(() -> {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter != null) {
                                Log.e("blueAdapter_status:", ""+bluetoothAdapter.isEnabled());
                                /*if (!bluetoothAdapter.isEnabled()) {
                                    bluetoothAdapter.enable();
                                }*/
                            }
                            result.success(resultStatus);
                            }, 1000);
                    }
                }else{
                    result.success(WatchConstants.BLE_NOT_SUPPORTED);
                }

                /*boolean enable = mobileConnect.isBleEnabled();
                boolean blu4 = mobileConnect.checkBlu4();
                boolean connectionStatus = SPUtil.getInstance(mContext).getBleConnectStatus();
                String resultStatus = mobileConnect.startListeners();
                Log.e("device_enable:", "" + enable);
                Log.e("device_blu4e:", "" + blu4);
                Log.e("connectionStatus:", "" + connectionStatus);
                if (!connectionStatus){
                    // connection_status == false then BluetoothAdapter not initialized

                }else{

                }

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
                }*/

            } else {
                Log.e("device_connect_err:", "device_connect not initiated..");
            }

        } catch (Exception exp) {
            Log.e("initDeviceExp::", "" + exp.getMessage());
        }
    }
    private boolean checkPermissionEnabled(String permission){
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }
    private void searchForBTDevices(Result result) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (mobileConnect != null) {
                /*mBluetoothLeService = mobileConnect.getBluetoothLeService();
                if (mBluetoothLeService != null) {
                    //initBlueServices(status);
                    initBlueServices();
                }*/

                new Handler().postDelayed(() -> {
                    String resultStat = mobileConnect.stopDevicesScan();
                    Log.e("resultStat ", "deviceScanStop::" + resultStat);
                    ArrayList<BleDevices> bleDeviceList = mobileConnect.getDevicesList();
                    JSONArray jsonArray = new JSONArray();

                    for (BleDevices device : bleDeviceList) {
                        Log.e("device_for ", "device::" + device.getName());

                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("name", device.getName());
                            jsonObj.put("address", device.getAddress());
                            jsonObj.put("rssi", device.getRssi());
                            jsonObj.put("deviceType", device.getDeviceType());
                            jsonObj.put("bondState", device.getBondState());
                            jsonObj.put("alias", device.getAlias());
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

                }, 8000);
                String resultStatus = mobileConnect.startDevicesScan();
                Log.e("startStatus", resultStatus);
            } else {
                try {
                    jsonObject.put("status", WatchConstants.SC_CANCELED); // not connected
                    jsonObject.put("data", "[]");
                } catch (Exception e) {
                    //e.printStackTrace();
                    Log.e("jsonExp123::", "jsonParse::" + e.getMessage());
                }
                result.success(jsonObject.toString());
            }

        } catch (Exception exp) {
            Log.e("searchForBTExp1::", exp.getMessage());
        }

    }

    private void connectBluDevice(MethodCall call, Result result) {
        try {

            //String index = (String) call.argument("index");
            //String name = call.argument("name");
            //String alias = call.argument("alias");
            String address = call.argument("address");
            //String deviceType = call.argument("deviceType");
            // String rssi = call.argument("rssi");
            // String bondState = call.argument("bondState");

            boolean status = mobileConnect.connectDevice(address);
           /* if (mBluetoothLeService != null) {
                mBluetoothLeService.readRssi();
            }*/
//                    mBluetoothLeService = mobileConnect.getBluetoothLeService();
//
//                    if (mBluetoothLeService != null) {
//                        initBlueServices(status);
//                    }
            result.success(status);
        } catch (Exception exp) {
            Log.e("connectBluDeviceExp:", exp.getMessage());
        }
    }

    private void updateReadRSSIThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (mBluetoothLeService != null) {
                        mBluetoothLeService.readRssi();
                        //mBluetoothLeService.initialize();
                    }
                }
            }
        }).start();
    }

    private Handler mHandlerMessage = new Handler() {
        public void handleMessage(Message msg) {
         Log.e("Msg_What_Handler: ", ""+msg.what);
         switch (msg.what) {
             case GlobalVariable.GET_RSSI_MSG:
                 Bundle bundle = msg.getData();
                 Log.e("GET_RSSI_MSG: ",bundle.getInt(GlobalVariable.EXTRA_RSSI) + "");
                 break;
         }
        }
    };

    private void disconnectBluDevice(Result result) {
        try {
            boolean status = mobileConnect.disconnectDevice();
            result.success(status);
        } catch (Exception exp) {
            Log.e("disconnectBluDeviceExp:", exp.getMessage());
        }
    }

    private void setUserParams(MethodCall call, Result result) {
        try {
            String age = call.argument("age");
            String height = call.argument("height");
            String weight = call.argument("weight");
            String gender = call.argument("gender");
            String steps = call.argument("steps");
            String isCel = call.argument("isCelsius");
            String screenOffTime = call.argument("screenOffTime");
            String isChineseLang = call.argument("isChineseLang");
            String raiseHandWakeUp = call.argument("raiseHandWakeUp");


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

            boolean isRaiseHandWakeUp = false;
            assert raiseHandWakeUp != null;
            if (raiseHandWakeUp.equalsIgnoreCase("true")) {
                isRaiseHandWakeUp = true;
            }


            boolean isSupported = GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_NEW_PARAMETER_SETTINGS_FUNCTION);
            Log.e("isSupported::", "isSupported>>" + isSupported);

            boolean isSupp = GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_NEW_PARAMETER_SETTINGS_FUNCTION);
            Log.e("isSupp::", "isSupp>>" + isSupp);

            //if (isSupported) {
            DeviceParametersInfo info = new DeviceParametersInfo();
            info.setBodyAge(bodyAge);
            info.setBodyHeight(bodyHeight);
            info.setBodyWeight(bodyWeight);
            info.setStepTask(bodySteps);
            info.setBodyGender(isMale ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);
            info.setCelsiusFahrenheitValue(isCelsius ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);
            info.setOffScreenTime(setScreenOffTime);
            info.setOnlySupportEnCn(isChinese ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);  // no for english, yes for chinese
            info.setRaisHandbrightScreenSwitch(isRaiseHandWakeUp ? DeviceParametersInfo.switchStatusYes : DeviceParametersInfo.switchStatusNo);  // true if bright light turn on

//    info.setRaisHandbrightScreenSwitch(DeviceParametersInfo.switchStatusYes);
//    info.setHighestRateAndSwitch(141, DeviceParametersInfo.switchStatusYes);
//     info.setDeviceLostSwitch(DeviceParametersInfo.switchStatusNo);
            if (mWriteCommand != null) {
                mWriteCommand.sendDeviceParametersInfoToBLE(info);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
//        } else {
//            result.success(WatchConstants.SC_NOT_SUPPORTED);
//        }

        } catch (Exception exp) {
            Log.e("setUserParamsExp::", exp.getMessage());
            //result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void set24HeartRate(MethodCall call, Result result) {
        try {
            String enable = call.argument("enable");
            boolean isEnable = false;
            assert enable != null;
            if (enable.trim().toLowerCase().equalsIgnoreCase("true")) {
                isEnable = true;
            }
            if (mWriteCommand != null) {
                mWriteCommand.open24HourRate(isEnable);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } catch (Exception exp) {
            Log.e("set24HeartRateExp::", exp.getMessage());
            //result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void setSevenDaysWeatherInfo(MethodCall call, Result result) {
        try {
            String infoData = call.argument("data");
            assert infoData != null;
            JSONObject jsonObject = new JSONObject(infoData);
            SevenDayWeatherInfo sevenDayWeatherInfo = new SevenDayWeatherInfo();

            String cityName = jsonObject.optString("cityName");//max only 4 characters
            sevenDayWeatherInfo.setCityName(cityName);

            String todayWeatherCode = jsonObject.optString("todayWeatherCode");
            int todayTmpCurrent = jsonObject.optInt("todayTmpCurrent");
            int todayTmpMax = jsonObject.optInt("todayTmpMax");
            int todayTmpMin = jsonObject.optInt("todayTmpMin");
            //today
            sevenDayWeatherInfo.setTodayWeatherCode(todayWeatherCode);
            sevenDayWeatherInfo.setTodayTmpCurrent(todayTmpCurrent);
            sevenDayWeatherInfo.setTodayTmpMax(todayTmpMax);
            sevenDayWeatherInfo.setTodayTmpMin(todayTmpMin);
            sevenDayWeatherInfo.setTodayPm25(0);
            sevenDayWeatherInfo.setTodayAqi(0);

            String secondDayWeatherCode = jsonObject.optString("secondDayWeatherCode");
            int secondDayTmpMax = jsonObject.optInt("secondDayTmpMax");
            int secondDayTmpMin = jsonObject.optInt("secondDayTmpMin");
            //second
            sevenDayWeatherInfo.setSecondDayWeatherCode(secondDayWeatherCode);
            sevenDayWeatherInfo.setSecondDayTmpMax(secondDayTmpMax);
            sevenDayWeatherInfo.setSecondDayTmpMin(secondDayTmpMin);

            String thirdDayWeatherCode = jsonObject.optString("thirdDayWeatherCode");
            int thirdDayTmpMax = jsonObject.optInt("thirdDayTmpMax");
            int thirdDayTmpMin = jsonObject.optInt("thirdDayTmpMin");
            //third
            sevenDayWeatherInfo.setThirdDayWeatherCode(thirdDayWeatherCode);
            sevenDayWeatherInfo.setThirdDayTmpMax(thirdDayTmpMax);
            sevenDayWeatherInfo.setThirdDayTmpMin(thirdDayTmpMin);

            String fourthDayWeatherCode = jsonObject.optString("fourthDayWeatherCode");
            int fourthDayTmpMax = jsonObject.optInt("fourthDayTmpMax");
            int fourthDayTmpMin = jsonObject.optInt("fourthDayTmpMin");
            //fourth
            sevenDayWeatherInfo.setFourthDayWeatherCode(fourthDayWeatherCode);
            sevenDayWeatherInfo.setFourthDayTmpMax(fourthDayTmpMax);
            sevenDayWeatherInfo.setFourthDayTmpMin(fourthDayTmpMin);

            String fifthDayWeatherCode = jsonObject.optString("fifthDayWeatherCode");
            int fifthDayTmpMax = jsonObject.optInt("fifthDayTmpMax");
            int fifthDayTmpMin = jsonObject.optInt("fifthDayTmpMin");
            //fifth
            sevenDayWeatherInfo.setFifthDayWeatherCode(fifthDayWeatherCode);
            sevenDayWeatherInfo.setFifthDayTmpMax(fifthDayTmpMax);
            sevenDayWeatherInfo.setFifthDayTmpMin(fifthDayTmpMin);

            String sixthDayWeatherCode = jsonObject.optString("sixthDayWeatherCode");
            int sixthDayTmpMax = jsonObject.optInt("sixthDayTmpMax");
            int sixthDayTmpMin = jsonObject.optInt("sixthDayTmpMin");
            //sixth
            sevenDayWeatherInfo.setSixthDayWeatherCode(sixthDayWeatherCode);
            sevenDayWeatherInfo.setSixthDayTmpMax(sixthDayTmpMax);
            sevenDayWeatherInfo.setSixthDayTmpMin(sixthDayTmpMin);

            String seventhDayWeatherCode = jsonObject.optString("seventhDayWeatherCode");
            int seventhDayTmpMax = jsonObject.optInt("seventhDayTmpMax");
            int seventhDayTmpMin = jsonObject.optInt("seventhDayTmpMin");
            //seventh
            sevenDayWeatherInfo.setSeventhDayWeatherCode(seventhDayWeatherCode);
            sevenDayWeatherInfo.setSeventhDayTmpMax(seventhDayTmpMax);
            sevenDayWeatherInfo.setSeventhDayTmpMin(seventhDayTmpMin);

//                    "todayPm25":"",
//                    "todayAqi":"",

            if (mWriteCommand != null) {
                mWriteCommand.syncWeatherToBLEForXiaoYang(sevenDayWeatherInfo);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } catch (Exception exp) {
            Log.e("set24HeartRateExp::", exp.getMessage());
            //result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void set24HrTemperatureTest(MethodCall call, Result result) {
        try {
            String inter = call.argument("interval");
            assert inter != null;
            int interval = Integer.parseInt(inter);
            if (mWriteCommand != null) {
                mWriteCommand.syncTemperatureAutomaticTestInterval(true, interval*60);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } catch (Exception exp) {
            Log.e("set24HeartRateExp::", exp.getMessage());
            //result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void getDeviceVersion(Result result) {
        try {
            deviceVersionIDResult = result;
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.sendToReadBLEVersion();
                    // result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                //device disconnected
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } catch (Exception exp) {
            Log.e("getDeviceVersionExp:", exp.getMessage());
        }
    }

    private void getDeviceBatteryStatus(Result result) {
        try {
            deviceBatteryResult = result;
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.sendToReadBLEBattery();
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                //device disconnected
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } catch (Exception exp) {
            Log.e("getBatteryStatusExp:", exp.getMessage());
        }
    }

    private void getCheckConnectionStatus(Result result) {
        try {
            result.success(SPUtil.getInstance(mContext).getBleConnectStatus());
        } catch (Exception exp) {
            Log.e("getConnectionStatusExp:", exp.getMessage());
        }
    }

    // sync the activities
    private void fetchAllJudgement(MethodCall call, Result result) {
        try {
            boolean bluConnect = SPUtil.getInstance(mContext).getBleConnectStatus();
            Log.e("bluConnect: ", "" + bluConnect);
            JSONObject judgeJson = new JSONObject();
            if (bluConnect) {

                boolean rkPlatform = SPUtil.getInstance(mContext).getRKPlatform();
                boolean isSupportNewParams = GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_NEW_PARAMETER_SETTINGS_FUNCTION);
                boolean isBandLostFunction = GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_BAND_LOST_FUNCTION);
                boolean isSwitchBraceletLang = GetFunctionList.isSupportFunction_Third(mContext, GlobalVariable.IS_SUPPORT_SWITCH_BRACELET_LANGUAGE);
                boolean isSupportTempUnitSwitch = GetFunctionList.isSupportFunction_Third(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_UNIT_SWITCH);
                boolean isMinHRAlarm = GetFunctionList.isSupportFunction_Fourth(mContext, GlobalVariable.IS_SUPPORT_MIN_HEAR_RATE_ALARM);

                boolean isSupportHorVer = GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_HOR_VER);

                boolean isSupport24HrRate = GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_24_HOUR_RATE_TEST);
                boolean isTemperatureTest = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST);
                boolean isTemperatureCalibration = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_CALIBRATION);
                boolean isSupportOxygen = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN);

                boolean isSupportBreathe = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_BREATHE);

                boolean isSupportWeather = GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_SEVEN_DAYS_WEATHER);

                boolean isWeatherForeCast =  GetFunctionList.isSupportFunction(mContext, GlobalVariable.IS_SUPPORT_WEATHER_FORECAST);

                boolean isSupportDrinkWater =  GetFunctionList.isSupportFunction_Fifth(mContext,GlobalVariable.IS_SUPPORT_DRINK_WATER_REMIND);

                boolean isSupportWristCalibra =  GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_TURN_WRIST_CALIBRATION);

                boolean isSupportBandFindPhone =    GetFunctionList.isSupportFunction_Second(mContext , GlobalVariable.IS_SUPPORT_BAND_FIND_PHONE_FUNCTION) ;

                boolean isSupportMusicalControl =  GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_MUSIC_CONTROL);
                boolean isSupportWristDetection =  GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_WRIST_DETECTION_SWITCH);
                boolean isSupportMultiSportsHR =  GetFunctionList.isSupportFunction_Third(mContext, GlobalVariable.IS_SUPPORT_MULTIPLE_SPORTS_MODES_HEART_RATE);
                boolean isSupportSportControl =  GetFunctionList.isSupportFunction_Fifth(mContext,GlobalVariable.IS_SUPPORT_SPORT_CONTROL);

                boolean isSupportOnlineDial =  GetFunctionList.isSupportFunction_Third(mContext, GlobalVariable.IS_SUPPORT_ONLINE_DIAL);
                boolean isSupportBodyComposition =  GetFunctionList.isSupportFunction_Third(mContext,GlobalVariable.IS_SUPPORT_BODY_COMPOSITION);
                boolean isSupportECG =  GetFunctionList.isSupportFunction_Third(mContext,GlobalVariable.IS_SUPPORT_ECG);


                try {

                    //judgeJson.put("status", WatchConstants.SC_SUCCESS);
                    judgeJson.put("rkPlatform", rkPlatform);
                    judgeJson.put("isSupportNewParams", isSupportNewParams);
                    judgeJson.put("isBandLostFunction", isBandLostFunction);
                    judgeJson.put("isBraceletLangSwitch", isSwitchBraceletLang);
                    judgeJson.put("isTempUnitSwitch", isSupportTempUnitSwitch);
                    judgeJson.put("isMinHRAlarm", isMinHRAlarm);
                    judgeJson.put("isTempTest", isTemperatureTest);
                    judgeJson.put("isTempCalibration", isTemperatureCalibration);
                    judgeJson.put("isSupportHorVer", isSupportHorVer);
                    judgeJson.put("isSupport24HrRate", isSupport24HrRate);
                    judgeJson.put("isSupportOxygen", isSupportOxygen);
                    judgeJson.put("isSupportBreathe", isSupportBreathe);
                    judgeJson.put("isSupportWeather", isSupportWeather);
                    judgeJson.put("isWeatherForeCast", isWeatherForeCast);
                    judgeJson.put("isSupportDrinkWater", isSupportDrinkWater);
                    judgeJson.put("isSupportWristCalibra", isSupportWristCalibra);
                    judgeJson.put("isSupportBandFindPhone", isSupportBandFindPhone);
                    judgeJson.put("isSupportMusicalControl", isSupportMusicalControl);
                    judgeJson.put("isSupportWristDetection", isSupportWristDetection);
                    judgeJson.put("isSupportMultiSportsHR", isSupportMultiSportsHR);
                    judgeJson.put("isSupportSportControl", isSupportSportControl);
                    judgeJson.put("isSupportOnlineDial", isSupportOnlineDial);
                    judgeJson.put("isSupportBodyComposition", isSupportBodyComposition);
                    judgeJson.put("isSupportECG", isSupportECG);

//                AsyncExecuteUpdate asyncTask=new AsyncExecuteUpdate();
//                asyncTask.execute("");
               /* if (mWriteCommand != null) {
                    mWriteCommand.syncAllStepData();
                    mWriteCommand.syncAllSleepData();
                    mWriteCommand.syncRateData();
                    *//*mWriteCommand.syncAllRateData();
                    if (isSupport24HrRate) {
                        mWriteCommand.sync24HourRate();
                    }*//*
                    mWriteCommand.syncAllBloodPressureData();
                    mWriteCommand.syncAllTemperatureData();
                    if (isSupportOxygen){
                        mWriteCommand.syncOxygenData();
                    }
                    result.success(WatchConstants.SC_INIT);

                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }*/
                    result.success(judgeJson.toString());
                    //result.success(WatchConstants.SC_FAILURE);
                } catch (Exception exp) {
                    Log.e("syncAllJSONExp: ", "" + exp.getMessage());
                    result.success(WatchConstants.SC_FAILURE);
                    //result.success(judgeJson.toString());
                }

            } else {
                //device disconnected
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } catch (Exception exp) {
            Log.e("fetchAllJudgeExp:", exp.getMessage());
        }
    }

    private void syncAllStepsData(Result result) {
        try {
            Log.e("steps_status", "" + SPUtil.getInstance(mContext).getBleConnectStatus());
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.syncAllStepData();
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                //device disconnected
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } catch (Exception exp) {
            Log.e("syncAllStepsExp:", exp.getMessage());
        }
    }

    private void syncAllSleepData(Result result) {
        try {
            Log.e("sleep_status", "" + SPUtil.getInstance(mContext).getBleConnectStatus());
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.syncAllSleepData();
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                //device disconnected
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } catch (Exception exp) {
            Log.e("syncAllSleepExp:", exp.getMessage());
        }
    }

    private void syncRateData(Result result) {
        try {
            boolean support = GetFunctionList.isSupportFunction_Second(mContext, GlobalVariable.IS_SUPPORT_24_HOUR_RATE_TEST);
            Log.e("support", "" + support);
            Log.e("steps_status", "" + SPUtil.getInstance(mContext).getBleConnectStatus());
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    //boolean supportHr24 = GetFunctionList.isSupportFunction_Second(mContext,GlobalVariable.IS_SUPPORT_24_HOUR_RATE_TEST);
                    //                    if (supportHr24){
//                        mWriteCommand.sync24HourRate();
//                    }else{
                    mWriteCommand.syncRateData();
                    //  }
                    //mWriteCommand.syncRateData();
                    /*mWriteCommand. syncAllRateData();
                     */
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                //device disconnected
                result.success(WatchConstants.SC_DISCONNECTED);
            }

        } catch (Exception exp) {
            Log.e("syncRateDataExp:", exp.getMessage());
        }
    }

    private void syncBloodPressure(Result result) {
        try {
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.syncAllBloodPressureData();
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                result.success(WatchConstants.SC_DISCONNECTED);
            }

        } catch (Exception exp) {
            Log.e("syncBPExp:", exp.getMessage());
        }
    }

    private void syncOxygenSaturation(Result result) {
        try {
            boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN);
            Log.e("syncOxygenSat", "isSupported: " + isSupported);
            if (isSupported) {
                if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                    if (mWriteCommand != null) {
                        mWriteCommand.syncOxygenData();
                        result.success(WatchConstants.SC_INIT);
                    } else {
                        result.success(WatchConstants.SC_FAILURE);
                    }
                } else {
                    result.success(WatchConstants.SC_DISCONNECTED);
                }
            } else {
                result.success(WatchConstants.SC_NOT_SUPPORTED);
            }

        } catch (Exception exp) {
            Log.e("syncOxygenExp:", exp.getMessage());
        }
        // below methods need to called, while it supports
        // mBluetoothLeService.setOxygenListener(oxygenRealListener);
    }

    private void syncBodyTemperature(Result result) {
        try {
            boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST);
            Log.e("syncBodyTemp", "isSupported: " + isSupported);
            if (isSupported) {
                if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                    if (mWriteCommand != null) {
                        mWriteCommand.syncAllTemperatureData();
                        result.success(WatchConstants.SC_INIT);
                    } else {
                        result.success(WatchConstants.SC_FAILURE);
                    }
                } else {
                    result.success(WatchConstants.SC_DISCONNECTED);
                }
            } else {
                result.success(WatchConstants.SC_NOT_SUPPORTED);
            }
        } catch (Exception exp) {
            Log.e("syncBodyTempExp:", exp.getMessage());
        }
        // mBluetoothLeService.setTemperatureListener(temperatureListener);
    }

    // fetch by date time
    private void fetchOverAllBySelectedDate(MethodCall call, Result result) {
        try {
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject overAllJson = new JSONObject();
            JSONObject stepsJsonData = new JSONObject();
            JSONObject sleepJsonData = new JSONObject();
            if (mUTESQLOperate != null) {
                overAllJson.put("status", WatchConstants.SC_SUCCESS);
                // stepByDate
                StepOneDayAllInfo stepOneDayAllInfo = mUTESQLOperate.queryRunWalkInfo(dateTime);
                List<RateOneDayInfo> rateOneDayInfoList = mUTESQLOperate.queryRateOneDayDetailInfo(dateTime);
                List<Rate24HourDayInfo> rate24HourDayInfoList = mUTESQLOperate.query24HourRateDayInfo(dateTime);
                List<BPVOneDayInfo> bpvOneDayInfoList = mUTESQLOperate.queryBloodPressureOneDayInfo(dateTime);
                SleepTimeInfo sleepTimeInfo = mUTESQLOperate.querySleepInfo(dateTime);
                List<TemperatureInfo> temperatureInfoList = mUTESQLOperate.queryTemperatureDate(dateTime);
                //steps data
                if (stepOneDayAllInfo != null) {
                    stepsJsonData.put("steps", stepOneDayAllInfo.getStep());
                    stepsJsonData.put("distance", "" + GlobalMethods.convertDoubleToStringWithDecimal(stepOneDayAllInfo.getDistance()));
                    stepsJsonData.put("calories", "" + GlobalMethods.convertDoubleToStringWithDecimal(stepOneDayAllInfo.getCalories()));
                    stepsJsonData.put("calender", stepOneDayAllInfo.getCalendar());
                    ArrayList<StepOneHourInfo> stepOneHourInfoArrayList = stepOneDayAllInfo.getStepOneHourArrayInfo();
                    JSONArray stepsArray = new JSONArray();
                    for (StepOneHourInfo stepOneHourInfo : stepOneHourInfoArrayList) {
                        JSONObject object = new JSONObject();
                        object.put("step", stepOneHourInfo.getStep());
                        object.put("time", GlobalMethods.getIntegerToHHmm(stepOneHourInfo.getTime()));
                        stepsArray.put(object);
                    }
                    stepsJsonData.put("data", stepsArray);
                    overAllJson.put("steps", stepsJsonData);
                }

                //heart rate data
                if (rateOneDayInfoList != null) {
                    JSONArray hrArray = new JSONArray();
                    for (RateOneDayInfo rateOneDayInfo : rateOneDayInfoList) {
                        JSONObject object = new JSONObject();
                        object.put("calender", rateOneDayInfo.getCalendar());
                        object.put("time", GlobalMethods.getTimeByIntegerMin(rateOneDayInfo.getTime()));
                        object.put("rate", rateOneDayInfo.getRate());
                        object.put("calenderTime", rateOneDayInfo.getCalendarTime());
                        object.put("currentRate", rateOneDayInfo.getCurrentRate());
                        object.put("high", rateOneDayInfo.getHighestRate());
                        object.put("low", rateOneDayInfo.getLowestRate());
                        object.put("average", rateOneDayInfo.getVerageRate());
                        hrArray.put(object);
                    }
                    overAllJson.put("hr", hrArray);
                }

                //HR 24 hours
                if (rate24HourDayInfoList != null) {
                    JSONArray hr24Array = new JSONArray();
                    for (Rate24HourDayInfo rate24HourDayInfo : rate24HourDayInfoList) {
                        JSONObject object = new JSONObject();
                        object.put("calender", rate24HourDayInfo.getCalendar());
                        object.put("time", GlobalMethods.getTimeByIntegerMin(rate24HourDayInfo.getTime()));
                        object.put("rate", rate24HourDayInfo.getRate());
                        //Log.e("jsonObject", "object: " + object.toString());
                        hr24Array.put(object);
                    }
                    overAllJson.put("hr24", hr24Array);
                }

                //BP
                if (bpvOneDayInfoList != null) {
                    JSONArray bpArray = new JSONArray();
                    for (BPVOneDayInfo bpvOneDayInfo : bpvOneDayInfoList) {
                        JSONObject object = new JSONObject();
                        object.put("calender", bpvOneDayInfo.getCalendar());
                        object.put("time", GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
                        object.put("high", bpvOneDayInfo.getHightBloodPressure());
                        object.put("low", bpvOneDayInfo.getLowBloodPressure());
                        //Log.e("bpObject", "object: " + object.toString());
                        bpArray.put(object);
                    }
                    overAllJson.put("bp", bpArray);
                }
                //sleep data
                if (sleepTimeInfo != null) {
                    sleepJsonData.put("calender", sleepTimeInfo.getCalendar());
                    sleepJsonData.put("total", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));
                    sleepJsonData.put("light", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
                    sleepJsonData.put("deep", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
                    sleepJsonData.put("awake", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
                    sleepJsonData.put("beginTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
                    sleepJsonData.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));

                    sleepJsonData.put("totalNum", ""+sleepTimeInfo.getSleepTotalTime());
                    sleepJsonData.put("lightNum", ""+sleepTimeInfo.getLightTime());
                    sleepJsonData.put("deepNum", ""+sleepTimeInfo.getDeepTime());
                    sleepJsonData.put("awakeNum", ""+sleepTimeInfo.getAwakeTime());
                    sleepJsonData.put("beginTimeNum",""+sleepTimeInfo.getBeginTime());
                    sleepJsonData.put("endTimeNum",""+sleepTimeInfo.getEndTime());

                    List<SleepInfo> sleepInfoList = sleepTimeInfo.getSleepInfoList();
                    JSONArray sleepArray = new JSONArray();
                    for (SleepInfo sleepInfo : sleepInfoList) {
                        JSONObject object = new JSONObject();
                        object.put("state", sleepInfo.getColorIndex()); // deep sleep: 0, Light sleep: 1,  awake: 2
                        object.put("startTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()));
                        object.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                        object.put("diffTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));

                        object.put("startTimeNum", ""+sleepInfo.getStartTime());
                        object.put("endTimeNum", ""+sleepInfo.getEndTime());
                        object.put("diffTimeNum",""+sleepInfo.getDiffTime());
                        sleepArray.put(object);
                    }
                    sleepJsonData.put("data", sleepArray);

                    overAllJson.put("sleep", sleepJsonData);
                }


                //Temperature
                if (temperatureInfoList != null) {
                    JSONArray temperatureArray = new JSONArray();
                    for (TemperatureInfo temperatureInfo : temperatureInfoList) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("calender", temperatureInfo.getCalendar());
                        tempObj.put("type", "" + temperatureInfo.getType());
                        tempObj.put("inCelsius", "" + GlobalMethods.convertDoubleToCelsiusWithDecimal(temperatureInfo.getBodyTemperature()));
                        tempObj.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                        tempObj.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                        tempObj.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));
                        Log.e("jsonObject", "object: " + tempObj.toString());
                        temperatureArray.put(tempObj);
                    }
                    overAllJson.put("temperature", temperatureArray);
                }

                result.success(overAllJson.toString());
            } else {
                result.success(overAllJson.toString());
            }

        } catch (Exception exp) {
            Log.e("fetchOverAllExp::", exp.getMessage());
            result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchStepsBySelectedDate(MethodCall call, Result result) {
        // providing proper list of the data on basis of the result.
        try {
            //  new SimpleDateFormat("yyyyMMdd", Locale.US)).format(var1) //20220212
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject jsonObject = new JSONObject();
            if (mUTESQLOperate != null) {

                StepOneDayAllInfo stepOneDayAllInfo = mUTESQLOperate.queryRunWalkInfo(dateTime);

                jsonObject.put("status", WatchConstants.SC_SUCCESS);

                jsonObject.put("steps", stepOneDayAllInfo.getStep());

                Log.e("onStepChange111", "getStep: " + stepOneDayAllInfo.getStep());
                Log.e("onStepChange112", "getCalories: " + stepOneDayAllInfo.getCalories());
                Log.e("onStepChange113", "getDistance: " + stepOneDayAllInfo.getDistance());

                jsonObject.put("distance", "" + GlobalMethods.convertDoubleToStringWithDecimal(stepOneDayAllInfo.getDistance()));
                jsonObject.put("calories", "" + GlobalMethods.convertDoubleToStringWithDecimal(stepOneDayAllInfo.getCalories()));

                ArrayList<StepOneHourInfo> stepOneHourInfoArrayList = stepOneDayAllInfo.getStepOneHourArrayInfo();
                JSONArray jsonArray = new JSONArray();
                for (StepOneHourInfo stepOneHourInfo : stepOneHourInfoArrayList) {
                    JSONObject object = new JSONObject();
                    object.put("stepValue", stepOneHourInfo.getStep());
                    Log.e("onStepChange114", "oneHourStep: " + stepOneHourInfo.getStep());
                    Log.e("onStepChange114", "oneHourStep: " + stepOneHourInfo.getTime());
                    Log.e("onStepChange114", "intToString: " + GlobalMethods.getIntegerToHHmm(stepOneHourInfo.getTime())); // as per glory fit
                    //Log.e("onStepChange114", "fromMinutesToHHmm: " + fromMinutesToHHmm(stepOneHourInfo.getTime()));
                    object.put("time", GlobalMethods.getIntegerToHHmm(stepOneHourInfo.getTime()));
                    jsonArray.put(object);
                }
                jsonObject.put("data", jsonArray);

                List<StepOneDayAllInfo> list = mUTESQLOperate.queryRunWalkAllDay();
                Log.e("list", "list: " + list.size());
                for (StepOneDayAllInfo info : list) {
                    Log.e("list_info:", "calender: " + info.getCalendar());
                    Log.e("list_info:", "step: " + info.getStep());
                    Log.e("list_info:", "cal: " + info.getCalories());
                    Log.e("list_info:", "dis: " + info.getDistance());
                }

                result.success(jsonObject.toString());
            } else {
                result.success(jsonObject.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchStepExp::", exp.getMessage());
            //  result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchSleepByDate(MethodCall call, Result result) {
        // providing proper list of the data on basis of the result.
        try {
            //  new SimpleDateFormat("yyyyMMdd", Locale.US)).format(var1) //20220212
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

                SleepTimeInfo sleepTimeInfo = mUTESQLOperate.querySleepInfo(dateTime);

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                resultJson.put("calender", sleepTimeInfo.getCalendar());
                resultJson.put("total", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));
                resultJson.put("light", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
                resultJson.put("deep", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
                resultJson.put("awake", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
                resultJson.put("beginTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
                resultJson.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));

                resultJson.put("totalNum", ""+sleepTimeInfo.getSleepTotalTime());
                resultJson.put("lightNum", ""+sleepTimeInfo.getLightTime());
                resultJson.put("deepNum", ""+sleepTimeInfo.getDeepTime());
                resultJson.put("awakeNum", ""+sleepTimeInfo.getAwakeTime());
                resultJson.put("beginTimeNum",""+sleepTimeInfo.getBeginTime());
                resultJson.put("endTimeNum",""+sleepTimeInfo.getEndTime());
//                Log.e("sleepTimeInfo111", "getBeginTime: " +  GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
//                Log.e("sleepTimeInfo111", "getEndTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));
//                Log.e("sleepTimeInfo111", "getDeepTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
//                Log.e("sleepTimeInfo111", "getAwakeTime: " +GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
//                Log.e("sleepTimeInfo111", "getLightTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
//                Log.e("sleepTimeInfo111", "getSleepTotalTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));

                // fetch the particular day sleep along state records
                List<SleepInfo> sleepInfoList = sleepTimeInfo.getSleepInfoList();
                Log.e("sleepInfoList", "sleepInfoList: " + sleepInfoList.size());
                JSONArray jsonArray = new JSONArray();
                for (SleepInfo sleepInfo : sleepInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("state", sleepInfo.getColorIndex()); // deep sleep: 0, Light sleep: 1,  awake: 2
                    object.put("startTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()));
                    object.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                    object.put("diffTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));

                    object.put("startTimeNum", ""+sleepInfo.getStartTime());
                    object.put("endTimeNum", ""+sleepInfo.getEndTime());
                    object.put("diffTimeNum",""+sleepInfo.getDiffTime());
//                    Log.e("sleepInfoList", "getColorIndex: " + sleepInfo.getColorIndex());
//                    Log.e("sleepInfoList", "getDiffTime: " + GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));
//                    Log.e("sleepInfoList", "getStartTime: " + GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()) + " -- " + GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                    // Log.e("sleepInfoList", );
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);

                result.success(resultJson.toString());
            } else {
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchStepExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchBPByDate(MethodCall call, Result result) {
        // providing proper list of the data on basis of the result.
        try {
            //  new SimpleDateFormat("yyyyMMdd", Locale.US)).format(var1) //20220212
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

                List<BPVOneDayInfo> bpvOneDayInfoList = mUTESQLOperate.queryBloodPressureOneDayInfo(dateTime);
                Log.e("bpvOneDayInfoList", "bpvOneDayInfoList: " + bpvOneDayInfoList.size());

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (BPVOneDayInfo bpvOneDayInfo : bpvOneDayInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender", bpvOneDayInfo.getCalendar());
                    object.put("time", GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
                    object.put("high", bpvOneDayInfo.getHightBloodPressure());
                    object.put("low", bpvOneDayInfo.getLowBloodPressure());
                    Log.e("bpObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);
                result.success(resultJson.toString());
            } else {
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchStepExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchHRByDate(MethodCall call, Result result) {
        // providing proper list of the data on basis of the result.
        try {
            //  new SimpleDateFormat("yyyyMMdd", Locale.US)).format(var1) //20220212
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {
                List<RateOneDayInfo> rateOneDayInfoList = mUTESQLOperate.queryRateOneDayDetailInfo(dateTime);
                // List<RateOneDayInfo> rateOneDayInfoList = mUTESQLOperate.queryRateAllInfo(); // list for the current date //size 5
//                RateOneDayInfo rateOneDay = mUTESQLOperate.queryRateOneDayMainInfo(dateTime);
//                Log.e("rateOneDay", "getRate: " +  rateOneDay.getRate());
//                Log.e("rateOneDay", "getTime: " +  GlobalMethods.getTimeByIntegerMin(rateOneDay.getTime()));
                Log.e("rateOneDayInfoList", "rateOneDayInfoList: " + rateOneDayInfoList.size());
                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (RateOneDayInfo rateOneDayInfo : rateOneDayInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender", rateOneDayInfo.getCalendar());
                    object.put("time", GlobalMethods.getTimeByIntegerMin(rateOneDayInfo.getTime()));
                    object.put("rate", rateOneDayInfo.getRate());
                    object.put("calenderTime", rateOneDayInfo.getCalendarTime());
                    object.put("currentRate", rateOneDayInfo.getCurrentRate());
                    object.put("high", rateOneDayInfo.getHighestRate());
                    object.put("low", rateOneDayInfo.getLowestRate());
                    object.put("average", rateOneDayInfo.getVerageRate());
                    Log.e("jsonObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);
                result.success(resultJson.toString());
            } else {
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchHRExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetch24HourHRDateByDate(MethodCall call, Result result) {
        try {
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {
                //  List<Rate24HourDayInfo> rate24HourDayInfoList =  mUTESQLOperate.query24HourRateAllInfo(); // provides overall available 24 hrs data from storage
                List<Rate24HourDayInfo> rate24HourDayInfoList = mUTESQLOperate.query24HourRateDayInfo(dateTime);
                Log.e("rateOneDayInfoList", "rateOneDayInfoList: " + rate24HourDayInfoList.size());

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (Rate24HourDayInfo rate24HourDayInfo : rate24HourDayInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender", rate24HourDayInfo.getCalendar());
                    object.put("time", GlobalMethods.getTimeByIntegerMin(rate24HourDayInfo.getTime()));
                    object.put("rate", rate24HourDayInfo.getRate());
                    Log.e("jsonObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);

                result.success(resultJson.toString());
            } else {
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetch24HourHRExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchTemperatureByDate(MethodCall call, Result result) {
        try {
            String dateTime = call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

                List<TemperatureInfo> temperatureInfoList = mUTESQLOperate.queryTemperatureDate(dateTime);
                Log.e("temperatureInfoList", "temperatureInfoList: " + temperatureInfoList.size());

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (TemperatureInfo temperatureInfo : temperatureInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender", temperatureInfo.getCalendar());

                    object.put("type", "" + temperatureInfo.getType());
                    object.put("inCelsius", "" + GlobalMethods.convertDoubleToCelsiusWithDecimal(temperatureInfo.getBodyTemperature()));
                    object.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
//                    object.put("ambientTemp", "" + temperatureInfo.getAmbientTemperature());
//                    object.put("surfaceTemp", "" + temperatureInfo.getBodySurfaceTemperature());
                    object.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                    object.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));
                    Log.e("jsonObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);

                result.success(resultJson.toString());
            } else {
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchTempByDate::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    //gathering individual data
    private void fetchAllStepsData(Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {
                List<StepOneDayAllInfo> list = mUTESQLOperate.queryRunWalkAllDay();
                Log.e("list", "list: " + list.size());
                if (list != null) {
                    resultObject.put("status", WatchConstants.SC_SUCCESS);
                    JSONArray jsonArray = new JSONArray();
                    for (StepOneDayAllInfo info : list) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("calender", info.getCalendar());
                        jsonObject.put("steps", info.getStep());
                        jsonObject.put("calories", GlobalMethods.convertDoubleToStringWithDecimal(info.getCalories()));
                        jsonObject.put("distance", GlobalMethods.convertDoubleToStringWithDecimal(info.getDistance()));

                        Log.e("list_info:", "calender: " + info.getCalendar());
                        Log.e("list_info:", "step: " + info.getStep());
                        Log.e("list_info:", "cal: " + info.getCalories());
                        Log.e("list_info:", "dis: " + info.getDistance());

                        ArrayList<StepOneHourInfo> stepOneHourInfoArrayList = info.getStepOneHourArrayInfo();
                        JSONArray stepsArray = new JSONArray();
                        for (StepOneHourInfo stepOneHourInfo : stepOneHourInfoArrayList) {
                            JSONObject object = new JSONObject();
                            object.put("step", stepOneHourInfo.getStep());
                            object.put("time", GlobalMethods.getIntegerToHHmm(stepOneHourInfo.getTime()));
                            stepsArray.put(object);
                        }
                        jsonObject.put("data", stepsArray);

                        jsonArray.put(jsonObject);
                    }
                    resultObject.put("data", jsonArray);
                } else {
                    resultObject.put("status", WatchConstants.SC_FAILURE);
                }
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllStepExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllSleepData(Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {

                List<SleepTimeInfo> sleepTimeInfoList = mUTESQLOperate.queryAllSleepInfo();
                Log.e("list", "list: " + sleepTimeInfoList.size());

                if (sleepTimeInfoList != null) {

                    resultObject.put("status", WatchConstants.SC_SUCCESS);
                    JSONArray jsonArray = new JSONArray();
                    for (SleepTimeInfo sleepTimeInfo : sleepTimeInfoList) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("calender", sleepTimeInfo.getCalendar());
                        jsonObject.put("total", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));
                        jsonObject.put("light", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
                        jsonObject.put("deep", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
                        jsonObject.put("awake", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
                        jsonObject.put("beginTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
                        jsonObject.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));

                        jsonObject.put("totalNum", ""+sleepTimeInfo.getSleepTotalTime());
                        jsonObject.put("lightNum", ""+sleepTimeInfo.getLightTime());
                        jsonObject.put("deepNum", ""+sleepTimeInfo.getDeepTime());
                        jsonObject.put("awakeNum", ""+sleepTimeInfo.getAwakeTime());
                        jsonObject.put("beginTimeNum",""+sleepTimeInfo.getBeginTime());
                        jsonObject.put("endTimeNum",""+sleepTimeInfo.getEndTime());


                        List<SleepInfo> sleepInfoList = sleepTimeInfo.getSleepInfoList();
                        Log.e("sleepInfoList", "sleepInfoList: " + sleepInfoList.size());

                        JSONArray sleepDataArray = new JSONArray();
                        for (SleepInfo sleepInfo : sleepInfoList) {
                            JSONObject object = new JSONObject();
                            object.put("state", sleepInfo.getColorIndex()); // deep sleep: 0, Light sleep: 1,  awake: 2
                            object.put("startTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()));
                            object.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                            object.put("diffTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));

                            object.put("startTimeNum", ""+sleepInfo.getStartTime());
                            object.put("endTimeNum", ""+sleepInfo.getEndTime());
                            object.put("diffTimeNum",""+sleepInfo.getDiffTime());
                            sleepDataArray.put(object);
                        }

                        jsonObject.put("data", sleepDataArray);

                        jsonArray.put(jsonObject);
                    }
                    resultObject.put("data", jsonArray);
                } else {
                    resultObject.put("status", WatchConstants.SC_FAILURE);
                }
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllSleepExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllBPData(Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {
                List<BPVOneDayInfo> bpvOneDayInfoList = mUTESQLOperate.queryAllBloodPressureInfo();
                Log.e("list", "list: " + bpvOneDayInfoList.size());
                if (bpvOneDayInfoList != null) {
                    resultObject.put("status", WatchConstants.SC_SUCCESS);
                    JSONArray jsonArray = new JSONArray();
                    for (BPVOneDayInfo bpvOneDayInfo : bpvOneDayInfoList) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("calender", bpvOneDayInfo.getCalendar());
                        jsonObject.put("time", GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
                        jsonObject.put("high", bpvOneDayInfo.getHightBloodPressure());
                        jsonObject.put("low", bpvOneDayInfo.getLowBloodPressure());
                        jsonArray.put(jsonObject);
                    }
                    resultObject.put("data", jsonArray);
                } else {
                    resultObject.put("status", WatchConstants.SC_FAILURE);
                }
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllBPExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllTemperatureData(Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {
                List<TemperatureInfo> temperatureInfoList = mUTESQLOperate.queryTemperatureAll();
                Log.e("list", "list: " + temperatureInfoList.size());
                if (temperatureInfoList != null) {
                    resultObject.put("status", WatchConstants.SC_SUCCESS);
                    JSONArray jsonArray = new JSONArray();
                    for (TemperatureInfo temperatureInfo : temperatureInfoList) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("calender", temperatureInfo.getCalendar());
                        jsonObject.put("type", "" + temperatureInfo.getType());
                        jsonObject.put("inCelsius", "" + GlobalMethods.convertDoubleToCelsiusWithDecimal(temperatureInfo.getBodyTemperature()));
                        jsonObject.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                        jsonObject.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                        jsonObject.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));
                        jsonArray.put(jsonObject);
                    }
                    resultObject.put("data", jsonArray);
                } else {
                    resultObject.put("status", WatchConstants.SC_FAILURE);
                }
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllTempExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllHeartRate24Data(Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {
                List<Rate24HourDayInfo> rate24HourDayInfoList = mUTESQLOperate.query24HourRateAllInfo();
                Log.e("hr24list", "list: " + rate24HourDayInfoList.size());
                if (rate24HourDayInfoList != null) {
                    resultObject.put("status", WatchConstants.SC_SUCCESS);
                    JSONArray jsonArray = new JSONArray();
                    for (Rate24HourDayInfo rate24HourDayInfo : rate24HourDayInfoList) {
                        JSONObject object = new JSONObject();
                        object.put("calender", rate24HourDayInfo.getCalendar());
                        object.put("time", GlobalMethods.getTimeByIntegerMin(rate24HourDayInfo.getTime()));
                        object.put("rate", rate24HourDayInfo.getRate());
                        //Log.e("jsonObject", "object: " + object.toString());
                        jsonArray.put(object);
                    }
                    resultObject.put("data", jsonArray);
                } else {
                    resultObject.put("status", WatchConstants.SC_FAILURE);
                }
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllHr24Exp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchOverAllDeviceData(Result result) {
        try {
            JSONObject overAllJson = new JSONObject();
            if (mUTESQLOperate != null) {
                overAllJson.put("status", WatchConstants.SC_SUCCESS);

                //steps data
                List<StepOneDayAllInfo> stepsInfoList = mUTESQLOperate.queryRunWalkAllDay();
                if (stepsInfoList != null) {
                    JSONArray stepsJsonArray = new JSONArray();
                    for (StepOneDayAllInfo info : stepsInfoList) {
                        JSONObject stepsObject = new JSONObject();
                        stepsObject.put("calender", info.getCalendar());
                        stepsObject.put("steps", info.getStep());
                        stepsObject.put("calories", GlobalMethods.convertDoubleToStringWithDecimal(info.getCalories()));
                        stepsObject.put("distance", GlobalMethods.convertDoubleToStringWithDecimal(info.getDistance()));
                        ArrayList<StepOneHourInfo> stepOneHourInfoArrayList = info.getStepOneHourArrayInfo();
                        JSONArray stepsArray = new JSONArray();
                        for (StepOneHourInfo stepOneHourInfo : stepOneHourInfoArrayList) {
                            JSONObject objStep = new JSONObject();
                            objStep.put("step", stepOneHourInfo.getStep());
                            objStep.put("time", GlobalMethods.getIntegerToHHmm(stepOneHourInfo.getTime()));
                            stepsArray.put(objStep);
                        }
                        stepsObject.put("data", stepsArray);
                        stepsJsonArray.put(stepsObject);
                    }
                    overAllJson.put("steps", stepsJsonArray);
                }


                //sleep data
                List<SleepTimeInfo> sleepInfoList = mUTESQLOperate.queryAllSleepInfo();
                if (sleepInfoList != null) {
                    JSONArray sleepJsonArray = new JSONArray();
                    for (SleepTimeInfo sleepTimeInfo : sleepInfoList) {
                        JSONObject sleepObject = new JSONObject();
                        sleepObject.put("calender", sleepTimeInfo.getCalendar());
                        sleepObject.put("total", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));
                        sleepObject.put("light", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
                        sleepObject.put("deep", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
                        sleepObject.put("awake", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
                        sleepObject.put("beginTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
                        sleepObject.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));

                        sleepObject.put("totalNum", ""+sleepTimeInfo.getSleepTotalTime());
                        sleepObject.put("lightNum", ""+sleepTimeInfo.getLightTime());
                        sleepObject.put("deepNum", ""+sleepTimeInfo.getDeepTime());
                        sleepObject.put("awakeNum", ""+sleepTimeInfo.getAwakeTime());
                        sleepObject.put("beginTimeNum",""+sleepTimeInfo.getBeginTime());
                        sleepObject.put("endTimeNum",""+sleepTimeInfo.getEndTime());
                        List<SleepInfo> sleepDataList = sleepTimeInfo.getSleepInfoList();
                        JSONArray sleepDataArray = new JSONArray();
                        for (SleepInfo sleepInfo : sleepDataList) {
                            JSONObject obj = new JSONObject();
                            obj.put("state", sleepInfo.getColorIndex()); // deep sleep: 0, Light sleep: 1,  awake: 2
                            obj.put("startTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()));
                            obj.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                            obj.put("diffTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));

                            obj.put("startTimeNum", ""+sleepInfo.getStartTime());
                            obj.put("endTimeNum", ""+sleepInfo.getEndTime());
                            obj.put("diffTimeNum",""+sleepInfo.getDiffTime());
                            sleepDataArray.put(obj);
                        }
                        sleepObject.put("data", sleepDataArray);
                        sleepJsonArray.put(sleepObject);
                    }
                    overAllJson.put("sleep", sleepJsonArray);
                }

                //heart rate data
                List<Rate24HourDayInfo> rate24InfoList = mUTESQLOperate.query24HourRateAllInfo();
                if (rate24InfoList != null) {
                    JSONArray rate24Array = new JSONArray();
                    for (Rate24HourDayInfo rate24HourDayInfo : rate24InfoList) {
                        JSONObject objectRate = new JSONObject();
                        objectRate.put("calender", rate24HourDayInfo.getCalendar());
                        objectRate.put("time", GlobalMethods.getTimeByIntegerMin(rate24HourDayInfo.getTime()));
                        objectRate.put("rate", rate24HourDayInfo.getRate());
                        //Log.e("jsonObject", "object: " + object.toString());
                        rate24Array.put(objectRate);
                    }
                    overAllJson.put("hr24", rate24Array);
                }

                // bp data
                List<BPVOneDayInfo> bpInfoList = mUTESQLOperate.queryAllBloodPressureInfo();
                if (bpInfoList != null) {
                    JSONArray bpArray = new JSONArray();
                    for (BPVOneDayInfo bpvOneDayInfo : bpInfoList) {
                        JSONObject bpObject = new JSONObject();
                        bpObject.put("calender", bpvOneDayInfo.getCalendar());
                        bpObject.put("time", GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
                        bpObject.put("high", bpvOneDayInfo.getHightBloodPressure());
                        bpObject.put("low", bpvOneDayInfo.getLowBloodPressure());
                        bpArray.put(bpObject);
                    }
                    overAllJson.put("bp", bpArray);
                }

                //temperature data
                List<TemperatureInfo> temperatureInfoList = mUTESQLOperate.queryTemperatureAll();
                Log.e("temperatureInfoList::", ""+temperatureInfoList.size());
                if (temperatureInfoList != null) {
                    JSONArray temperatureArray = new JSONArray();
                    for (TemperatureInfo temperatureInfo : temperatureInfoList) {
                        JSONObject temperatureObject = new JSONObject();
                        temperatureObject.put("calender", temperatureInfo.getCalendar());
                        temperatureObject.put("type", "" + temperatureInfo.getType());
                        temperatureObject.put("inCelsius", "" + GlobalMethods.convertDoubleToCelsiusWithDecimal(temperatureInfo.getBodyTemperature()));
                        temperatureObject.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                        temperatureObject.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                        temperatureObject.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));
                        temperatureArray.put(temperatureObject);
                    }
                    overAllJson.put("temperature", temperatureArray);
                }


                result.success(overAllJson.toString());
            } else {
                result.success(overAllJson.toString());
            }

        } catch (Exception exp) {
            Log.e("fetchOverAllExp::", exp.getMessage());
            result.success(WatchConstants.SC_FAILURE);
        }
    }


    // start -stop test
    private void startOxygenSaturation(Result result) {
        boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN);
        if (isSupported) {
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.startOxygenTest();
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } else {
            result.success(WatchConstants.SC_NOT_SUPPORTED);
        }
    }

    private void stopOxygenSaturation(Result result) {
        boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN);
        if (isSupported) {
            if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
                if (mWriteCommand != null) {
                    mWriteCommand.stopOxygenTest();
                    result.success(WatchConstants.SC_INIT);
                } else {
                    result.success(WatchConstants.SC_FAILURE);
                }
            } else {
                result.success(WatchConstants.SC_DISCONNECTED);
            }
        } else {
            result.success(WatchConstants.SC_NOT_SUPPORTED);
        }
    }

    private void startBloodPressure(Result result) {
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand != null) {
                mWriteCommand.sendBloodPressureTestCommand(GlobalVariable.BLOOD_PRESSURE_TEST_START);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    private void stopBloodPressure(Result result) {
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand != null) {
                mWriteCommand.sendBloodPressureTestCommand(GlobalVariable.BLOOD_PRESSURE_TEST_STOP);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    private void startHeartRate(Result result) {
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand != null) {
                mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_START);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    private void stopHeartRate(Result result) {
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand != null) {
                mWriteCommand.sendRateTestCommand(GlobalVariable.RATE_TEST_STOP);
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    private void startTempTest(Result result) {
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand != null) {
                if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST)) {
                    mWriteCommand.queryCurrentTemperatureData();
                    result.success(WatchConstants.SC_INIT);
                }else{
                    result.success(WatchConstants.SC_NOT_SUPPORTED);
                }
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
        bpEventChannel.setStreamHandler(null);
        tempEventChannel.setStreamHandler(null);
        methodChannel = null;
        eventChannel = null;
        bpEventChannel = null;
        tempEventChannel = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        Log.e("onAttachedToActivity", "inside_attached");
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        Log.e("onAttachedToActivity", "onReattachedToActivityForConfigChanges");
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        Log.e("onDetachedFromActivity", "onDetachedFromActivityForConfigChanges");
    }

    @Override
    public void onDetachedFromActivity() {
        Log.e("onDetachedFromActivity", "onDetachedFromActivity");
    }

    private void sendEventToDart(final JSONObject params, String channel) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(WatchConstants.BROADCAST_ACTION_NAME);
        intent.putExtra("params", params.toString());
        intent.putExtra("channel", channel);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void startListening(Object arguments, Result rawResult) {
        try {
            // Get callback id
            String callbackName = (String) arguments;

            Log.e("callbackName", "start_listener " + callbackName);// smartCallbacks

            if (callbackName.equals(WatchConstants.SMART_CALLBACK)) {
                validateDeviceListCallback = true;
            }

            Map<String, Object> args = new HashMap<>();
            args.put("id", callbackName);
            mCallbacks.put(callbackName, args);

            rawResult.success(null);

        } catch (Exception exp) {
            Log.e("startListeningExp:", exp.getMessage());
        }
    }

    private void cancelListening(Object args, MethodChannel.Result result) {
        // Get callback id
        //  int currentListenerId = (int) args;
        String callbackName = (String) args;
        Log.e("callbackName", "cancel_listener " + callbackName);
        // Remove callback
        mCallbacks.remove(callbackName);
        // Do additional stuff if required to cancel the listener
        result.success(null);
    }

    private void pushEventCallBack(final String result, final JSONObject data, final String status) {
        uiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject args = new JSONObject();
                    args.put("status", status);
                    args.put("result", result);
                    args.put("data", data);
                    sendEventToDart(args, WatchConstants.SMART_EVENT_CHANNEL);
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.e("sendEventExp:", e.getMessage());
                }
            }
        }, 200);
    }

    private void pushBPEventCallBack(final String result, final JSONObject data, final String status) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject args = new JSONObject();
                    args.put("status", status);
                    args.put("result", result);
                    args.put("data", data);
                    sendEventToDart(args, WatchConstants.SMART_BP_TEST_CHANNEL);
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.e("sendEventExp:", e.getMessage());
                }
            }
        }, 200);
    }

    private void pushTemperatureEventCallBack(final String result, final JSONObject data, final String status) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject args = new JSONObject();
                    args.put("status", status);
                    args.put("result", result);
                    args.put("data", data);
                    sendEventToDart(args, WatchConstants.SMART_TEMP_TEST_CHANNEL);
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.e("sendEventExp:", e.getMessage());
                }
            }
        }, 200);
    }


    /*private void runOnUIThread(final String result, final JSONObject data, final String callbackName, final String status) {
        try {
            *//*activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("runOnUIThread", "Calling runOnUIThread with activity: " + data);

                    try {
                        JSONObject args = new JSONObject();
                        args.put("id", callbackName);
                        args.put("status", status);
                        args.put("result", result);
                        args.put("data", data);
                        Log.e("mCallbackChannel:: ", ""+mCallbackChannel);
                        mCallbackChannel.invokeMethod(WatchConstants.CALL_LISTENER, args.toString());

                    } catch (Exception e) {
                        // e.printStackTrace();
                        Log.e("data_run_exp:", e.getMessage());
                    }
                }
            });*//*
            uiThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject args = new JSONObject();
                        args.put("id", callbackName);
                        args.put("status", status);
                        args.put("result", result);
                        args.put("data", data);
                        Log.e("mCallbackChannel2:: ", "" + mCallbackChannel);
                        mCallbackChannel.invokeMethod(WatchConstants.CALL_LISTENER, args.toString());

                    } catch (Exception e) {
                        // e.printStackTrace();
                        Log.e("data_run_exp2:", e.getMessage());
                    }
                }
            });
            //  final String result
            // uiThreadHandler
            *//*new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });*//*
           *//* new Handler(Looper.getMainLooper()).post(new Runnable() {
                 @Override
                 public void run() {
                 Log.e("runOnUIThread", "Calling runOnUIThread with: " + data);

                  try {
                       JSONObject args = new JSONObject();
                       args.put("id", callbackName);
                       args.put("status", status);
                       args.put("result", result);
                       args.put("data", data);
                       Log.e("mCallbackChannel:: ", ""+mCallbackChannel);
                       mCallbackChannel.invokeMethod(WatchConstants.CALL_LISTENER, args.toString());

                   } catch (Exception e) {
                       // e.printStackTrace();
                       Log.e("data_run_exp:", e.getMessage());
                   }
                }
              }
            );*//*
        } catch (Exception exp) {
            Log.e("onUIThreadPushExp: ", "" + exp.getMessage());
        }
    }*/


  /* private void updateConnectionStatus(boolean status) {
        uiThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                flutterResultBluConnect.success(status);
            }
        },200);
        //getMainExecutor().
    }
    private void updateConnectionStatus2(boolean status) {
        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    flutterResultBluConnect.success(status);
                    Log.e("updateConnectionStatus2", "return success");
                }
            });
        }catch (Exception exp) {
            Log.e("updateConnectionStatus2", exp.getMessage());
        }
        *//*activity.getMainExecutor().post(new Runnable() {
            @Override
            public void run() {
                flutterResultBluConnect.success(status);
            }
        });*//*
     *//*  activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flutterResultBluConnect.success(status);
            }
        });*//*
    }
    private void updateConnectionStatus3(boolean status) {
        try {
            this.flutterResultBluConnect.success(status);
            Log.e("updateConnectionStatus3", "flutterResultBluConnectSuccess");
        }catch (Exception exp) {
            Log.e("updateConnectionStatus3", exp.getMessage());
        }
    }*/

   /* @Override
    public void OnServiceStatuslt(int status) {
        if (status == ICallbackStatus.BLE_SERVICE_START_OK) {
            Log.e("inside_service_result", ""+mBluetoothLeService);
           // LogUtils.d(TAG, "OnServiceStatuslt mBluetoothLeService11 ="+mBluetoothLeService);
            if (mBluetoothLeService == null) {
                mBluetoothLeService = mobileConnect.getBLEServiceOperate().getBleService();
                mobileConnect.setBluetoothLeService(mBluetoothLeService);
                mBluetoothLeService.setICallback(this);
                mBluetoothLeService.setRateCalibrationListener(this);
                mBluetoothLeService.setTurnWristCalibrationListener(this);
                mBluetoothLeService.setTemperatureListener(this);
                mBluetoothLeService.setOxygenListener(this);
                *//*if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST)) {
                    mBluetoothLeService.setTemperatureListener(this);
                }
                if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN)) {
                    mBluetoothLeService.setOxygenListener(this);
                }*//*
                mBluetoothLeService.setBreatheRealListener(this);
                Log.e("inside_service_result", "listeners"+mBluetoothLeService);
            }
        }
    }*/

   /* private final OxygenRealListener oxygenRealListener = new OxygenRealListener() {
        @Override
        public void onTestResult(int status, OxygenInfo oxygenInfo) {
            Log.e("oxygenRealListener", "value: " + oxygenInfo.getOxygenValue() + ", status: " + status);
        }
    };*/

   /* private final TemperatureListener temperatureListener = new TemperatureListener() {
        @Override
        public void onTestResult(TemperatureInfo temperatureInfo) {

            Log.e("temperatureListener", "temperature: " + temperatureInfo.getBodyTemperature() + ", type: " + temperatureInfo.getType());
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
//                jsonObject.put("calender", "" + temperatureInfo.getCalendar());
//                jsonObject.put("type", "" + temperatureInfo.getType());
//                jsonObject.put("bodyTemp", "" + temperatureInfo.getBodyTemperature());
//                jsonObject.put("ambientTemp", "" + temperatureInfo.getAmbientTemperature());
//                jsonObject.put("surfaceTemp", "" + temperatureInfo.getBodySurfaceTemperature());
//                jsonObject.put("startDate", "" + temperatureInfo.getStartDate());
//                jsonObject.put("time", "" + temperatureInfo.getSecondTime());

                        try {
                            jsonObject.put("calender", temperatureInfo.getCalendar());
                            jsonObject.put("type", "" + temperatureInfo.getType());
                            jsonObject.put("inCelsius", "" + GlobalMethods.convertDoubleToStringWithDecimal(temperatureInfo.getBodyTemperature()));
                            jsonObject.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                            jsonObject.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                            jsonObject.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));

                            Log.e("onTestResult", "object: " + jsonObject.toString());

                        } catch (Exception e) {
                           // e.printStackTrace();
                            Log.e("onTestResultJSONExp:", e.getMessage());
                        }

                        runOnUIThread(WatchConstants.TEMP_RESULT, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                    }
                });

            } catch (Exception exp) {
                Log.e("onTestResultExp:", exp.getMessage());
            }

        }

        @Override
        public void onSamplingResult(TemperatureInfo temperatureInfo) {

        }
    };*/

   /* private final RateChangeListener mOnRateListener = new RateChangeListener() {
        @Override
        public void onRateChange(int rate, int status) {
            Log.e("onRateListener", "rate: " + rate + ", status: " + status);
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("hr", "" + rate);
                        } catch (Exception e) {
                           // e.printStackTrace();
                            Log.e("onRateJSONExp: ", e.getMessage());
                        }
                        runOnUIThread(WatchConstants.HR_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                    }
                });
            } catch (Exception exp) {
                Log.e("onRateExp: ", exp.getMessage());
            }
        }
    };*/

    /*private final RateOf24HourRealTimeListener mOnRateOf24HourListener = new RateOf24HourRealTimeListener() {
        @Override
        public void onRateOf24HourChange(int maxHeartRateValue, int minHeartRateValue, int averageHeartRateValue, boolean isRealTimeValue) {
            //Monitor the maximum, minimum, and average values of the 24-hour heart rate bracelet.
            // Need to enter the heart rate test interface on the wristband (or call the synchronization method) to get the value
            Log.e("onRateOf24Hour", "maxHeartRateValue: " + maxHeartRateValue + ", minHeartRateValue: " + minHeartRateValue + ", averageHeartRateValue=" + averageHeartRateValue + ", isRealTimeValue=" + isRealTimeValue);
        }
    };*/

   /* private final SleepChangeListener mOnSleepChangeListener = new SleepChangeListener() {
        @Override
        public void onSleepChange() {
            try{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("onSleepChangeCalender", CalendarUtils.getCalendar(0));
                        SleepTimeInfo sleepTimeInfo = UTESQLOperate.getInstance(mContext).querySleepInfo(CalendarUtils.getCalendar(0));
                        int deepTime, lightTime, awakeCount, sleepTotalTime;
                        if (sleepTimeInfo != null) {
                            deepTime = sleepTimeInfo.getDeepTime();
                            lightTime = sleepTimeInfo.getLightTime();
                            awakeCount = sleepTimeInfo.getAwakeCount();
                            sleepTotalTime = sleepTimeInfo.getSleepTotalTime();
                            Log.e("sleepTimeInfo", "deepTime: " + deepTime + ", lightTime: " + lightTime + ", awakeCount=" + awakeCount + ", sleepTotalTime=" + sleepTotalTime);
                        }
                    }
                });
            }catch (Exception exp){
                Log.e("onSleepChangeExp::", exp.getMessage());
            }

        }
    };

    private final BloodPressureChangeListener mOnBloodPressureListener = new BloodPressureChangeListener() {

        @Override
        public void onBloodPressureChange(int highPressure, int lowPressure, int status) {
            Log.e("onBloodPressureChange", "highPressure: " + highPressure + ", lowPressure: " + lowPressure + ", status=" + status);
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("high", "" + highPressure);
                            jsonObject.put("low", "" + lowPressure);
                            jsonObject.put("status", "" + status);
                            runOnUIThread(WatchConstants.BP_RESULT, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            Log.e("bpChangeJSONExp::", e.getMessage());
                        }
                    }
                });

            } catch (Exception exp) {
                Log.e("bpChangeExp::", exp.getMessage());
            }
        }
    };

    private final StepChangeListener mOnStepChangeListener = new StepChangeListener() {
        @Override
        public void onStepChange(StepOneDayAllInfo info) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (info != null) {
                            //Log.e("onStepChange1", "calendar: " + info.getCalendar());
                            Log.e("onStepChange2", "mSteps: " + info.getStep() + ", mDistance: " + info.getDistance() + ", mCalories=" + info.getCalories());
                            Log.e("onStepChange3", "mRunSteps: " + info.getRunSteps() + ", mRunDistance: " + info.getRunDistance() + ", mRunCalories=" + info.getRunCalories() + ", mRunDurationTime=" + info.getRunDurationTime());
                            Log.e("onStepChange4", "mWalkSteps: " + info.getWalkSteps() + ", mWalkDistance: " + info.getWalkDistance() + ", mWalkCalories=" + info.getWalkCalories() + ", mWalkDurationTime=" + info.getWalkDurationTime());
                            Log.e("onStepChange5", "getStepOneHourArrayInfo: " + info.getStepOneHourArrayInfo() + ", getStepRunHourArrayInfo: " + info.getStepRunHourArrayInfo() + ", getStepWalkHourArrayInfo=" + info.getStepWalkHourArrayInfo());

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("steps", "" + info.getStep());
                                //   jsonObject.put("distance", ""+info.getDistance());
                                //  jsonObject.put("calories", ""+info.getCalories());
                                jsonObject.put("distance", "" + GlobalMethods.convertDoubleToStringWithDecimal(info.getDistance()));
                                jsonObject.put("calories", "" + GlobalMethods.convertDoubleToStringWithDecimal(info.getCalories()));
                                runOnUIThread(WatchConstants.STEPS_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);

                            } catch (Exception e) {
                               // e.printStackTrace();
                                Log.e("onStepJSONExp::", e.getMessage());
                            }

                        }
                    }
                });

            } catch (Exception exp) {
                Log.e("onStepChangeExp::", exp.getMessage());
                // runOnUIThread(WatchConstants.STEPS_REAL_TIME, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
            }
        }
    };*/

   /* private class AsyncExecuteUpdate extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            p = new ProgressDialog(MainActivity.this);
//            p.setMessage("Please wait...It is downloading");
//            p.setIndeterminate(false);
//            p.setCancelable(false);
//            p.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                if (mWriteCommand != null) {
                    mWriteCommand.syncAllStepData();
                    mWriteCommand.syncAllSleepData();
                    mWriteCommand.syncRateData();
                   *//*mWriteCommand.syncAllRateData();
                    if (isSupport24HrRate) {
                        mWriteCommand.sync24HourRate();
                    }*//*
                    mWriteCommand.syncAllBloodPressureData();
                    mWriteCommand.syncAllTemperatureData();
                    if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN)){
                        mWriteCommand.syncOxygenData();
                    }
                    return WatchConstants.SC_SUCCESS;
                }else{
                    return WatchConstants.SC_FAILURE;
                }
               // return WatchConstants.SC_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return WatchConstants.SC_FAILURE;
            }
        }
        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);

        }
    }*/
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

    /*private void initBlueServices() {
        //boolean connectionStatus
        Log.e("mBluetoothLeService::", "initBlueServices");
        try{
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                                        // deviceVersionIDResult.success(jsonObject.toString());
                                        // runOnUIThread(new JSONObject(), WatchConstants.DEVICE_VERSION, WatchConstants.SC_SUCCESS);
                                        runOnUIThread(WatchConstants.DEVICE_VERSION, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;
                                    case ICallbackStatus.GET_BLE_BATTERY_OK:
                                        //String deviceVer = SPUtil.getInstance(mContext).getImgLocalVersion();
                                        String batteryStatus = "" + SPUtil.getInstance(mContext).getBleBatteryValue();
                                        Log.e("batteryStatus::", batteryStatus);
                                        //jsonObject.put("deviceVersion", deviceVer);
                                        jsonObject.put("batteryStatus", batteryStatus);
                                        // runOnUIThread(jsonObject, WatchConstants.BATTERY_VERSION, WatchConstants.SC_SUCCESS);
                                        //deviceBatteryResult.success(jsonObject.toString());
                                        runOnUIThread(WatchConstants.BATTERY_STATUS, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;
                                    // while connecting a device
                                    case ICallbackStatus.READ_CHAR_SUCCESS: // 137
                                        break;
                                    case ICallbackStatus.WRITE_COMMAND_TO_BLE_SUCCESS: // 148
                                        break;
                                    case ICallbackStatus.SYNC_TIME_OK: // 6
                                        //sync time ok
                                        break;

                                    case ICallbackStatus.SET_STEPLEN_WEIGHT_OK: // 8
                                        runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;
                                    case ICallbackStatus.OFFLINE_BLOOD_PRESSURE_SYNCING: // 46
                                        // runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;
                                    case ICallbackStatus.OFFLINE_BLOOD_PRESSURE_SYNC_OK: // 47
                                        //runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;

                                    case ICallbackStatus.BLOOD_PRESSURE_TEST_START: // 50
                                        //runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;


                                    case ICallbackStatus.RATE_TEST_START: // 79
                                        // runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;
                                    case ICallbackStatus.RATE_TEST_STOP: // 80
                                        // runOnUIThread(WatchConstants.UPDATE_DEVICE_PARAMS,  new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;

                                    case ICallbackStatus.CONNECTED_STATUS: // 20
                                        // connected successfully
                                        //runOnUIThread(new JSONObject(), WatchConstants.DEVICE_CONNECTED, WatchConstants.SC_SUCCESS);
                                        //flutterResultBluConnect.success(connectionStatus);
                                        runOnUIThread(WatchConstants.DEVICE_CONNECTED, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        break;
                                    case ICallbackStatus.DISCONNECT_STATUS: // 19
                                        // disconnected successfully
                                        // mobileConnect.disconnectDevice();

                                        runOnUIThread(WatchConstants.DEVICE_DISCONNECTED, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                                        // runOnUIThread(new JSONObject(), WatchConstants.DEVICE_DISCONNECTED, WatchConstants.SC_SUCCESS);
                                        break;
                                }
                            } catch (Exception exp) {
                                Log.e("ble_service_exp:", exp.getMessage());
                                runOnUIThread(WatchConstants.CALLBACK_EXCEPTION, new JSONObject(), WatchConstants.SERVICE_LISTENING, WatchConstants.SC_FAILURE);
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

                    if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST)) {
                        mBluetoothLeService.setTemperatureListener(temperatureListener);
                    }

                    if (GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN)) {
                        mBluetoothLeService.setOxygenListener(oxygenRealListener);
                    }
                }
            });


        }catch (Exception exp) {
            Log.e("mBluetoothLeExp::", exp.getMessage());
        }


    }*/
}