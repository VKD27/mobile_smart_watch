import Flutter
import UIKit
import UTESmartBandApi

class SwiftMobileSmartWatchPlugin: NSObject, FlutterPlugin, FlutterStreamHandler, UTEManagerDelegate{
    
    private var eventSink: FlutterEventSink?
    //private var callbackId :NSMutableDictionary
    
    //smart band
    public typealias findDevicesBlock = () -> Void
    
    var smartBandMgr = UTESmartBandClient.init()
    //var smartBandTool  = SmartBandDelegateTool.init()
    
    // weak var uteManagerDelegate : UTEManagerDelegate?
    
    //weak var connectVc : SmartBandConnectedControl?
    
    open var mArrayDevices : [UTEModelDevices] = NSMutableArray.init() as! [UTEModelDevices]
    open var findDevicesBlock : findDevicesBlock?
    
    override init() {
        //self.callbackId = NSMutableDictionary()
        super.init()
        self.smartBandMgr = UTESmartBandClient.sharedInstance()
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
    
    // MARK: - UTEManagerDelegate
    func uteManagerDiscover(_ modelDevices: UTEModelDevices!) {
        
        var sameDevices = false
        for model in self.mArrayDevices {
            if (model.identifier?.isEqual(modelDevices.identifier as String))! {
                model.rssi = modelDevices.rssi
                model.name = modelDevices.name
                sameDevices = true
                break
            }
            
        }
        
        if !sameDevices {
            print("***Scanned device name=\(String(describing: modelDevices.name)) id=\(String(describing: modelDevices.identifier))")
            self.mArrayDevices.append(modelDevices)
        }
        
        //print(modelDevices!)
        print("in discover is \(modelDevices!)")
        print("Inside the discover callback ", modelDevices!)
        print("****** data = \(String(describing: modelDevices))")
    }
    
    func uteManagerDevicesSate(_ devicesState: UTEDevicesSate, error: Error!, userInfo info: [AnyHashable : Any]! = [:]) {
        print("****** devicesState = \(String(describing: devicesState))")
        print("****** error = \(String(describing: error))")
        print("****** userInfo = \(String(describing: info))")
    }
    
    func uteManagerExtraIsAble(_ isAble: Bool) {
        if isAble {
            print("***Successfully turn on the additional functions of the device")
        }else{
            print("***Failed to open the extra functions of the device, the device is actively disconnected, please reconnect the device")
        }
    }
    func uteManagerReceiveTodaySteps(_ runData: UTEModelRunData!) {
        print("***Today time=\(String(describing: runData.time))，Total steps=\(runData.totalSteps),Total distance=\(runData.distances),Total calories=\(runData.calories),Current hour steps=\(runData.hourSteps)")
    }
    
    func uteManagerReceiveTodaySport(_ dict: [AnyHashable : Any]!) {
        let walk : UTEModelSportWalkRun = dict[kUTEQuerySportWalkRunData] as! UTEModelSportWalkRun
        print("sport device step=\(walk.stepsTotal)")
    }
    func uteManagerUTEIbeaconOption(_ option: UTEIbeaconOption, value: String!) {
        print("ibeacon value = \(String(describing: value))")
    }
    
    func uteManagerTakePicture() {
        print("***I took a photo, if I don’t take a photo, please exit the photo mode")
    }
    
    func uteManagerBluetoothState(_ bluetoothState: UTEBluetoothState) {
        
        print("****** bluetoothState = \(String(describing: bluetoothState))")
        
        if bluetoothState == UTEBluetoothState.close {
           // if self.alertView != nil {
           //     return
           // }
           // weak var weakSelf = self
            DispatchQueue.main.async {
                //Please turn on the phone Bluetooth
                //let alterVC = UIAlertController.init(title: "hint", message: "Please turn on your phone's bluetooth", preferredStyle: UIAlertController.Style.alert)
                
                //weakSelf?.alertView = alterVC
                
                //let window = UIApplication.shared.keyWindow
               // let nav : UINavigationController = window?.rootViewController as! UINavigationController
                
               // alterVC.addAction(UIAlertAction.init(title: "it is good", style: UIAlertAction.Style.cancel, handler: { (cancelAction) in
                    
              //  }))
                
               // nav.present(alterVC, animated: true, completion: nil)
                
            }
        }else{
            //self.alertView?.dismiss(animated: true, completion: nil)
           // self.alertView = nil
        }
        
    }
    
    func uteManagerReceiveCustomData(_ data: Data!, result: Bool) {
        if result {
            print("******Successfully received data = \(String(describing: data))")
        }else{
            print("***Failed to receive data")
        }
    }
    
    func uteManagerSendCustomDataResult(_ result: Bool) {
        if result {
            print("***Send custom data successfully")
        }else{
            print("***Failed to send custom data")
        }
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
        case GlobalConstants.DEVICE_RE_INITIATE:
            self.deviceReInitialize(returnResult: result)
            
        case GlobalConstants.DEVICE_INITIALIZE:
            self.deviceInitialize(returnResult: result)
            
        case GlobalConstants.START_DEVICE_SEARCH:
            self.searchForBTDevices(result: result)
            
            
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
    
    //    private func connectivityUpdateHandler(connectivityType: ConnectivityType) {
    //        DispatchQueue.main.async {
    //          self.eventSink?(self.statusFrom(connectivityType: connectivityType))
    //        }
    //      }
    
    public func onCancel(withArguments _: Any?) -> FlutterError? {
        // connectivityProvider.stop()
        eventSink = nil
        return nil
    }
    
    // SDK Methods Starts Here
    public func deviceReInitialize(returnResult: FlutterResult){
        self.smartBandMgr.initUTESmartBandClient()
        self.smartBandMgr.debugUTELog = true
        self.smartBandMgr.isScanRepeat = true
        self.smartBandMgr.filerRSSI = -90
        
        print("re-sdk vsersion = \(self.smartBandMgr.sdkVersion())")
        // return nil
        //self.smartBandMgr.startScanDevices()
        //self.smartBandMgr.stopScanDevices()
        
        //self.smartBandMgr.delegate = self.smartBandTool
        
        returnResult(GlobalConstants.SC_RE_INIT)
    }
    
    public func deviceInitialize(returnResult: FlutterResult){
        
        
        //print("log sdk vsersion = \(self.smartBandMgr.sdkVersion())")
        // return nil
        //self.smartBandMgr.startScanDevices()
        //self.smartBandMgr.stopScanDevices()
        
        //self.smartBandMgr.delegate = self.smartBandTool
        //returnResult(GlobalConstants.SC_INIT)
        
        //do {
            self.smartBandMgr.initUTESmartBandClient()
            
            self.smartBandMgr.debugUTELog = true
            
            self.smartBandMgr.isScanRepeat = true
            
            self.smartBandMgr.filerRSSI = -60
            
            
            self.smartBandMgr.filerServers = ["5533","2222","FEE7"]
            
            print("log sdk vsersion = \(self.smartBandMgr.sdkVersion())")
            
            //                if let bundlePath = Bundle.main.path(forResource: name,
            //                                                     ofType: "json"),
            //                    let jsonData = try String(contentsOfFile: bundlePath).data(using: .utf8) {
            //                    return jsonData
            //                }
            returnResult(GlobalConstants.SC_INIT)
        //} catch {
       //     print("IOS: Could not initalize")
       //     returnResult(GlobalConstants.SC_FAILURE)
       // }
    }
    
    public func searchForBTDevices(result: FlutterResult){
        print("inseide device start scan")
        
        DispatchQueue.main.async {
            self.smartBandMgr.startScanDevices()
        }
        
        //let jsonObject = createJSONObject(firstName: "firstName", middleName: "middleName", lastName: "lastName", age: 21, weight: 82)
        let jsonObject: [String: Any] = [
            "status" : GlobalConstants.SC_SUCCESS,
            "data": []
        ]
        
        let resultData = try! JSONSerialization.data(withJSONObject: jsonObject)
        let jsonString = String(data: resultData, encoding: .utf8)!
        result(jsonString)
    }
    
    private func getCheckConnectionStatus(result: FlutterResult) {
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
