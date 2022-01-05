part of mobile_smart_watch;

class SmartBPModel {
  final String calender;
  final String time;
  final String high;
  final String low;

  const SmartBPModel({
    required this.calender,
    required this.time,
    required this.high,
    required this.low
  });

  factory SmartBPModel.fromJson(Map<String, dynamic> data) => SmartBPModel(
    calender: data['calender'].toString(),
    time: data['time'].toString(),
    high: data['high'].toString(),
    low: data['low'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
    formData['calender'] = this.calender;
    formData['time'] = this.time;
    formData['high'] = this.high;
    formData['low'] = this.low;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}