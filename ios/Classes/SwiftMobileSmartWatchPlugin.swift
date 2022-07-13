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
        
        //let funcState = self.uteManagerDelight.uteManagerDevicesSate?(<#T##self: UTEManagerDelegate##UTEManagerDelegate#>);
        //funcState.
        
        //        self.uteManagerDelegate.uteManagerDiscover?(self: UTEManagerDelegate) -> (UTEModelDevices?)){
        //
        //        }
        //
        //        self.uteManagerDelegate.uteManagerDiscover?(self: modelDevices: UTEModelDevices!) -> {
        //
        //        }
        
        //let discover = uteManagerDelegate.uteManagerDiscover?(<#T##self: UTEManagerDelegate##UTEManagerDelegate#>)
        
        //        self.uteManagerDelegate.uteManagerDiscover(T##modelDevices: UTEModelDevices!##UTEModelDevices?) Void -> (
        //
        //        )
        
        //        self.uteManagerDiscover?(T##modelDevices: UTEModelDevices!##UTEModelDevices?){
        //
        //        }
        
        //        self.uteManagerDelegate?.uteManagerDiscover?(T##modelDevices: UTEModelDevices!##UTEModelDevices?) { modelDevices in
        //
        //        }
        
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
        DispatchQueue.main.async {
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
        self.smartBandMgr.filerRSSI = -90
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
        
        self.smartBandTool.manageStateCallback = {(resultant :String) in
            print("main>> resultant>> \(resultant)")
            
            self.pushEventCallBack(result: resultant, status: GlobalConstants.SC_SUCCESS, sendData: [])
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
            
            let ageStr = args["age"] as? String
            let heightStr = args["height"] as? String
            let weightStr = args["weight"] as? String
            let genderStr = args["gender"] as? String
            let stepsStr = args["steps"] as? String
            //let isCelsiusStr = args["isCelsius"] as? String
            let screenOffTimeStr = args["screenOffTime"] as? String
           // let isChineseLangStr = args["isChineseLang"] as? String
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
            self.smartBandMgr.setUTEInfoModel(infoModel)
           
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
       // self.smartBandMgr.readUTEDeviceVersion()
    }
    
    
    func set24HeartRate(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            let enableStr = args["enable"] as? String
            
            if enableStr?.lowercased() == "true" {
                self.smartBandMgr.setUTEOption(UTEOption.open24HourHRM)
            }else {
                self.smartBandMgr.setUTEOption(UTEOption.close24HourHRM)
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
            
            if enableStr?.lowercased() == "true" {
                self.smartBandMgr.setBloodOxygenAutoTest(true, time: UTECommonTestTime.time1Hour)
            }else {
                self.smartBandMgr.setBloodOxygenAutoTest(false, time: UTECommonTestTime.time1Hour)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func set24HrTemperatureTest(call: FlutterMethodCall, result: FlutterResult) {
        if let args = call.arguments as? Dictionary<String, Any>{
            let enableStr = args["enable"] as? String
            
            if enableStr?.lowercased() == "true" {
                self.smartBandMgr.setBodyTemperatureAutoTest(true, time: UTECommonTestTime.time1Hour)
            }else {
                self.smartBandMgr.setBodyTemperatureAutoTest(false, time: UTECommonTestTime.time1Hour)
            }
            result(GlobalConstants.SC_INIT)
        }else{
            result(GlobalConstants.SC_FAILURE)
        }
    }
    
    func setSevenDaysWeatherInfo(call: FlutterMethodCall, result: FlutterResult) {
        
    }
    
    func setDeviceBandLanguage(call: FlutterMethodCall, result: FlutterResult) {
        
    }
}
