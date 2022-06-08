part of mobile_smart_watch;

class SmartOxygenModel {
  final String calender;
  final String time;
  final String value;
  final String startDate;  //yyyyMMddHHmmss

  const SmartOxygenModel({
    required this.calender,
    required this.time,
    required this.value,
    required this.startDate,
  });

  factory SmartOxygenModel.fromJson(Map<String, dynamic> data) => SmartOxygenModel(
    calender: data['calender'].toString(),
    time: data['time'].toString(),
    value: data['value'].toString(),
    startDate: data['startDate'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
    formData['calender'] = this.calender;
    formData['time'] = this.time;
    formData['value'] = this.value;
    formData['startDate'] = this.startDate;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}