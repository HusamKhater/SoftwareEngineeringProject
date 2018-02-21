package model;

import java.util.Calendar;

public class ParkingReservation {
	
	private String  _carNumber;
	private String  _lotName;
	private String _name;
	private Calendar _start;
	private Calendar _end;
	private double cost;
	
	public ParkingReservation(String _carNumber, String _lotName, String _name, Calendar _start, Calendar _end,
			double cost) {
		super();
		this._carNumber = _carNumber;
		this._lotName = _lotName;
		this._name = _name;
		this._start = _start;
		this._end = _end;
		this.cost = cost;
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

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
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

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	
	
	
		
}
