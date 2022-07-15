//
//  SmartBandDelegateTool.swift
//  mobile_smart_watch
//
//  Created by MacOS on 05/07/22.
//

import Foundation
import UTESmartBandApi

class SmartBandDelegateTool: NSObject,UTEManagerDelegate {
    
    public typealias manageStateCallback = (String, Any) -> Void
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
        print("uteManagerDevicesSate = \(String(describing: devicesState))")
        print("error = \(String(describing: error))")
        print("userInfo = \(String(describing: info))")
        
        if error != nil {
            let code = (error! as NSError).code
            let msg = (error! as NSError).domain
            print("***error code=\(code),msg=\(msg)")
        }
        switch devicesState {
        case .connected:
            print("IOS_STATE:: Device Connected")
            if self.manageStateCallback != nil {
                self.manageStateCallback!(GlobalConstants.DEVICE_CONNECTED, []);
            }
            break
            
        case .disconnected:
            print("IOS_STATE:: Device DisConnected")
            if self.manageStateCallback != nil {              
                self.manageStateCallback!(GlobalConstants.DEVICE_DISCONNECTED, []);
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
            print("IOS_STATE:: Device bloodDetectingStart")
            break
        case .bloodDetectingProcess:
            print("IOS_STATE:: Device bloodDetectingProcess")
            break
        case .bloodDetectingStop:
            print("IOS_STATE:: Device bloodDetectingStop")
            break
        case .bloodDetectingError:
            print("IOS_STATE:: Device bloodDetectingError")
            break
        case .bloodOxygenDetectingStart:
            print("IOS_STATE:: Device bloodOxygenDetectingStart")
            break
        case .bloodOxygenDetectingProcess:
            print("IOS_STATE:: Device bloodOxygenDetectingProcess")
            break
        case .bloodOxygenDetectingStop:
            print("IOS_STATE:: Device bloodOxygenDetectingStop")
            break
        case .bloodOxygenDetectingError:
            print("IOS_STATE:: Device bloodOxygenDetectingError")
            break
        case .respirationDetectingStart:
            print("IOS_STATE:: Device respirationDetectingStart")
            break
        case .respirationDetectingProcess:
            print("IOS_STATE:: Device respirationDetectingProcess")
            break
        case .respirationDetectingStop:
            print("IOS_STATE:: Device respirationDetectingStop")
            break
        case .respirationDetectingError:
            print("IOS_STATE:: Device respirationDetectingError")
            break
        case .checkFirmwareError:
            print("IOS_STATE:: Device checkFirmwareError")
            break
        case .updateHaveNewVersion:
            print("IOS_STATE:: Device updateHaveNewVersion")
            break
        case .updateNoNewVersion:
            print("IOS_STATE:: Device updateNoNewVersion")
            break
        case .updateBegin:
            print("IOS_STATE:: Device updateBegin")
            break
        case .updateSuccess:
            print("IOS_STATE:: Device updateSuccess")
            break
        case .updateError:
            print("IOS_STATE:: Device updateError")
            break
        case .cardApduError:
            print("IOS_STATE:: Device cardApduError")
            break
        case .passwordState:
            print("IOS_STATE:: Device passwordState")
            break
        case .step:
            print("IOS_STATE:: Device step")
            break
        case .sleep:
            print("IOS_STATE:: Device sleep")
            break
        case .other:
            print("IOS_STATE:: Device other")
            break
        case .UV:
            print("IOS_STATE:: Device UV")
            break
        case .hrmCalibrateStart:
            print("IOS_STATE:: hrmCalibrateStart")
            break
        case .hrmCalibrateFail:
            print("IOS_STATE:: hrmCalibrateFail")
            break
        case .hrmCalibrateComplete:
            print("IOS_STATE:: hrmCalibrateComplete")
            break
        case .hrmCalibrateDefault:
            print("IOS_STATE:: hrmCalibrateDefault")
            break
        case .raiseHandCalibrateStart:
            print("IOS_STATE:: raiseHandCalibrateStart")
            break
        case .raiseHandCalibrateFail:
            print("IOS_STATE:: raiseHandCalibrateFail")
            break
        case .raiseHandCalibrateComplete:
            print("IOS_STATE:: raiseHandCalibrateComplete")
            break
        case .raiseHandCalibrateDefault:
            print("IOS_STATE:: raiseHandCalibrateDefault")
            break
        case .bodyFatStart:
            print("IOS_STATE:: bodyFatStart")
            break
        case .bodyFatStop:
            print("IOS_STATE:: bodyFatStop")
            break
        case .bodyFatStateIn:
            print("IOS_STATE:: bodyFatStateIn")
            break
        case .bodyFatStateOut:
            print("IOS_STATE:: bodyFatStateOut")
            break
        case .bodyFatFail:
            print("IOS_STATE:: bodyFatFail")
            break
        case .bodyFatComplete:
            print("IOS_STATE:: bodyFatComplete")
            break
        case .ecgDetectingStart:
            print("IOS_STATE:: ecgDetectingStart")
            break
        case .ecgDetectingProcess:
            print("IOS_STATE:: ecgDetectingProcess")
            break
        case .ecgDetectingFail:
            print("IOS_STATE:: ecgDetectingFail")
            break
        case .ecgDetectingComplete:
            print("IOS_STATE:: ecgDetectingComplete")
            break
        case .ecgDetectingStatusBothHand:
            print("IOS_STATE:: ecgDetectingStatusBothHand")
            break
        case .ecgDetectingStatusOneHand:
            print("IOS_STATE:: ecgDetectingStatusOneHand")
            break
        case .bodyTempNormal:
            print("IOS_STATE:: bodyTempNormal")
            break
        case .bodyTempCurrent:
            print("IOS_STATE:: bodyTempCurrent")
            break
        case .bodyTempAlarm:
            print("IOS_STATE:: bodyTempAlarm")
            break
        case .drop:
            print("IOS_STATE:: Device drop")
            break
        case .bloodCalibrateStart:
            print("IOS_STATE:: bloodCalibrateStart")
            break
        case .bloodCalibrateStop:
            print("IOS_STATE:: bloodCalibrateStop")
            break
        case .bloodCalibrateComplete:
            print("IOS_STATE:: bloodCalibrateComplete")
            break
        case .bloodCalibrateFail:
            print("IOS_STATE:: bloodCalibrateFail")
            break
        case .bloodCalibrateReset:
            print("IOS_STATE:: bloodCalibrateReset")
            break
        case .mpfDetectingStart:
            print("IOS_STATE:: mpfDetectingStart")
            break
        case .mpfDetectingStop:
            print("IOS_STATE:: mpfDetectingStop")
            break
        case .mpfDetectingComplete:
            print("IOS_STATE:: mpfDetectingComplete")
            break
        case .mpfDetectingFail:
            print("IOS_STATE:: mpfDetectingFail")
            break
        @unknown default:
            print("IOS_STATE:: Default Case")
            break
        }
    }
    
    func uteManageUTEOptionCallBack(_ callback: UTECallBack) {
        print("uteManageUTEOptionCallBack rawValue=\(String(describing: callback.rawValue)) hashValue=\(String(describing: callback.hashValue))")
        
        switch callback.rawValue {
        case 1:
            // user profile updated successfully, Set height, weight, brightness, etc.
            if self.manageStateCallback != nil {            
                self.manageStateCallback!(GlobalConstants.UPDATE_DEVICE_PARAMS, []);
            }
            break
        case 96:
            // open 24-hour heart rate test
            var status = false
            if (self.smartBandMgr.connectedDevicesModel != nil){
                status = self.smartBandMgr.connectedDevicesModel!.isHas24HourHRM
            }
            print("24_hr_sync_status \(status)")
            if self.manageStateCallback != nil {
                let jsonSendData: [String: Any] = [
                    "status" : status
                ]
                self.manageStateCallback!(GlobalConstants.SYNC_STATUS_24_HOUR_RATE_OPEN, jsonSendData);
            }
            break
        case 107:
            // Body Temperature AutoTest Open - send true
            if self.manageStateCallback != nil {
                let jsonSendData: [String: Any] = [
                    "status" : true
                ]
                self.manageStateCallback!(GlobalConstants.SYNC_TEMPERATURE_24_HOUR_AUTOMATIC, jsonSendData);
            }
            break
        case 108:
            // Body Temperature AutoTest Close -  send false
            if self.manageStateCallback != nil {
                let jsonSendData: [String: Any] = [
                    "status" : false
                ]
                self.manageStateCallback!(GlobalConstants.SYNC_TEMPERATURE_24_HOUR_AUTOMATIC, jsonSendData);
            }
            break
        default:
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
        print("Today time=\(String(describing: runData.time))，Total steps=\(runData.totalSteps),Total distance=\(runData.distances),Total calories=\(runData.calories),Current hour steps=\(runData.hourSteps)")
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
        
        print("uteManagerBluetoothState = \(String(describing: bluetoothState))")
        
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
