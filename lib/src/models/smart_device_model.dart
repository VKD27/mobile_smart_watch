part of mobile_smart_watch;

class SmartDeviceModel {
  //final String index;
  final String name;
  final String rssi;
  final String address;
  final String alias;
  final String deviceType;
  final String bondState;


  const SmartDeviceModel({
    //required this.index,
    required this.name,
    required this.rssi,
    required this.alias,
    required this.address,
    required this.deviceType,
    required this.bondState,

  });

  factory SmartDeviceModel.fromJson(Map<String, dynamic> data) => SmartDeviceModel(
   // index: data['index'].toString(),
    name: data['name'].toString(),
    rssi: data['rssi'].toString(),
    alias: data['alias'].toString(),
    address: data['address'].toString(),
    deviceType: data['type'].toString(),
    bondState: data['bondState'].toString(),
  );

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> formData = new Map<String, dynamic>();
   // formData['index'] = this.index;
    formData['name'] = this.name;
    formData['alias'] = this.alias;
    formData['address'] = this.address;
    formData['type'] = this.deviceType;
    formData['bondState'] = this.bondState;
    formData['rssi'] = this.rssi;
    return formData;
  }

/* @override
  // TODO: implement props
  List<Object> get props => [index, name, alias, address, type, bondState];

  @override
  bool get stringify => false;*/
}