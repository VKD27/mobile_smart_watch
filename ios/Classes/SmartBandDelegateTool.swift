//
//  SmartBandDelegateTool.swift
//  mobile_smart_watch
//
//  Created by MacOS on 05/07/22.
//

import Foundation
import UTESmartBandApi

class SmartBandDelegateTool: NSObject,UTEManagerDelegate {
    
    public typealias manageStateCallback = (String) -> Void
    open var manageStateCallback : manageStateCallback?
    
    
    public typealias getDevicesList = (Array<UTEModelDevices>) -> Void
    open var getDevicesList : getDevicesList?
    
    open var mArrayDevices : [UTEModelDevices] = NSMutableArray.init() as! [UTEModelDevices]
    
    var smartBandMgr = UTESmartBandClient.init()
    var passwordType : UTEPasswordType?
    //weak var connectVc : SmartBandConnectedControl?
    
    override init() {
        super.init()
        self.smartBandMgr = UTESmartBandClient.sharedInstance()
    }
    
    func uteManagerDiscover(_ modelDevices: UTEModelDevices!) {
        //print("SmartBandDelegateTool>> in discover is \(modelDevices!)")
        //print("SmartBandDelegateTool>> Inside the discover callback ", modelDevices!)
        //print("SmartBandDelegateTool>> deviceName = \(String(describing: modelDevices.name))")
        
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
            //            print("***Scanned device name11=\(String(describing: modelDevices.name)) id=\(String(describing: modelDevices.identifier))")
            //            print("***Scanned device name22=\(String(describing: modelDevices.address)) str=\(String(describing: modelDevices.addressStr))")
            //            print("***Scanned device name33=\(String(describing: modelDevices.description)) rssi=\(String(describing: modelDevices.rssi))")
            //            print("***Scanned device name44=\(String(describing: modelDevices.advertisementData)) addr=\(String(describing: modelDevices.advertisementAddress))")
            if modelDevices.name.lowercased().contains("docty") || modelDevices.name.lowercased().contains("kmo4") {
                self.mArrayDevices.append(modelDevices)
                if self.getDevicesList != nil {
                    self.getDevicesList!(self.mArrayDevices);
                }
            }
        }
    }
    
    
    func uteManagerDevicesSate(_ devicesState: UTEDevicesSate, error: Error!, userInfo info: [AnyHashable : Any]! = [:]) {
        print("****** devicesState = \(String(describing: devicesState))")
        print("****** error = \(String(describing: error))")
        print("****** userInfo = \(String(describing: info))")
        if error != nil {
            let code = (error! as NSError).code
            let msg = (error! as NSError).domain
            print("***error code=\(code),msg=\(msg)")
        }
        switch devicesState {
            
//        case UTEDevicesSate.connected:
//            break
//        case UTEDevicesSate.disconnected:
//            if error != nil {
//                print("***Device disconnected abnormally=\(String(describing: error))")
//            }else{
//                print("***Device disconnected normally connectedDevicesModel=\(String(describing: self.smartBandMgr.connectedDevicesModel))")
//            }
//            break
        case .connected:
            print("IOS Device Connected")
            if self.manageStateCallback != nil {
                self.manageStateCallback!(GlobalConstants.DEVICE_CONNECTED);
            }
            break
            
        case .disconnected:
            print("IOS Device DisConnected")
            if self.manageStateCallback != nil {
                self.manageStateCallback!(GlobalConstants.DEVICE_DISCONNECTED);
            }
            break
            
        case .connectingError:
            <#code#>
        case .syncBegin:
            <#code#>
        case .syncSuccess:
            <#code#>
        case .syncError:
            <#code#>
        case .heartDetectingStart:
            <#code#>
        case .heartDetectingProcess:
            <#code#>
        case .heartDetectingStop:
            <#code#>
        case .heartDetectingError:
            <#code#>
        case .heartCurrentValue:
            <#code#>
        case .bloodDetectingStart:
            <#code#>
        case .bloodDetectingProcess:
            <#code#>
        case .bloodDetectingStop:
            <#code#>
        case .bloodDetectingError:
            <#code#>
        case .bloodOxygenDetectingStart:
            <#code#>
        case .bloodOxygenDetectingProcess:
            <#code#>
        case .bloodOxygenDetectingStop:
            <#code#>
        case .bloodOxygenDetectingError:
            <#code#>
        case .respirationDetectingStart:
            <#code#>
        case .respirationDetectingProcess:
            <#code#>
        case .respirationDetectingStop:
            <#code#>
        case .respirationDetectingError:
            <#code#>
        case .checkFirmwareError:
            <#code#>
        case .updateHaveNewVersion:
            <#code#>
        case .updateNoNewVersion:
            <#code#>
        case .updateBegin:
            <#code#>
        case .updateSuccess:
            <#code#>
        case .updateError:
            <#code#>
        case .cardApduError:
            <#code#>
        case .passwordState:
            <#code#>
        case .step:
            <#code#>
        case .sleep:
            <#code#>
        case .other:
            <#code#>
        case .UV:
            <#code#>
        case .hrmCalibrateStart:
            <#code#>
        case .hrmCalibrateFail:
            <#code#>
        case .hrmCalibrateComplete:
            <#code#>
        case .hrmCalibrateDefault:
            <#code#>
        case .raiseHandCalibrateStart:
            <#code#>
        case .raiseHandCalibrateFail:
            <#code#>
        case .raiseHandCalibrateComplete:
            <#code#>
        case .raiseHandCalibrateDefault:
            <#code#>
        case .bodyFatStart:
            <#code#>
        case .bodyFatStop:
            <#code#>
        case .bodyFatStateIn:
            <#code#>
        case .bodyFatStateOut:
            <#code#>
        case .bodyFatFail:
            <#code#>
        case .bodyFatComplete:
            <#code#>
        case .ecgDetectingStart:
            <#code#>
        case .ecgDetectingProcess:
            <#code#>
        case .ecgDetectingFail:
            <#code#>
        case .ecgDetectingComplete:
            <#code#>
        case .ecgDetectingStatusBothHand:
            <#code#>
        case .ecgDetectingStatusOneHand:
            <#code#>
        case .bodyTempNormal:
            <#code#>
        case .bodyTempCurrent:
            <#code#>
        case .bodyTempAlarm:
            <#code#>
        case .drop:
            <#code#>
        case .bloodCalibrateStart:
            <#code#>
        case .bloodCalibrateStop:
            <#code#>
        case .bloodCalibrateComplete:
            <#code#>
        case .bloodCalibrateFail:
            <#code#>
        case .bloodCalibrateReset:
            <#code#>
        case .mpfDetectingStart:
            <#code#>
        case .mpfDetectingStop:
            <#code#>
        case .mpfDetectingComplete:
            break
        case .mpfDetectingFail:
            break
        @unknown default:
            break
        }
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
}
