package model;

import java.time.LocalDate;
import java.util.Calendar;

public class OccasionalParking {

	
	String _name;
	Calendar leaveHour;
	Calendar _start;
	Calendar _end;
	//json.put("type", "o");
	//json.put("activated", 1);
	String _carNumber;
	String _lotName;
	
	public OccasionalParking(String _name, Calendar leaveHour, Calendar _start, Calendar _end, String _carNumber,
			String _lotName) {
		super();
		this._name = _name;
		this.leaveHour = leaveHour;
		this._start = _start;
		this._end = _end;
		this._carNumber = _carNumber;
		this._lotName = _lotName;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public Calendar getLeaveHour() {
		return leaveHour;
	}

	public void setLeaveHour(Calendar leaveHour) {
		this.leaveHour = leaveHour;
	}

	public Calendar get_start() {
		return _start;
	}

	public void set_start(Calendar _start) {
		this._start = _start;
	}

	public Calendar get_end() {
		return _end;
	}

	public void set_end(Calendar _end) {
		this._end = _end;
	}

	public String get_carNumber() {
		return _carNumber;
	}

	public void set_carNumber(String _carNumber) {
		this._carNumber = _carNumber;
	}

	public String get_lotName() {
		return _lotName;
	}

	public void set_lotName(String _lotName) {
		this._lotName = _lotName;
	}
	
	
}
