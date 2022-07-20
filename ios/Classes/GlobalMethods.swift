//
//  GlobalMethods.swift
//  mobile_smart_watch
//
//  Created by MacOS on 20/07/22.
//

import Foundation

struct GlobalMethods {
    
    public static func getTempIntoFahrenheit(tempInCelsius: String) -> String{
        let tempCelsius = Double(tempInCelsius)!
        let infoValue: Double = (tempCelsius * 1.8000)+32.00;
        return String(format: "%.1f", infoValue);
    }
    
    public static func convertDoubleToStringWithDecimal(infoValue: String) -> String{
        let info = Double(infoValue)!
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
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyyMMdd"
        //String calenderDate = DateFormat('yyyyMMdd').format(dateTime);
        //return calenderDate;
        let date = dateFormatter.date(from: dateTime)
        let timestamp = dateFormatter.string(from: date!)
        print("timestamp>> \(timestamp)")
        return timestamp;
    }
}
