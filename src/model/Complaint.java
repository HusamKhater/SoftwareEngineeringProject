package model;

import java.util.Calendar;

public class Complaint {
	
	private String _name;
	private String  _email;
	private String _date;
	private String _carNumber;
	private String _lotName;
	private String _orderId;
	private String _complaint; 
	private Calendar _cal;
	
	public Complaint(String _name, String _email, String _date, String _carNumber, String _lotName, String _orderId,
			String _complaint, Calendar _cal) {
		super();
		this._name = _name;
		this._email = _email;
		this._date = _date;
		this._carNumber = _carNumber;
		this._lotName = _lotName;
		this._orderId = _orderId;
		this._complaint = _complaint;
		this._cal = _cal;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_email() {
		return _email;
	}

	public void set_email(String _email) {
		this._email = _email;
	}

	public String get_date() {
		return _date;
	}

	public void set_date(String _date) {
		this._date = _date;
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

	public String get_orderId() {
		return _orderId;
	}

	public void set_orderId(String _orderId) {
		this._orderId = _orderId;
	}

	public String get_complaint() {
		return _complaint;
	}

	public void set_complaint(String _complaint) {
		this._complaint = _complaint;
	}

	public Calendar get_cal() {
		return _cal;
	}

	public void set_cal(Calendar _cal) {
		this._cal = _cal;
	}
	
	
		

}
