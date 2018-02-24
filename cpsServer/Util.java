package cpsServer;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cpsScheduling.Reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class Util {
	
	private static Connection dbcon;
	public static java.text.SimpleDateFormat sdf = 
		     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void initializeConnection() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbcon = DriverManager.getConnection("jdbc:mysql://softengproject.cspvcqknb3vj.eu-central-1.rds.amazonaws.com/chevron_skink_schema","chevron_skink","C.b<8]jJhR{4J/kS");  

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Connection getConnection(){
		return dbcon;
	}
	
	
	public static void closeConnection(){
		try {
			dbcon.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param a - username
	 * @param b - email
	 * @param c - password
	 * @param d - firstName
	 * @param e - lastName
	 * @param f - type
	 * @param g - balance
	 * @param h - company
	 * 
	 * @return Json object that represent a user info
	 */
	public static JSONObject constructUser(String a,String b,String c,String d,String e,String f,int g, String h){
		try {
			return new JSONObject().put("username",a)
					 .put("email", b)
					 .put("password", c)
					 .put("firstName", d)
					 .put("lastName", e)
					 .put("type", f)
					 .put("balance", g)
					 .put("company", h);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Utility function that used in each servlet in the server, used to get the request info from the POST request
	 * @param request - http request
	 * @return String
	 */
	public static String getRequestJson(HttpServletRequest request){
		StringBuffer req = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = request.getReader();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line = null;
		
		try {
			while((line = reader.readLine())!=null){
				req.append(line);
			}
		
		reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return req.toString();
	}

	/**
	 * Utility function for constructing a request upon failure
	 * @param string - the message returned to the client
	 * @return json object that contains a response upon failure with message
	 */
	public static JSONObject badRequest(String string) {
		JSONObject json = new JSONObject();
		try {
			json.put("result", false)
			.put("info", string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * Utility function that constructs a response upon failure WITHOUT a message
	 * @return json object that contains a response upon failure with message
	 */
	public static JSONObject _false(){
		JSONObject json = new JSONObject();
		try {
			json.put("result", false);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * Utility function that returns true upon success
	 * @return json object that contains a response upon success
	 */
	public static String success() {
		JSONObject json = new JSONObject();
		try {
			json.put("result", true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json.toString();
	}
	
	/**
	 * Utility function that logs the beginning of a service usage
	 * @param string - message upon using a service
	 */
	public static void log_start(String string) {
		
		System.out.println("$:> " + string );
	}
	

	/**
	 * Utility function that logs the end of a service usage
	 * @param string message upon finishing using a service
	 */
	public static void log_end(String string) {
		
		System.out.println("$:> " + string );
	}
	
	
	
	/**
	 * Utility function that generates a code for each business subscription
	 * When triggering a business subscription, a code is generated that all participants of the subscription can use 
	 * in order to activate their subscription.
	 * @param input  - company name + number of old subscriptions for that company in order to prevent similar codes in the sql table
	 * @return String - code for subscription
	 */
	public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	
	/**
	 * 
	 * @param a - workerID
	 * @param b - username
	 * @param c - email
	 * @param d - password
	 * @param e - firstName
	 * @param f - lastName
	 * @param g - rank (position can be A(CEO),B(Lot Manager),C(Customer service),D(Lot Worker))
	 * @param h - affiliation (to which lot the user belongs)
	 * 
	 * @return Json object that represent a system user info
	 */
	public static JSONObject constructSystemUser(int a, String b, String c, String d,
			String e, String f, String g, String h) {
		try {
			return new JSONObject()
					.put("workerID",a)
					.put("username", b)
					.put("email", c)
					.put("password", d)
					.put("firstName", e)
					.put("lastName", f)
					.put("rank", g)
					.put("affiliation", h);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Utility function that convert Long ArrayList to long array
	 * used in calculating overlapping orders upon reservation/subscription
	 * @param a - ArrayList of Longs
	 * @return array of longs
	 */
	public static long[] LTol(ArrayList<Long> a){
		long[] aa = new long[a.size()];
		for(int i = 0; i < aa.length ; i++){
			aa[i] = a.get(i).longValue();
		}
		return aa;
	}
	
	/**
	 * Utility function that returns the Lots of the company
	 * @return JSONArray that contains al the ParkingLots of the company
	 */
	public static JSONArray getLots(){
		String query = "Select * from Lots ";
		JSONArray lots = new JSONArray();
		Connection con = Util.getConnection();
		//String query2 = "SELECT * FROM CpsCosts ORDER BY CpsCosts.index DESC LIMIT 1;";
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				//JSONObject res = 
				lots.put(
						new JSONObject()
						.put("lotName", rs.getString(1))
						.put("width", rs.getInt(2))
						.put("location", rs.getString(3))
						
				);
			}
			rs.close();
			
			stmt.close();
			//return lots;
		}catch(Exception e){
			e.printStackTrace();
		}
		return lots;
	}

	/**
	 * Utility function, given start date and end date, this function returns all the valid reservations between these two dates
	 * @param start - Date
	 * @param end - Date
	 * @return ArrayList of Reservations
	 */
	public static ArrayList<Reservation> getReservationsBetween(Date start, Date end) {
		java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String _start = sdf.format(start),_end = sdf.format(end);
		String query = DBQueries.GET_TODAYS_RESERVATIONS;//"Select * from Reservations where rType = 'r' and status = 1 and activated = 'false' and start >= ? and start <= ?";
		ArrayList<Reservation> _reservations = new ArrayList<Reservation>();
		Connection con = Util.getConnection();
		//String query2 = "SELECT * FROM CpsCosts ORDER BY CpsCosts.index DESC LIMIT 1;";
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, _start);
			stmt.setString(2, _end);
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()){
				_reservations.add(new Reservation
						(
							rs.getInt(1), 
							rs.getString(2), 
							rs.getString(3), 
							rs.getString(4), 
							sdf.parse(rs.getString(5)),
							sdf.parse(rs.getString(6)),
							rs.getString(7)
						)
				);
			}
			rs.close();
			
			stmt.close();
			//return lots;
		}catch(Exception e){
			e.printStackTrace();
		}
		return _reservations;

	}
	
	/**
	 * Utility function, given start date and end date, this function returns ALL (valid and expired) reservations between these two dates
	 * @param start
	 * @param end
	 * @return
	 * @throws JSONException
	 */
	/*public static JSONObject getAllReservationsBetween(Date start, Date end) throws JSONException {
		java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String _start = sdf.format(start),_end = sdf.format(end);
		String query = DBQueries.GET_ALL_RESERVATIONS;
		JSONArray actualized = new JSONArray();
		JSONArray canceled = new JSONArray();
		JSONArray late = new JSONArray();
		Connection con = Util.getConnection();
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, _start);
			stmt.setString(2, _end);
			//System.out.println("!@#$%&&&&****" + stmt.toString());
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()){
				if(rs.getString(9).equals("c")){
					canceled.put(new JSONObject()
							.put("rid", rs.getInt(1))
							.put("carNumber", rs.getString(2))
							.put("lotName", rs.getString(3))
							.put("username",rs.getString(4))
							.put("start", rs.getString(5))
							.put("end", rs.getString(6))
							.put("status", rs.getString(7))
							.put("activated", rs.getBoolean(8))
							.put("type",rs.getString(9))
							
							);
				}else{
					actualized.put(new JSONObject()
							.put("rid", rs.getInt(1))
							.put("carNumber", rs.getString(2))
							.put("lotName", rs.getString(3))
							.put("username",rs.getString(4))
							.put("start", rs.getString(5))
							.put("end", rs.getString(6))
							.put("status", rs.getString(7))
							.put("activated", rs.getBoolean(8))
							.put("type",rs.getString(9))
							
							);
					if(isLate((sdf.parse(rs.getString(5))),(sdf.parse(rs.getString(11))))){
						late.put(new JSONObject()
								.put("rid", rs.getInt(1))
								.put("carNumber", rs.getString(2))
								.put("lotName", rs.getString(3))
								.put("username",rs.getString(4))
								.put("start", rs.getString(5))
								.put("end", rs.getString(6))
								.put("status", rs.getString(7))
								.put("activated", rs.getBoolean(8))
								.put("type",rs.getString(9))
								
								);
					}
				}
			}
			rs.close();
			
			stmt.close();
			//return lots;
		}catch(Exception e){
			e.printStackTrace();
		}
		return new JSONObject().put("canceled", canceled).put("actualized",actualized).put("late", late);

	}
*/
	
	/**
	 * Utility function used for checking if an order is late from being activated
	 * @param start
	 * @param entered
	 * @return boolean
	 */
	private static boolean isLate(Date start, Date entered) {
		long diffInMillies = TimeUnit.MILLISECONDS.toMillis(start.getTime()) - TimeUnit.MILLISECONDS.toMillis(entered.getTime());
		long diff =TimeUnit.MILLISECONDS.toMinutes(diffInMillies);
		
		return diff >= 5;
	}

	/**
	 * Utility function used upon reminding the nearly ending full/routine subscriptions
	 * @return JSONArray
	 */
	@SuppressWarnings("deprecation")
	public static JSONArray getNearlyEndingSubscriptions(){
		
		
		
		String query = DBQueries.GET_NEARLY_ENDING_SUBSCRIPTIONS_F;
		
		String query2 = DBQueries.GET_NEARLY_ENDING_SUBSCRIPTIONS_R;
		
		JSONArray subscriptions = new JSONArray();
		
		Connection con = Util.getConnection();
		
		Date now = new Date();
		Date expiration = new Date();
		expiration.setDate(now.getDate() + 7);
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query2);
			stmt.setString(1, sdf.format(expiration));
			stmt.setString(2, sdf.format(now));
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()){
				subscriptions.put(new JSONObject()
						.put("id", rs.getInt(1))
						.put("carNumber",rs.getString(2))
						.put("lotName",rs.getString(3))
						.put("username",rs.getString(4))
						.put("start",sdf.format(sdf.parse(rs.getString(5))))
						.put("end",sdf.format(sdf.parse(rs.getString(6))))
						.put("email",rs.getString(7))
						.put("type","R")
						);
			}
			rs.close();
			stmt.close();
			
			stmt = con.prepareStatement(query);
			stmt.setString(1, sdf.format(expiration));
			stmt.setString(2, sdf.format(now));
			rs = stmt.executeQuery();
			
			
			while(rs.next()){
				subscriptions.put(new JSONObject()
						.put("id", rs.getInt(1))
						.put("carNumber",rs.getString(2))
						.put("username",rs.getString(3))
						.put("start",sdf.format(sdf.parse(rs.getString(4))))
						.put("end",sdf.format(sdf.parse(rs.getString(5))))
						.put("email",rs.getString(6))
						.put("type","F")
						);
			}
			rs.close();
			stmt.close();
			
			updatedNotified(now,expiration,subscriptions);
		}catch(Exception e){
			e.printStackTrace();
		}
		return subscriptions;

	}

	/**
	 * Utility function that used right after using getNearlyEndingSubscriptions function in order to prevent sending more than one notification
	 * @param now
	 * @param expiration 
	 * @param subscriptions
	 */
	private static void updatedNotified(Date now, Date expiration, JSONArray subscriptions) {
		String routine = DBQueries.UPDATE_NOTIFIED_SUBSCRIPTIONS_R;
		String full = DBQueries.UPDATE_NOTIFIED_SUBSCRIPTIONS_F;
		String business = DBQueries.UPDATE_NOTIFIED_SUBSCRIPTIONS_B;
		if(subscriptions.length() == 0){
			System.out.println("Nothing to be updated as notified");
			return;
		}
		for(int i = 0; i < subscriptions.length(); i++){
			try {
				JSONObject obj = subscriptions.getJSONObject(i);
				if(obj.getString("type").equals("F")){
					if(full.contains("fsid")){
						full = full + " Or fsid = " + obj.getInt("id");
					}
					else{
						full = full + " fsid = " + obj.getInt("id");
					}
				}
				else if(obj.getString("type").equals("R")){
					if(routine.contains("rsid")){
						routine = routine + " Or rsid = " + obj.getInt("id");
					}
					else{
						routine = routine + " rsid = " + obj.getInt("id");
					}
				}else if(obj.getString("type").equals("B")){
					if(business.contains("subCode")){
						business = business + " Or subCode = " + obj.getString("id");
					}
					else{
						business = business + " subCode = " + obj.getString("id");
					}
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Connection con = getConnection();
		try {
			PreparedStatement stmt;
			if(routine.contains("rsid")){
				stmt = con.prepareStatement(routine);
				
				
				System.out.println(stmt.toString());
				stmt.executeUpdate();
				stmt.close();
			}
			
			if(full.contains("fsid")){
				stmt = con.prepareStatement(full);
				System.out.println(stmt.toString());

				stmt.executeUpdate();
				stmt.close();
			}
			if(business.contains("subCode")){
				stmt = con.prepareStatement(business);
				System.out.println(stmt.toString());

				stmt.executeUpdate();
				stmt.close();
			}
			
			
			System.out.println("Updated notified");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Utility function used upon reminding full subscription customers that has been more than 16 days in the Lot
	 * @return JSONArray
	 */
	@SuppressWarnings("deprecation")
	public static JSONArray getExceededSubscriptions() {
	
		String query = DBQueries.GET_EXCEEDED_SUBSCRIPTIONS_F;
		
		
		JSONArray subscriptions = new JSONArray();
		
		Connection con = Util.getConnection();
		
		Date now = new Date();
		Date expiration = new Date();
		expiration.setDate(now.getDate() - 14);
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, sdf.format(now));
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()){
				subscriptions.put(new JSONObject()
						.put("email",rs.getString(1))
						.put("username", rs.getString(2))
						.put("lastEntry", sdf.format(sdf.parse(rs.getString(3))))
						
						);
			}
			rs.close();
			stmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return subscriptions;

	
	}

	/**
	 * Utility function used upon reminding the nearly ending business subscriptions
	 * @return JSONArray
	 */
	@SuppressWarnings("deprecation")
	public static JSONArray getNearlyEndingBusinessSubscriptions() {
		String query = DBQueries.GET_NEARLY_ENDING_SUBSCRIPTIONS_B;
		
		
		
		JSONArray subscriptions = new JSONArray();
		
		Connection con = Util.getConnection();
		
		Date now = new Date();
		Date expiration = new Date();
		expiration.setDate(now.getDate() + 7);
		
		PreparedStatement stmt;
		try {
			ResultSet rs;
			stmt = con.prepareStatement(query);
			stmt.setString(1, sdf.format(expiration));
			stmt.setString(2, sdf.format(now));
			rs = stmt.executeQuery();
			
			//username,email,end,company,id
			while(rs.next()){
				subscriptions.put(new JSONObject()
						.put("id", rs.getString(5))
						.put("username",rs.getString(1))
						.put("end",sdf.format(sdf.parse(rs.getString(3))))
						.put("email",rs.getString(2))
						.put("type","B")
						.put("company", rs.getString(4))
						);
			}
			updatedNotified(now, expiration, subscriptions);
			rs.close();
			stmt.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return subscriptions;

	}

	/**
	 * Utility function, given start date and end date, this function returns ALL (valid and expired) reservations between these two dates and by a specific ParkingLot name
	 * @param start - Date
	 * @param end - Date
	 * @param lotName - name of the ParkingLot
	 * @return JSONObject
	 * @throws JSONException
	 */
	public static JSONObject getAllReservationsBetweenByLotName(Date start, Date end , String lotName) throws JSONException {
		java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String _start = sdf.format(start),_end = sdf.format(end);
		String query = DBQueries.GET_ALL_RESERVATIONS + " and lotName = ?";//"Select * from Reservations where rType = 'r' and status = 1 and activated = 'false' and start >= ? and start <= ?";
		JSONArray actualized = new JSONArray();
		JSONArray canceled = new JSONArray();
		JSONArray late = new JSONArray();
		Connection con = Util.getConnection();
		//String query2 = "SELECT * FROM CpsCosts ORDER BY CpsCosts.index DESC LIMIT 1;";
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, _start);
			stmt.setString(2, _end);
			stmt.setString(3, lotName);
			//System.out.println("!@#$%&&&&****" + stmt.toString());
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next()){
				if(rs.getString(9).equals("c")){
					canceled.put(new JSONObject()
							.put("rid", rs.getInt(1))
							.put("carNumber", rs.getString(2))
							.put("lotName", rs.getString(3))
							.put("username",rs.getString(4))
							.put("start", rs.getString(5))
							.put("end", rs.getString(6))
							.put("status", rs.getString(7))
							.put("activated", rs.getBoolean(8))
							.put("type",rs.getString(9))
							
							);
				}else{
					actualized.put(new JSONObject()
							.put("rid", rs.getInt(1))
							.put("carNumber", rs.getString(2))
							.put("lotName", rs.getString(3))
							.put("username",rs.getString(4))
							.put("start", rs.getString(5))
							.put("end", rs.getString(6))
							.put("status", rs.getString(7))
							.put("activated", rs.getBoolean(8))
							.put("type",rs.getString(9))
							
							);
					if(isLate((sdf.parse(rs.getString(5))),(sdf.parse(rs.getString(11))))){
						late.put(new JSONObject()
								.put("rid", rs.getInt(1))
								.put("carNumber", rs.getString(2))
								.put("lotName", rs.getString(3))
								.put("username",rs.getString(4))
								.put("start", rs.getString(5))
								.put("end", rs.getString(6))
								.put("status", rs.getString(7))
								.put("activated", rs.getBoolean(8))
								.put("type",rs.getString(9))
								
								);
					}
				}
			}
			rs.close();
			
			stmt.close();
			//return lots;
		}catch(Exception e){
			e.printStackTrace();
		}
		return new JSONObject().put("canceled", canceled).put("actualized",actualized).put("late", late);

	}
}
