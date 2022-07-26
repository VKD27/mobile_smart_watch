import Flutter
import UIKit
import UTESmartBandApi

public class SwiftMobileSmartWatchPlugin: NSObject, FlutterPlugin, FlutterStreamHandler{
    
    private var eventSink: FlutterEventSink?
    //private var callbackId :NSMutableDictionary
    
    var smartBandMgr = UTESmartBandClient.init()
    var smartBandTool = SmartBandDelegateTool.init()
    
    open var listDevices : [UTEModelDevices] = NSMutableArray.init() as! [UTEModelDevices]
    
    //var uteManagerDelegate = UTEManagerDelegate
    //weak var connectVc : SmartBandConnectedControl?
    
    private var syncDateTime : String = "2022-01-01-01-01"
    
    override init() {
        //self.callbackId = NSMutableDictionary()
        super.init()
        self.smartBandMgr = UTESmartBandClient.sharedInstance()
        self.smartBandMgr.initUTESmartBandClient();
        //EN:Print log
        self.smartBandMgr.debugUTELog = true
        
        
        self.smartBandMgr.delegate = self.smartBandTool
        //self.connectivityProvider.connectivityUpdateHandler = connectivityUpdateHandler
        print(GlobalConstants.GET_LAST_DEVICE_ADDRESS)
        self.registerDeviceCallback()
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let binaryMessenger = registrar.messenger()
        let instance = SwiftMobileSmartWatchPlugin()
        
        let methodChannel = FlutterMethodChannel(name: GlobalConstants.SMART_METHOD_CHANNEL, binaryMessenger: binaryMessenger)
        
        let eventChannel = FlutterEventChannel(name: GlobalConstants.SMART_EVENT_CHANNEL, binaryMessenger: binaryMessenger)
        let bpEventChannel = FlutterEventChannel(name: GlobalConstants.SMART_BP_TEST_CHANNEL, binaryMessenger: binaryMessenger)
        let oxygenEventChannel = FlutterEventChannel(name: GlobalConstants.SMART_OXYGEN_TEST_CHANNEL, binaryMessenger: binaryMessenger)
        let tempEventChannel = FlutterEventChannel(name: GlobalConstants.SMART_TEMP_TEST_CHANNEL, binaryMessenger: binaryMessenger)
        
        let callBackChannel = FlutterMethodChannel(name: GlobalConstants.SMART_CALLBACK, binaryMessenger: binaryMessenger)
        
        eventChannel.setStreamHandler(instance)
        bpEventChannel.setStreamHandler(instance)
        oxygenEventChannel.setStreamHandler(instance)
        tempEventChannel.setStreamHandler(instance)
        
        
        registrar.addMethodCallDelegate(instance, channel: methodChannel)
        registrar.addMethodCallDelegate(instance, channel: callBackChannel)
    }
    
    public func detachFromEngine(for registrar: FlutterPluginRegistrar) {
        eventSink = nil
        //connectivityProvider.stop()
        
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        print("IOS_HANDLER " + call.method)
        switch call.method {
            //        case GlobalConstants.DEVICE_RE_INITIATE:
            //            self.deviceReInitialize(returnResult: result)
            
        case GlobalConstants.DEVICE_INITIALIZE:
            self.deviceInitialize(result: result)
            
        case GlobalConstants.START_DEVICE_SEARCH:
            self.searchForBTDevices(result: result)
            
        case GlobalConstants.BIND_DEVICE:
            self.connectBluDevice(call: call,result: result)
            
        case GlobalConstants.UNBIND_DEVICE:
            self.disconnectBluDevice(result: result);
            
        case GlobalConstants.SET_USER_PARAMS:
            self.setUserParams(call: call, result: result);
            
        case GlobalConstants.CHECK_CONNECTION_STATUS:
            self.getCheckConnectionStatus(result: result)
            
        case GlobalConstants.GET_LAST_DEVICE_ADDRESS:
            self.getLastConnectedAddress(result: result)
            
        case GlobalConstants.SET_24_HEART_RATE:
            self.set24HeartRate(call: call,result: result)
            
        case GlobalConstants.SET_24_OXYGEN:
            self.set24BloodOxygen(call: call,result: result)
            
        case GlobalConstants.SET_24_TEMPERATURE_TEST:
            self.set24HrTemperatureTest(call: call,result: result)
            
        case GlobalConstants.SET_WEATHER_INFO:
            self.setSevenDaysWeatherInfo(call: call,result: result)
            
        case GlobalConstants.SET_BAND_LANGUAGE:
            self.setDeviceBandLanguage(call: call, result: result)
            
        case GlobalConstants.GET_DEVICE_VERSION:
            self.getDeviceVersion(result: result)
            
        case GlobalConstants.GET_DEVICE_BATTERY_STATUS:
            self.getDeviceBatteryStatus(result: result)
            
            //sync calls
        case GlobalConstants.GET_SYNC_STEPS:
            self.syncAllStepsData(result: result)
        case GlobalConstants.GET_SYNC_SLEEP:
            self.syncAllSleepData(result: result)
        case GlobalConstants.GET_SYNC_RATE:
            self.syncRateData(result: result)
        case GlobalConstants.GET_SYNC_BP:
            self.syncBloodPressure(result: result)
        case GlobalConstants.GET_SYNC_OXYGEN:
            self.syncOxygenSaturation(result: result)
        case GlobalConstants.GET_SYNC_TEMPERATURE:
            self.syncBodyTemperature(result: result)
        case GlobalConstants.GET_SYNC_SPORT_INFO:
            self.syncAllSportsInfo(result: result)
            
        case GlobalConstants.FETCH_STEPS_BY_DATE:
            self.fetchStepsBySelectedDate(call: call, result: result)
            
            //fetchoveralldata
        case GlobalConstants.FETCH_OVERALL_DEVICE_DATA:
            self.fetchOverAllDeviceData(result: result)
            
        case GlobalConstants.FIND_BAND_DEVICE:
            self.findBandDevice(result: result)
            
        case GlobalConstants.READ_ONLINE_DIAL_CONFIG:
            self.readOnlineDialConfig(result: result)
     
        
            
        case "ios":
            result("iOS " + UIDevice.current.systemVersion)
            
        default:
            result(FlutterMethodNotImplemented)
        }
    }
    
    public func onListen(withArguments _: Any?,eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        eventSink = events
        // connectivityProvider.start()
        //connectivityUpdateHandler(connectivityType: connectivityProvider.currentConnectivityType)
        return nil
    }
    
    private func pushEventCallBack(result: String, status: String, sendData: Any) {
        
        let jsonSendObj: [String: Any] = [
            "status" : status,
            "result" : result,
            "data": sendData
        ]
        
        print("jsonSendObj>> \(jsonSendObj)")
        //DispatchQueue.main.async {
        DispatchQueue.global().async {
            let resultData = try! JSONSerialization.data(withJSONObject: jsonSendObj)
            let jsonString = String(data: resultData, encoding: .utf8)!
            self.eventSink?(jsonString)
        }
    }
    
    public func onCancel(withArguments _: Any?) -> FlutterError? {
        // connectivityProvider.stop()
        eventSink = nil
        return nil
    }
    
    // SDK Methods Starts Here
    public func deviceReInitialize(returnResult: FlutterResult){
        
        self.smartBandMgr = UTESmartBandClient.sharedInstance()
        self.smartBandMgr.initUTESmartBandClient();
        //EN:Print log
        self.smartBandMgr.debugUTELog = true
        self.smartBandMgr.delegate = self.smartBandTool
        self.smartBandMgr.isScanRepeat = true
        self.smartBandMgr.filerRSSI = -90
        self.smartBandMgr.filerServers = ["5533","2222","FEE7"]
        
        print("re-sdk vsersion = \(self.smartBandMgr.sdkVersion())")
        self.smartBandMgr.delegate = self.smartBandTool
        // return nil
        //self.smartBandMgr.startScanDevices()
        //self.smartBandMgr.stopScanDevices()
        
        //self.smartBandMgr.delegate = self.smartBandTool
        
        returnResult(GlobalConstants.SC_RE_INIT)
    }
    
    func deviceInitialize(result: FlutterResult){
        //do {
        //self.smartBandMgr.initUTESmartBandClient()
        self.smartBandMgr.debugUTELog = true
        self.smartBandMgr.isScanRepeat = true
        self.smartBandMgr.filerRSSI = -99
        self.smartBandMgr.filerServers = ["5533","2222","FEE7"]
        print("log sdk vsersion = \(self.smartBandMgr.sdkVersion())")
        
        //self.smartBandMgr.delegate = self.smartBandTool
        //                if let bundlePath = Bundle.main.path(forResource: name,
        //                                                     ofType: "json"),
        //                    let jsonData = try String(contentsOfFile: bundlePath).data(using: .utf8) {
        //                    return jsonData
        //                }
        result(GlobalConstants.SC_INIT)
        //} catch {
        //     print("IOS: Could not initalize")
        //     returnResult(GlobalConstants.SC_FAILURE)
        // }
    }
    
    func registerDeviceCallback(){
        self.smartBandTool.getDevicesList = {(mArrayDevices : [UTEModelDevices]) in
            print("count in update>> \(mArrayDevices.count)")
            self.listDevices = mArrayDevices
            var deviceData : [Any] = [];
            //DispatchQueue.main.async {
            
            self.listDevices.forEach{model in
                let jsonObject = ["name": model.name as NSString, "address": model.advertisementAddress.uppercased() as NSString,"rssi": model.rssi as NSInteger,"identifier": model.identifier as NSString,"bondState":"","alias":""] as [String : Any]
                deviceData.append(jsonObject)
            }
            print(deviceData);
            self.pushEventCallBack(result: GlobalConstants.UPDATE_DEVICE_LIST, status: GlobalConstants.SC_SUCCESS, sendData: deviceData)
            
            // }
        }
        
        self.smartBandTool.manageStateCallback = {(resultant :String, data : Any) in
            print("main>> resultant>> \(resultant)")
            
            self.pushEventCallBack(result: resultant, status: GlobalConstants.SC_SUCCESS, sendData: data)
        }
    }
    
    public func searchForBTDevices(result: FlutterResult){
        //print("inseide device start scan")
        //self.registerDeviceCallback()
        
        //print(self.smartBandMgr.isScanRepeat)
        //self.smartBandMgr.isScanRepeat = true
        print(self.smartBandMgr.isScanRepeat)
        self.smartBandTool.mArrayDevices.removeAll()
        // DispatchQueue.main.async {
        self.smartBandMgr.startScanDevices()
        
        let jsonObject: [String: Any] = [
            "status" : GlobalConstants.SC_SUCCESS,
            "data": []
        ]
        
        let resultData = try! JSONSerialization.data(withJSONObject: jsonObject)
        let jsonString = String(data: resultData, encoding: .utf8)!
        result(jsonString)
    }
    
    func connectBluDevice(call: FlutterMethodCall, result: FlutterResult){
        var bleResult : NSNumber? = false
        if let args = call.arguments as? Dictionary<String, Any>{
            print("connect_arguments  \(String(describing: args))")
            
            let address = args["address"] as? String
            let name = args["name"] as? String
            
            print("connect_arguments_address  \(String(describing: address))")
            print("connect_arguments_address  \(String(describing: name))")
            
            
            if self.smartBandTool.mArrayDevices.count == 0 {
                self.smartBandMgr.startScanDevices()
                return
            } else{
                let index = self.smartBandTool.mArrayDevices.firstIndex(where: {$0.advertisementAddress.uppercased() == address?.uppercased()}) ?? nil
                
                print("connect_index \(String(describing: index))")
                
                if index != nil {
                    let model = self.smartBandTool.mArrayDevices[index!]
                    print("connect_with  \(String(describing: model.name))")
                    self.smartBandMgr.connect(model)
                    bleResult = true;
                    result(bleResult)
                }else{
                    result(bleResult)
                }
            }
        } else {
            result(bleResult)
            //result(FlutterError.init(code: "errorSetDebug", message: "data or format error", details: nil))
        }
    }
    
    func disconnectBluDevice(result: FlutterResult) {
        let devices = UTEModelDevices.init()
        //devices.identifier = self.devicesID as String?
        print("devices \(String(describing: devices.name))")
        //self.smartBandMgr.disConnect(devices)
        
    }
    
    func setUserParams(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            print("user_params_arguments \(String(describing: args))")
            let group = DispatchGroup()
            group.enter()
            
            DispatchQueue.global().async {
                let ageStr = args["age"] as? String
                let heightStr = args["height"] as? String
                let weightStr = args["weight"] as? String
                let genderStr = args["gender"] as? String
                let stepsStr = args["steps"] as? String
                let isCelsiusStr = args["isCelsius"] as? String
                let screenOffTimeStr = args["screenOffTime"] as? String
                let isChineseLangStr = args["isChineseLang"] as? String
                let raiseHandWakeUpStr = args["raiseHandWakeUp"] as? String
                
                //print("user1 age =\(String(describing: ageStr)) height =\(String(describing: heightStr)) weight =\(String(describing: weightStr))")
                // print("user2 gender =\(String(describing: genderStr)) steps =\(String(describing: stepsStr)) isCelsius =\(String(describing: isCelsiusStr))")
                // print("user3 screenOffTime =\(String(describing: screenOffTimeStr)) isChineseLang =\(String(describing: isChineseLangStr)) raiseHandWakeUp =\(String(describing: raiseHandWakeUpStr)) ")
                
                //EN:Turn off scan
                self.smartBandMgr.stopScanDevices()
                //EN:Set device time
                self.smartBandMgr.setUTEOption(UTEOption.syncTime)
                //EN:Set device unit: meters or inches
                // self.smartBandMgr.setUTEOption(UTEOption.unitInch)
                self.smartBandMgr.setUTEOption(UTEOption.unitMeter)
                
                
                var heightFloat: CGFloat?
                var weightFloat: CGFloat?
                
                if let doubleValue = Double(heightStr ?? "0.0") {
                    heightFloat = CGFloat(doubleValue)
                }
                
                if let doubleValue = Double(weightStr ?? "0.0") {
                    weightFloat = CGFloat(doubleValue)
                }
                
                let age = Int(ageStr ?? "0")
                
                let genderSex : UTEDeviceInfoSex
                if genderStr?.lowercased() == "female" {
                    genderSex = UTEDeviceInfoSex.female
                }else if genderStr?.lowercased() == "male"{
                    genderSex =  UTEDeviceInfoSex.male
                }else{
                    genderSex = UTEDeviceInfoSex.default
                }
                
                let stepsTarget = Int(stepsStr ?? "8000")
                
                let screenLightTime = Int(screenOffTimeStr ?? "6")
                
                var handLight = 0
                if raiseHandWakeUpStr?.lowercased() == "true" {
                    handLight = 1
                } else if raiseHandWakeUpStr?.lowercased() == "false" {
                    handLight = -1
                }else{
                    handLight = 0
                }
                
                print("values_after H=\(String(describing: heightFloat)) W=\(String(describing: weightFloat)) A=\(String(describing: age)) G=\(String(describing: genderSex)) T=\(String(describing: stepsTarget)) SL=\(String(describing: screenLightTime)) HL=\(String(describing: handLight))")
                
                let infoModel = UTEModelDeviceInfo.init()
                infoModel.heigh = heightFloat!
                infoModel.weight = weightFloat!
                infoModel.age = age!
                infoModel.sex = genderSex
                infoModel.sportTarget = stepsTarget!
                // light  (unit second), range<5,60>
                infoModel.lightTime = screenLightTime!
                // Hand Light 1 is open, -1 is close, 0 is default
                infoModel.handlight = handLight
                
                
                var isChinese : Bool = false
                var isCelFah : Bool = false
                
                if isChineseLangStr?.lowercased() == "true" {
                    isChinese = true
                }else{
                    isChinese = true
                }
                
                if isCelsiusStr?.lowercased() == "true" {
                    isCelFah = false
                }else{
                    isCelFah = true
                }
                
                if self.smartBandMgr.connectedDevicesModel!.isHasSwitchCH_EN {
                    infoModel.languageIsChinese = isChinese
                }
                
                if self.smartBandMgr.connectedDevicesModel!.isHasSwitchTempUnit {
                    infoModel.isFahrenheit = isCelFah
                }
                
                self.smartBandMgr.setUTEInfoModel(infoModel)
                group.leave()
            }
            group.wait()
            //print("information_set_returning")
            result(GlobalConstants.SC_INIT)
        }else {
            result(GlobalConstants.SC_FAILURE)
            // result(FlutterError.init(code: "errorSetDebug", message: "data or format error", details: nil))
        }
    }
    
    func getCheckConnectionStatus(result: FlutterResult) {
        var connectResult : NSNumber? = false
        let connectedModel = self.smartBandMgr.connectedDevicesModel
        if connectedModel != nil && connectedModel!.isConnected {
            //let status = connectedModel!.isConnected
            //connectResult = status as NSNumber
            connectResult = true
            result(connectResult)
        }else{
            result(connectResult)
        }
    }
    
    func getLastConnectedAddress(result: FlutterResult) {
        var connectAddress = "";
        let connectedModel = self.smartBandMgr.connectedDevicesModel
        if connectedModel != nil {
            //let status = connectedModel!.isConnected
            //connectResult = status as NSNumber
            connectAddress = connectedModel!.advertisementAddress
            result(connectAddress)
        }else{
            result(connectAddress)
        }
    }
    
    func getDeviceVersion(result: FlutterResult) {
        self.smartBandMgr.readUTEDeviceVersion()
        
    }
    
    func getDeviceBatteryStatus(result: FlutterResult) {
        
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            self.smartBandMgr.setUTEOption(UTEOption.readDevicesBattery)
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }   
    
    func findBandDevice(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            
            self.smartBandMgr.setUTEOption(UTEOption.findBand)
                        
            //self.smartBandMgr.setUTEOption(UTEOption.findPhoneFunctionOpen)
            //self.smartBandMgr.setUTEOption(UTEOption.findPhoneFunctionClose)
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func readOnlineDialConfig(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            
            self.smartBandMgr.setUTEOption(UTEOption.findBand)
                        
            //self.smartBandMgr.setUTEOption(UTEOption.findPhoneFunctionOpen)
            //self.smartBandMgr.setUTEOption(UTEOption.findPhoneFunctionClose)
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    
    
    func set24HeartRate(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            let enableStr = args["enable"] as? String
            DispatchQueue.global().async {
                if enableStr?.lowercased() == "true" {
                    self.smartBandMgr.setUTEOption(UTEOption.open24HourHRM)
                }else {
                    self.smartBandMgr.setUTEOption(UTEOption.close24HourHRM)
                }
            }
            //self.smartBandMgr.connectedDevicesModel?.isHas24HourHRM
            print("24_hrm_status \(String(describing: self.smartBandMgr.connectedDevicesModel?.isHas24HourHRM))")
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func set24BloodOxygen(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            let enableStr = args["enable"] as? String
            DispatchQueue.global().async {
                if enableStr?.lowercased() == "true" {
                    self.smartBandMgr.setBloodOxygenAutoTest(true, time: UTECommonTestTime.time1Hour)
                }else {
                    self.smartBandMgr.setBloodOxygenAutoTest(false, time: UTECommonTestTime.time1Hour)
                }
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func set24HrTemperatureTest(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            let enableStr = args["enable"] as? String
            DispatchQueue.global().async {
                if enableStr?.lowercased() == "true" {
                    self.smartBandMgr.setBodyTemperatureAutoTest(true, time: UTECommonTestTime.time1Hour)
                }else {
                    self.smartBandMgr.setBodyTemperatureAutoTest(false, time: UTECommonTestTime.time1Hour)
                }
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func getWeatherType(code: Int) -> (UTEWeatherType) {
        //print("inside_code>> \(String(describing: code))")
        if code == 100 || code == 900{
            return UTEWeatherType.sunny
        }
        if code >= 101 && code <= 103 {
            return UTEWeatherType.cloudy
        }
        if code == 104{
            return UTEWeatherType.overcast
        }
        if code >= 200 && code <= 213{
            return UTEWeatherType.wind
        }
        
        if code == 300 || code == 301 {
            return UTEWeatherType.shower
        }
        
        if code >= 302 && code <= 304 {
            return UTEWeatherType.thunderStorm
        }
        
        if code == 305 {
            return UTEWeatherType.lightRain
        }
        
        if code >= 306 && code <= 309 {
            return UTEWeatherType.rainSnow
        }
        
        if code >= 310 && code <= 313 {
            return UTEWeatherType.pouring
        }
        
        if code >= 400 && code <= 407 {
            return UTEWeatherType.snow
        }
        
        if code >= 500 && code <= 502 {
            return UTEWeatherType.mistHaze
        }
        
        if code >= 503 && code <= 508 {
            return UTEWeatherType.sandstorm
        }else{
            return UTEWeatherType.overcast
        }
    }
    
    func setSevenDaysWeatherInfo(call: FlutterMethodCall, result: FlutterResult) {
        // let resultData = try! JSONSerialization.data(withJSONObject: jsonSendObj)
        
        if let args = call.arguments as? Dictionary<String, Any>{
            
            let dataStr = args["data"] as? String
            print("dataStr: \(String(describing: dataStr))")
            
            var returnResult = ""
            let group = DispatchGroup()
            group.enter()
            
            DispatchQueue.global().async {
                let data = dataStr?.data(using: .utf8)!
                do {
                    if let jsonArray = try JSONSerialization.jsonObject(with: data!, options : .allowFragments) as? NSDictionary
                    {
                        print(jsonArray) // use the json here
                        
                        let cityName = jsonArray["cityName"] as? String
                        
                        let todayWeatherCode = jsonArray["todayWeatherCode"] as? String
                        let todayTmpCurrent = jsonArray["todayTmpCurrent"] as? Int
                        let todayTmpMax = jsonArray["todayTmpMax"] as? Int
                        let todayTmpMin = jsonArray["todayTmpMin"] as? Int
                        let todayPm25 = jsonArray["todayPm25"] as? Int
                        let todayAqi = jsonArray["todayAqi"] as? Int
                        
                        let secondDayTmpMax = jsonArray["secondDayTmpMax"] as? Int
                        let secondDayTmpMin = jsonArray["secondDayTmpMin"] as? Int
                        let thirdDayTmpMax = jsonArray["thirdDayTmpMax"] as? Int
                        let thirdDayTmpMin = jsonArray["thirdDayTmpMin"] as? Int
                        let fourthDayTmpMax = jsonArray["fourthDayTmpMax"] as? Int
                        let fourthDayTmpMin = jsonArray["fourthDayTmpMin"] as? Int
                        let fifthDayTmpMax = jsonArray["fifthDayTmpMax"] as? Int
                        let fifthDayTmpMin = jsonArray["fifthDayTmpMin"] as? Int
                        let sixthDayTmpMax = jsonArray["sixthDayTmpMax"] as? Int
                        let sixthDayTmpMin = jsonArray["sixthDayTmpMin"] as? Int
                        let seventhDayTmpMax = jsonArray["seventhDayTmpMax"] as? Int
                        let seventhDayTmpMin = jsonArray["seventhDayTmpMin"] as? Int
                        
                        let todayWeather = UTEModelWeather();
                        todayWeather.city = cityName
                        todayWeather.type = self.getWeatherType(code: Int(todayWeatherCode!)!)
                        todayWeather.temperatureCurrent = todayTmpCurrent!
                        todayWeather.temperatureMax = todayTmpMax!
                        todayWeather.temperatureMin = todayTmpMin!
                        todayWeather.pm25 = todayPm25!
                        todayWeather.aqi  = todayAqi!
                        
                        let secondDayWeatherCode = jsonArray["secondDayWeatherCode"] as? String
                        let secondWeather = UTEModelWeather();
                        secondWeather.city = cityName
                        secondWeather.type = self.getWeatherType(code: Int(secondDayWeatherCode!)!)
                        secondWeather.temperatureMax = secondDayTmpMax!
                        secondWeather.temperatureMin = secondDayTmpMin!
                        
                        let thirdDayWeatherCode = jsonArray["thirdDayWeatherCode"] as? String
                        let thirdWeather = UTEModelWeather();
                        thirdWeather.city = cityName
                        thirdWeather.type = self.getWeatherType(code: Int(thirdDayWeatherCode!)!)
                        thirdWeather.temperatureMax = thirdDayTmpMax!
                        thirdWeather.temperatureMin = thirdDayTmpMin!
                        
                        let fourthDayWeatherCode = jsonArray["fourthDayWeatherCode"] as? String
                        let fourthWeather = UTEModelWeather();
                        fourthWeather.city = cityName
                        fourthWeather.type = self.getWeatherType(code: Int(fourthDayWeatherCode!)!)
                        fourthWeather.temperatureMax = fourthDayTmpMax!
                        fourthWeather.temperatureMin = fourthDayTmpMin!
                        
                        let fifthDayWeatherCode = jsonArray["fifthDayWeatherCode"] as? String
                        let fifthhWeather = UTEModelWeather();
                        fifthhWeather.city = cityName
                        fifthhWeather.type = self.getWeatherType(code: Int(fifthDayWeatherCode!)!)
                        fifthhWeather.temperatureMax = fifthDayTmpMax!
                        fifthhWeather.temperatureMin = fifthDayTmpMin!
                        
                        let sixthDayWeatherCode = jsonArray["sixthDayWeatherCode"] as? String
                        let sixthWeather = UTEModelWeather();
                        sixthWeather.city = cityName
                        sixthWeather.type = self.getWeatherType(code: Int(sixthDayWeatherCode!)!)
                        sixthWeather.temperatureMax = sixthDayTmpMax!
                        sixthWeather.temperatureMin = sixthDayTmpMin!
                        
                        let seventhDayWeatherCode = jsonArray["seventhDayWeatherCode"] as? String
                        let seventhWeather = UTEModelWeather();
                        seventhWeather.city = cityName
                        seventhWeather.type = self.getWeatherType(code: Int(seventhDayWeatherCode!)!)
                        seventhWeather.temperatureMax = seventhDayTmpMax!
                        seventhWeather.temperatureMin = seventhDayTmpMin!
                        
                        print("cityName: \(String(describing: cityName))")
                        
                        var mArrayWeather : [UTEModelWeather] = NSMutableArray.init() as! [UTEModelWeather]
                        
                        mArrayWeather.append(todayWeather)
                        mArrayWeather.append(secondWeather)
                        mArrayWeather.append(thirdWeather)
                        mArrayWeather.append(fourthWeather)
                        mArrayWeather.append(fifthhWeather)
                        mArrayWeather.append(sixthWeather)
                        mArrayWeather.append(seventhWeather)
                        
                        self.smartBandTool.weatherSync = 0
                        self.smartBandMgr.sendUTESevenWeather(mArrayWeather)
                        
                        returnResult = GlobalConstants.SC_INIT
                        //result(GlobalConstants.SC_INIT)
                    } else {
                        print("bad json")
                        returnResult = GlobalConstants.SC_FAILURE
                        // result(GlobalConstants.SC_FAILURE)
                    }
                    
                } catch let error as NSError {
                    print("NSerror",error)
                    print("\(error.localizedDescription)")
                    //result(GlobalConstants.SC_FAILURE)
                    returnResult = GlobalConstants.SC_FAILURE
                }
                
                group.leave()
            }
            
            group.wait()
            result(returnResult)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func setDeviceBandLanguage(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            let langStr = args["lang"] as? String
            var returnResult = ""
            let group = DispatchGroup()
            group.enter()
            DispatchQueue.global().async {
                if self.smartBandMgr.connectedDevicesModel!.isHasLanguageSwitchDirectly {
                    if langStr?.lowercased() == "es" {
                        self.smartBandMgr.setUTELanguageSwitchDirectly(UTEDeviceLanguage.spanish)
                    }else{
                        self.smartBandMgr.setUTELanguageSwitchDirectly(UTEDeviceLanguage.english)
                    }
                    
                    //self.smartBandMgr.readDeviceLanguage { (language) in
                    //  print("read_lang>> rawValue: \(language.rawValue) hashValue: \(language.hashValue) value: \(language)")
                    // }
                    // result(GlobalConstants.SC_INIT)
                    returnResult = GlobalConstants.SC_INIT
                }else{
                    //result(GlobalConstants.SC_FAILURE)
                    returnResult = GlobalConstants.SC_FAILURE
                }
                group.leave()
            }
            group.wait()
            result(returnResult)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
        //        self.smartBandMgr.readDeviceLanguage { (language) in
        //
        //        }
    }
    
    func fetchStepsBySelectedDate(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            //let dateTime = args["dateTime"] as? String
            //self.smartBandMgr.
            
            // String dateTime = call.argument("dateTime");
        }
    }
    
    //sync related
    func syncAllStepsData(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            //if self.smartBandMgr.connectedDevicesModel!.isHasDataStatus {
            //   self.smartBandMgr.syncDataCustomTime("2022-01-01-01-01", type: UTEDeviceDataType.steps)
            //     print("syncAllStepsData>> Inside IF")
            // }else{
            
            //print("syncAllStepsData>> Inside ELSE")
            DispatchQueue.global().async {
                self.smartBandMgr.setUTEOption(UTEOption.syncAllStepsData)
                // self.smartBandMgr.setUTEOption(UTEOption.syncAllSleepData)
                // self.smartBandMgr.setUTEOption(UTEOption.syncAllHRMData)
                //self.smartBandMgr.setUTEOption(UTEOption.syncAllBloodData)
                //self.smartBandMgr.setUTEOption(UTEOption.syncAllBloodOxygenData)
                //self.smartBandMgr.setUTEOption(UTEOption.syncAllRespirationData)
            }
            //}
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    
    func syncAllSleepData(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            //            if self.smartBandMgr.connectedDevicesModel!.isHasDataStatus {
            //                self.smartBandMgr.syncDataCustomTime("2022-01-01-01-01", type: UTEDeviceDataType.sleep)
            //            }else{
            //                self.smartBandMgr.setUTEOption(UTEOption.syncAllSleepData)
            //            }
            DispatchQueue.global().async {
                self.smartBandMgr.setUTEOption(UTEOption.syncAllSleepData)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    
    func syncRateData(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            //            if self.smartBandMgr.connectedDevicesModel!.isHasDataStatus {
            //                self.smartBandMgr.syncDataCustomTime("2022-01-01-01-01", type: UTEDeviceDataType.HRM)
            //            }else{
            //                self.smartBandMgr.setUTEOption(UTEOption.syncAllHRMData)
            //            }
            DispatchQueue.global().async {
                self.smartBandMgr.setUTEOption(UTEOption.syncAllHRMData)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    func syncBloodPressure(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            //            if self.smartBandMgr.connectedDevicesModel!.isHasDataStatus {
            //                self.smartBandMgr.syncDataCustomTime("2022-01-01-01-01", type: UTEDeviceDataType.blood)
            //            }else{
            //                self.smartBandMgr.setUTEOption(UTEOption.syncAllBloodData)
            //            }
            DispatchQueue.global().async {
                self.smartBandMgr.setUTEOption(UTEOption.syncAllBloodData)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    
    func syncOxygenSaturation(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            //            if self.smartBandMgr.connectedDevicesModel!.isHasDataStatus {
            //                self.smartBandMgr.syncDataCustomTime("2022-01-01-01-01", type: UTEDeviceDataType.bloodOxygen)
            //            }else{
            //                self.smartBandMgr.setUTEOption(UTEOption.syncAllBloodOxygenData)
            //            }
            DispatchQueue.global().async {
                self.smartBandMgr.setUTEOption(UTEOption.syncAllBloodOxygenData)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    
    func syncBodyTemperature(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            DispatchQueue.global().async {
                //                if self.smartBandMgr.connectedDevicesModel!.isHasBodyTemp{
                //                    print("isHasBodyTemp>> value: \(self.smartBandMgr.connectedDevicesModel!.isHasBodyTemp)")
                //                }
                //                if self.smartBandMgr.connectedDevicesModel!.isHasBodyTemperature{
                //
                //                }
                //
                //                if self.smartBandMgr.connectedDevicesModel!.isHasBodyTemperatureFunction2{
                //
                //                }
                
                print("isHasBodyTemp>> value: \(self.smartBandMgr.connectedDevicesModel!.isHasBodyTemp)")
                print("isHasBodyTemperature>> value: \(self.smartBandMgr.connectedDevicesModel!.isHasBodyTemperature)")
                print("isHasBodyTemperatureFunction2>> value: \(self.smartBandMgr.connectedDevicesModel!.isHasBodyTemperatureFunction2)")
                
                self.smartBandMgr.syncBodyTemperature(self.syncDateTime)
                //self.smartBandMgr.syncUTESportModelCustomTime("2020-08-08-08-08")
                // self.smartBandMgr.setUTEOption(UTEOption.syncTime)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    
    func syncAllSportsInfo(result: FlutterResult) {
        if self.smartBandMgr.connectedDevicesModel!.isConnected {
            DispatchQueue.global().async {
                self.smartBandMgr.syncUTESportModelCustomTime(self.syncDateTime)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
        
    }
    
    func fetchOverAllDeviceData(result: FlutterResult) {
        //            if self.smartBandMgr.connectedDevicesModel!.isConnected {
        //                if self.smartBandMgr.connectedDevicesModel!.isHasDataStatus {
        //                    self.smartBandMgr.syncDataCustomTime("2022-01-01-01-01", type: UTEDeviceDataType.HRM24)
        //                }else{
        //                    self.smartBandMgr.setUTEOption(UTEOption.syncAllRespirationData)
        //                }
        //                result(GlobalConstants.SC_INIT)
        //            }else{
        //                result(GlobalConstants.SC_FAILURE)
        //            }
        
    }
    
    
}


//struct WeatherInfo: HandyJSON {
//    var name: String?
//    var type: AnimalType?
//}
