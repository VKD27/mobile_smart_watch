part of mobile_smart_watch;

const _channel = MethodChannel(SmartWatchConstants.SMART_CALLBACK);
typedef MultiUseCallback = void Function(dynamic msg);
typedef CancelListening = void Function();

Map<String, MultiUseCallback> _callbacksById = <String, void Function(dynamic)> {};

Future<void> _methodCallHandler(MethodCall call) async {
  print('methodCallHandler method: ${call.method}');
  print('methodCallHandler argument : ${jsonDecode(call.arguments)}');
  dynamic callMap = jsonDecode(call.arguments);
  print('methodCallHandler callMap : ${callMap}');
}

Future<CancelListening> startListening(MultiUseCallback callback, String callbackName) async {

  _channel.setMethodCallHandler(_methodCallHandler);

  _callbacksById[callbackName] = callback;

  await _channel.invokeMethod(SmartWatchConstants.START_LISTENING, callbackName);

  return () {
    _channel.invokeMethod(SmartWatchConstants.STOP_LISTENING, callbackName);
    _callbacksById.remove(callbackName);
  };
}
