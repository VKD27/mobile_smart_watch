import 'package:flutter/material.dart';

class Global {
  //static String baseUrl = "https://apidevice.docty.mobi/api/";

/*  static void setValidateDeviceConnectivity(dynamic res, BuildContext context, bool isDeviceConnected) {

    String status = res['status'].toString();
    if (status.isNotEmpty) {
      // connected && disconnected handle
      switch (status) {
        case SpiroSDKConstants.CONNECT_UNSUPPORT_DEVICETYPE:
          showAlertDialog(context, "Connection", "You device is Unsupportable");
          break;

        case SpiroSDKConstants.CONNECT_UNSUPPORT_BLUETOOTHTYPE:
          showAlertDialog(context, "Connection", "You bluetooth is Unsupportable");
          break;

        case SpiroSDKConstants.CONNECT_CONNECTING:
          break;

        case SpiroSDKConstants.CONNECT_CONNECTED:
          if (isDeviceConnected) {
            navigateToDeviceOperations();
          }else{
            showAlertDialog(context, "Connection", "Could not able to connect with this device.");
          }
          break;

        case SpiroSDKConstants.CONNECT_DISCONNECTED:
          showDisconnectedAlertDialog(context, "Connection", "You device is disconnected.");
          *//*if (!isDeviceConnected) {
            Navigator.pop(context);
          }*//*
          break;
      }
    }
  }

  static void showDisconnectedAlertDialog(BuildContext context, String title, String message) {
    showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text(title),
            content: Text(message),
            actions: [
              TextButton(
                style: TextButton.styleFrom(
                  // primary: Color(0xFF6200EE),
                  primary: Colors.teal,
                ),
                // textColor: Color(0xFF6200EE),
                onPressed: () {

                  SpiroSdk  spiroSdk = SpiroSdk({});
                  spiroSdk.getDisposeAll();

                  Navigator.of(context).pop();
                  Get.offAll(HomePage());
                  // navigate to the home
                  // disconnect the sdk connection
                },
                child: Text('OK'),
              )
            ],
          );
        });
  }*/

  static void showAlertDialog(BuildContext context, String title, String message) {
    showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialog(
            title: Text(title),
            content: Text(message),
            actions: [
              TextButton(
                style: TextButton.styleFrom(
                  // primary: Color(0xFF6200EE),
                  primary: Colors.teal,
                ),
                // textColor: Color(0xFF6200EE),
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: Text('OK'),
              )
            ],
          );
        });
  }
/*
  static void navigateToDeviceOperations() {
    Get.to(()=>DeviceOperation());
  }

  static String getDateBind(String date) {
    // input date format 2021-9-6 19:41:52
    if (date.isNotEmpty ) {
      // if you want to parse you can show it in a parse it
      return date;
    }else{
      return '';
    }
  }

  static showWaiting(BuildContext context, bool cancelable) {
    AlertDialog alert = AlertDialog(
        content: Container(
            height:  50,
            child: Column(
              children: <Widget>[
                CircularProgressIndicator(),
              ],
            ),
        ),
    );

    showDialog(
      barrierDismissible: Platform.isIOS ? true : cancelable,
      context: context,
      builder: (BuildContext context) {
        return alert;
      },
    );
  }*/

}
