//
//  GlobalConstants.swift
//  mobile_smart_watch
//
//  Created by MacOS on 27/06/22.
//

import Foundation

struct GlobalConstants {
    //static let baseURL = "https://api.com"

    // result constants
    static let SC_SUCCESS = "success"
    static let SC_FAILURE = "failure"
    static let SC_COMPLETE = "complete"
    static let SC_CANCELED = "canceled"
    static let SC_DISCONNECTED = "disconnected"
    static let SC_INIT = "initiated"
    static let SC_RE_INIT = "reInitiated"
    static let SC_BLE_RE_INIT = "bleReInitiated"
    static let SC_NOT_SUPPORTED = "notSupported"
    
    // device connections
    static let BLE_RE_INITIALIZE = "bleReInitialize"
    static let DEVICE_RE_INITIATE = "deviceReInitiate"
    static let DEVICE_INITIALIZE = "initDeviceConnection"
    static let START_DEVICE_SEARCH = "startDeviceSearch"
    //  static let STOP_DEVICE_SEARCH = "stopDeviceSearch"
    static let BIND_DEVICE = "connectDevice"
    static let UNBIND_DEVICE = "disconnectDevice"
    static let BLE_NOT_SUPPORTED = "bleNotSupported"
    static let BLE_NOT_ENABLED = "bleNotEnabled"
    
    //device data
    static let GET_LAST_DEVICE_ADDRESS = "getLastDeviceAddress"
    static let CONNECT_LAST_DEVICE = "connectLastDevice"
    static let CLEAR_GATT_DISCONNECT = "clearGattDisconnect"
    static let CHECK_CONNECTION_STATUS = "checkConnectionStatus"
    static let GET_DEVICE_VERSION = "fetchDeviceVersion"
    static let GET_DEVICE_BATTERY_STATUS = "fetchBatteryStatus"
    static let GET_DEVICE_DATA_INFO = "fetchDeviceDataInfo"
    static let SET_USER_PARAMS = "setUserDetails"
    static let SET_24_HEART_RATE = "set24HeartRate"
    static let SET_24_OXYGEN = "set24Oxygen"
    static let SET_24_TEMPERATURE_TEST = "set24TempTest"
    static let SET_WEATHER_INFO = "setWeatherInfo"
    static let SET_BAND_LANGUAGE = "setBandLanguage"
    
    static let CHECK_FIND_BAND = "checkFindBand"
    static let FIND_BAND_DEVICE = "findBandDevice"
    
    static let CHECK_DIAL_SUPPORT = "checkDialSupport"
    static let READ_ONLINE_DIAL_CONFIG = "readOnlineDialConfig"
    static let PREPARE_SEND_ONLINE_DIAL = "prepareSendOnlineDial"
    static let LISTEN_WATCH_DIAL_PROGRESS = "listenWatchDialProgress"
    static let SEND_ONLINE_DIAL_DATA = "sendOnlineDialData"
    static let STOP_ONLINE_DIAL_DATA = "stopOnlineDialData"
    
    static let WATCH_DIAL_PROGRESS_STATUS = "watchDialProgressStatus"
    
    
    static let SET_DO_NOT_DISTURB = "setDoNotDisturb"
    static let SET_REJECT_CALL = "setRejectCall"
    
    // daily activities & operations
    static let SYNC_ALL_JUDGE = "fetchAllJudgement"
    static let GET_SYNC_STEPS = "syncAllStepsData"
    static let GET_SYNC_RATE = "syncRateData"
    static let GET_SYNC_SLEEP = "syncSleepData"
    static let GET_SYNC_BP = "syncBP"
    static let GET_SYNC_OXYGEN = "syncOxygen"
    static let GET_SYNC_TEMPERATURE = "syncTemperature"
    
    static let START_TEST_TEMP = "startTestTemp"
    static let START_BP_TEST = "startBPTest"
    static let STOP_BP_TEST = "stopBPTest"
    
    static let START_HR_TEST = "startHRTest"
    static let STOP_HR_TEST = "stopHRTest"
    
    static let START_OXYGEN_TEST = "startOxygenTest"
    static let STOP_OXYGEN_TEST = "stopOxygenTest"
    
    static let FETCH_OVERALL_BY_DATE = "fetchOverAllByDate"
    static let FETCH_STEPS_BY_DATE = "fetchStepsByDate"
    static let FETCH_SLEEP_BY_DATE = "fetchSleepByDate"
    static let FETCH_BP_BY_DATE = "fetchBPByDate"
    static let FETCH_HR_BY_DATE = "fetchHRByDate"
    static let FETCH_OXYGEN_BY_DATE = "fetchOxygenByDate"
    static let FETCH_24_HOUR_HR_BY_DATE = "fetch24HourHRDateByDate"
    static let FETCH_TEMP_BY_DATE = "fetchTemperatureByDate"
    
    static let FETCH_OVERALL_DEVICE_DATA = "fetchOverAllDeviceData"
    static let FETCH_ALL_STEPS_DATA = "fetchAllStepsData"
    static let FETCH_ALL_SLEEP_DATA = "fetchAllSleepData"
    static let FETCH_ALL_BP_DATA = "fetchAllBPData"
    static let FETCH_ALL_TEMP_DATA = "fetchAllTempData"
    static let FETCH_ALL_HR_24_DATA = "fetchAllHr24Data"
    
    static let SYNC_STEPS_FINISH = "syncStepsFinish"
    static let SYNC_SLEEP_FINISH = "syncSleepFinish"
    static let SYNC_BP_FINISH = "syncBpFinish"
    static let SYNC_RATE_FINISH = "syncRateFinish"
    static let SYNC_TEMPERATURE_FINISH = "syncTempFinish"
    static let SYNC_24_HOUR_RATE_FINISH = "sync24hrRateFinish"
    static let SYNC_ECG_DATA_FINISH = "syncEcgDataFinish"
    static let SYNC_OXYGEN_FINISH = "syncOxygenFinish"
    
    
    static let SYNC_STATUS_24_HOUR_RATE_OPEN = "syncStatus24hrOpen"
    static let SYNC_STATUS_24_HOUR_OXYGEN_OPEN = "syncStatus24OxyOpen"
    static let SYNC_TEMPERATURE_24_HOUR_AUTOMATIC = "syncTemp24hrAutomatic"
    static let SYNC_WEATHER_SUCCESS = "syncWeatherSuccess"
    static let SYNC_STATUS_CURRENT_OXYGEN_CMD = "syncStatusCurrentOxyCmd"
    
    static let DND_OPENED = "dndOpened"
    static let DND_CLOSED = "dndClosed"
    
    
    static let CHECK_QUICK_SWITCH_SETTING = "callQuickSwitchSettingStatus"
    static let QUICK_SWITCH_STATUS = "quickSwitchStatus"
    static let QUICK_SWITCH_SUPPORT = "quickSwitchSupport"
    
    
    //any sync failed
    static let SYNC_BLE_WRITE_SUCCESS = "syncBleWriteSuccess"
    static let SYNC_BLE_WRITE_FAIL = "syncBleWriteFail"
    static let SYNC_SLEEP_TIME_OUT = "syncSleepTimeOut"
    static let SYNC_STEPS_TIME_OUT = "syncStepsTimeOut"
    static let SYNC_TEMPERATURE_TIME_OUT = "syncTempTimeOut"
    
    static let BP_TEST_STARTED = "bpTestStarted"
    static let BP_TEST_FINISHED = "bpTestFinished"
    static let BP_TEST_TIME_OUT = "bpTestTimeOut"
    static let BP_TEST_ERROR = "bpTestError"
    
    static let OXYGEN_TEST_STARTED = "oxyTestStarted"
    static let OXYGEN_TEST_FINISHED = "oxyTestFinished"
    static let OXYGEN_TEST_TIME_OUT = "oxyTestTimeOut"
    static let OXYGEN_TEST_ERROR = "oxyTestError"
    
    static let TEMP_TEST_OK = "tempTestOK"
    static let TEMP_TEST_TIME_OUT = "tempTestTimeOut"
    
    static let HR_TEST_STARTED = "hrTestStarted"
    static let HR_TEST_FINISHED = "hrTestFinished"
    
    //method channel
    static let SMART_METHOD_CHANNEL = "smartMethodChannel"
    static let SMART_EVENT_CHANNEL = "smartEventChannel"
    static let SMART_BP_TEST_CHANNEL = "smartBPTestChannel"
    static let SMART_OXYGEN_TEST_CHANNEL = "smartOxygenTestChannel"
    static let SMART_DIAL_EVENT_CHANNEL = "smartDialEventChannel"
    static let SMART_TEMP_TEST_CHANNEL = "smartTempTestChannel"
    
    //to listen for continuous call backs from the hardware device search
    static let SMART_CALLBACK = "smartCallbacks"
    static let START_LISTENING = "startListening"
    static let SERVICE_LISTENING = "serviceListener"
    static let CALL_LISTENER = "callListener"
    static let STOP_LISTENING = "cancelListening"
    
    //listeners result callback list
    static let BATTERY_STATUS = "batteryStatus"
    static let DEVICE_VERSION = "deviceVersion"
    // static let DEVICE_NOT_VALID = "deviceNotValid"
    static let DEVICE_DISCONNECTED = "deviceDisConnected"
    static let DEVICE_CONNECTED = "deviceConnected"
    static let UPDATE_DEVICE_PARAMS = "updateDeviceParams"
    
    static let QUERY_BAND_LANGUAGE = "queryBandLanguage"
    static let SYNC_BAND_LANGUAGE = "syncBandLanguage"
    
    //real time sync data constants
    static let STEPS_REAL_TIME = "stepsRealTime"
    static let HR_REAL_TIME = "heartRateRT"
    static let BP_RESULT = "bpResult"
    static let OXYGEN_RESULT = "oxygenResult"
    static let TEMP_RESULT = "tempResult"
    static let HR_24_REAL_RESULT = "hr24RealResult"
    
    static let CALLBACK_EXCEPTION = "callbackException"
    
    
    // for streaming broadcast action event name
    static let BROADCAST_ACTION_NAME = "ai.docty.smart_watch"
    
}
