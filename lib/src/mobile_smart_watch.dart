part of mobile_smart_watch;

class MobileSmartWatch {
  late EventChannel _eventChannel;
  late MethodChannel _methodChannel;
  static MobileSmartWatch? _instance;
  Map? mapOptions;

  late StreamSubscription<dynamic> eventChannelListener;

  factory MobileSmartWatch([options]) {
    if (_instance == null) {
      MethodChannel methodChannel = const MethodChannel(SmartWatchConstants.SMART_METHOD_CHANNEL);

      EventChannel eventChannel = EventChannel(SmartWatchConstants.SMART_EVENT_CHANNEL); // temporary for stream events

      //check if the option variable is AFOptions type or map type
      //assert(options is Map);
     // if (options is Map) {
        _instance = MobileSmartWatch.private(methodChannel, eventChannel, mapOptions: options);
     // }
    }
    return _instance!;
  }

  @visibleForTesting
  MobileSmartWatch.private(this._methodChannel, this._eventChannel, {this.mapOptions});


  Future<String> initializeDeviceConnection() async {
    var result = await _methodChannel.invokeMethod(SmartWatchConstants.DEVICE_INITIALIZE);
    // result can be status ==
    // SC_CANCELED if permission is not allowed,
    // SC_INIT if permission is allowed,
    // BLE_NOT_SUPPORTED if bluetooth device does not supports for v4.

    print('result>>$result');
    return result.toString().trim();
    /*if (result != null && result.toString().isNotEmpty) {
      if (result.toString() == SmartWatchConstants.SC_INIT) {
        return true;
      }else  if (result.toString() == SmartWatchConstants.BLE_NOT_SUPPORTED){
        return false;
      }else{
        return false;
      }
    }else{
      return false;
    }*/
  }

  Future<List<SmartDeviceModel>> startSearchingDevices() async {
    var resultDevices = await _methodChannel.invokeMethod(SmartWatchConstants.START_DEVICE_SEARCH);
    print('resultDevices>> $resultDevices');
    if (resultDevices != null) {
      List<SmartDeviceModel> deviceList = [];
      Map<String, dynamic> responseBody = jsonDecode(resultDevices);
     // List<dynamic> responseData = jsonDecode(responseBody["data"]);
      List<dynamic> responseData = responseBody["data"];
      for (var data in responseData) {
        deviceList.add(new SmartDeviceModel.fromJson(data));
      }
      print('deviceList>> $deviceList');
      //List<SmartDeviceModel> deviceList = new ;
      return deviceList;
    } else {
      return [];
    }
  }

  Future<dynamic> stopSearchingDevices() async {
    return await _methodChannel.invokeMethod(SmartWatchConstants.STOP_DEVICE_SEARCH);
  }

  Future<bool> connectDevice(SmartDeviceModel deviceModel) async{
    var deviceParams = {
     // 'index': deviceModel.index,
      'name': deviceModel.name,
      'address': deviceModel.address,
      'rssi': deviceModel.rssi,
      'alias': deviceModel.alias,
      'deviceType': deviceModel.deviceType,
      'bondState': deviceModel.bondState,
    };
    return await _methodChannel.invokeMethod(SmartWatchConstants.BIND_DEVICE, deviceParams);
  }

  Future<bool> disconnectDevice() async{
    return await _methodChannel.invokeMethod(SmartWatchConstants.UNBIND_DEVICE);
  }

  Future<String> setUserParameters(var userParams) async{
    var userParamsSample = {
      "age":"50",  // user age (0-254)
      "height":"50", // always cm
      "weight":"50", // always in kgs
      "gender":"male", //male  or female in lower case
      "steps": "11000", // targetted goals
      "isCelsius": "true", // if celsius then send "true" else "false" for Fahrenheit
      "screenOffTime": "15", //screen off time
      "isChineseLang": "false", //true for chinese lang setup and false for english
      "raiseHandWakeUp": "false", //true or false -- send true to wake up bright light switch
    };
    return await _methodChannel.invokeMethod(SmartWatchConstants.SET_USER_PARAMS, userParams);
  }

  Future<Map<String, dynamic>> fetchOverAllByDate(String dateTime) async{
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    //returns result status == SC_INIT or SC_FAILURE
    var result = await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_OVERALL_BY_DATE, params);
    var returnResponse;
    if (result != null) {
      if(result.toString().isNotEmpty){
        if (result.toString() == SmartWatchConstants.SC_FAILURE) {
          returnResponse ={
            "status": SmartWatchConstants.SC_FAILURE,
            "data":""
          };
        }else if (result.toString() == SmartWatchConstants.SC_DISCONNECTED) {
          returnResponse ={
            "status": SmartWatchConstants.SC_DISCONNECTED,
            "data":""
          };
        }else{
          Map<String, dynamic> response = jsonDecode(result);
          returnResponse ={
            "status": SmartWatchConstants.SC_SUCCESS,
            "data":response
          };
        }
        return returnResponse;
      }else {
        return returnResponse;
      }
    }else{
      return returnResponse;
    }
  }

  Future<Map<String, dynamic>> getDeviceVersion() async{
    //returns result status == SC_INIT or SC_FAILURE
    var result = await _methodChannel.invokeMethod(SmartWatchConstants.GET_DEVICE_VERSION);
    var returnResponse;
    if (result != null) {
      if(result.toString().isNotEmpty){
        if (result.toString() == SmartWatchConstants.SC_FAILURE) {
          returnResponse ={
            "status": SmartWatchConstants.SC_FAILURE,
            "data":""
          };
        }else if (result.toString() == SmartWatchConstants.SC_DISCONNECTED) {
          returnResponse ={
            "status": SmartWatchConstants.SC_DISCONNECTED,
            "data":""
          };
        }else{
          Map<String, dynamic> response = jsonDecode(result);
          returnResponse ={
            "status": SmartWatchConstants.SC_SUCCESS,
            "data":response
          };
        }
        return returnResponse;
      }else {
        return returnResponse;
      }
    }else{
      return returnResponse;
    }
  }

  Future<Map<String, dynamic>> getBatteryStatus() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    var result = await _methodChannel.invokeMethod(SmartWatchConstants.GET_DEVICE_BATTERY_STATUS);
    var returnResponse;
    if (result != null) {
      if(result.toString().isNotEmpty){
        if (result.toString() == SmartWatchConstants.SC_FAILURE) {
          returnResponse ={
            "status": SmartWatchConstants.SC_FAILURE,
            "data":""
          };
        }else if (result.toString() == SmartWatchConstants.SC_DISCONNECTED) {
          returnResponse ={
            "status": SmartWatchConstants.SC_DISCONNECTED,
            "data":""
          };
        }else{
          Map<String, dynamic> response = jsonDecode(result);
          returnResponse ={
            "status": SmartWatchConstants.SC_SUCCESS,
            "data":response
          };
        }
        return returnResponse;
      }else {
        return returnResponse;
      }
    }else{
      return returnResponse;
    }
  }

  Future<String> syncStepsData() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_STEPS);
  }

  Future<Map<String, dynamic>> fetchAllJudgement() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    var result =  await _methodChannel.invokeMethod(SmartWatchConstants.SYNC_ALL_JUDGE);
    print("judgement_reaponse>> $result");
    var returnResponse;
    if (result != null) {
      if(result.toString().isNotEmpty){
        if (result.toString() == SmartWatchConstants.SC_FAILURE) {
          returnResponse ={
            "status": SmartWatchConstants.SC_FAILURE,
            "data":""
          };
        }else if (result.toString() == SmartWatchConstants.SC_DISCONNECTED) {
          returnResponse ={
            "status": SmartWatchConstants.SC_DISCONNECTED,
            "data":""
          };
        }else{
          Map<String, dynamic> response = jsonDecode(result);
          returnResponse ={
            "status": SmartWatchConstants.SC_SUCCESS,
            "data":response
          };
        }
        return returnResponse;
      }else {
        return returnResponse;
      }
    }else{
      return returnResponse;
    }
  }

  Future<String> syncSleepData() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_SLEEP);
  }


  Future<String> syncRateData() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_RATE);
  }

  Future<String> syncBloodPressure() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_BP);
  }

  Future<String> syncOxygenSaturation() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_OXYGEN);
  }

  Future<String> syncTemperature() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_TEMPERATURE);
  }

  Future<String> startBloodPressure() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.START_BP_TEST);
  }
  Future<String> stopBloodPressure() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.STOP_BP_TEST);
  }

  Future<String> startHR() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.START_HR_TEST);
  }
  Future<String> stopHR() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.STOP_HR_TEST);
  }

  Future<String> startOxygenTest() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.START_OXYGEN_TEST);
  }

  Future<String> stopOxygenTest() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.STOP_OXYGEN_TEST);
  }


  Future<String> fetchStepsByDate(String dateTime) async{
    // dateTime = "yyyyMMdd" // 20220212
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    var result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_STEPS_BY_DATE, params);
    print("result_reaponse>> $result");
    return "";
  }

  Future<Map<String, dynamic>> fetchSleepByDate(String dateTime) async{
    // dateTime = "yyyyMMdd" // 20220103
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    // state // deep sleep: 0, Light sleep: 1,  awake: 2
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    var _result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_SLEEP_BY_DATE, params);
    print("sleep_reaponse>> $_result");
    if (_result != null) {
      Map<String, dynamic> response = jsonDecode(_result);
      return response;
    }else{
      return {};
    }
  }

  Future<Map<String, dynamic>> fetchBPByDate(String dateTime) async{
    // dateTime = "yyyyMMdd" // 20220103
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    // state // deep sleep: 0, Light sleep: 1,  awake: 2
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    var _result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_BP_BY_DATE, params);
    print("sleep_reaponse>> $_result");
    if (_result != null) {
      Map<String, dynamic> response = jsonDecode(_result);
      return response;
    }else{
      return {};
    }
  }


  Future<Map<String, dynamic>> fetchHeartRateByDate(String dateTime) async{
    // dateTime = "yyyyMMdd" // 20220103
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    // state // deep sleep: 0, Light sleep: 1,  awake: 2
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    var _result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_HR_BY_DATE, params);
    print("hr_reaponse>> $_result");
    if (_result != null) {
      Map<String, dynamic> response = jsonDecode(_result);
      return response;
    }else{
      return {};
    }
  }

  Future<Map<String, dynamic>> fetch24HourHRByDate(String dateTime) async{
    // dateTime = "yyyyMMdd" // 20220103
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    // state // deep sleep: 0, Light sleep: 1,  awake: 2
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    var _result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_24_HOUR_HR_BY_DATE, params);
    print("hr_reaponse>> $_result");
    if (_result != null) {
      Map<String, dynamic> response = jsonDecode(_result);
      return response;
    }else{
      return {};
    }
  }

  Future<Map<String, dynamic>> fetchTemperatureByDate(String dateTime) async{
    // dateTime = "yyyyMMdd" // 20220103
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    // state // deep sleep: 0, Light sleep: 1,  awake: 2
    var params = {
      "dateTime":dateTime,  // dateTime is mandatory to pass
    };
    var _result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_TEMP_BY_DATE, params);
    print("temp_reaponse>> $_result");
    if (_result != null) {
      Map<String, dynamic> response = jsonDecode(_result);
      return response;
    }else{
      return {};
    }
  }

  Future<String> fetchAllStepsData() async{
    var result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_ALL_STEPS_DATA);
    print("all_reaponse>> $result");
    return "";
  }

  Future<String> fetchAllSleepData() async{
    var result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_ALL_SLEEP_DATA);
    print("all_reaponse>> $result");
    return "";
  }

  Future<String> fetchAllBPData() async{
    var result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_ALL_BP_DATA);
    print("all_reaponse>> $result");
    return "";
  }

  Future<String> fetchAllTemperatureData() async{
    var result =  await _methodChannel.invokeMethod(SmartWatchConstants.FETCH_ALL_TEMP_DATA);
    print("all_reaponse>> $result");
    return "";
  }


  Future<String> testTempData() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.START_TEST_TEMP);
  }

  void onDeviceCallbackData(Function callback) async {
    await startListening(callback as void Function(dynamic), SmartWatchConstants.SMART_CALLBACK);
  }

  void onCancelCallbackData() async {
   // stopListening(callback as void Function(), SmartWatchConstants.SMART_CALLBACK);
  }

  Stream<dynamic> registerEventCallBackListeners(){
    return _eventChannel.receiveBroadcastStream();
  }

  void receiveEventListeners({Function(dynamic)? onData, Function(dynamic)? onError, Function()? onDone}) {
    eventChannelListener = _eventChannel.receiveBroadcastStream().listen(onData,onError:onError, onDone: onDone, cancelOnError: false);
  }
  void cancelEventListeners(){
    eventChannelListener.cancel();
  }
  /*void registerCallBackListeners(Function callback) async{
    _eventChannel.receiveBroadcastStream().listen((data) {
      var decodedJSON = jsonDecode(data);

      print('register_call_back: $decodedJSON');
      return decodedJSON;
     // String? status = decodedJSON['status'];
    });
  }*/

/*static const MethodChannel _channel = const MethodChannel('mobile_smart_watch');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }*/
}
