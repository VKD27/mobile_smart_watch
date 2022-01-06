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
import com.yc.pedometer.info.BPVOneDayInfo;
import com.yc.pedometer.info.CustomTestStatusInfo;
import com.yc.pedometer.info.DeviceParametersInfo;
import com.yc.pedometer.info.HeartRateHeadsetSportModeInfo;
import com.yc.pedometer.info.OxygenInfo;
import com.yc.pedometer.info.Rate24HourDayInfo;
import com.yc.pedometer.info.RateOneDayInfo;
import com.yc.pedometer.info.SleepInfo;
import com.yc.pedometer.info.SleepTimeInfo;
import com.yc.pedometer.info.SportsModesInfo;
import com.yc.pedometer.info.StepInfo;
import com.yc.pedometer.info.StepOneDayAllInfo;
import com.yc.pedometer.info.StepOneHourInfo;
import com.yc.pedometer.info.StepRunHourInfo;
import com.yc.pedometer.info.StepWalkHourInfo;
import com.yc.pedometer.info.TemperatureInfo;
import com.yc.pedometer.listener.OxygenRealListener;
import com.yc.pedometer.listener.TemperatureListener;
import com.yc.pedometer.sdk.BloodPressureChangeListener;
import com.yc.pedometer.sdk.BluetoothLeService;
import com.yc.pedometer.sdk.DataProcessing;
import com.yc.pedometer.sdk.ICallback;
import com.yc.pedometer.sdk.ICallbackStatus;
import com.yc.pedometer.sdk.RateChangeListener;
import com.yc.pedometer.sdk.RateOf24HourRealTimeListener;
import com.yc.pedometer.sdk.SleepChangeListener;
import com.yc.pedometer.sdk.StepChangeListener;
import com.yc.pedometer.sdk.UTESQLOperate;
import com.yc.pedometer.sdk.WriteCommandToBLE;
import com.yc.pedometer.utils.CalendarUtils;
import com.yc.pedometer.utils.GetFunctionList;
import com.yc.pedometer.utils.GlobalVariable;
import com.yc.pedometer.utils.LogUtils;
import com.yc.pedometer.utils.SPUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ai.docty.mobile_smart_watch.model.BleDevices;
import ai.docty.mobile_smart_watch.util.GlobalMethods;
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
public class MobileSmartWatchPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

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
    private UTESQLOperate mUTESQLOperate;
    // private Updates mUpdates;
    private DataProcessing mDataProcessing;


    MethodCallHandler callbacksHandler = new MethodCallHandler() {
        @Override
        public void onMethodCall(MethodCall call, Result result) {
            final String method = call.method;
            Log.e("calling_method", "callbacksHandler++ " + method); // startListening
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
   //private Result flutterInitResultBlu;
   private Result flutterResultBluConnect;

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
        mUTESQLOperate = UTESQLOperate.getInstance(applicationContext.getApplicationContext());

        mDataProcessing.setOnStepChangeListener(mOnStepChangeListener);
        mDataProcessing.setOnSleepChangeListener(mOnSleepChangeListener);
        mDataProcessing.setOnRateListener(mOnRateListener);
        mDataProcessing.setOnRateOf24HourListenerRate(mOnRateOf24HourListener);
        mDataProcessing.setOnBloodPressureListener(mOnBloodPressureListener);

    }

    private final OxygenRealListener oxygenRealListener = new OxygenRealListener() {
        @Override
        public void onTestResult(int  status, OxygenInfo oxygenInfo) {
            Log.e("oxygenRealListener", "value: " + oxygenInfo.getOxygenValue() + ", status: " + status);
        }
    };

    private final TemperatureListener temperatureListener = new TemperatureListener() {
        @Override
        public void onTestResult(TemperatureInfo temperatureInfo) {

            Log.e("temperatureListener", "temperature: " + temperatureInfo.getBodyTemperature() + ", type: " + temperatureInfo.getType());

            try {
                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("calender", "" + temperatureInfo.getCalendar());
//                jsonObject.put("type", "" + temperatureInfo.getType());
//                jsonObject.put("bodyTemp", "" + temperatureInfo.getBodyTemperature());
//                jsonObject.put("ambientTemp", "" + temperatureInfo.getAmbientTemperature());
//                jsonObject.put("surfaceTemp", "" + temperatureInfo.getBodySurfaceTemperature());
//                jsonObject.put("startDate", "" + temperatureInfo.getStartDate());
//                jsonObject.put("time", "" + temperatureInfo.getSecondTime());

                jsonObject.put("calender",  temperatureInfo.getCalendar());
                jsonObject.put("type", "" + temperatureInfo.getType());
                jsonObject.put("inCelsius", "" + GlobalMethods.convertDoubleToStringWithDecimal(temperatureInfo.getBodyTemperature()));
                jsonObject.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                jsonObject.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                jsonObject.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));

                Log.e("onTestResult", "object: " + jsonObject.toString());

                runOnUIThread(WatchConstants.TEMP_RESULT, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
            } catch (Exception exp) {
                Log.e("onTestResultExp:", exp.getMessage());
            }

        }

        @Override
        public void onSamplingResult(TemperatureInfo temperatureInfo) {

        }
    };

    private final RateChangeListener mOnRateListener = new RateChangeListener() {
        @Override
        public void onRateChange(int rate, int status) {
            Log.e("onRateListener", "rate: " + rate + ", status: " + status);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("hr", "" + rate);
                runOnUIThread(WatchConstants.HR_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
            } catch (Exception exp) {
                Log.e("onRateExp: ", exp.getMessage());
            }
        }
    };

    private final RateOf24HourRealTimeListener mOnRateOf24HourListener = new RateOf24HourRealTimeListener() {
        @Override
        public void onRateOf24HourChange(int maxHeartRateValue, int minHeartRateValue, int averageHeartRateValue, boolean isRealTimeValue) {
            //Monitor the maximum, minimum, and average values of the 24-hour heart rate bracelet.
            // Need to enter the heart rate test interface on the wristband (or call the synchronization method) to get the value
            Log.e("onRateOf24Hour", "maxHeartRateValue: " + maxHeartRateValue + ", minHeartRateValue: " + minHeartRateValue + ", averageHeartRateValue=" + averageHeartRateValue + ", isRealTimeValue=" + isRealTimeValue);
        }
    };

    private final SleepChangeListener mOnSleepChangeListener = new SleepChangeListener() {
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
    };

    private final BloodPressureChangeListener mOnBloodPressureListener = new BloodPressureChangeListener() {

        @Override
        public void onBloodPressureChange(int highPressure, int lowPressure, int status) {
            Log.e("onBloodPressureChange", "highPressure: " + highPressure + ", lowPressure: " + lowPressure + ", status=" + status);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("high", "" + highPressure);
                jsonObject.put("low", "" + lowPressure);
                jsonObject.put("status", "" + status);
                runOnUIThread(WatchConstants.BP_RESULT, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
            } catch (Exception exp) {
                Log.e("bpChangeExp::", exp.getMessage());
            }
        }
    };

    private final StepChangeListener mOnStepChangeListener = new StepChangeListener() {
        @Override
        public void onStepChange(StepOneDayAllInfo info) {
            try {
                if (info != null) {
                    //Log.e("onStepChange1", "calendar: " + info.getCalendar());
                    Log.e("onStepChange2", "mSteps: " + info.getStep() + ", mDistance: " + info.getDistance() + ", mCalories=" + info.getCalories());
                    Log.e("onStepChange3", "mRunSteps: " + info.getRunSteps() + ", mRunDistance: " + info.getRunDistance() + ", mRunCalories=" + info.getRunCalories() + ", mRunDurationTime=" + info.getRunDurationTime());
                    Log.e("onStepChange4", "mWalkSteps: " + info.getWalkSteps() + ", mWalkDistance: " + info.getWalkDistance() + ", mWalkCalories=" + info.getWalkCalories() + ", mWalkDurationTime=" + info.getWalkDurationTime());
                    Log.e("onStepChange5", "getStepOneHourArrayInfo: " + info.getStepOneHourArrayInfo() + ", getStepRunHourArrayInfo: " + info.getStepRunHourArrayInfo() + ", getStepWalkHourArrayInfo=" + info.getStepWalkHourArrayInfo());

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("steps", "" + info.getStep());
//                    jsonObject.put("distance", ""+info.getDistance());
//                    jsonObject.put("calories", ""+info.getCalories());
                    jsonObject.put("distance", "" + GlobalMethods.convertDoubleToStringWithDecimal((double) info.getDistance()));
                    jsonObject.put("calories", "" + GlobalMethods.convertDoubleToStringWithDecimal((double) info.getCalories()));

                    runOnUIThread(WatchConstants.STEPS_REAL_TIME, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                }
            } catch (Exception exp) {
                Log.e("onStepChangeExp::", exp.getMessage());
                // runOnUIThread(WatchConstants.STEPS_REAL_TIME, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
            }
        }
    };

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        String method = call.method;
        switch (method) {
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

            case WatchConstants.GET_DEVICE_VERSION:
                getDeviceVersion(result);
                break;

            case WatchConstants.GET_DEVICE_BATTERY_STATUS:
                getDeviceBatteryStatus(result);
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

                // fetch all the dats
            case WatchConstants.FETCH_ALL_STEPS_DATA:
                fetchAllStepsData( result);
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
            default:
                result.notImplemented();
                break;
        }
    }

    private void initDeviceConnection(Result result) {
        //this.flutterInitResultBlu = result;
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
        this.flutterResultBluConnect = result;
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
            initBlueServices(status);
        }
        //result.success(status);
    }

    private void initBlueServices(boolean connectionStatus) {
        Log.e("mBluetoothLeService::", "initBlueServices");
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
                            // runOnUIThread(new JSONObject(), WatchConstants.DEVICE_VERSION, WatchConstants.SC_SUCCESS);
                            runOnUIThread(WatchConstants.DEVICE_VERSION, jsonObject, WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.GET_BLE_BATTERY_OK:
                            String deviceVer = SPUtil.getInstance(mContext).getImgLocalVersion();
                            String batteryStatus = "" + SPUtil.getInstance(mContext).getBleBatteryValue();
                            Log.e("batteryStatus::", batteryStatus);
                            jsonObject.put("deviceVersion", deviceVer);
                            jsonObject.put("batteryStatus", batteryStatus);
                            // runOnUIThread(jsonObject, WatchConstants.BATTERY_VERSION, WatchConstants.SC_SUCCESS);
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
                            flutterResultBluConnect.success(connectionStatus);
                            runOnUIThread(WatchConstants.DEVICE_CONNECTED, new JSONObject(), WatchConstants.SMART_CALLBACK, WatchConstants.SC_SUCCESS);
                            break;
                        case ICallbackStatus.DISCONNECT_STATUS: // 19
                            // disconnected successfully
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

        if(GetFunctionList.isSupportFunction_Fifth(mContext,GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST)){
            mBluetoothLeService.setTemperatureListener(temperatureListener);
        }

        if(GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN)){
            mBluetoothLeService.setOxygenListener(oxygenRealListener);
        }

    }

    private void disconnectBluDevice(Result result) {
        boolean status = mobileConnect.disconnectDevice();
        result.success(status);
    }

    /*private String GlobalMethods.convertDoubleToStringWithDecimal(double infoValue) {
        String resultValue = new DecimalFormat("0.00").format(infoValue);
        Log.e("resultValue", "decimal_ddf: " + resultValue);
        return resultValue;
    }*/

    private void setUserParams(MethodCall call, Result result) {
        try {
            String age = (String) call.argument("age");
            String height = (String) call.argument("height");
            String weight = (String) call.argument("weight");
            String gender = (String) call.argument("gender");
            String steps = (String) call.argument("steps");
            String isCel = (String) call.argument("isCelsius");
            String screenOffTime = (String) call.argument("screenOffTime");
            String isChineseLang = (String) call.argument("isChineseLang");
            String raiseHandWakeUp = (String) call.argument("raiseHandWakeUp");


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
        }catch (Exception exp){
            Log.e("setUserParamsExp::", exp.getMessage());
            result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void getDeviceVersion(Result result) {
        if (mWriteCommand != null) {
            mWriteCommand.sendToReadBLEVersion();
            result.success(WatchConstants.SC_INIT);
        } else {
            result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void getDeviceBatteryStatus(Result result) {
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
    }

    // sync the activities
    private void fetchAllJudgement(MethodCall call, Result result) {

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

            try{

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
            }catch (Exception exp){
                Log.e("syncAllJSONExp: ", "" + exp.getMessage());
                result.success(WatchConstants.SC_FAILURE);
                //result.success(judgeJson.toString());
            }

        } else {
            //device disconnected
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    private void syncAllStepsData(Result result) {
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
    }

    private void syncAllSleepData(Result result) {
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
    }

    private void syncRateData(Result result) {
        boolean support = GetFunctionList.isSupportFunction_Second(mContext,GlobalVariable.IS_SUPPORT_24_HOUR_RATE_TEST);
        Log.e("support", "" + support);
        Log.e("steps_status", "" + SPUtil.getInstance(mContext).getBleConnectStatus());
        if (SPUtil.getInstance(mContext).getBleConnectStatus()) {
            if (mWriteCommand != null) {
                mWriteCommand.syncRateData();
                /*mWriteCommand. syncAllRateData();
                boolean support = GetFunctionList.isSupportFunction_Second(mContext,GlobalVariable.IS_SUPPORT_24_HOUR_RATE_TEST);
                if (support){
                    mWriteCommand.sync24HourRate();
                }*/
                result.success(WatchConstants.SC_INIT);
            } else {
                result.success(WatchConstants.SC_FAILURE);
            }
        } else {
            //device disconnected
            result.success(WatchConstants.SC_DISCONNECTED);
        }
    }

    private void syncBloodPressure(Result result) {
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
    }

    private void syncOxygenSaturation(Result result) {
        boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN);
        Log.e("syncOxygenSat", "isSupported: " + isSupported);
        if (isSupported){
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

        // below methods need to called, while it supports
        // mBluetoothLeService.setOxygenListener(oxygenRealListener);
    }

    private void syncBodyTemperature(Result result) {
        boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext,GlobalVariable.IS_SUPPORT_TEMPERATURE_TEST);
        Log.e("syncBodyTemp", "isSupported: " + isSupported);
        if (isSupported){
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
        // mBluetoothLeService.setTemperatureListener(temperatureListener);
    }

    // fetch by date time

    private void fetchStepsBySelectedDate(MethodCall call, Result result) {
        // providing proper list of the data on basis of the result.
        try {
            //  new SimpleDateFormat("yyyyMMdd", Locale.US)).format(var1) //20220212
            String dateTime = (String) call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject jsonObject = new JSONObject();
            if (mUTESQLOperate != null) {

                StepOneDayAllInfo stepOneDayAllInfo = mUTESQLOperate.queryRunWalkInfo(dateTime);

                jsonObject.put("status", WatchConstants.SC_SUCCESS);

                jsonObject.put("steps", stepOneDayAllInfo.getStep());

                Log.e("onStepChange111", "getStep: " + stepOneDayAllInfo.getStep());
                Log.e("onStepChange112", "getCalories: " + stepOneDayAllInfo.getCalories());
                Log.e("onStepChange113", "getDistance: " + stepOneDayAllInfo.getDistance());

                jsonObject.put("distance", "" + GlobalMethods.convertDoubleToStringWithDecimal((double) stepOneDayAllInfo.getDistance()));
                jsonObject.put("calories", "" + GlobalMethods.convertDoubleToStringWithDecimal((double) stepOneDayAllInfo.getCalories()));

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
                for(StepOneDayAllInfo info: list){
                    Log.e("list_info:", "calender: " + info.getCalendar());
                    Log.e("list_info:", "step: " + info.getStep());
                    Log.e("list_info:", "cal: " + info.getCalories());
                    Log.e("list_info:", "dis: " + info.getDistance());
                }

                result.success(jsonObject.toString());
            }else{
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
            String dateTime = (String) call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

                SleepTimeInfo sleepTimeInfo = mUTESQLOperate.querySleepInfo(dateTime);

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                resultJson.put("total", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));
                resultJson.put("light", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
                resultJson.put("deep", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
                resultJson.put("awake", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
                resultJson.put("beginTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
                resultJson.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));


//                Log.e("sleepTimeInfo111", "getBeginTime: " +  GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
//                Log.e("sleepTimeInfo111", "getEndTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));
//                Log.e("sleepTimeInfo111", "getDeepTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
//                Log.e("sleepTimeInfo111", "getAwakeTime: " +GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
//                Log.e("sleepTimeInfo111", "getLightTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
//                Log.e("sleepTimeInfo111", "getSleepTotalTime: " + GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));

                // fetch the particular day sleep along state records
                List<SleepInfo>  sleepInfoList  = sleepTimeInfo.getSleepInfoList();
                Log.e("sleepInfoList", "sleepInfoList: " + sleepInfoList.size());
                JSONArray jsonArray = new JSONArray();
                for (SleepInfo sleepInfo : sleepInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("state", sleepInfo.getColorIndex()); // deep sleep: 0, Light sleep: 1,  awake: 2
                    object.put("startTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()));
                    object.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                    object.put("diffTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));

                    Log.e("sleepInfoList", "getColorIndex: " + sleepInfo.getColorIndex());
                    Log.e("sleepInfoList", "getDiffTime: " + GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));
                    Log.e("sleepInfoList", "getStartTime: " + GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime())+ " -- " + GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                   // Log.e("sleepInfoList", );
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);

                result.success(resultJson.toString());
            }else{
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
            String dateTime = (String) call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

                List<BPVOneDayInfo> bpvOneDayInfoList = mUTESQLOperate.queryBloodPressureOneDayInfo(dateTime);
                Log.e("bpvOneDayInfoList", "bpvOneDayInfoList: " + bpvOneDayInfoList.size());

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (BPVOneDayInfo bpvOneDayInfo : bpvOneDayInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender",  bpvOneDayInfo.getCalendar());
                    object.put("time",  GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
                    object.put("high",  bpvOneDayInfo.getHightBloodPressure());
                    object.put("low",  bpvOneDayInfo.getLowBloodPressure());
                    Log.e("bpObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);
                result.success(resultJson.toString());
            }else{
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
            String dateTime = (String) call.argument("dateTime"); // always in "yyyyMMdd";
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
                    object.put("calender",  rateOneDayInfo.getCalendar());
                    object.put("time",  GlobalMethods.getTimeByIntegerMin(rateOneDayInfo.getTime()));
                    //object.put("calenderTime",  rateOneDayInfo.getCalendarTime());
                    object.put("rate",  rateOneDayInfo.getRate());
                    //object.put("currentRate",  rateOneDayInfo.getCurrentRate());
                    //object.put("high",  rateOneDayInfo.getHighestRate());
                    //object.put("low",  rateOneDayInfo.getLowestRate());
                   // object.put("average",  rateOneDayInfo.getVerageRate());
                    Log.e("jsonObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);
                result.success(resultJson.toString());
            }else{
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchHRExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetch24HourHRDateByDate(MethodCall call, Result result) {
        try {
            String dateTime = (String) call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

              //  List<Rate24HourDayInfo> rate24HourDayInfoList =  mUTESQLOperate.query24HourRateAllInfo(); // provides overall available 24 hrs data from storage
                List<Rate24HourDayInfo> rate24HourDayInfoList =  mUTESQLOperate.query24HourRateDayInfo(dateTime);
                Log.e("rateOneDayInfoList", "rateOneDayInfoList: " + rate24HourDayInfoList.size());

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (Rate24HourDayInfo rate24HourDayInfo : rate24HourDayInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender",  rate24HourDayInfo.getCalendar());
                    object.put("time",  GlobalMethods.getTimeByIntegerMin(rate24HourDayInfo.getTime()));
                    object.put("rate",  rate24HourDayInfo.getRate());
                    Log.e("jsonObject", "object: " + object.toString());
                    jsonArray.put(object);
                }

                resultJson.put("data", jsonArray);

                result.success(resultJson.toString());
            }else{
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetch24HourHRExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchTemperatureByDate(MethodCall call, Result result) {
        try {
            String dateTime = (String) call.argument("dateTime"); // always in "yyyyMMdd";
            JSONObject resultJson = new JSONObject();
            if (mUTESQLOperate != null) {

                List<TemperatureInfo> temperatureInfoList =  mUTESQLOperate.queryTemperatureDate(dateTime);
                Log.e("temperatureInfoList", "temperatureInfoList: " + temperatureInfoList.size());

                resultJson.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for (TemperatureInfo temperatureInfo : temperatureInfoList) {
                    JSONObject object = new JSONObject();
                    object.put("calender",  temperatureInfo.getCalendar());

                    object.put("type", "" + temperatureInfo.getType());
                    object.put("inCelsius", "" + GlobalMethods.convertDoubleToStringWithDecimal(temperatureInfo.getBodyTemperature()));
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
            }else{
                result.success(resultJson.toString());
            }
        } catch (Exception exp) {
            Log.e("fetchTempByDate::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllStepsData(Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {

                List<StepOneDayAllInfo> list = mUTESQLOperate.queryRunWalkAllDay();
                Log.e("list", "list: " + list.size());

                resultObject.put("status", WatchConstants.SC_SUCCESS);

                JSONArray jsonArray = new JSONArray();
                for(StepOneDayAllInfo info: list){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("calender", info.getCalendar());
                    jsonObject.put("steps", info.getStep());
                    jsonObject.put("cal", GlobalMethods.convertDoubleToStringWithDecimal(info.getCalories()));
                    jsonObject.put("distance", GlobalMethods.convertDoubleToStringWithDecimal(info.getDistance()));

                    Log.e("list_info:", "calender: " + info.getCalendar());
                    Log.e("list_info:", "step: " + info.getStep());
                    Log.e("list_info:", "cal: " + info.getCalories());
                    Log.e("list_info:", "dis: " + info.getDistance());

                    ArrayList<StepOneHourInfo> stepOneHourInfoArrayList = info.getStepOneHourArrayInfo();
                    JSONArray stepsArray = new JSONArray();
                    for (StepOneHourInfo stepOneHourInfo : stepOneHourInfoArrayList) {
                        JSONObject object = new JSONObject();
                        object.put("stepValue", stepOneHourInfo.getStep());
                        object.put("time", GlobalMethods.getIntegerToHHmm(stepOneHourInfo.getTime()));
                        stepsArray.put(object);
                    }
                    jsonObject.put("stepsData", stepsArray);

                    jsonArray.put(jsonObject);
                }
                resultObject.put("data",jsonArray);
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllStepExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllSleepData( Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {

                List<SleepTimeInfo> sleepTimeInfoList = mUTESQLOperate.queryAllSleepInfo();
                Log.e("list", "list: " + sleepTimeInfoList.size());

                resultObject.put("status", WatchConstants.SC_SUCCESS);

                JSONArray jsonArray = new JSONArray();
                for(SleepTimeInfo sleepTimeInfo: sleepTimeInfoList){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("calender", sleepTimeInfo.getCalendar());
                    jsonObject.put("total", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getSleepTotalTime()));
                    jsonObject.put("light", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getLightTime()));
                    jsonObject.put("deep", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getDeepTime()));
                    jsonObject.put("awake", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getAwakeTime()));
                    jsonObject.put("beginTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getBeginTime()));
                    jsonObject.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepTimeInfo.getEndTime()));

                    List<SleepInfo>  sleepInfoList  = sleepTimeInfo.getSleepInfoList();
                    Log.e("sleepInfoList", "sleepInfoList: " + sleepInfoList.size());
                    
                    JSONArray sleepDataArray = new JSONArray();
                    for (SleepInfo sleepInfo : sleepInfoList) {
                        JSONObject object = new JSONObject();
                        object.put("state", sleepInfo.getColorIndex()); // deep sleep: 0, Light sleep: 1,  awake: 2
                        object.put("startTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getStartTime()));
                        object.put("endTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getEndTime()));
                        object.put("diffTime", GlobalMethods.getTimeByIntegerMin(sleepInfo.getDiffTime()));
                        sleepDataArray.put(object);
                    }
                    
                    jsonObject.put("sleepData", sleepDataArray);

                    jsonArray.put(jsonObject);
                }
                resultObject.put("data",jsonArray);
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllStepExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllBPData( Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {
                List<BPVOneDayInfo> bpvOneDayInfoList = mUTESQLOperate.queryAllBloodPressureInfo();
                Log.e("list", "list: " + bpvOneDayInfoList.size());
                resultObject.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for(BPVOneDayInfo bpvOneDayInfo: bpvOneDayInfoList){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("calender",  bpvOneDayInfo.getCalendar());
                    jsonObject.put("time",  GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
                    jsonObject.put("high",  bpvOneDayInfo.getHightBloodPressure());
                    jsonObject.put("low",  bpvOneDayInfo.getLowBloodPressure());
                    jsonArray.put(jsonObject);
                }
                resultObject.put("data",jsonArray);
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllBPExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    private void fetchAllTemperatureData( Result result) {
        // providing proper list of the data on basis of the result.
        try {
            JSONObject resultObject = new JSONObject();
            if (mUTESQLOperate != null) {
                List<TemperatureInfo> temperatureInfoList = mUTESQLOperate.queryTemperatureAll();
                Log.e("list", "list: " + temperatureInfoList.size());
                resultObject.put("status", WatchConstants.SC_SUCCESS);
                JSONArray jsonArray = new JSONArray();
                for(TemperatureInfo temperatureInfo: temperatureInfoList){
                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("calender",  temperatureInfo.getCalendar());
//                    jsonObject.put("bodyTemp",  temperatureInfo.getBodyTemperature());
//                    jsonObject.put("time",  GlobalMethods.getTimeByIntegerMin(bpvOneDayInfo.getBloodPressureTime()));
//                    jsonObject.put("high",  bpvOneDayInfo.getHightBloodPressure());
//                    jsonObject.put("low",  bpvOneDayInfo.getLowBloodPressure());


                    jsonObject.put("calender",  temperatureInfo.getCalendar());
                    jsonObject.put("type", "" + temperatureInfo.getType());
                    jsonObject.put("inCelsius", "" + GlobalMethods.convertDoubleToStringWithDecimal(temperatureInfo.getBodyTemperature()));
                    jsonObject.put("inFahrenheit", "" + GlobalMethods.getTempIntoFahrenheit(temperatureInfo.getBodyTemperature()));
                    jsonObject.put("startDate", "" + temperatureInfo.getStartDate()); //yyyyMMddHHmmss
                    jsonObject.put("time", "" + GlobalMethods.convertIntToHHMmSs(temperatureInfo.getSecondTime()));
                    jsonArray.put(jsonObject);
                }
                resultObject.put("data",jsonArray);
            }
            result.success(resultObject.toString());
        } catch (Exception exp) {
            Log.e("fetchAllBPExp::", exp.getMessage());
            // result.success(WatchConstants.SC_FAILURE);
        }
    }

    // start -stop test
    private void startOxygenSaturation(Result result) {
        boolean isSupported = GetFunctionList.isSupportFunction_Fifth(mContext, GlobalVariable.IS_SUPPORT_OXYGEN);
        if (isSupported){
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
        if (isSupported){
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
                mWriteCommand.queryCurrentTemperatureData();
                result.success(WatchConstants.SC_INIT);
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

        Log.e("callbackName", "start_listener " + callbackName);// smartCallbacks

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
        Log.e("callbackName", "cancel_listener " + callbackName);
        // Remove callback
        mCallbacks.remove(callbackName);
        // Do additional stuff if required to cancel the listener
        result.success(null);
    }

    private void runOnUIThread(final String result, final JSONObject data, final String callbackName, final String status) {
        //  final String result
        uiThreadHandler.post(new Runnable() {
          @Override
           public void run() {
            Log.d("runOnUIThread", "Calling runOnUIThread with: " + data);

            try {
             JSONObject args = new JSONObject();
             args.put("id", callbackName);
             args.put("status", status);
             args.put("result", result);
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
