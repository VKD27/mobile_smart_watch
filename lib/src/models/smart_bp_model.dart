part of mobile_smart_watch;

class SmartBPModel {
  final String calender;
  final String time;
  final String high;
  final String low;
  final String dateTime;

  const SmartBPModel({
    required this.calender,
    required this.time,
    required this.dateTime,
    required this.high,
    required this.low
  });

  factory SmartBPModel.fromJson(Map<String, dynamic> data) => SmartBPModel(
    calender: data['calender'].toString(),
    time: data['time'].toString(),
    dateTime: data['dateTime'].toString(),
    high: data['high'].toString(),
    low: data['low'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = <String, dynamic>{};
    formData['calender'] = calender;
    formData['time'] = time;
    formData['dateTime'] = dateTime;
    formData['high'] = high;
    formData['low'] = low;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}