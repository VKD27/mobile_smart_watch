part of mobile_smart_watch;

// spirometer supported user params enum
enum MeasureMode { ALL, FVC, VC, MVV, MV }
enum Smoke { NOSMOKE, SMOKE }
enum Sex { MALE, FEMALE}
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
  static const  String BLE_NOT_SUPPORTED = "bleNotSupported";

  //device data
  static const String GET_DEVICE_BATTERY_VERSION = "fetchBatteryNVersion";
  static const String SET_USER_PARAMS = "setUserDetails";

  // daily activities & operations
  static const String GET_SYNC_STEPS = "syncAllStepsData";
  static const String GET_SYNC_RATE = "syncRateData";


  //method channel
  static const String SMART_METHOD_CHANNEL = "mobile_smart_watch";

  //for continuous call backs from the hardware device search
  static const String SMART_CALLBACK = "smartCallbacks";
  static const String START_LISTENING = "startListening";
  static const String STOP_LISTENING = "cancelListening";



  static const String DC_DEVICE_CALLBACK = "deviceCallbacks";

  static const String DC_EVENTS_CHANNEL = "deviceEvents";



  static const String DC_DEVICE_CONNECT = "deviceConnect";

  static const String DC_DEVICE_OPERATIONS = "deviceOperations";

  static const String DC_SUCCESS = "success";
  static const String DC_FAILURE = "failure";
  static const String DC_COMPLETE = "complete";


  //device init methods calls
  /*static const String SPIRO_INITIALIZE = "initializeSDK";
  static const String SPIRO_START_SEARCH = "startDeviceSearch";
  static const String SPIRO_STOP_SEARCH = "stopDeviceSearch";
  static const String SPIRO_SET_USER_DATA = "setUserParams";
  static const String SPIRO_CONNECT_DEVICE = "connectWithDevice";
  static const String SPIRO_DISCONNECT_DEVICE = "disconnectDevice";
  static const String SPIRO_DELETE_DATA = "deleteData";
  static const String SPIRO_GET_DATA = "getDeviceData";
  static const String SPIRO_DISPOSE_ALL = "disposeAll";*/

  //DEVICE_STATUS
  static const  String CONNECT_UNSUPPORT_DEVICETYPE = "unSupportedDevice";
  static const  String CONNECT_UNSUPPORT_BLUETOOTHTYPE = "unSupportedBluetooth";
  static const  String CONNECT_CONNECTING = "connecting";
  static const  String CONNECT_CONNECTED = "connected";
  static const  String CONNECT_DISCONNECTED = "disconnected";


  //requires only for IOS
  static const String DC_APP_Id = "dcAppId";

}
