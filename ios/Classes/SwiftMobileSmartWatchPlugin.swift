import Flutter
import UIKit
import UTESmartBandApi

public class SwiftMobileSmartWatchPlugin: NSObject, FlutterPlugin, FlutterStreamHandler, UTEManagerDelegate{
    
    private var eventSink: FlutterEventSink?
    //private var callbackId :NSMutableDictionary
    
    //smart band
    public typealias findDevicesBlock = () -> Void
    
    var smartBandMgr = UTESmartBandClient.init()
    
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
    public func uteManagerDiscover(_ modelDevices: UTEModelDevices!) {
        print(modelDevices!)
        print("in discover is \(modelDevices!)")
        print("Inside the discover callback ", modelDevices!)
        print("****** data = \(String(describing: modelDevices))")
    }
    
    public func uteManagerDevicesSate(_ devicesState: UTEDevicesSate, error: Error!, userInfo info: [AnyHashable : Any]! = [:]) {
        print("****** devicesState = \(String(describing: devicesState))")
        print("****** error = \(String(describing: error))")
        print("****** userInfo = \(String(describing: info))")
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
        //result("iOS " + UIDevice.current.systemVersion)
        switch call.method {
        case GlobalConstants.BLE_RE_INITIALIZE:
            self.bleReInitialize(returnResult: result)
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
    public func bleReInitialize(returnResult: FlutterResult){
        // return nil
    }
    
    private func getCheckConnectionStatus(result: FlutterResult) {
        var connectResult : NSNumber? = false
        let connectedModel = self.smartBandMgr.connectedDevicesModel
        if connectedModel != nil && connectedModel.isConnected {
            //let status = connectedModel!.isConnected
            //connectResult = status as NSNumber
            connectResult = true
            result(connectResult)
        }else{
            result(connectResult)
        }
    }
}
