package model;

public class RegularRoutinelySubscription {
	
	private String _carNumber;
	private String _lotName;
	private String _name;
	private String leaveHour;
	private String _start;
	private String end;
	private boolean isFull;
	
	public RegularRoutinelySubscription(String _carNumber, String _lotName, String _name, String leaveHour,
			String _start, String end, boolean isFull) {
		super();
		this._carNumber = _carNumber;
		this._lotName = _lotName;
		this._name = _name;
		this.leaveHour = leaveHour;
		this._start = _start;
		this.end = end;
		this.isFull = isFull;
		
		
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

	public String getLeaveHour() {
		return leaveHour;
	}

	public void setLeaveHour(String leaveHour) {
		this.leaveHour = leaveHour;
	}

	public String get_start() {
		return _start;
	}

	public void set_start(String _start) {
		this._start = _start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public boolean isFull() {
		return isFull;
	}

	public void setFull(boolean isFull) {
		this.isFull = isFull;
	}
	
	
}
