package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 * Servlet implementation class SystemUserServices
 * 
 * this servlet maps the requests received from the client. 
 * by reaching out to this servlet, the system user can perform these operations: 
 * 1) get CostChange Requests
 * 2) handle Change Request
 * 3) request Cost Change
 * 4) disable Spot
 * 5) enable Spot
 * 6) management Request
 * 7) get Management Requests
 * 8) handle Management Request
 * 
 * further operations can be implemented in the future regarding system user's services.
 */
@WebServlet("/SystemUserServices")
public class SystemUserServices extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SystemUserServices() {
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
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String reqtext = Util.getRequestJson(request);
		
		JSONObject req;
		try {
			req = new JSONObject(reqtext);
			switch(req.getString("cmd")){
				case "getCostChangeRequests": {
					getCostChangeRequests(request,response,req);
					return;
				}
				case "handleChangeRequest":{
					handleChangeRequest(request,response,req);
					return;
				}
				case "requestCostChange":{
					requestChange(request,response,req);
					return;
				}
				case "disableSpot":{
					disableSpot(request,response,req);
					return;
				}
				case "managementRequest" :{
					managementRequest(request,response,req);
					return;
				}
				case "getManagementRequests" :{
					getManagementRequests(request,response,req);
					return;
				}
				case "handleManagementRequest" : {
					handleManagementRequest(request,response,req);
					return;
				}
				case "enableSpot":{
					enableSpot(request,response,req);
					return;
				}
			}
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage()+"\n");
		}
	}
	
	/**
	 * Helper function that enables a parking spot on the database that was being disabled sometime before
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void enableSpot(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		String query = "Update DisabledSpots set expired = true where x = ? and y = ? and z = ?";
		Connection con = Util.getConnection();
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, req.getInt("x"));
			ps.setInt(2, req.getInt("y"));
			ps.setInt(3, req.getInt("z"));
			ps.execute();
			ps.close();
			response.getWriter().print(Util.success());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Helper function that the ParkingLot manager uses when the CEO demand some management request like ( current situation pdf)
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void handleManagementRequest(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Handle Management request..."+request.getRemoteAddr());

		Connection con = Util.getConnection();
		try{
			PreparedStatement stmt;
			String query = "Update ManagementRequests set handled = true where requestID = " + req.getInt("requestID") + " and handled = false ";
			stmt = con.prepareStatement(query);
			stmt.execute(query);
			stmt.close();			
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Handle Management request "+request.getRemoteAddr());
		}catch(Exception e){
			try {
				response.getWriter().print(Util._false());
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
	}

	/**
	 * Helper function that gets the requests of the CEO
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void getManagementRequests(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Get Management Requests request..."+request.getRemoteAddr());

		Connection con = Util.getConnection();
		try{
			String query = "Select * from ManagementRequests where handled = false and lotName = '" + req.getString("lotName") +"'";
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			JSONArray managementRequests = new JSONArray();
			while(rs.next()){
				managementRequests.put(new JSONObject()	
									.put("requestID", rs.getInt(1))
									.put("requestType", rs.getString(3)));
			}
			
			rs.close();
			stmt.close();
			
			response.getWriter().print(new JSONObject(Util.success()).put("managementRequests", managementRequests));
			Util.log_end("...Success at Get Management Requests request "+request.getRemoteAddr());
		}catch(Exception e){
			try {
				response.getWriter().print(Util._false());
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
	}

	/**
	 * Helper function for inserting new management request to the Parkinglot manager
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void managementRequest(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Send Management Request request..."+request.getRemoteAddr());
		//System.out.println(req);
		Connection con = Util.getConnection();
		try{
			String query = "Select * from ManagementRequests where handled = false and lotName = '" 
									+ req.getString("lotName") + "' and request = '" + req.getString("requestType")+"'";
			PreparedStatement stmt = con.prepareStatement(query);
			
			//System.out.println(stmt.toString());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				rs.close();
				stmt.close();
				response.getWriter().print(Util.badRequest("Request already exist"));
				return;
			}
			rs.close();
			query = "Insert into ManagementRequests ( lotName, request) "
					+ "values(?,?)";
			
			stmt = con.prepareStatement(query);
			
			stmt.setString(1, req.getString("lotName"));
			stmt.setString(2, req.getString("requestType"));
			stmt.execute();
			stmt.close();
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Send Management Request "+request.getRemoteAddr());
		}catch(Exception e){
			try {
				response.getWriter().print(Util.badRequest("Server Error"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	
		
	}

	/**
	 * Helper function that update the DB with the disabled spot
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void disableSpot(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Connection con = Util.getConnection();
		try{
			
			String query = "Insert into DisabledSpots (systemUsername, lotName, x,y,z,issued) "
					+ "values(?,?,?,?,?)";
			
			PreparedStatement stmt = con.prepareStatement(query);
			
			stmt.setString(1, req.getString("username"));
			stmt.setString(2, req.getString("lotName"));
			stmt.setInt(3, req.getInt("x"));
			stmt.setInt(4, req.getInt("y"));
			stmt.setInt(5, req.getInt("z"));
			stmt.setString(6, Util.sdf.format(new Date()));
			
			stmt.close();
			response.getWriter().print(Util.success());
		}catch(Exception e){
			try {
				response.getWriter().print(Util.badRequest("Server Error"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}

	/**
	 * Helper function for requesting costs changes, issued by the parking lot manager
	 * @param request
	 * @param response
	 * @param req
	 */
	private void requestChange(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Request Cost Change request..."+request.getRemoteAddr());

		Connection con = Util.getConnection();
		try{
			String query = "Insert into ChangePrices (username,lotName, occasional, reserveAhead, routineHours, fullHours, businessHours)"
					+ "values(?,?,?,?,?,?,?)";
			
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1,req.getString("username"));
			stmt.setString(2,req.getString("lotName"));
			stmt.setDouble(3,req.getDouble("occasional"));
			stmt.setDouble(4,req.getDouble("reserveAhead"));
			stmt.setInt(5,req.getInt("routineHours"));
			stmt.setInt(6,req.getInt("fullHours"));
			stmt.setInt(7,req.getInt("businessHours"));
			System.out.println(stmt.toString());
			stmt.execute();
			stmt.close();
			response.getWriter().print(Util.success());

			Util.log_end("...Success at Request Cost Change request "+request.getRemoteAddr());
		}catch(Exception e){
				try {
					response.getWriter().print(Util._false());
					e.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}

	}

	/**
	 * Helper function that handles the requests of costs changes. used by the CEO
	 * @param request
	 * @param response
	 * @param req
	 */
	private void handleChangeRequest(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Handle Change request..."+request.getRemoteAddr());

		Connection con = Util.getConnection();
		try{
			PreparedStatement stmt;
			Statement stm =  con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			String query = "select * from ChangePrices where requestID = " + req.getInt("requestID") + " and handled = false ";
			ResultSet rs = stm.executeQuery(query);
			
			double oc = 0,re = 0;
			int ro = 0,fu = 0,bu = 0;
			if(rs.next()){
				oc = rs.getDouble(4);
				re = rs.getDouble(5);
				ro = rs.getInt(6);
				fu = rs.getInt(7);
				bu = rs.getInt(8);
			}else{
				response.getWriter().print(Util.badRequest("Request can not be done"));
				rs.close();
				stm.close();
				return;
			}
			//System.out.println(req);
			if(req.getBoolean("performChange")){
				//System.out.println("Approved change");
				query = "Insert into CpsCosts( occasional, reservation, routineHours, fullHours, businessHours)"
						+ " values(?,?,?,?,?)";
				stmt = con.prepareStatement(query);
				stmt.setDouble(1,oc);
				stmt.setDouble(2,re);
				stmt.setInt(3,ro);
				stmt.setInt(4,fu);
				stmt.setInt(5,bu);
				
				stmt.execute();
				//System.out.println("### Intent : "+stmt.toString());
				
				stmt.close();
			}
			
			rs.updateBoolean(9, true);
			rs.updateRow();
			
			rs.close();
			stm.close();
			//stmt.close();
			
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Handle Change request "+request.getRemoteAddr());
		}catch(Exception e){
			try {
				response.getWriter().print(Util._false());
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
	}

	/**
	 * Heleper function that returns all the costs changes requests issued by the parking lots managers
	 * @param request
	 * @param response
	 * @param req
	 */
	private void getCostChangeRequests(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Get Costs Change requests..."+request.getRemoteAddr());

		Connection con = Util.getConnection();
		try{
			String query = "Select * from ChangePrices where handled = false";
			PreparedStatement stmt = con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			JSONArray changeRequests = new JSONArray();
			while(rs.next()){
				changeRequests.put(new JSONObject()	
									.put("requestID", rs.getInt(1))
									.put("username",rs.getString(2))
									.put("lotName", rs.getString(3))
									.put("occasional", rs.getDouble(4))
									.put("reserveAhead", rs.getDouble(5))
									.put("routineHours", rs.getInt(6))
									.put("fullHours", rs.getInt(7))
									.put("businessHours", rs.getInt(8)));
			}
			
			rs.close();
			stmt.close();
			
			response.getWriter().print(new JSONObject(Util.success()).put("changeRequests", changeRequests));
			Util.log_end("...Success at Get Costs Change Requests request "+request.getRemoteAddr());
		}catch(Exception e){
			try {
				response.getWriter().print(Util._false());
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

}
