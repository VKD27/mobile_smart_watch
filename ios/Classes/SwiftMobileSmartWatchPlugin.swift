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
        
        print("re-sdk vsersion = \(self.smartBandMgr.sdkVersion())")
        self.smartBandMgr.delegate = self.smartBandTool
        // return nil
        //self.smartBandMgr.startScanDevices()
        //self.smartBandMgr.stopScanDevices()
        
        //self.smartBandMgr.delegate = self.smartBandTool
        
        returnResult(GlobalConstants.SC_RE_INIT)
    }
    
    public func deviceInitialize(result: FlutterResult){
        //do {
        //self.smartBandMgr.initUTESmartBandClient()
        self.smartBandMgr.debugUTELog = true
        self.smartBandMgr.isScanRepeat = true
        self.smartBandMgr.filerRSSI = -80
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
    }
    
    public func searchForBTDevices(result: FlutterResult){
        print("inseide device start scan")
        self.registerDeviceCallback()
        
        //print(self.smartBandMgr.isScanRepeat)
        //self.smartBandMgr.isScanRepeat = true
        print(self.smartBandMgr.isScanRepeat)
        self.smartBandTool.mArrayDevices.removeAll()
        // DispatchQueue.main.async {
        self.smartBandMgr.startScanDevices()
        
        //            self.smartBandTool.getDevicesList = {(mArrayDevices : [UTEModelDevices]) in
        //                print("count in update>> \(mArrayDevices.count)")
        //                self.listDevices = mArrayDevices
        //                var deviceData : [Any] = [];
        //                //DispatchQueue.main.async {
        //
        //                    self.listDevices.forEach{model in
        //                        let jsonObject = ["name": model.name as NSString, "address": model.advertisementAddress.uppercased() as NSString,"rssi": model.rssi as NSInteger,"identifier": model.identifier as NSString,"bondState":"","alias":""] as [String : Any]
        //                        deviceData.append(jsonObject)
        //                    }
        //                    print(deviceData);
        //                    self.pushEventCallBack(result: GlobalConstants.UPDATE_DEVICE_LIST, status: GlobalConstants.SC_SUCCESS, sendData: deviceData)
        //
        //                //}
        //            }
        // }
        
        
        
        // self.smartBandTool.getDevicesList
        
        //let jsonObject = createJSONObject(firstName: "firstName", middleName: "middleName", lastName: "lastName", age: 21, weight: 82)
        let jsonObject: [String: Any] = [
            "status" : GlobalConstants.SC_SUCCESS,
            "data": []
        ]
        
        let resultData = try! JSONSerialization.data(withJSONObject: jsonObject)
        let jsonString = String(data: resultData, encoding: .utf8)!
        result(jsonString)
    }
    
    func connectBluDevice(call: FlutterMethodCall, result: FlutterResult){
        if let args = call.arguments as? Dictionary<String, Any>{
            print("connect_arguments  \(String(describing: args))")
            
            let address = args["address"] as? String
            let name = args["name"] as? String
            
            print("connect_arguments_address  \(String(describing: address))")
            print("connect_arguments_address  \(String(describing: name))")
            
            
            if self.smartBandTool.mArrayDevices.count == 0 {
                self.smartBandMgr.startScanDevices()
                return
            }else{
                let index = self.smartBandTool.mArrayDevices.firstIndex(where: {$0.advertisementAddress.uppercased() == address?.uppercased()}) ?? nil
                
                print("connect_index  \(String(describing: index))")
                
                if index != nil {
                    let model = self.smartBandTool.mArrayDevices[index!]
                    print("connect_with  \(String(describing: model.name))")
                    self.smartBandMgr.connect(model)
                }
            }
            
            
        }else {
            result(FlutterError.init(code: "errorSetDebug", message: "data or format error", details: nil))
        }
        //print("recent_list_update>> \(self.listDevices.count)")
        // print("connect_arguments_address  \(address)")
    }
    
    func disconnectBluDevice(result: FlutterResult) {
        
    }
    
    func setUserParams(call: FlutterMethodCall, result: FlutterResult) {
        //let arguments = call.arguments;
        //print("user_arguments \(String(describing: arguments))")
        
        if let args = call.arguments as? Dictionary<String, Any>{
            print("user_params_arguments \(String(describing: args))")
            
            let age = args["age"] as? String
            let height = args["height"] as? String
            let weight = args["weight"] as? String
            let gender = args["gender"] as? String
            let steps = args["steps"] as? String
            let isCelsius = args["isCelsius"] as? String
            let screenOffTime = args["screenOffTime"] as? String
            let isChineseLang = args["isChineseLang"] as? String
            let raiseHandWakeUp = args["raiseHandWakeUp"] as? String
            
            print("user1 age =\(String(describing: age)) height =\(String(describing: height)) weight =\(String(describing: weight)) ")
            print("user2 gender =\(String(describing: gender)) steps =\(String(describing: steps)) isCelsius =\(String(describing: isCelsius)) ")
            print("user3 screenOffTime =\(String(describing: screenOffTime)) isChineseLang =\(String(describing: isChineseLang)) raiseHandWakeUp =\(String(describing: raiseHandWakeUp)) ")
            
            
        }else {
            result(FlutterError.init(code: "errorSetDebug", message: "data or format error", details: nil))
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
}
