package model;

public class User {
	private String _username;
	private String _email;
	private String _password;
	private String _firstName;
	private String _lastName;
	private String _type;
	private double _balance;
	private String _company;
	
	public User(String username, String email, String password, String firstName, 
			String lastName, String type, int balance, String company) {
		super();
		this._username = username;
		this._email = email;
		this._password = password;
		this._firstName = firstName;
		this._lastName = lastName;
		this._type = type;
		_balance = balance;
		_company = company;
	}
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		this._username = username;
	}
	public String getEmail() {
		return _email;
	}
	public void setEmail(String email) {
		this._email = email;
	}
	public String getPassword() {
		return _password;
	}
	public void setPassword(String password) {
		this._password = password;
	}
	public String getFirstName() {
		return _firstName;
	}
	public void setFirstName(String firstName) {
		this._firstName = firstName;
	}
	public String getLastName() {
		return _lastName;
	}
	public void setLastName(String lastName) {
		this._lastName = lastName;
	}
	public String getType() {
		return _type;
	}
	public void setType(String type) {
		this._type = type;
	}
	public double getBalance() {
		return _balance;
	}
	public void setBalance(double d) {
		this._balance = d;
	}
	public String getCompnay() {
		return _company;
	}
	public void setCompany(String type) {
		this._company = type;
	}
	
	

}
