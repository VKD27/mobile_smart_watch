part of mobile_smart_watch;

// spirometer supported user params enum
enum MeasureMode { ALL, FVC, VC, MVV, MV }
enum Smoke { NOSMOKE, SMOKE }
enum Sex { MALE, FEMALE }
enum Standard { ECCS, KNUDSON, USA }

// its enum extensions
extension MeasureModeExtension on MeasureMode {
  String get name {
    return ["ALL", "FVC", "VC", "MVV", "MV"][this.index];
  }
}

extension SmokeExtension on Smoke {
  String get name {
    return ["NOSMOKE", "SMOKE"][this.index];
  }
}

extension SexExtension on Sex {
  String get name {
    return ["MALE", "FEMALE"][this.index];
  }
}

extension StandardExtension on Standard {
  String get name {
    return ["ECCS", "KNUDSON", "USA"][this.index];
  }
}

// use it like
// MeasureMode.ALL.name
class SmartWatchConstants {
  // result constants
  static const String SC_SUCCESS = "success";
  static const String SC_FAILURE = "failure";
  static const String SC_COMPLETE = "complete";
  static const String SC_CANCELED = "canceled";
  static const String SC_DISCONNECTED = "disconnected";
  static const String SC_INIT = "initiated";
  static const String SC_NOT_SUPPORTED = "notSupported";

  //relates to device connections
  static const String DEVICE_INITIALIZE = "initDeviceConnection";
  static const String START_DEVICE_SEARCH = "startDeviceSearch";
  static const String STOP_DEVICE_SEARCH = "stopDeviceSearch";
  static const String BIND_DEVICE = "connectDevice";
  static const String UNBIND_DEVICE = "disconnectDevice";
  static const String BLE_NOT_SUPPORTED = "bleNotSupported";

  //device data
  static const String CHECK_CONNECTION_STATUS = "checkConnectionStatus";
  static const String GET_DEVICE_VERSION = "fetchDeviceVersion";
  static const String GET_DEVICE_BATTERY_STATUS = "fetchBatteryStatus";
  static const String SET_USER_PARAMS = "setUserDetails";
  static const String SET_24_HEART_RATE = "set24HeartRate";
  static const String SET_24_TEMPERATURE_TEST = "set24TempTest";
  static const  String SET_WEATHER_INFO = "setWeatherInfo";

  // daily activities & operations
  static const String SYNC_ALL_JUDGE = "fetchAllJudgement";
  static const String GET_SYNC_STEPS = "syncAllStepsData";
  static const String GET_SYNC_RATE = "syncRateData";
  static const String GET_SYNC_SLEEP = "syncSleepData";
  static const String GET_SYNC_BP = "syncBP";
  static const String GET_SYNC_OXYGEN = "syncOxygen";
  static const String GET_SYNC_TEMPERATURE = "syncTemperature";

  // test starts
  static const String START_TEST_TEMP = "startTestTemp";
  static const String START_BP_TEST = "startBPTest";
  static const String STOP_BP_TEST = "stopBPTest";

  static const String START_HR_TEST = "startHRTest";
  static const String STOP_HR_TEST = "stopHRTest";

  static const String START_OXYGEN_TEST = "startOxygenTest";
  static const String STOP_OXYGEN_TEST = "stopOxygenTest";

  static const String FETCH_OVERALL_BY_DATE = "fetchOverAllByDate";
  static const String FETCH_STEPS_BY_DATE = "fetchStepsByDate";
  static const String FETCH_SLEEP_BY_DATE = "fetchSleepByDate";
  static const String FETCH_BP_BY_DATE = "fetchBPByDate";
  static const String FETCH_HR_BY_DATE = "fetchHRByDate";
  static const String FETCH_24_HOUR_HR_BY_DATE = "fetch24HourHRDateByDate";
  static const String FETCH_TEMP_BY_DATE = "fetchTemperatureByDate";


  static const String FETCH_OVERALL_DEVICE_DATA = "fetchOverAllDeviceData";
  static const String FETCH_ALL_STEPS_DATA = "fetchAllStepsData";
  static const String FETCH_ALL_SLEEP_DATA = "fetchAllSleepData";
  static const String FETCH_ALL_BP_DATA = "fetchAllBPData";
  static const String FETCH_ALL_TEMP_DATA = "fetchAllTempData";
  static const String FETCH_ALL_HR_24_DATA = "fetchAllHr24Data";

  static const String SYNC_STEPS_FINISH = "syncStepsFinish";
  static const String SYNC_SLEEP_FINISH = "syncSleepFinish";
  static const String SYNC_BP_FINISH = "syncBpFinish";
  static const String SYNC_RATE_FINISH = "syncRateFinish";
  static const String SYNC_TEMPERATURE_FINISH = "syncTempFinish";
  static const String SYNC_24_HOUR_RATE_FINISH = "sync24hrRateFinish";
  static const String SYNC_ECG_DATA_FINISH = "syncEcgDataFinish";
  static const String SYNC_STATUS_24_HOUR_RATE_OPEN = "syncStatus24hrOpen";
  static const String SYNC_TEMPERATURE_24_HOUR_AUTOMATIC = "syncTemp24hrAutomatic";
  static const String SYNC_WEATHER_SUCCESS = "syncWeatherSuccess";


  static const String SYNC_SLEEP_TIME_OUT = "syncSleepTimeOut";
  static const String SYNC_TEMPERATURE_TIME_OUT = "syncTempTimeOut";

  static const String BP_TEST_STARTED = "bpTestStarted";
  static const String BP_TEST_FINISHED = "bpTestFinished";
  static const String BP_TEST_TIME_OUT = "bpTestTimeOut";
  static const String BP_TEST_ERROR = "bpTestError";

  static const String TEMP_TEST_OK = "tempTestOK";
  static const String TEMP_TEST_TIME_OUT = "tempTestTimeOut";

  static const String HR_TEST_STARTED = "hrTestStarted";
  static const String HR_TEST_FINISHED = "hrTestFinished";

  //method channel
  static const String SMART_METHOD_CHANNEL = "smartMethodChannel";
  static const String SMART_EVENT_CHANNEL = "smartEventChannel";
  static const String SMART_BP_TEST_CHANNEL = "smartBPTestChannel";
  static const String SMART_TEMP_TEST_CHANNEL = "smartTempTestChannel";
  static const String SMART_CONNECTION_CHANNEL = "smartConnectionChannel";


  //for continuous call backs from the hardware device search
  static const String SMART_CALLBACK = "smartCallbacks";
  static const String START_LISTENING = "startListening";
  static const String SERVICE_LISTENING = "serviceListener";
  static const String CALL_LISTENER = "callListener";
  static const String STOP_LISTENING = "cancelListening";

  //listeners result callback list
  static const String BATTERY_STATUS = "batteryStatus";
  static const String DEVICE_VERSION = "deviceVersion";
 // static const String DEVICE_NOT_VALID = "deviceNotValid";
  static const String DEVICE_DISCONNECTED = "deviceDisConnected";
  static const String DEVICE_CONNECTED = "deviceConnected";
  static const String UPDATE_DEVICE_PARAMS = "updateDeviceParams";

  //real time sync data constants
  static const String STEPS_REAL_TIME = "stepsRealTime";
  static const String HR_REAL_TIME = "heartRateRT";
  static const String BP_RESULT = "bpResult";
  static const String TEMP_RESULT = "tempResult";
  static const String HR_24_REAL_RESULT = "hr24RealResult";

  static const String CALLBACK_EXCEPTION = "callbackException";

 // static const String SMART_EVENTS = "smartEvents";

  static const  String BROADCAST_ACTION_NAME = "ai.docty.smart_watch";

  //requires only for IOS
//  static const String DC_APP_Id = "dcAppId";
}
