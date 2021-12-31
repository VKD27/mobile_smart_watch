part of mobile_smart_watch;

class MobileSmartWatch {
  late EventChannel _eventChannel;
  late MethodChannel _methodChannel;
  static MobileSmartWatch? _instance;
  Map? mapOptions;


  factory MobileSmartWatch([options]) {
    if (_instance == null) {
      MethodChannel methodChannel = const MethodChannel(SmartWatchConstants.SMART_METHOD_CHANNEL);

      EventChannel eventChannel = EventChannel(SmartWatchConstants.DC_EVENTS_CHANNEL); // temporary

      //check if the option variable is AFOptions type or map type
      // assert(options is Map);
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
      List<dynamic> responseData = jsonDecode(responseBody["data"]);
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
      'index': deviceModel.index,
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
    };
    return await _methodChannel.invokeMethod(SmartWatchConstants.SET_USER_PARAMS, userParams);
  }


  Future<String> getBatteryStatus() async{
    //returns result status == SC_INIT or SC_FAILURE
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_DEVICE_BATTERY_VERSION);
  }

  Future<String> syncStepsData() async{
    //returns result status == SC_INIT or SC_FAILURE or SC_DISCONNECTED (if the device gor disconnected)
    return await _methodChannel.invokeMethod(SmartWatchConstants.GET_SYNC_STEPS);
  }


  void onDeviceCallbackData(Function callback) async {
    startListening(callback as void Function(dynamic), SmartWatchConstants.SMART_CALLBACK);
  }

  void onCancelCallbackData(Function callback) async {
    startListening(callback as void Function(dynamic), SmartWatchConstants.SMART_CALLBACK);
  }

/*static const MethodChannel _channel = const MethodChannel('mobile_smart_watch');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }*/
}
