package ai.docty.mobile_smart_watch.util;

public class WatchConstants {

    // result constants
    public static final String SC_SUCCESS = "success";
    public static final String SC_FAILURE = "failure";
    public static final String SC_COMPLETE = "complete";
    public static final String SC_CANCELED = "canceled";
    public static final String SC_DISCONNECTED = "disconnected";
    public static final String SC_INIT = "initiated";
    public static final String SC_RE_INIT = "reInitiated";
    public static final String SC_BLE_RE_INIT = "bleReInitiated";
    public static final String SC_NOT_SUPPORTED = "notSupported";

    // device connections
    public static final String BLE_RE_INITIALIZE = "bleReInitialize";
    public static final String DEVICE_RE_INITIATE = "deviceReInitiate";
    public static final String DEVICE_INITIALIZE = "initDeviceConnection";
    public static final String START_DEVICE_SEARCH = "startDeviceSearch";
    //  public static final String STOP_DEVICE_SEARCH = "stopDeviceSearch";
    public static final String BIND_DEVICE = "connectDevice";
    public static final String UNBIND_DEVICE = "disconnectDevice";
    public static final String BLE_NOT_SUPPORTED = "bleNotSupported";
    public static final String BLE_NOT_ENABLED = "bleNotEnabled";

    //device data
    public static final String GET_LAST_DEVICE_ADDRESS = "getLastDeviceAddress";
    public static final String CONNECT_LAST_DEVICE = "connectLastDevice";
    public static final String CHECK_CONNECTION_STATUS = "checkConnectionStatus";
    public static final String GET_DEVICE_VERSION = "fetchDeviceVersion";
    public static final String GET_DEVICE_BATTERY_STATUS = "fetchBatteryStatus";
    public static final String SET_USER_PARAMS = "setUserDetails";
    public static final String SET_24_HEART_RATE = "set24HeartRate";
    public static final String SET_24_TEMPERATURE_TEST = "set24TempTest";
    public static final String SET_WEATHER_INFO = "setWeatherInfo";

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

    public static final String FETCH_OVERALL_DEVICE_DATA = "fetchOverAllDeviceData";
    public static final String FETCH_ALL_STEPS_DATA = "fetchAllStepsData";
    public static final String FETCH_ALL_SLEEP_DATA = "fetchAllSleepData";
    public static final String FETCH_ALL_BP_DATA = "fetchAllBPData";
    public static final String FETCH_ALL_TEMP_DATA = "fetchAllTempData";
    public static final String FETCH_ALL_HR_24_DATA = "fetchAllHr24Data";

    public static final String SYNC_STEPS_FINISH = "syncStepsFinish";
    public static final String SYNC_SLEEP_FINISH = "syncSleepFinish";
    public static final String SYNC_BP_FINISH = "syncBpFinish";
    public static final String SYNC_RATE_FINISH = "syncRateFinish";
    public static final String SYNC_TEMPERATURE_FINISH = "syncTempFinish";
    public static final String SYNC_24_HOUR_RATE_FINISH = "sync24hrRateFinish";
    public static final String SYNC_ECG_DATA_FINISH = "syncEcgDataFinish";
    public static final String SYNC_STATUS_24_HOUR_RATE_OPEN = "syncStatus24hrOpen";
    public static final String SYNC_TEMPERATURE_24_HOUR_AUTOMATIC = "syncTemp24hrAutomatic";
    public static final String SYNC_WEATHER_SUCCESS = "syncWeatherSuccess";

    //any sync failed
    public static final String SYNC_BLE_WRITE_FAIL = "syncBleWriteFail";
    public static final String SYNC_SLEEP_TIME_OUT = "syncSleepTimeOut";
    public static final String SYNC_TEMPERATURE_TIME_OUT = "syncTempTimeOut";

    public static final String BP_TEST_STARTED = "bpTestStarted";
    public static final String BP_TEST_FINISHED = "bpTestFinished";
    public static final String BP_TEST_TIME_OUT = "bpTestTimeOut";
    public static final String BP_TEST_ERROR = "bpTestError";

    public static final String TEMP_TEST_OK = "tempTestOK";
    public static final String TEMP_TEST_TIME_OUT = "tempTestTimeOut";

    public static final String HR_TEST_STARTED = "hrTestStarted";
    public static final String HR_TEST_FINISHED = "hrTestFinished";

    //method channel
    public static final String SMART_METHOD_CHANNEL = "smartMethodChannel";
    public static final String SMART_EVENT_CHANNEL = "smartEventChannel";
    public static final String SMART_BP_TEST_CHANNEL = "smartBPTestChannel";
    public static final String SMART_TEMP_TEST_CHANNEL = "smartTempTestChannel";

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
    public static final String HR_24_REAL_RESULT = "hr24RealResult";

    public static final String CALLBACK_EXCEPTION = "callbackException";


    // for streaming broadcast action event name
    public static final String BROADCAST_ACTION_NAME = "ai.docty.smart_watch";

}
