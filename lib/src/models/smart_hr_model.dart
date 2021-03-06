part of mobile_smart_watch;

class SmartHRModel {
  final String calender;
  final String time;
  final String rate;

  const SmartHRModel({
    required this.calender,
    required this.time,
    required this.rate
  });

  factory SmartHRModel.fromJson(Map<String, dynamic> data) => SmartHRModel(
    calender: data['calender'].toString(),
    time: data['time'].toString(),
    rate: data['rate'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
    formData['calender'] = this.calender;
    formData['time'] = this.time;
    formData['rate'] = this.rate;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}