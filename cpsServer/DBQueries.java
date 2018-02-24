package cpsServer;

/**
 * 
 * @author Baselscs
 * This class holds constatnts used in the schduling processes
 * To-Do: All the DB queries should be here.
 */
public final class DBQueries {

	public static final String GET_TODAYS_RESERVATIONS = "SELECT rid,carNumber,lotName,T1.username,start,end,email FROM "
			+ "  (SELECT * FROM Reservations where rType = 'r' and status = 1 and activated = 'false' and start >= ? and start <= ?) T1 "
			+ "INNER JOIN "
			+ " (SELECT username,email FROM USERS) T2 "
			+ " ON T1.username=T2.username";
	
	public static final String GET_NEARLY_ENDING_SUBSCRIPTIONS_F = "SELECT fsid,carNumber,T1.username,start,end,email FROM "
			+ "  (SELECT * FROM FullSubscription where status = 1 and end < ? and end > ? and notified = 'false') T1 "
			+ "INNER JOIN "
			+ " (SELECT username,email FROM USERS) T2 "
			+ " ON T1.username=T2.username";
	
	public static final String GET_NEARLY_ENDING_SUBSCRIPTIONS_R = "SELECT rsid,carNumber,lotName,T1.username,start,end,email FROM "
			+ "  (SELECT * FROM RoutineSubscription where status = 1 and end < ? and end > ? and business = 'false' and notified = 'false') T1 "
			+ "INNER JOIN "
			+ " (SELECT username,email FROM USERS) T2 "
			+ " ON T1.username=T2.username";
	
	public static final String UPDATE_NOTIFIED_SUBSCRIPTIONS_R = "Update RoutineSubscription set notified = true where ";
	
	public static final String UPDATE_NOTIFIED_SUBSCRIPTIONS_F = "Update FullSubscription set notified = true where ";
	
	public static final String UPDATE_NOTIFIED_SUBSCRIPTIONS_B = "Update BusinessSubscription set notified = true where ";

	public static final String GET_EXCEEDED_SUBSCRIPTIONS_F = "SELECT email,T1.username,lastEntry FROM "
			+ "  (SELECT * FROM FullSubscription where status = 1 and isParking = true and lastEntry <= ?) T1 "
			+ "INNER JOIN "
			+ " (SELECT username,email FROM USERS) T2 "
			+ " ON T1.username=T2.username";

	public static final String GET_ALL_RESERVATIONS = "SELECT * FROM Reservations where start >= ? and start <= ? ";
	//public static final String GET_ALL_RESERVATIONS = "SELECT * FROM Reservations where rType != 'c' and start >= ? and start <= ? and activated = true ";

	public static final String GET_NEARLY_ENDING_SUBSCRIPTIONS_B = "SELECT T1.username,email,end,company,subCode FROM "
			+ "  (SELECT * FROM BusinessSubscription where status = 1 and end < ? and end > ? and notified = 'false') T1 "
			+ "INNER JOIN "
			+ " (SELECT username,email FROM USERS) T2 "
			+ " ON T1.username=T2.username";

	
	
	
	
}
