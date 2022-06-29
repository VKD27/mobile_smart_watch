//
//  SmartBandConnectedControl.swift
//  mobile_smart_watch
//
//  Created by MacOS on 28/06/22.
//

import Foundation
import UTESmartBandApi

let kUTESyncMethod : NSString         = "UTE SyncData Method"
let kUTETestMethod : NSString         = "UTE Test Method"
let kUpdateFirmware : NSString        = "Update Firmware"
let kAppRemind : NSString             = "App Remind"
let kAboutSport : NSString            = "About Sport"
let kAboutDial : NSString             = "About Dial"
let kAboutRespiration : NSString      = "About Respiration"
let kOther : NSString                 = "Other"

enum UTESyncType : NSInteger {
    case SyncTime
    case SyncStep
    case SyncSleep
    case SyncHRM
    case Sync24HRM
    case SyncBlood
    case SyncSaO2
    case SyncBodyFat
    case SyncECG
    case SyncSportGPS
    case SyncSkip
    case SyncSwim
    case SyncBall
    case SyncAllSport
}

enum UTETestType : NSInteger {
    case SetStaticHRM
    case SetDynamicHRM
    case StartHRM
    case StopHRM
    case AutomaticHRM
    case Open24HRM
    case Close24HRM
    case StartBlood
    case StopBlood
    case StartSaO2
    case StopSaO2
    case StartBodyFat
    case StopBodyFat
    case StartECG
    case StopECG
}

enum UTEFirmwareType : NSInteger {
    case Check
    case Update
}

enum UTERemindType : NSInteger {
    case OpenQQ
    case CloseQQ
    case OpenOther
    case CloseOther
    case OpenSit
    case CloseSit
}

enum UTESportType : NSInteger {
    case ReadStatus
    case StartCycling
    case StopCycling
    case PauseCycling
    case ContinueCycling
    case CyclingSameData
}

enum UTEDialType : NSInteger {
    case ReadDialInfo
    case ReadOnlineDial
    case SyncDial
    case LoadLocalDial
    case LoadOnlineCustomDial
    case OnlineCustomDialModel
    case ChangeOnlineCustomDial
    case SyncCustomDial
}

enum UTERespirationRateType : NSInteger {
    case StartTest
    case StopTest
    case ReadStatus
    case LoadLocalDial
    case Sync
    case OpenTime
    case CloseTime
}

enum UTEOtherType : NSInteger {
    case SetAlarm
    case SetPersonalInfo
    case ReSetDevice
    case DontDisturbMode
    case SetWeather
    case CustomDeviceText
    case SleepCorrect
    case CustomSleep
    case CustomNapSleep
    case FindDevice
    case CustomDataSend
    case OpenCameraMode
    case CloseCameraMode
    case Meter_12
    case Inch_12
    case Meter_24
    case Inch_24
    case ReadBatteryPower
    case OpenFindPhone
    case CloseFindPhone
    case Shutdown
    case StartCalibrationHRM
    case CalibrationHRMDefault
    case StartCalibrationHand
    case CalibrationHandDefault
    case ReSetTempCalibration
    case ClearAllBodyTemp
    case ReadUV
    case SetMenstruation
    case CustomDeviceUI
    case CustomContentSend
    case ReadDeviceVersion
    case ReadDeviceAddress
    case ReadDeviceLanguage
    case CalibrationBodyTemp
    case AutomaticTestBodyTemp
    case AutomaticTestBodyTempPeriod
    case AutomaticTestBodyTempAlarm
    case ReadCurrentBodyTemp
    case ReadConnectDevice
    case AutoTestBloodOxygen
    case AutoTestDurationBloodOxygen
   
}

