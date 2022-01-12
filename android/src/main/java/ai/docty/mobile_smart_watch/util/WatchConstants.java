package ai.docty.mobile_smart_watch.util;

public class WatchConstants {

    // result constants
    public static final String SC_SUCCESS = "success";
    public static final String SC_FAILURE = "failure";
    public static final String SC_COMPLETE = "complete";
    public static final String SC_CANCELED = "canceled";
    public static final String SC_DISCONNECTED = "disconnected";
    public static final String SC_INIT = "initiated";
    public static final String SC_NOT_SUPPORTED = "notSupported";
    
    // device connections
    public static final String DEVICE_INITIALIZE = "initDeviceConnection";
    public static final String START_DEVICE_SEARCH = "startDeviceSearch";
  //  public static final String STOP_DEVICE_SEARCH = "stopDeviceSearch";
    public static final  String BIND_DEVICE = "connectDevice";
    public static final  String UNBIND_DEVICE = "disconnectDevice";
    public static final  String BLE_NOT_SUPPORTED = "bleNotSupported";
    
    //device data
    public static final String GET_DEVICE_VERSION = "fetchDeviceVersion";
    public static final String GET_DEVICE_BATTERY_STATUS = "fetchBatteryStatus";
    public static final String SET_USER_PARAMS = "setUserDetails";

    // daily activities & operations
    public static final String SYNC_ALL_JUDGE = "fetchAllJudgement";
    public static final String GET_SYNC_STEPS = "syncAllStepsData";
    public static final String GET_SYNC_RATE = "syncRateData";
    public static final String GET_SYNC_SLEEP = "syncSleepData";
    public static final String GET_SYNC_BP = "syncBP";
    public static final String GET_SYNC_OXYGEN = "syncOxygen";
    public static final String GET_SYNC_TEMPERATURE = "syncTemperature";
    
    public static final String START_TEST_TEMP = "startTestTemp";
    public static final String START_BP_TEST = "startBPTest";
    public static final String STOP_BP_TEST = "stopBPTest";

    public static final String START_HR_TEST = "startHRTest";
    public static final String STOP_HR_TEST = "stopHRTest";

    public static final String START_OXYGEN_TEST = "startOxygenTest";
    public static final String STOP_OXYGEN_TEST = "stopOxygenTest";

    public static final String FETCH_OVERALL_BY_DATE = "fetchOverAllByDate";
    public static final String FETCH_STEPS_BY_DATE = "fetchStepsByDate";
    public static final String FETCH_SLEEP_BY_DATE = "fetchSleepByDate";
    public static final String FETCH_BP_BY_DATE = "fetchBPByDate";
    public static final String FETCH_HR_BY_DATE = "fetchHRByDate";
    public static final String FETCH_24_HOUR_HR_BY_DATE = "fetch24HourHRDateByDate";
    public static final String FETCH_TEMP_BY_DATE = "fetchTemperatureByDate";

    public static final String FETCH_ALL_STEPS_DATA = "fetchAllStepsData";
    public static final String FETCH_ALL_SLEEP_DATA = "fetchAllSleepData";
    public static final String FETCH_ALL_BP_DATA = "fetchAllBPData";
    public static final String FETCH_ALL_TEMP_DATA = "fetchAllTempData";


    //method channel
    public static final String SMART_METHOD_CHANNEL = "smartMethodChannel";
    public static final String SMART_EVENT_CHANNEL = "smartEventChannel";

    //to listen for continuous call backs from the hardware device search
    public static final String SMART_CALLBACK = "smartCallbacks";
    public static final String START_LISTENING = "startListening";
    public static final String SERVICE_LISTENING = "serviceListener";
    public static final String CALL_LISTENER = "callListener";
    public static final String STOP_LISTENING = "cancelListening";

    //listeners result callback list
    public static final String BATTERY_STATUS = "batteryStatus";
    public static final String DEVICE_VERSION = "deviceVersion";
   // public static final String DEVICE_NOT_VALID = "deviceNotValid";
    public static final String DEVICE_DISCONNECTED = "deviceDisConnected";
    public static final String DEVICE_CONNECTED = "deviceConnected";
    public static final String UPDATE_DEVICE_PARAMS = "updateDeviceParams";

    //real time sync data constants
    public static final String STEPS_REAL_TIME = "stepsRealTime";
    public static final String HR_REAL_TIME = "heartRateRT";
    public static final String BP_RESULT = "bpResult";
    public static final String TEMP_RESULT = "tempResult";


    public static final String CALLBACK_EXCEPTION = "callbackException";


    // for streaming broadcast action event name
    public static final String BROADCAST_ACTION_NAME = "ai.docty.smart_watch";

}
