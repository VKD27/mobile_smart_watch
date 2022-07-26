//
//  GlobalMethods.swift
//  mobile_smart_watch
//
//  Created by MacOS on 20/07/22.
//

import Foundation

struct GlobalMethods {
    
    public static func getTempIntoFahrenheit(tempInCelsius: String) -> String{
        let tempCelsius = Double(tempInCelsius) ?? 0.0
        let infoValue: Double = (tempCelsius * 1.8000)+32.00;
        return String(format: "%.1f", infoValue);
    }
    
    public static func convertDoubleToStringWithDecimal(infoValue: String) -> String{
        let info = Double(infoValue) ?? 0.0
        let stringValue = String(format: "%.2f", info)
        return stringValue
    }
    
    public static func convertDoubleToStringWithDecimal(infoValue: Float) -> String{
        let info = Double(infoValue)
        let stringValue = String(format: "%.2f", info)
        return stringValue
    }
  
    public static func convertBandReadableCalender(dateTime:String) -> String{
        //2022-07-20-13-00-00 input datetime
        //var formatter: NSDateFormatter = NSDateFormatter()
        //formatter.dateFormat="yyyy-MM-dd 00:00:00 Z"
        let timeList = dateTime.components(separatedBy: "-")
        let timestamp : String = "\(timeList[0])\(timeList[1])\(timeList[2])"
        
       // let dateFormatter = DateFormatter()
       // dateFormatter.dateFormat = "yyyyMMdd"
        
        //String calenderDate = DateFormat('yyyyMMdd').format(dateTime);
        //return calenderDate;
        
        //let date = dateFormatter.date(from: dateTime)
        //let timestamp = dateFormatter.string(from: date!)
        print("timestamp>> \(timestamp)")
        return timestamp;
    }
    
    public static func convertDateTimeToyyyyMMddHHmmss(inputDateTime:String) -> String {
        // Input: 2022-07-20-13-00-00 input datetime
        // Output: yyyMMddHHmmss
        let timeList = inputDateTime.components(separatedBy: "-")
        let dateTime = timeList.joined(separator: "")+"00"
        return dateTime
    }
    
    public static func convertDateTimeCalenderReturn(inputDateTime:String) -> [String]{
        // Input: 2022-07-20-13-00-00 input datetime
        // Output: yyyMMddHHmmss
        let timeList = inputDateTime.components(separatedBy: "-")
        
        let dateTime = timeList.joined(separator: "") // returns in yyyMMddHHmmss or yyyMMddHHmm
        
        let calender : String = "\(timeList[0])\(timeList[1])\(timeList[2])"
        let time : String
        if(timeList.count > 5){
            time = "\(timeList[timeList.count - 3]):\(timeList[timeList.count - 2])"
        }else{
            time = "\(timeList[timeList.count - 2]):\(timeList[timeList.count - 1])"
        }
        //let time : String = "\(timeList[timeList.count - 2]):\(timeList[timeList.count - 1])"
        
        print("timeList>> \(timeList)")
        print("dateTime>> \(dateTime)")
        print("calender>> \(calender)")
        print("time>> \(time)")
        
        return [calender, dateTime, time];
    }
    
    public static func getDateTimeInNumber(startTime:String, endTime:String) -> [String]{
        // inputTime == 2022-07-26-00-38
        let startList = startTime.components(separatedBy: "-")
        let startDateTime = startList.joined(separator: "") // returns in yyyMMddHHmmss or yyyMMddHHmm
        
        let endList = endTime.components(separatedBy: "-")
        let endDateTime = endList.joined(separator: "") // returns in yyyMMddHHmmss or yyyMMddHHmm
        
        let calender : String = "\(startList[0])\(startList[1])\(startList[2])"
        
        var startHourNum : Int  = 0
        var startMinNum : Int  = 0
        var startTotalSeconds : Int = 0
       
        if(startList.count > 5){
            let hour = Int(startList[startList.count - 3])
            let min = Int(startList[startList.count - 2])
            let sec = Int(startList[startList.count - 1])
            
            startHourNum = hour! * 60 * 60
            startMinNum = min! * 60
            startTotalSeconds = startHourNum + startMinNum + sec!;
        }else{
            let hour = Int(startList[startList.count - 2])
            let min = Int(startList[startList.count - 1])
            startHourNum = hour! * 60 * 60
            startMinNum = min! * 60
            startTotalSeconds = startHourNum + startMinNum;
        }
        
        var endHourNum : Int  = 0
        var endMinNum : Int  = 0
        var endTotalSeconds : Int = 0
       
        if(endList.count > 5){
            let hour = Int(endList[endList.count - 3])
            let min = Int(endList[endList.count - 2])
            let sec = Int(endList[endList.count - 1])
            
            endHourNum = hour! * 60 * 60
            endMinNum = min! * 60
            endTotalSeconds = endHourNum + endMinNum + sec!;
        }else{
            let hour = Int(endList[endList.count - 2])
            let min = Int(endList[endList.count - 1])
            endHourNum = hour! * 60 * 60
            endMinNum = min! * 60
            endTotalSeconds = endHourNum + endMinNum;
        }
        
        return [calender, startDateTime, endDateTime, "\(startTotalSeconds)", "\(endTotalSeconds)"];
    }
    
    
    public static func getCommonSleepState(inputState:Int) -> String {
        if inputState == 0{
            return "2"; // Awake
        }else if inputState == 1{
            return "0"; // deep sleep
        }else if inputState == 2{
            return "1"; // light sleep
        }else{
            return "";
        }
    }
}
