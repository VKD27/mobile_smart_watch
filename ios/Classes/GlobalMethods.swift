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
        
        let dateTime = timeList.joined(separator: "") // returns in yyyMMddHHmmss
        
        let calenderTime : String = "\(timeList[0])\(timeList[1])\(timeList[2])"
        
        let time : String = "\(timeList[timeList.count - 2]):\(timeList[timeList.count - 1])"
        
        print("timeList>> \(timeList)")
        print("dateTime>> \(dateTime)")
        print("calenderTime>> \(calenderTime)")
        print("time>> \(time)")
        
        return [calenderTime, dateTime,time];
    }
}
