package model;

public class BusinessSubscription {
	private String _carNumber;
	private String _activationCode;
	private String _username;
	
	public BusinessSubscription(String _carNumber, String _activationCode, String _username) {
		super();
		this._carNumber = _carNumber;
		this._activationCode = _activationCode;
		this._username = _username;
	}

	public String get_carNumber() {
		return _carNumber;
	}

	public void set_carNumber(String _carNumber) {
		this._carNumber = _carNumber;
	}

	public String get_activationCode() {
		return _activationCode;
	}

	public void set_activationCode(String _activationCode) {
		this._activationCode = _activationCode;
	}

	public String get_username() {
		return _username;
	}

	public void set_username(String _username) {
		this._username = _username;
	}

	


}
