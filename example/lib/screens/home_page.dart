//import 'dart:io';
//import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/material.dart';
import 'package:mobile_smart_watch/mobile_smart_watch.dart';
import 'package:mobile_smart_watch_example/global/global.dart';
import 'package:permission_handler/permission_handler.dart';


class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return HomePageState();
  }
}

class HomePageState extends State<HomePage> {
  // late SmartCare smartCare;
  //
   List<SmartDeviceModel> smartDevicesList = [];

  final MobileSmartWatch _mobileSmartWatch = MobileSmartWatch();

  bool showProgress = false;
  bool deviceConnected = false;

  String deviceMessage ='';
  String deviceVersion ='';
  String batteryStatus ='';

  String  mSteps ='',mCal ='',mDistance ='';
  String  heartRate ='';
  String  sleepTimings ='';
  String  mHigh ='',mLow ='';

  @override
  void initState() {
    super.initState();
    initialize();
    /*_mobileSmartWatch.onDeviceCallbackData((response) {
      debugPrint("onDeviceCallbackData1 res: " + response.toString());
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
            debugPrint('inside battery status');
            String batteryStat = jsonData['batteryStatus'].toString();
            //debugPrint('inside battery status');
            setState(() {
              batteryStatus = batteryStat + "%";
            });
            break;

          case SmartWatchConstants.DEVICE_CONNECTED:
            // data object will be empty always
            if (status == SmartWatchConstants.SC_SUCCESS) {
              debugPrint('inside device connect status');
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
              Global.showAlertDialog(context, "Updated User Params", "We have updated your profile data with the smart watch.");
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
              debugPrint('inside steps real time');
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
              debugPrint('inside hr real time');
              String hr = jsonData['hr'].toString();
              setState(() {
                heartRate = hr; // always bpm
              });
            }
            break;

          case SmartWatchConstants.TEMP_RESULT:
          // real time sync as well as the daily sync
            if (status == SmartWatchConstants.SC_SUCCESS) {
              debugPrint('inside temperature result');
              String inCelsius = jsonData['inCelsius'].toString();
              String inFahrenheit = jsonData['inFahrenheit'].toString();
              String startDate = jsonData['startDate'].toString();
              String time = jsonData['time'].toString();
              String calender = jsonData['calender'].toString();

              setState(() {
               // heartRate = hr; // always bpm
              });
            }
            break;

          case SmartWatchConstants.BP_RESULT:
          // real time sync as well as the daily sync
            if (status == SmartWatchConstants.SC_SUCCESS) {
              debugPrint('inside bp result');
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
              debugPrint('callbackException occurred.');
            break;
          default:
            break;
        }
      }
    });*/

    /*_mobileSmartWatch.registerCallBackListeners((response){
      debugPrint("registerCallBackListeners>>" + response.toString());
    });*/

    _mobileSmartWatch.registerEventCallBackListeners().listen((event) {
      debugPrint("registerCallBackListeners>>" + event.toString());
     /* String hr = jsonData['hr'].toString();
      setState(() {
        heartRate = hr; // always bpm
      });*/
    }, onError: (dynamic error){
      debugPrint("registerCallBackError>> $error");
    });

    _mobileSmartWatch.receiveEventListeners(onData: (eventData) {
      debugPrint("on onData eventData: $eventData");
    },onError: (dynamic error){
      debugPrint("on error occured: $error");
    },onDone :() {

    });
  }
   Future<bool> locationPermissionsGranted() async {
     var permission = await Permission.location.status;
     if (permission.isPermanentlyDenied) {
       await openAppSettings();
     } else if (await Permission.location.request().isGranted) {
       return true;
     } else {
       permission = await Permission.location.request();
     }
     return permission.isGranted;
   }

   Future<void> initialize() async {

     // bool bluetoothStatus = await Permissions.bluetoothPermissionsGranted();
     // debugdebugPrint('bluetoothStatus>> $bluetoothStatus');
     // if (bluetoothStatus) {
     // myLocation = await GlobalMethods.fetchDeviceCurrentLocation(context);
     // if (myLocation != null) {
     // await _activityServiceProvider.setLocationCoOrdinates(myLocation.latitude, myLocation.longitude);
     // if(Platform.isAndroid) {
     //   AndroidDeviceInfo androidInfo  = await deviceInfo.androidInfo;
     //   if(androidInfo.version.sdkInt! >= 31) {
     //     statuses = await [Permission.bluetoothConnect, Permission.bluetoothScan, Permission.locationWhenInUse, Permission.location,].request();
     //     // Permission.bluetoothAdvertise,
     //   } else {
     //     statuses = await [Permission.bluetooth, Permission.location].request();
     //   }
     // } else {
     Map<Permission, PermissionStatus> statuses = await [Permission.bluetooth, Permission.location, Permission.locationAlways, Permission.locationWhenInUse].request();
    // }
     debugPrint('statuses>> $statuses');
     if (await locationPermissionsGranted()) {
       debugPrint('locationPermissionsGranted>> $statuses');
     }
     /*var bluPermission = await Permission.bluetooth.status;
    debugdebugPrint('permission>>  $bluPermission');

    if (bluPermission.isDenied) {

      var bluetoothConnect = await Permission.bluetoothConnect.request();
      var bluetoothScan = await Permission.bluetoothScan.request();
      var bluetoothAdvertise = await Permission.bluetoothAdvertise.request();
      debugdebugPrint('bluetoothConnect>>  $bluetoothConnect');
      debugdebugPrint('bluetoothConnect>>  $bluetoothScan');
      debugdebugPrint('bluetoothConnect>>  $bluetoothAdvertise');

    }*/
   }

  void callOnInitStateSync(){
    // if the device is connected then call the below methods
    // in the initstate of the app while every sync
    // this is to sync the data from the device to the SDK, and then the APP side can obtain the data by calling each individual methods
    fetchSyncStepsData();
    fetchSyncSleepData();
    syncBloodPressure();

  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: const Text('Mobile Smart Care'),
        elevation: 1.5,
        actions: [
          IconButton(
            icon: const Icon( Icons.refresh_outlined, color: Colors.white),
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
                         primary: const Color(0xFF0BB8FC), // background
                         onPrimary: Colors.white, // foreground
                         onSurface: const Color(0xFFCCCCCC),
                         textStyle: const TextStyle(fontSize: 18.0),
                       ),
                       onPressed: () async {
                         // call this in the initstate in the app.
                         await callInitiation(context);
                       },
                       child: const Text('Initialize SDK',
                           style: TextStyle(color: Colors.white)),
                     ),
                   ),
                 ),
                 Expanded(
                   child: Padding(
                     padding: const EdgeInsets.all(8.0),
                     child: ElevatedButton(
                       style: ElevatedButton.styleFrom(
                         primary: const Color(0xFF0BB8FC), // background
                         onPrimary: Colors.white, // foreground
                         onSurface: const Color(0xFFCCCCCC),
                         textStyle: const TextStyle(fontSize: 18.0),
                       ),
                       onPressed: () async {
                         await disconnectDevice();
                       },
                       child: const Text('Disconnect', style: TextStyle(color: Colors.white)),
                     ),
                   ),
                 ),
               ],
             ),
           ),
           const SizedBox(
             height: 8,
           ),
           Row(
             children: [
               TextButton(
                   onPressed: () async {
                     await setUserParams();
                   }, child:  const Text('SET USER UPDATE',
                   style: TextStyle(color: Colors.blue,  decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.wavy)),
               ),
               TextButton(
                 onPressed: () async {
                  // await fetchBatteryNVersion();
                   await syncOverAll();
                  // await fetchAllJudgement();
                   //await fetchOverAllByDate();
                 }, child:  const Text('GET BATTERY STATUS /Sync',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),
           const SizedBox(
             height: 8,
           ),
           Wrap(
             alignment: WrapAlignment.center,
             direction: Axis.horizontal,
             children: [
               TextButton(
                 onPressed: () async {
                   // if device is connected call this method in the initState after checking the device connection condition.

                   await fetchSyncStepsData();
                 }, child:  const Text('S_Steps',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   await fetchSyncSleepData();
                 }, child:  const Text('S_Sleep',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   await syncBloodPressure();
                 }, child:  const Text('S_BP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
               TextButton(
                 onPressed: () async {
                   await syncOxygen();
                 }, child:  const Text('S_OXY',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
               TextButton(
                 onPressed: () async {
                   await syncHeartRate();
                 }, child:  const Text('S_HR',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   await syncTemperature();
                 }, child:  const Text('S_TEMP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
             ],
           ),
           Wrap(
             alignment: WrapAlignment.center,
             direction: Axis.horizontal,
             children: [
               TextButton(
                 onPressed: () async {
                   await startBloodPressure();
                 }, child:  const Text('Start BP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   await stopBloodPressure();
                 }, child:  const Text('Stop BP',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   // call this function on the dispose method of the screens.
                   await startHR();
                 }, child:  const Text('Start HR',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
               TextButton(
                 onPressed: () async {
                   await stopHR();
                 }, child:  const Text('Stop HR',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),
           Container(
             child: Row(
               mainAxisAlignment: MainAxisAlignment.start,
               mainAxisSize: MainAxisSize.max,
               children: [
                 TextButton(
                   onPressed: () async {
                     // call this function on the dispose method of the screens.
                     await fetchStepsByDate();
                   }, child:  const Text('F_StepsDT',
                     style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
                 ),
                 TextButton(
                   onPressed: () async {
                     await fetchSleepByDate();
                   }, child:  const Text('F_SleepDT',
                     style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
                 ),
                 TextButton(
                   onPressed: () async {
                     await fetchHeartRateByDate();
                   }, child:  const Text('F_HRDT',
                     style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
                 ),
                 TextButton(
                   onPressed: () async {
                     await fetch24HourHRByDate();
                   }, child:  const Text('F_24HRDT',
                     style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
                 ),

               ],
             ),
           ),
           Wrap(
             alignment: WrapAlignment.center,
             direction: Axis.horizontal,
             children: [
               TextButton(
                 onPressed: () async {
                   await fetchTemperatureByDate();
                 }, child:  const Text('F_TEMPDT',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
               TextButton(
                 onPressed: () async {
                   // if device is connected call this method in the initState after checking the device connection condition.
                   await startTempTest();
                 }, child:  const Text('Test Temp',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dotted)),
               ),
               TextButton(
                 onPressed: () async {
                   // call this function on the dispose method of the screens.
                   await fetchAllTemperatureData();
                 }, child:  const Text('GetAll Temp',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),
           Row(
             children: [
               TextButton(
                 onPressed: () async {
                   // call this function on the dispose method of the screens.
                   await fetchAllStepsData();
                 }, child:  const Text('GetAll Steps',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
               TextButton(
                 onPressed: () async {
                   // call this function on the dispose method of the screens.
                   await fetchAllSleepData();
                 }, child:  const Text('GetAll Sleep',
                   style: TextStyle(color: Colors.blue, decoration: TextDecoration.underline, decorationStyle: TextDecorationStyle.dashed)),
               ),
             ],
           ),
           Row(
             children: [
               TextButton(
                 onPressed: () async {
                   // call this function on the dispose method of the screens.
                   await cancelCallback();
                 }, child:  const Text('Cancel CallBacks',
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
                    child: Text(deviceMessage),
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
                  Container(
                    margin: const EdgeInsets.all(8.0),
                    child: Text('Sleep Timings: $sleepTimings'),
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
      return const Center(child: CircularProgressIndicator());
    } else {
      if (deviceList.isNotEmpty) {
        return Container(
          margin: const EdgeInsets.symmetric(vertical: 2, horizontal: 2),
          //padding: const EdgeInsets.all(2.0),
          child: ListView.separated(
            shrinkWrap: true,
            scrollDirection: Axis.vertical,
            physics: const NeverScrollableScrollPhysics(),
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
      margin: const EdgeInsets.all(1.0),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(8.0),
        color: Colors.white,
        boxShadow: const [
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
        leading: const Icon(
          Icons.bluetooth_audio_outlined,
          color: Colors.black,
        ),
        title: Text(device.name),
        subtitle: Text(device.address),
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
    debugPrint('initResult $initState');
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
    debugPrint('fetchBluDevicesList $deviceList');

    if (deviceList.isNotEmpty) {
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
    debugPrint('connectDevice>> $startDeviceSearch');
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
       "raiseHandWakeUp": "true", //true or false -- send true to wake up bright light switch
     };
     String resultStatus = await _mobileSmartWatch.setUserParameters(userData);
     debugPrint('resultStatus>> $resultStatus');
     // resultStatus ==  SC_NOT_SUPPORTED  (device not supported) or SC_INIT (write command initiated)or SC_FAILURE
   }

  Future<void> disconnectDevice() async {
    bool deviceDisconnected = await _mobileSmartWatch.disconnectDevice();
    debugPrint('disconnectDevice>> $deviceDisconnected');
    setState(() {
     // deviceConnected = false;
      deviceMessage = "Device Disconnected";
    });
  }

   Future<void> syncOverAll() async {
     // String stepsStatus = await _mobileSmartWatch.syncStepsData();
     // debugPrint('syncStepsStatus>> $stepsStatus');
     String sleepStatus = await _mobileSmartWatch.syncSleepData();
     debugPrint('syncSleepStatus>> $sleepStatus');
   }

   Future<void> fetchBatteryNVersion() async {
     String batteryStatus = await _mobileSmartWatch.getBatteryStatus();
     String deviceVersion = await _mobileSmartWatch.getDeviceVersion();
     debugPrint('batteryStatus>> $batteryStatus');
     debugPrint('deviceVersion>> $deviceVersion');
   }

   Future<void> fetchSyncStepsData() async{
     String stepsStatus = await _mobileSmartWatch.syncStepsData();
     debugPrint('syncStepsStatus>> $stepsStatus');
   }

   Future<void> fetchAllJudgement() async{
     Map<String, dynamic> resultJudgeData = await _mobileSmartWatch.fetchAllJudgement();
     String status = resultJudgeData['status'].toString();
     if (status == SmartWatchConstants.SC_SUCCESS) {
       Map<String, dynamic> data = resultJudgeData['data'];

     }else if (status == SmartWatchConstants.SC_DISCONNECTED) {
       // device got diconnected.

     }else if (status == SmartWatchConstants.SC_FAILURE){
       // someting went wrong
     }
     debugPrint('resultJudgeData>> $resultJudgeData');
   }


   Future<void> fetchOverAllByDate() async{
     Map<String, dynamic> resultJudgeData = await _mobileSmartWatch.fetchOverAllByDate("20220110");
     String status = resultJudgeData['status'].toString();
     if (status == SmartWatchConstants.SC_SUCCESS) {
       Map<String, dynamic> data = resultJudgeData['data'];
       debugPrint('steps data>> ${data['steps']}');
       debugPrint('sleep data>> ${data['sleep']}');
       debugPrint('hr data>> ${data['hr']}');
       debugPrint('hr24 data>> ${data['hr24']}');
       debugPrint('bp data>> ${data['bp']}');
       debugPrint('temperature data>> ${data['temperature']}');

      // SmartStepsModel model  = new  SmartStepsModel.fromJson(data['steps']);
       List<SmartStepsModel> smartList = convertDataToList(data['steps']['data']);
       debugPrint('smartList data>> $smartList');

     }else if (status == SmartWatchConstants.SC_DISCONNECTED) {
       // device got diconnected.

     }else if (status == SmartWatchConstants.SC_FAILURE){
       // someting went wrong
     }
     debugPrint('resultOverAllDayData>> $resultJudgeData');
   }

   List<SmartStepsModel> convertDataToList(var json) {
    List<SmartStepsModel> smartList = [];
    if (json.length != 0) {
      json.forEach((element) {
        smartList.add(SmartStepsModel.fromJson(element));
      });
    }
    return smartList;
  }

  Future<void> fetchSyncSleepData() async{
     String stepsStatus = await _mobileSmartWatch.syncSleepData();
     debugPrint('syncSleepStatus>> $stepsStatus');
   }

   Future<void>  startBloodPressure() async {
     String startBPStatus = await _mobileSmartWatch.startBloodPressure();
     debugPrint('startBloodPressure>> $startBPStatus');
   }
   Future<void>  stopBloodPressure() async {
     String stopBPStatus = await _mobileSmartWatch.stopBloodPressure();
     debugPrint('stopBloodPressure>> $stopBPStatus');
   }
   Future<void> syncBloodPressure() async {
     String syncBPStatus = await _mobileSmartWatch.syncBloodPressure();
     debugPrint('syncBloodPressure>> $syncBPStatus');
   }
   Future<void> syncOxygen() async {
     String syncBPStatus = await _mobileSmartWatch.syncOxygenSaturation();
     debugPrint('syncOxygen>> $syncBPStatus');
   }
   Future<void> syncTemperature() async {
     String syncTempStatus = await _mobileSmartWatch.syncTemperature();
     debugPrint('syncTemperature>> $syncTempStatus');
   }

   Future<void> syncHeartRate() async {
     String syncBPStatus = await _mobileSmartWatch.syncRateData();
     debugPrint('syncHeartRate>> $syncBPStatus');
   }

   Future<void> startHR() async {
     String startBPStatus = await _mobileSmartWatch.startHR();
     debugPrint('startHR>> $startBPStatus');
   }
   Future<void> stopHR() async {
     String stopBPStatus = await _mobileSmartWatch.stopHR();
     debugPrint('stopHR>> $stopBPStatus');
   }

   Future<void> fetchStepsByDate() async{
     var tempStatus = await _mobileSmartWatch.fetchStepsByDate("20220105");

   }

   Future<void> fetchHeartRateByDate() async{
     var tempStatus = await _mobileSmartWatch.fetchHeartRateByDate("20220105");

   }
   Future<void> fetch24HourHRByDate() async{
     var tempStatus = await _mobileSmartWatch.fetch24HourHRByDate("20220105");

   }

   Future<void> fetchTemperatureByDate() async{
     var tempStatus = await _mobileSmartWatch.fetchTemperatureByDate("20220105");

   }

  Future<void> fetchSleepByDate() async {
    Map<String, dynamic> resultData = await _mobileSmartWatch.fetchSleepByDate("20220105");
    String status = resultData['status'];
    String total = resultData['total'];
    String light = resultData['light'];
    String deep = resultData['deep'];
    String awake = resultData['awake'];
    String beginTime = resultData['beginTime'];
    String endTime = resultData['endTime'];
    List<dynamic> responseData = resultData['data'];
    List<SmartSleepModel> sleepList =[];
    for (var data in responseData) {
      sleepList.add(SmartSleepModel.fromJson(data));
    }
    debugPrint("sleepList>> ${sleepList.length}");

    setState(() {
      sleepTimings = "Total: "+total+", Light: "+light+", deep: "+deep+", awake: "+awake+", beginTime: "+beginTime+", endTime: "+endTime;
    });
  }

  Future<void> fetchAllStepsData() async{
     var tempStatus = await _mobileSmartWatch.fetchAllStepsData();
   }

   Future<void> fetchAllSleepData() async{
     var tempStatus = await _mobileSmartWatch.fetchAllSleepData();
   }

   Future<void> fetchAllTemperatureData() async{
     var tempStatus = await _mobileSmartWatch.fetchAllTemperatureData();
   }

   Future<void> startTempTest() async{
     String tempStatus = await _mobileSmartWatch.testTempData();

   }

   Future<void> cancelCallback() async {

  }






/* Future<bool> checkAllPermissions() async {
    var location = await Permission.locationWhenInUse.status;
    var bluetooth = await Permission.bluetooth.status;

    debugPrint('location $location');
    debugPrint('bluetooth $bluetooth');

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