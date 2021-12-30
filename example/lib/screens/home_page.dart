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

  @override
  void initState() {
   // smartCare = SmartCare();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        title: Text('Device Care'),
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
                       child: Text('Initialize & Search Devices',
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
                       child: Text('Disconnect Device', style: TextStyle(color: Colors.white)),
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
           //Text('No Devices Found'),
           Container(
             margin: const EdgeInsets.all(8.0),
             child: showDeviceContainer(smartDevicesList),
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


      setState(() {
        deviceConnected = startDeviceSearch;
      });
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
      deviceConnected = deviceDisconnected;
    });
  }

   Future<void> fetchBatteryNVersion() async {
     String batteryStatus = await _mobileSmartWatch.getBatteryStatus();
     print('batteryStatus>> $batteryStatus');
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