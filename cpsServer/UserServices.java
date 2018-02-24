package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Servlet implementation class UserServices
 * This servlet is responsible for returning the customer info - all orders and balance
 */
@WebServlet("/UserServices")
public class UserServices extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServices() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Util.log_start("User service request..."+request.getRemoteAddr());

		JSONArray ResArr = new JSONArray();
		Connection con = Util.getConnection();
		
		
		String infoText = Util.getRequestJson(request);
		JSONObject info = null;
		try {
			 info = new JSONObject(infoText);
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		}
		String query = "Select * from Reservations where username = ? and status = 1 and end > ? and rType != 'c' ";
		java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = new Date();
		String cpr = sdf.format(dd);
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, info.getString("username"));
			stmt.setString(2, cpr);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				//JSONObject res = 
				ResArr.put(
						new JSONObject()
						.put("reserveID", rs.getInt(1))
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
			rs.close();
			stmt.close();
		} catch (SQLException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		}
		
		String query2 = "Select * from RoutineSubscription where username = ? and status = 1";
		JSONArray RoutSubArr = new JSONArray();
		PreparedStatement stmt2;
		try {
			stmt2 = con.prepareStatement(query2);
			stmt2.setString(1, info.getString("username"));
			
			ResultSet rs = stmt2.executeQuery();
			
			while(rs.next()){
				//JSONObject res = 
				RoutSubArr.put(
						new JSONObject()
						.put("routineSubID", rs.getInt(1))
						.put("carNumber", rs.getString(2))
						.put("lotName", rs.getString(3))
						.put("username",rs.getString(4))
						.put("start", rs.getString(5))
						.put("end", rs.getString(6))
						.put("leaveHour", rs.getString(7))
						.put("status", rs.getBoolean(8))
						.put("used", rs.getInt(9))
						
				);
			}
			rs.close();
			stmt2.close();
		} catch (SQLException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		}
		
		String query3 = "Select * from FullSubscription where username = ? and status = 1";
		JSONArray FullSubArr = new JSONArray();
		PreparedStatement stmt3;
		try {
			stmt3 = con.prepareStatement(query3);
			stmt3.setString(1, info.getString("username"));
			
			ResultSet rs = stmt3.executeQuery();
			
			while(rs.next()){
				//JSONObject res = 
				FullSubArr.put(
						new JSONObject()
						.put("fullSubID", rs.getInt(1))
						.put("carNumber", rs.getString(2))
						.put("username",rs.getString(3))
						.put("start", rs.getString(4))
						.put("end", rs.getString(5))
						.put("isParking", rs.getBoolean(7))	
						
				);
			}
			rs.close();
			stmt3.close();
		} catch (SQLException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		}

		
		try {
			JSONObject res = new JSONObject().put("resArr",ResArr).put("fullSubArr",FullSubArr ).put("routSubArr", RoutSubArr).put("result", true);
			response.getWriter().println(res);
			
			Util.log_end("...Success at User service request "+request.getRemoteAddr());
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		}
		
		
		
	}

}
