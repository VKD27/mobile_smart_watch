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
            print("IOS_STATE:: Device Connected")
            if self.manageStateCallback != nil {
                self.manageStateCallback!(GlobalConstants.DEVICE_CONNECTED);
            }
            break
            
        case .disconnected:
            print("IOS_STATE:: Device DisConnected")
            if self.manageStateCallback != nil {
                self.manageStateCallback!(GlobalConstants.DEVICE_DISCONNECTED);
            }
            break
            
        case .connectingError:
            print("IOS_STATE:: Device connectingError")
            break
        case .syncBegin:
            print("IOS_STATE:: Device syncBegin")
            break
        case .syncSuccess:
            print("IOS_STATE:: Device syncSuccess")
            break
        case .syncError:
            print("IOS_STATE:: Device syncError")
            break
        case .heartDetectingStart:
            print("IOS_STATE:: Device heartDetectingStart")
            break
        case .heartDetectingProcess:
            print("IOS_STATE:: Device heartDetectingProcess")
            break
        case .heartDetectingStop:
            print("IOS_STATE:: Device heartDetectingStop")
            break
        case .heartDetectingError:
            print("IOS_STATE:: Device heartDetectingError")
            break
        case .heartCurrentValue:
            print("IOS_STATE:: Device heartCurrentValue")
            break
        case .bloodDetectingStart:
            break
        case .bloodDetectingProcess:
            break
        case .bloodDetectingStop:
            break
        case .bloodDetectingError:
            break
        case .bloodOxygenDetectingStart:
            break
        case .bloodOxygenDetectingProcess:
            break
        case .bloodOxygenDetectingStop:
            break
        case .bloodOxygenDetectingError:
            break
        case .respirationDetectingStart:
            break
        case .respirationDetectingProcess:
            break
        case .respirationDetectingStop:
            break
        case .respirationDetectingError:
            break
        case .checkFirmwareError:
            break
        case .updateHaveNewVersion:
            break
        case .updateNoNewVersion:
            break
        case .updateBegin:
            break
        case .updateSuccess:
            break
        case .updateError:
            break
        case .cardApduError:
            break
        case .passwordState:
            break
        case .step:
            break
        case .sleep:
            break
        case .other:
            break
        case .UV:
            break
        case .hrmCalibrateStart:
            break
        case .hrmCalibrateFail:
            break
        case .hrmCalibrateComplete:
            break
        case .hrmCalibrateDefault:
            break
        case .raiseHandCalibrateStart:
            break
        case .raiseHandCalibrateFail:
            break
        case .raiseHandCalibrateComplete:
            break
        case .raiseHandCalibrateDefault:
            break
        case .bodyFatStart:
            break
        case .bodyFatStop:
            break
        case .bodyFatStateIn:
            break
        case .bodyFatStateOut:
            break
        case .bodyFatFail:
            break
        case .bodyFatComplete:
            break
        case .ecgDetectingStart:
            break
        case .ecgDetectingProcess:
            break
        case .ecgDetectingFail:
            break
        case .ecgDetectingComplete:
            break
        case .ecgDetectingStatusBothHand:
            break
        case .ecgDetectingStatusOneHand:
            break
        case .bodyTempNormal:
            break
        case .bodyTempCurrent:
            break
        case .bodyTempAlarm:
            break
        case .drop:
            break
        case .bloodCalibrateStart:
            break
        case .bloodCalibrateStop:
            break
        case .bloodCalibrateComplete:
            break
        case .bloodCalibrateFail:
            break
        case .bloodCalibrateReset:
            break
        case .mpfDetectingStart:
            break
        case .mpfDetectingStop:
            break
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
