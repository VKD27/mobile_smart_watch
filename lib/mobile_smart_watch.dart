library mobile_smart_watch;

import 'dart:async';
import 'dart:convert';
import 'dart:core';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';
import 'dart:async';


part 'src/mobile_smart_watch.dart';
part 'src/util/smart_watch_constants.dart';
part 'src/models/smart_device_model.dart';


/*import 'dart:async';

import 'package:flutter/services.dart';

class MobileSmartWatch {
  static const MethodChannel _channel = const MethodChannel('mobile_smart_watch');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}*/
