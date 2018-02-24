package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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
 * @author Baselscs
 * Servlet implementation class ReportsGenerator
 * this servlet maps the requests received from the client. 
 * by reaching out to this servlet, the system user can perform these operations: 
 * 1) Generate Fiscal Quarter by quarter
 * 2) Acknowledge that Quarter Report is Ready
 * 3) Get Quarters that are Ready
 * 4) Get Weekly Report
 * 5) Exclusive Info
 * 
 */
@WebServlet("/ReportsGenerator")
public class ReportsGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsGenerator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject req;
		
		try {
				req = new JSONObject(Util.getRequestJson(request));
				//System.out.println(req.toString());
			switch(req.getString("cmd")){
				case "FiscalQuarter":{
					generateFiscalQuarter(request,response,req);
					return;
					}
				case "QuarterReady" : {
					quarterReady(request,response,req);
					return;
					}
				case "getQuarterReady" :{
					getQuarterReady(request,response,req);
					return;
					
					}
				case "getWeeklyReport":{
					getWeeklyReport(request,response,req);
					return;
				}
				case "exclusiveInfo" :{
					getExlusive(request,response,req);
					return;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Function that returns the exclusive info about the subscriptions
	 * @param request
	 * @param response
	 * @param req
	 */
	private void getExlusive(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		try {
			String numOfSubs = "select (select count(fsid) from  FullSubscription where status = 1) +(select count(rsid) from  RoutineSubscription where status = 1 and business = false)";
			String business = "select count(subCode) from BusinessSubscription where status = 1";
			
			Connection con = Util.getConnection();
			
			PreparedStatement ps = con .prepareStatement(numOfSubs);
			
			ResultSet rs = ps.executeQuery();
			JSONObject obj = new JSONObject();
			if(rs.next()){
				obj
				.put("allSubscriptions", rs.getInt(1));
			}else{
				response.getWriter().print(new JSONObject().put("result", false).put("info", "Server Error!"));
				rs.close();
				return;
			}
			ps .close();
			rs.close();
			
			ps = con.prepareStatement(business);
			rs = ps.executeQuery();
			
			if(rs.next()){
				obj.put("business", rs.getInt(1));
			}
			ps.close();
			rs.close();
			//System.out.println(obj.toString());
			
			
			
			
			
			response.getWriter().print(new JSONObject().put("result", true).put("info", obj));
		} catch (JSONException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * Function that returns from the DB that requested weekly report
	 * @param request
	 * @param response
	 * @param req
	 */
	private void getWeeklyReport(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		try {
			String weekly = "select * from  WeeklyReports where start <= ? and end >= ?";
			
			Connection con = Util.getConnection();
			
			PreparedStatement ps = con .prepareStatement(weekly);
			ps.setString(1, Util.sdf.format(new Date(req.getLong("date"))));
			ps.setString(2, Util.sdf.format(new Date(req.getLong("date"))));
			ResultSet rs = ps.executeQuery();
			JSONObject obj = new JSONObject();
			if(rs.next()){
				obj
				.put("start", rs.getString(2))
				.put("end",rs.getString(3))
				.put("content",new JSONObject(rs.getString(4)));
				
			}else{
				response.getWriter().print(new JSONObject().put("result", false).put("info", "Report not found"));
				rs.close();
				return;
			}
			//System.out.println(obj.toString());
			rs.close();
			response.getWriter().print(new JSONObject().put("result", true).put("info", obj));
		} catch (JSONException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * Function that inform the CEO which quarters reports are ready to get
	 * @param request
	 * @param response
	 * @param req
	 */
	private void getQuarterReady(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		try {
			String quarters = "select * from  QuartersReady";
			
			Connection con = Util.getConnection();
			
			PreparedStatement ps = con .prepareStatement(quarters);
			
			ResultSet rs = ps.executeQuery();
			JSONArray obj = new JSONArray();
			while(rs.next()){
				obj.put(new JSONObject()
				.put("jan", rs.getBoolean(2))
				.put("apr",rs.getBoolean(3))
				.put("jul",rs.getBoolean(4))
				.put("oct",rs.getBoolean(5))
				.put("lotName", rs.getString(6))
				
						);
				
			}
			
			response.getWriter().print(new JSONObject().put("result", true).put("info", obj));
		} catch (JSONException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Function updates that a quarter is ready 
	 * @param request
	 * @param response
	 * @param req
	 */
	private void quarterReady(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		try {
			String quarters = "Update QuartersReady set " + getMonthByQuarter(req.getInt("quarter")) + " = true where lotName = '" + req.getString("lotName")+"'";
			
			Connection con = Util.getConnection();
			
			PreparedStatement ps = con .prepareStatement(quarters);
			
			ps.execute();
			
			response.getWriter().print(Util.success());
		} catch (JSONException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Helper function that returns the beginning of quarter month
	 * @param quarter
	 * @return String
	 */
	private String getMonthByQuarter(int quarter) {
		switch(quarter){
		case 1 : return "jan";
		case 2 : return "apr";
		case 3 : return "jul";
		case 4 : return "oct";
		default : return "jan";
		}
	}

	/**
	 * Function that generates Fiscal Quarter Report
	 * @param request
	 * @param response
	 * @param req
	 */
	private void generateFiscalQuarter(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		String reses = "Select * from Reservations where start >= ? and end <= ? and lotName = ?";
		String fSubs = "Select * from FullSubscription where start >= ? and end <= ?";
		String rSubs = "Select * from RoutineSubscription where start >= ? and end <= ? and lotName = ?";
		String dSpots = "select * from DisabledSpots where issued >= ? and issued <= ? and lotName = ?";
		String comps = "SELECT * FROM Complaints where date >= ? and date <= ? and lotName = ?";
		
		
		JSONArray reservations = new JSONArray();
		JSONArray routineSubscriptions = new JSONArray();
		JSONArray fullSubscriptions = new JSONArray();
		JSONArray disabledSpots = new JSONArray();
		JSONArray complaints = new JSONArray();

		
		
		try{
			
			Date quarterStart = new Date();
			Date quarterEnd = new Date();
			int quarter = req.getInt("quarter");//getCurrentQuarter();
			
			getDates(quarterStart,quarterEnd,quarter);
			Connection con = Util.getConnection();
			PreparedStatement stmt = con.prepareStatement(reses);
			stmt.setString(1, Util.sdf.format(quarterStart));
			stmt.setString(2, Util.sdf.format(quarterEnd));
			stmt.setString(3, req.getString("lotName"));
			
			ResultSet rs = stmt.executeQuery();
			
			//rid, carNumber, lotName, username, start, end, status, activated, rType, cost
			while(rs.next()){
				reservations.put(new JSONObject()
						
						.put("rid", rs.getInt(1))
						.put("carNumber", rs.getString(2))
						.put("lotName", rs.getString(3))
						.put("username", rs.getString(4))
						.put("start", Util.sdf.format(Util.sdf.parse(rs.getString(5))))
						.put("end", Util.sdf.format(Util.sdf.parse(rs.getString(6))))
						.put("status", rs.getBoolean(7))
						.put("activated", rs.getBoolean(8))
						.put("rType", rs.getString(9))
						.put("cost", rs.getDouble(10))
						.put("dateActivated", Util.sdf.format(Util.sdf.parse(rs.getString(11) ==null? "2018-01-01 00:00:00" :rs.getString(11))))
						
						);
			}
			
			stmt.close();
			rs.close();
			
			stmt = con.prepareStatement(fSubs);
			stmt.setString(1, Util.sdf.format(quarterStart));
			stmt.setString(2, Util.sdf.format(quarterEnd));
			
			//System.out.println(stmt.toString());
			rs = stmt.executeQuery();
			
			//fsid, carNumber, username, start, end, status, isParking, notified, lastEntry
			while(rs.next()){
				fullSubscriptions.put(new JSONObject()
						
						.put("fsid", rs.getInt(1))
						.put("carNumber", rs.getString(2))
						.put("username", rs.getString(3))
						.put("start", Util.sdf.format(Util.sdf.parse(rs.getString(4))))
						.put("end", Util.sdf.format(Util.sdf.parse(rs.getString(5))))
						.put("status", rs.getBoolean(6))
						.put("isParking", rs.getBoolean(7))
						.put("notified", rs.getBoolean(8))
						.put("lastEntry", Util.sdf.format(Util.sdf.parse(rs.getString(9))))
						
						);
			}
			stmt.close();
			rs.close();
			
			stmt = con.prepareStatement(rSubs);
			stmt.setString(1, Util.sdf.format(quarterStart));
			stmt.setString(2, Util.sdf.format(quarterEnd));
			stmt.setString(3, req.getString("lotName"));
			rs = stmt.executeQuery();
			//rsid, carNumber, lotName, username, start, end, leaveHour, status, used, business, notified
			while(rs.next()){
				routineSubscriptions.put(new JSONObject()
						
						.put("rsid", rs.getInt(1))
						.put("carNumber", rs.getString(2))
						.put("lotName", rs.getString(3))
						.put("username", rs.getString(4))
						.put("start", Util.sdf.format(Util.sdf.parse(rs.getString(5))))
						.put("end", Util.sdf.format(Util.sdf.parse(rs.getString(6))))
						.put("leaveHour", rs.getString(7))
						.put("status", rs.getBoolean(8))
						.put("used", rs.getBoolean(9))
						.put("business", rs.getBoolean(10))
						.put("notified", rs.getBoolean(11))
						
						);
			}
			stmt.close();
			rs.close();
			
			//requestID, systemUsername, lotName, x, y, z
			stmt = con.prepareStatement(dSpots);
			stmt.setString(1, Util.sdf.format(quarterStart));
			stmt.setString(2, Util.sdf.format(quarterEnd));
			stmt.setString(3, req.getString("lotName"));
			//System.out.println(stmt.toString());
			rs = stmt.executeQuery();
			
			//requestID, systemUsername, lotName, x, y, z, issued, expired
			while(rs.next()){
				disabledSpots.put(new JSONObject()
						
						.put("requestID", rs.getInt(1))
						.put("systemUsername", rs.getString(2))
						.put("lotName", rs.getString(3))
						.put("x", rs.getInt(4))
						.put("y", rs.getInt(5))
						.put("z", rs.getInt(6))
						.put("issued", Util.sdf.format(Util.sdf.parse(rs.getString(7))))
						.put("expired", rs.getBoolean(8))
						
						
						);
			}
			stmt.close();
			rs.close();
			
			
			stmt = con.prepareStatement(comps);
			stmt.setString(1, Util.sdf.format(quarterStart));
			stmt.setString(2, Util.sdf.format(quarterEnd));
			stmt.setString(3, req.getString("lotName"));
			rs = stmt.executeQuery();
			//complaintID, carNumber, username, orderID, lotName, handled, content, date, email, dateHandled
			
			while(rs.next()){
				complaints.put(new JSONObject()
						
						.put("complaintID", rs.getInt(1))
						.put("carNumber", rs.getString(2))
						.put("username", rs.getString(3))
						.put("orderID", rs.getInt(4))
						.put("lotName", rs.getString(5))
						.put("handled", rs.getBoolean(6))
						.put("content", rs.getString(7))
						.put("date", Util.sdf.format(Util.sdf.parse(rs.getString(8))))
						.put("email", rs.getString(9))
						.put("dateHandled", Util.sdf.format(Util.sdf.parse(rs.getString(10) == null? "2018-01-01 00:00:00" :rs.getString(10) )))
						.put("refund", rs.getDouble(11))
						);
			}
			
			stmt.close();
			rs.close();
			
			JSONObject reports = new JSONObject();
			reports.put("reservations", reservations);
			reports.put("routineSubscriptions", routineSubscriptions);
			reports.put("fullSubscriptions", fullSubscriptions);
			reports.put("complaints", complaints);
			reports.put("disabledSpots", disabledSpots);
			//System.out.println(reports);
			response.getWriter().print(new JSONObject().put("report", reports).put("result", true));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}

	/**
	 * Helper Function that set up the dates dut to a given quarter
	 * @param quarterStart
	 * @param quarterEnd
	 * @param quarter
	 */
	@SuppressWarnings("deprecation")
	private static void getDates(Date quarterStart, Date quarterEnd, int quarter) {
		if(quarter == 1){
			quarterStart.setMonth(0);
			quarterStart.setDate(1);
			quarterStart.setHours(0);
			quarterStart.setMinutes(0);
			quarterStart.setSeconds(0);
			
			quarterEnd.setMonth(2);
			quarterEnd.setDate(31);
			quarterEnd.setHours(23);
			quarterEnd.setMinutes(59);
			quarterEnd.setSeconds(59);
			
		}
		if(quarter == 2){
			quarterStart.setMonth(3);
			quarterStart.setDate(1);
			quarterStart.setHours(0);
			quarterStart.setMinutes(0);
			quarterStart.setSeconds(0);
			
			quarterEnd.setMonth(5);
			quarterEnd.setDate(30);
			quarterEnd.setHours(23);
			quarterEnd.setMinutes(59);
			quarterEnd.setSeconds(59);
			
		}
		
		if(quarter == 3){
			quarterStart.setMonth(6);
			quarterStart.setDate(1);
			quarterStart.setHours(0);
			quarterStart.setMinutes(0);
			quarterStart.setSeconds(0);
			
			quarterEnd.setMonth(8);
			quarterEnd.setDate(30);
			quarterEnd.setHours(23);
			quarterEnd.setMinutes(59);
			quarterEnd.setSeconds(59);
			
		}
		if(quarter == 4){
			quarterStart.setMonth(9);
			quarterStart.setDate(1);
			quarterStart.setHours(0);
			quarterStart.setMinutes(0);
			quarterStart.setSeconds(0);
			
			quarterEnd.setMonth(11);
			quarterEnd.setDate(31);
			quarterEnd.setHours(23);
			quarterEnd.setMinutes(59);
			quarterEnd.setSeconds(59);
			
		}
	}


	@SuppressWarnings("unused")
	private int getCurrentQuarter() {
		Calendar.getInstance();
		int month = Calendar.MONTH;
		
		if(month >= 1 && month <=3)
			return 1;
		if(month >= 4 && month <=6)
			return 2;
		if(month >= 7 && month <=9)
			return 3;
		if(month >= 10 && month <=12)
			return 4;
		
		return 0;
	}

}
