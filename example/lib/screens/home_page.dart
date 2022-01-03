import 'package:flutter/material.dart';
import 'package:mobile_smart_watch/mobile_smart_watch.dart';
import 'package:mobile_smart_watch_example/global/global.dart';



class HomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return HomePageState();
  }
}

class HomePageState extends State<HomePage> {
  // late SmartCare smartCare;
  //
   List<SmartDeviceModel> smartDevicesList = [];

  MobileSmartWatch _mobileSmartWatch = MobileSmartWatch();

  bool showProgress = false;
  bool deviceConnected = false;

  String deviceMessage ='';
  String deviceVersion ='';
  String batteryStatus ='';

  String  mSteps ='',mCal ='',mDistance ='';
  String  heartRate ='';
  String  mHigh ='',mLow ='';

  @override
  void initState() {
    super.initState();
    _mobileSmartWatch.onDeviceCallbackData((response) {
      print("onDeviceCallbackData1 res: " + response.toString());
      if (response['id'].toString() == SmartWatchConstants.SMART_CALLBACK) {
        // only 3 params, result is return resultant, status is success/failure, & data is json object of multiple data
        String result = response['result'].toString();
        String status = response['status'].toString();
        var jsonData = response['data'];
        //
        switch (result) {
          case SmartWatchConstants.DEVICE_VERSION:
            // data contains only "deviceVersion" returns as String
            String deviceVer = jsonData['deviceVersion'].toString();
            setState(() {
              deviceVersion = deviceVer;
            });
            break;
          case SmartWatchConstants.BATTERY_STATUS:
            // data contains only "deviceVersion"," batteryStatus" returns as String
            // String deviceVersion = jsonData['deviceVersion'].toString();
            print('inside battery status');
            String batteryStat = jsonData['batteryStatus'].toString();
            //print('inside battery status');
            setState(() {
              batteryStatus = batteryStat + "%";
            });
            break;

          case SmartWatchConstants.DEVICE_CONNECTED:
            // data object will be empty always
            if (status == SmartWatchConstants.SC_SUCCESS) {
              print('inside device connect status');
              // then the device is successfully connected.
              setState(() {
                deviceConnected = true;
                deviceMessage = "Device Connected";
              });
            }
            break;

          case SmartWatchConstants.UPDATE_DEVICE_PARAMS:
            // data object will be empty always
            if (status == SmartWatchConstants.SC_SUCCESS) {
              // this message can vary with the corresponding update from/to the patient app.
              Global.showAlertDialog(context, "Updated User Params", "We ahve updated your profile data with the smart watch.");
            }
            break;

          case SmartWatchConstants.DEVICE_DISCONNECTED:
            // data object will be empty always
            if (status == SmartWatchConstants.SC_SUCCESS) {
              setState(() {
                deviceConnected = false;
                deviceMessage = "Device Disconnected";
              });
              // not a valid device to connect the data or fetch the data
              //Global.showAlertDialog(context, "Invalid Device", "The device trying to connect is not a valid device or unsupported to connect the data or fetch the data");
              Global.showAlertDialog(context, "Device Disconnected", "Your device has been diconnected");
            }
            break;
          case SmartWatchConstants.STEPS_REAL_TIME:
          // real time sync as well as the daily sync
            if (status == SmartWatchConstants.SC_SUCCESS) {
              print('inside steps real time');
              String steps = jsonData['steps'].toString();
              String distance = jsonData['distance'].toString();
              String calories = jsonData['calories'].toString();

              setState(() {
                mSteps = steps;
                mCal = calories; // will be always in kCal units
                mDistance = distance;// will be always in kM units
              });
            }
            break;
          case SmartWatchConstants.HR_REAL_TIME:
          // real time sync as well as the daily sync
            if (status == SmartWatchConstants.SC_SUCCESS) {
              print('inside hr real time');
              String hr = jsonData['hr'].toString();
              setState(() {
                heartRate = hr; // always bpm
              });
            }
            break;

          case SmartWatchConstants.BP_RESULT:
          // real time sync as well as the daily sync
            if (status == SmartWatchConstants.SC_SUCCESS) {
              print('inside bp result');
              String high = jsonData['high'].toString();
              String low = jsonData['low'].toString();
              setState(() {
                mHigh = high;
                mLow = low;
              });
            }
            break;


          case SmartWatchConstants.CALLBACK_EXCEPTION:
            // something went wrong, which falls in the exception
            break;
          default:
            break;
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text('Mobile Smart Care'),
        elevation: 1.5,
        actions: [
          IconButton(
            icon: Icon( Icons.refresh_outlined, color: Colors.white),
            onPressed: () async {
              // refresh list
             // await fetchBluDevicesList();

              //navigateToNext();
            },
          ),
        ],
      ),
     body: SingleChildScrollView(
       child: Column(
         mainAxisAlignment: MainAxisAlignment.start,
         crossAxisAlignment: CrossAxisAlignment.start,
         mainAxisSize: MainAxisSize.max,
         children: [
          /* Padding(
             padding: const EdgeInsets.all(8.0),
             child: ElevatedButton(
               style: ElevatedButton.styleFrom(
                 primary: Color(0xFF0BB8FC), // background
                 onPrimary: Colors.white, // foreground
                 onSurface: Color(0xFFCCCCCC),
                 textStyle: TextStyle(fontSize: 18.0),
               ),
               onPressed: () {

               },
               child: Text('Login to Fetch Data', style: TextStyle(color: Colors.white)),
             ),
           ),
           Padding(
             padding: const EdgeInsets.all(8.0),
             child: ElevatedButton(
               style: ElevatedButton.styleFrom(
                 primary: Color(0xFF0BB8FC), // background
                 onPrimary: Colors.white, // foreground
                 onSurface: Color(0xFFCCCCCC),
                 textStyle: TextStyle(fontSize: 18.0),
               ),
               onPressed: () {

               },
               child: Text('Fetch Monitor Data HMS7500', style: TextStyle(color: Colors.white)),
             ),
           ),
           SizedBox(
             height: 8,
           ),*/
           Padding(
             padding: const EdgeInsets.all(8.0),
             child: Row(
               mainAxisAlignment: MainAxisAlignment.spaceBetween,
               crossAxisAlignment: CrossAxisAlignment.start,
               mainAxisSize: MainAxisSize.max,
               children: [
                 Expanded(
                   child: Padding(
                     padding: const EdgeInsets.all(8.0),
                     child: ElevatedButton(
                       style: ElevatedButton.styleFrom(
                         primary: Color(0xFF0BB8FC), // background
                         onPrimary: Colors.white, // foreground
                         onSurface: Color(0xFFCCCCCC),
                         textStyle: TextStyle(fontSize: 18.0),
                       ),
                       onPressed: () {
                         // call this in the initstate in the app.
                         callInitiation(context);
                       },
                       child: Text('Initialize SDK',
                           style: TextStyle(color: Colors.white)),
                     ),
                   ),
                 ),
                 Expanded(
                   child: Padding(
                     padding: const EdgeInsets.all(8.0),
                     child: ElevatedButton(
                       style: ElevatedButton.styleFrom(
                         primary: Color(0xFF0BB8FC), // background
                         onPrimary: Colors.white, // foreground
                         onSurface: Color(0xFFCCCCCC),
                         textStyle: TextStyle(fontSize: 18.0),
                       ),
                       onPressed: () async {
                         await disconnectDevice();
                       },
                       child: Text('Disconnect', style: TextStyle(color: Colors.white)),
                     ),
                   ),
                 ),
               ],
             ),
           ),
           SizedBox(
             height: 8,
           ),
           Row(
             children: [
               TextButton(
                   onPressed: () async {
                     await setUserParams();
                   }, child:  Text('SET USER UPDATE',
                   style: TextStyle(color: Colors.blue,  decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.wavy)),
               ),
               TextButton(
                 onPressed: () async {
                   await fetchBatteryNVersion();
                 }, child:  Text('GET BATTERY STATUS',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),
           SizedBox(
             height: 8,
           ),
           Row(
             children: [
               TextButton(
                 onPressed: () async {
                   // if device is connected call this method in the initState after checking the device connection condition.
                   await fetchSyncStepsData();
                 }, child:  Text('Sync Steps Data',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {

                 }, child:  Text('Sync Heart Rate',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),

             ],
           ),
           Row(
             children: [
               TextButton(
                 onPressed: () async {
                   await startBloodPressure();
                 }, child:  Text('Start BP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   await stopBloodPressure();
                 }, child:  Text('Stop BP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   await syncBloodPressure();
                 }, child:  Text('Sync BP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),

           Row(
             children: [
               TextButton(
                 onPressed: () async {
                   // if device is connected call this method in the initState after checking the device connection condition.
                   await startTempTest();
                 }, child:  Text('Test Temperature',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   // call this function on the dispose method of the screens.
                   await cancelCallback();
                 }, child:  Text('Cancel CallBacks',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),
           //Text('No Devices Found'),
           Visibility(
              visible: !deviceConnected,
             child: Container(
               margin: const EdgeInsets.all(8.0),
               child: showDeviceContainer(smartDevicesList),
             ),
           ),
           Visibility(
              visible: deviceConnected,
              child: Column(
                children: [
                  Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('$deviceMessage'),
                  ),
                  Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('Version: ($deviceVersion) -- Battery: $batteryStatus'),
                  ),
                  Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('Steps: $mSteps -- Cal: $mCal kCal -- Distance: $mDistance km'),
                  ),
                  Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('HR: $heartRate bpm'),
                  ),
                  Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('Blood Pressure: $mHigh / $mLow mmHg'),
                  ),
                 /* Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('HR: $heartRate bpm'),
                  ),*/
                ],
              ),
            )
          ],

          // child: showDeviceContainer(deviceList),
       ),
     ),
    );
  }

  Widget showDeviceContainer(List<SmartDeviceModel> deviceList) {
    if (showProgress) {
      return Center(child: CircularProgressIndicator());
    } else {
      if (deviceList.length > 0) {
        return Container(
          margin: const EdgeInsets.symmetric(vertical: 2, horizontal: 2),
          //padding: const EdgeInsets.all(2.0),
          child: ListView.separated(
            shrinkWrap: true,
            scrollDirection: Axis.vertical,
            physics: NeverScrollableScrollPhysics(),
            separatorBuilder: (BuildContext context, int index) => const Divider(),
            itemCount: deviceList.length,
            itemBuilder: (BuildContext context, int index) {
              return _deviceItem(index);
            },
          ),
        );
      } else {
        return Container();
        /*return Center(
          child: Column(
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Color(0xFF0BB8FC), // background
                    onPrimary: Colors.white, // foreground
                    onSurface: Color(0xFFCCCCCC),
                    textStyle: TextStyle(fontSize: 18.0),
                  ),
                  onPressed: () {
                  //  Get.to(()=>SignIn());
                  },
                  child: Text('Login to Fetch Data', style: TextStyle(color: Colors.white)),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Color(0xFF0BB8FC), // background
                    onPrimary: Colors.white, // foreground
                    onSurface: Color(0xFFCCCCCC),
                    textStyle: TextStyle(fontSize: 18.0),
                  ),
                  onPressed: () {
                    //Get.to(()=>MonitorData());
                  },
                  child: Text('Fetch Monitor Data HMS7500', style: TextStyle(color: Colors.white)),
                ),
              ),
              SizedBox(
                height: 8,
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    primary: Color(0xFF0BB8FC), // background
                    onPrimary: Colors.white, // foreground
                    onSurface: Color(0xFFCCCCCC),
                    textStyle: TextStyle(fontSize: 18.0),
                  ),
                  onPressed: () {
                    callInitiation(context);
                  },
                  child: Text('Initialize & Search Devices',
                      style: TextStyle(color: Colors.white)),
                ),
              ),
              SizedBox(
                height: 8,
              ),
              Text('No Devices Found'),
            ],
          ),
        );*/
      }
    }
  }


  Widget _deviceItem(int index) {
    SmartDeviceModel device = smartDevicesList[index];
    return Container(
      margin: EdgeInsets.all(1.0),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(8.0),
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black,
            blurRadius: 1.0,
            spreadRadius: 0.0,
            offset: Offset(0.2, 0.2), // shadow direction: bottom right
          )
        ],
      ),
      child: ListTile(
        onTap: () {
          // set the device connection in SP
          // set the device mac details like address in SP.
          connectDevice(device);
        },
         /*subtitle:  Divider(
          color: Colors.grey,
        ),*/
        leading: Icon(
          Icons.bluetooth_audio_outlined,
          color: Colors.black,
        ),
        title: Text('${device.name}'),
        subtitle: Text('${device.address}'),
      ),
    );

   /* return ListTile(
      onTap: onTap,
      trailing:menuList[index].hasNavigation ? Icon(Icons.keyboard_arrow_right_outlined, size: 30):Container(),
      title: Text(menuList[index].title, style: TextStyle(
        // fontWeight: FontWeight.w500,
          fontSize: 16.0
      )),
      // subtitle: Text('allow user'),
      leading: Icon(menuList[index].icon, size: 30),
    );*/
  }

  /////////////////////////////////////Use the below method in the app implementation/////////////////////////////////////////

  Future<void> callInitiation(BuildContext context) async {
    // check all the permissions
    // bool status = await checkAllPermissions();
    String initResult = await _mobileSmartWatch.initializeDeviceConnection();
    print('initResult $initState');
    if (initResult != null) {
      if (initResult.toString() == SmartWatchConstants.BLE_NOT_SUPPORTED) {
        Global.showAlertDialog(context, "Bluetooth 4.0",
            "The Bluetooth version of your handeset is lower than the expectation. Your handset bluetooth doesn't support for the Bluetoothv4.0");
      } else if (initResult.toString() == SmartWatchConstants.SC_CANCELED) {
        Global.showAlertDialog(context, "Bluetooth",
            "Please enable the Bluetooth connectivity to search for devices.");
      } else if (initResult.toString() == SmartWatchConstants.SC_INIT) {
        // fecth the bluetooth devices list
        await fetchBluDevicesList();
      }
    }
  }

  Future<void> fetchBluDevicesList() async {
    setState(() {
      showProgress = true;
    });
    List<SmartDeviceModel> deviceList = await _mobileSmartWatch.startSearchingDevices();
    print('fetchBluDevicesList $deviceList');

    if (deviceList.length > 0) {
      setState(() {
        showProgress = false;
        smartDevicesList = deviceList;
      });
      //return deviceList;
    } else {
      // no devices are found
      setState(() {
        showProgress = false;
        smartDevicesList = [];
      });
    }
  }

  Future<void> connectDevice(SmartDeviceModel device) async {
    bool startDeviceSearch = await _mobileSmartWatch.connectDevice(device);
    print('connectDevice>> $startDeviceSearch');
    if (!startDeviceSearch) {
      // device.address;
      // device.name;
      // shave it in the SP
      // show the message
      // Global.showAlertDialog(context, "Connection","You can't connect this device, since it's invalid device.");
    } else {


    /*  setState(() {
        deviceConnected = startDeviceSearch;
      });*/
    }
  }

   Future<void> setUserParams() async {
     var userData = {
       "age":"30",  // user age (0-254)
       "height":"173", // always cm
       "weight":"70", // always in kgs
       "gender":"male", //male  or female in lower case
       "steps": "10000", // targetted goals
       "isCelsius": "false", // if celsius then send "true" else "false" for Fahrenheit
       "screenOffTime": "15", //screen off time
       "isChineseLang": "false", //true for chinese lang setup and false for english
     };
     String resultStatus = await _mobileSmartWatch.setUserParameters(userData);
     print('resultStatus>> $resultStatus');
     // resultStatus ==  SC_NOT_SUPPORTED  (device not supported) or SC_INIT (write command initiated)or SC_FAILURE
   }

  Future<void> disconnectDevice() async {
    bool deviceDisconnected = await _mobileSmartWatch.disconnectDevice();
    print('disconnectDevice>> $deviceDisconnected');
    setState(() {
     // deviceConnected = false;
      deviceMessage = "Device Disconnected";
    });
  }

   Future<void> fetchBatteryNVersion() async {
     String batteryStatus = await _mobileSmartWatch.getBatteryStatus();
     String deviceVersion = await _mobileSmartWatch.getDeviceVersion();
     print('batteryStatus>> $batteryStatus');
     print('deviceVersion>> $deviceVersion');
   }

   Future<void> fetchSyncStepsData() async{
     String stepsStatus = await _mobileSmartWatch.syncStepsData();
     print('syncStepsStatus>> $stepsStatus');
   }

   Future<void>  startBloodPressure() async {
     String startBPStatus = await _mobileSmartWatch.startBloodPressure();
     print('startBloodPressure>> $startBPStatus');
   }
   Future<void>  stopBloodPressure() async {
     String stopBPStatus = await _mobileSmartWatch.stopBloodPressure();
     print('stopBloodPressure>> $stopBPStatus');
   }
   Future<void> syncBloodPressure() async {
     String syncBPStatus = await _mobileSmartWatch.syncBloodPressure();
     print('syncBloodPressure>> $syncBPStatus');
   }


   Future<void> startTempTest() async{
     String tempStatus = await _mobileSmartWatch.testTempData();

   }

   Future<void> cancelCallback() async {

  }






/* Future<bool> checkAllPermissions() async {
    var location = await Permission.locationWhenInUse.status;
    var bluetooth = await Permission.bluetooth.status;

    print('location $location');
    print('bluetooth $bluetooth');

    if (location.isGranted && bluetooth.isGranted) {
      return true;
    } else {
      //  await  Permission.bluetooth.request();
      // await Permission.location.request();
      //return false;
      if (await Permission.locationWhenInUse.request().isGranted &&
          await Permission.bluetooth.request().isGranted) {
        return true;
      } else {
        return false;
      }
    }
  }*/

 /* Future<bool> _checkDeviceLocationIsOn() async {
    return await Permission.locationWhenInUse.serviceStatus.isEnabled;
  }

  Future<bool> _checkDeviceBluetoothIsOn() async {
    return await FlutterBlue.instance.isOn;
  }*/

}