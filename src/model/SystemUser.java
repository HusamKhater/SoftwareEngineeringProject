package model;

public class SystemUser {
	private String _username;
	private String _email;
	private int _workerID;
	private String _affiliation;
	private String _password;
	private String _firstName;
	private String _lastName;
	private String _rank;
	
	public SystemUser(String _username, String _email, int _workerID, String _affiliation, String _password,
			String _firstName, String _lastName, String _rank) {
		super();
		this._username = _username;
		this._email = _email;
		this._workerID = _workerID;
		this._affiliation = _affiliation;
		this._password = _password;
		this._firstName = _firstName;
		this._lastName = _lastName;
		this._rank = _rank;
	}

	public String get_username() {
		return _username;
	}

	public void set_username(String _username) {
		this._username = _username;
	}

	public String get_email() {
		return _email;
	}

	public void set_email(String _email) {
		this._email = _email;
	}

	public int get_workerID() {
		return _workerID;
	}

	public void set_workerID(int _workerID) {
		this._workerID = _workerID;
	}

	public String get_affiliation() {
		return _affiliation;
	}

	public void set_affiliation(String _affiliation) {
		this._affiliation = _affiliation;
	}

	public String get_password() {
		return _password;
	}

	public void set_password(String _password) {
		this._password = _password;
	}

	public String get_firstName() {
		return _firstName;
	}

	public void set_firstName(String _firstName) {
		this._firstName = _firstName;
	}

	public String get_lastName() {
		return _lastName;
	}

	public void set_lastName(String _lastName) {
		this._lastName = _lastName;
	}

	public String get_rank() {
		return _rank;
	}

	public void set_rank(String _rank) {
		this._rank = _rank;
	}
	
	
}
