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
  static const String GET_DEVICE_VERSION = "fetchDeviceVersion";
  static const String GET_DEVICE_BATTERY_STATUS = "fetchBatteryStatus";
  static const String SET_USER_PARAMS = "setUserDetails";

  // daily activities & operations
  static const String GET_SYNC_STEPS = "syncAllStepsData";
  static const String GET_SYNC_RATE = "syncRateData";
  static const String GET_SYNC_SLEEP = "syncSleepData";
  static const String GET_SYNC_BP = "syncBP";
  static const String GET_SYNC_OXYGEN = "syncOxygen";
  static const String GET_SYNC_TEMPERATURE = "syncTemperature";

  static const String START_TEST_TEMP = "startTestTemp";
  static const String START_BP_TEST = "startBPTest";
  static const String STOP_BP_TEST = "stopBPTest";

  static const String START_HR_TEST = "startHRTest";
  static const String STOP_HR_TEST = "stopHRTest";

  static const String START_OXYGEN_TEST = "startOxygenTest";
  static const String STOP_OXYGEN_TEST = "stopOxygenTest";

  static const String FETCH_STEPS_BY_DATE = "fetchStepsByDate";
  static const String FETCH_SLEEP_BY_DATE = "fetchSleepByDate";
  static const String FETCH_BP_BY_DATE = "fetchBPByDate";
  static const String FETCH_HR_BY_DATE = "fetchHRByDate";
  static const String FETCH_24_HOUR_HR_BY_DATE = "fetch24HourHRDateByDate";
  static const String FETCH_TEMP_BY_DATE = "fetchTemperatureByDate";


  static const String FETCH_ALL_STEPS_DATA = "fetchAllStepsData";
  static const String FETCH_ALL_SLEEP_DATA = "fetchAllSleepData";
  static const String FETCH_ALL_BP_DATA = "fetchAllBPData";
  static const String FETCH_ALL_TEMP_DATA = "fetchAllTempData";

  //method channel
  static const String SMART_METHOD_CHANNEL = "mobile_smart_watch";

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

  static const String CALLBACK_EXCEPTION = "callbackException";

  static const String SMART_EVENTS = "smartEvents";


  //requires only for IOS
  static const String DC_APP_Id = "dcAppId";
}
