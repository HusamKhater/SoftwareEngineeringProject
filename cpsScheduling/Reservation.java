package cpsScheduling;

/**
 * Model class : Reservation, used in scheduling reminders
 */
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Reservation {
	
	int rid;
	String carNumber;
	String lotName;
	String username;
	Date start;
	Date end;
	String email;
	
	public static HashMap<Integer ,Reservation> res = new HashMap<>();
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getCarNumber() {
		return carNumber;
	}
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public String getLotName() {
		return lotName;
	}
	public void setLotName(String lotName) {
		this.lotName = lotName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	
	/**
	 * @param rid
	 * @param carNumber
	 * @param lotName
	 * @param username
	 * @param start
	 * @param end
	 * @param email
	 */
	public Reservation(int rid, String carNumber, String lotName, String username, Date start, Date end ,String email) {
		//super();
		this.rid = rid;
		this.carNumber = carNumber;
		this.lotName = lotName;
		this.username = username;
		this.start = start;
		this.end = end;
		
		this.email = email;
	}
	
	public Boolean isLate() {
		
		Date now = new Date();
		
		long diffInMillies = TimeUnit.MILLISECONDS.toMillis(now.getTime()) - TimeUnit.MILLISECONDS.toMillis(this.start.getTime());
		long diff =TimeUnit.MILLISECONDS.toSeconds(diffInMillies);
		
		if(diff > 0)
			return true;
		
		return false;
	}
	@Override
	public String toString() {
		return "Reservation [rid=" + rid + ", carNumber=" + carNumber + ", lotName=" + lotName + ", username="
				+ username + ", start=" + start + ", end=" + end + ", email=" + email + "]";
	}
	
	
	
	
	
	
	
	
}
