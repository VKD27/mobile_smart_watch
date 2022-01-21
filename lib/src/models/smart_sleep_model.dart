part of mobile_smart_watch;

class SmartSleepModel {
  final String state;
  final String startTime;
  final String endTime;
  final String diffTime;

  final String startTimeNum;
  final String endTimeNum;
  final String diffTimeNum;

  const SmartSleepModel({
    required this.state,
    required this.startTime,
    required this.endTime,
    required this.diffTime,
    required this.startTimeNum,
    required this.endTimeNum,
    required this.diffTimeNum
  });

  factory SmartSleepModel.fromJson(Map<String, dynamic> data) => SmartSleepModel(
    state: data['state'].toString(),
    startTime: data['startTime'].toString(),
    endTime: data['endTime'].toString(),
    diffTime: data['diffTime'].toString(),
    startTimeNum: data['startTimeNum'].toString(),
    endTimeNum: data['endTimeNum'].toString(),
    diffTimeNum: data['diffTimeNum'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
    formData['state'] = this.state;
    formData['startTime'] = this.startTime;
    formData['endTime'] = this.endTime;
    formData['diffTime'] = this.diffTime;
    formData['startTimeNum'] = this.startTimeNum;
    formData['endTimeNum'] = this.endTimeNum;
    formData['diffTimeNum'] = this.diffTimeNum;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}