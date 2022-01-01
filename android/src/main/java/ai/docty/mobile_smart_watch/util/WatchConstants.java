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
    public static final String GET_DEVICE_BATTERY_VERSION = "fetchBatteryNVersion";
    public static final String SET_USER_PARAMS = "setUserDetails";

    // daily activities & operations
    public static final String GET_SYNC_STEPS = "syncAllStepsData";
    public static final String GET_SYNC_RATE = "syncRateData";


    //method channel
    public static final String SMART_METHOD_CHANNEL = "mobile_smart_watch";

    //to listen for continuous call backs from the hardware device search
    public static final String SMART_CALLBACK = "smartCallbacks";
    public static final String START_LISTENING = "startListening";
    public static final String SERVICE_LISTENING = "serviceListener";
    public static final String CALL_LISTENER = "callListener";
    public static final String STOP_LISTENING = "cancelListening";

    //listeners ids list
    public static final String BATTERY_VERSION = "batteryVersion";
    public static final String DEVICE_VERSION = "deviceVersion";
    public static final String DEVICE_DISCONNECTED = "deviceDisconnected";
    public static final String DEVICE_CONNECTED = "deviceConnected";


    // for streaming broadcast action event name
    public static final String BROADCAST_ACTION_NAME = "ai.docty.smartcare";


   


    //device init methods calls
//    final static String SMART_STOP_SEARCH = "stopDeviceSearch";
//    final static String SMART_SET_USER_DATA = "setUserParams";
//    final static String SMART_CONNECT_DEVICE = "connectWithDevice";
//    final static String SMART_DISCONNECT_DEVICE = "disconnectDevice";
//    final static String SMART_DELETE_DATA = "deleteData";
//    final static String SMART_GET_DATA = "getDeviceData";
//    final static String SMART_DISPOSE_ALL = "disposeAll";

    //DEVICE_STATUS
//    final static String CONNECT_UNSUPPORT_DEVICETYPE = "unSupportedDevice";
//    final static String CONNECT_UNSUPPORT_BLUETOOTHTYPE = "unSupportedBluetooth";
//    final static String CONNECT_CONNECTING = "connecting";
//    final static String CONNECT_CONNECTED = "connected";
//    final static String CONNECT_DISCONNECTED = "disconnected";
}
