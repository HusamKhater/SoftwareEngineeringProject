package model;

import java.util.Calendar;

enum SpotStatus {
	Busy, Available, Reserved, Unavailable
}

public class ParkingSlot {
	private String _carNumber;
	private SpotStatus _status;
	private Calendar _arrive;
	private Calendar _leave;

	public String getCarNumber() {
		return _carNumber;
	}

	public void setCarNumber(String _carNumber) {
		this._carNumber = _carNumber;
	}

	public SpotStatus getStatus() {
		return _status;
	}

	public void setStatus(SpotStatus _status) {
		this._status = _status;
	}

	public Calendar getArrive() {
		return _arrive;
	}

	public void setArrive(Calendar _arrive) {
		this._arrive = _arrive;
	}

	public Calendar getLeave() {
		return _leave;
	}

	public void setLeave(Calendar _leave) {
		this._leave = _leave;
	}

	// public ParkingSlot(String _carNumber, SpotStatus _status, Calendar
	// _arrive, Calendar _leave) {
	// super();
	// this._carNumber = _carNumber;
	//// this._status = _status;
	// this._arrive = _arrive;
	// this._leave = _leave;
	// this._status = SpotStatus.Available;
	//
	// }

	public ParkingSlot() {
		super();
		this._carNumber = "";
		this._status = SpotStatus.Available;
		_arrive = null;
		_leave = null;
	}

}
