part of mobile_smart_watch;

class SmartTempModel {
  final String calender;
  final String time;
  final String type;
  final String inCelsius;
  final String inFahrenheit;
  final String startDate;  //yyyyMMddHHmmss

  const SmartTempModel({
    required this.calender,
    required this.time,
    required this.type,
    required this.inCelsius,
    required this.inFahrenheit,
    required this.startDate,
  });

  factory SmartTempModel.fromJson(Map<String, dynamic> data) => SmartTempModel(
    calender: data['calender'].toString(),
    time: data['time'].toString(),
    type: data['type'].toString(),
    inCelsius: data['inCelsius'].toString(),
    inFahrenheit: data['inFahrenheit'].toString(),
    startDate: data['startDate'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
    formData['calender'] = this.calender;
    formData['time'] = this.time;
    formData['type'] = this.type;
    formData['inCelsius'] = this.inCelsius;
    formData['inFahrenheit'] = this.inFahrenheit;
    formData['startDate'] = this.startDate;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}