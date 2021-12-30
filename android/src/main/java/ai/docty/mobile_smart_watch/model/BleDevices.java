package ai.docty.mobile_smart_watch.model;

public class BleDevices implements Comparable<BleDevices>{
	private String name;
	private String address, alias;
	private int rssi, deviceType, bondState, index;

	public BleDevices() {
	}

	public BleDevices(String name, String address, int rssi, int deviceType, int bondState, String alias) {
		setName(name);
		setAddress(address);
		setRssi(rssi);
		setDeviceType(deviceType);
		setBondState(bondState);
		setAlias(alias);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public int getBondState() {
		return bondState;
	}

	public void setBondState(int bondState) {
		this.bondState = bondState;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int compareTo(BleDevices bleDevices) {
		return bleDevices.getRssi()-this.getRssi();
	}
}
