part of mobile_smart_watch;

class SmartStepsModel {
  final String time;
  final String step;

  const SmartStepsModel({
    required this.step,
    required this.time,
  });

  factory SmartStepsModel.fromJson(Map<String, dynamic> data) => SmartStepsModel(
    step: data['step'].toString(),
    time: data['time'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
    formData['step'] = this.step;
    formData['time'] = this.time;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}