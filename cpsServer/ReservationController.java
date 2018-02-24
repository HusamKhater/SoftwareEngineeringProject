package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import cpsScheduling.RemindersManager;
import cpsScheduling.Reservation;

/**
 * Servlet implementation class ReservationController
 * 
 * this servlet maps the requests received from the client. 
 * by reaching out to this servlet, the user can perform these operations: 
 * 1) reserve Ahead
 * 2) cancel Reserve
 * 3) activate Reserve (Enter to ParkingLot)
 * 4) exit from Lot
 * 5) get order associated to CarNumber
 * 6) toggle Parking situation (Parked, not Parked) for full/routine subscription
 * 

 */
@WebServlet("/ReservationController")
public class ReservationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReservationController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject req;
			try {
				req = new JSONObject(Util.getRequestJson(request));
			
	
			switch(req.getString("cmd")){
				case "reserveAhead":{
					reserveAhead(request,response,req);
					return;
					}
				case "cancelReserve" : {
					cancelReserve(request,response,req);
					return;
					}
				case "activateReserve":{
					activateReserve(request,response,req);
					return;
				}
				case "exit":{
					exit(request,response,req);
					return;
				}
				case "orderByCarNumber":{
					getOrderByID(request,response,req);
					return;
				}
				case "toggleParkingFull" :{
					toggleParkingFull(request,response,req);
					return;
				}
				case "toggleRoutineParking" :{
					toggleRoutineParking(request,response,req);
					return;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Helper function for toggle between Parking status of some car (Parked,Not parked) RoutineSubscription
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void toggleRoutineParking(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Toggle Routine isParking status request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		
		
		try{
			Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet uprs = stmt2.executeQuery(" select * from RoutineSubscription where rsid = '"+req.getInt("rsid") +"'" );
			
			if(uprs.next()){
				uprs.updateBoolean(9, req.getBoolean("flag"));
				uprs.updateRow();
			}else{
				response.getWriter().print(Util.badRequest("Car Not found"));
				uprs.close();
				stmt2.close();
				return;
			}
			uprs.close();
			stmt2.close();
			
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Toggle Routine isParking status request "+request.getRemoteAddr());
		}catch(SQLException | JSONException | IOException e){
			//e.printStackTrace();
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\nError: "+ e.getMessage());
			try {
				response.getWriter().print(Util.badRequest("Server Error"));
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}
		}

		
	}

	
	/**
	 * Helper function for toggle between Parking status of some car (Parked,Not parked) FullSubscription
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void toggleParkingFull(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Toggle isParking status request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		
		
		try{
			Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet uprs = stmt2.executeQuery(" select * from FullSubscription where fsid = '"+req.getInt("fsid") +"' and carNumber = " + req.getString("carNumber"));
			
			if(uprs.next()){
				uprs.updateBoolean(7, req.getBoolean("flag"));
				uprs.updateRow();
				if(req.getBoolean("flag")){
					uprs.updateString(9, Util.sdf.format(new Date()));
					uprs.updateRow();
				}
			}else{
				response.getWriter().print(Util.badRequest("Car Not found"));
				uprs.close();
				stmt2.close();
				return;
			}
			uprs.close();
			stmt2.close();
			
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Toggle isParking status request "+request.getRemoteAddr());
		}catch(SQLException | JSONException | IOException e){
			//e.printStackTrace();
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\nError: "+ e.getMessage());
			try {
				response.getWriter().print(Util.badRequest("Server Error"));
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}
		}
		


		
	}

	/**
	 * Helper function to return all the orders associated with car number
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void getOrderByID(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Get Order By ID request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();
		
		JSONObject order = new JSONObject();
		String query ="select * from Reservations "
				+ "where carNumber = ? and status = 1 and rType != 'c' and activated = 1 and lotName = ?";
		PreparedStatement stmt;
		//rid, carNumber, lotName, username, start, end, status, activated, rType, cost
		try {
			stmt = con.prepareStatement(query);
			stmt.setString(1, req.getString("carNumber"));
			stmt.setString(2, req.getString("lotName"));
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				order.put("rid", rs.getInt(1))
						   .put("carNumber", rs.getString(2))
						   .put("start", rs.getString(5))
						   .put("end",rs.getString(6));
			}else{
				response.getWriter().print(Util.badRequest("Car not found"));
				rs.close();
				stmt.close();
				return;

			}
			rs.close();
			stmt.close();
		} catch (SQLException | JSONException | IOException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
		}
		try {
			response.getWriter().println(new JSONObject().put("result", true).put("order", order));
			
			Util.log_end("...Success at Get Order By ID request "+request.getRemoteAddr());
			
		} catch (IOException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
		}

	}

	
	/**
	 * Helper function to update status of a car to exit, when issuing a request to this function, the reservation will be updated to expired.
	 * issued when finishing parking
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void exit(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Exit Parking lot request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		JSONObject order = new JSONObject();
		
		try{
			Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			
			ResultSet uprs = stmt2.executeQuery(" select * from Reservations where rid = '"+req.getInt("rid") +"' and carNumber = " + req.getString("carNumber"));
			
			if(uprs.next()){
				uprs.updateBoolean(7, false);
				order.put("start", uprs.getString(5))
					 .put("end", uprs.getString(6))
					 .put("type", uprs.getString(9));
				uprs.updateRow();
			}else{
				response.getWriter().print(Util.badRequest("Car Not found"));
				uprs.close();
				stmt2.close();
				return;
			}
			uprs.close();
			stmt2.close();
		}catch(SQLException | JSONException | IOException e){
			//e.printStackTrace();
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\nError: "+ e.getMessage());
			try {
				response.getWriter().print(Util.badRequest("Server Error"));
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}
		}
		try {
			response.getWriter().print(new JSONObject().put("order", order).put("result", true));
			Util.log_end("...Success at Exit Parking lot request "+request.getRemoteAddr());
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
	}

	/**
	 * Helper function to update status of a car to Parked, and updates the reservation status to be activated.
	 * this function used by occasional and reservation orders
	 * @param request
	 * @param response
	 * @param req - request info
	 */
	private void activateReserve(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Activate Reservation request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		String query = " Update Reservations set dateActivated = ?,activated = 1 where rid = ? and carNumber = ?";		
		PreparedStatement stmt;
		
		try{
			stmt = con.prepareStatement(query);
			stmt.setString(1,Util.sdf.format(new Date()));
			stmt.setInt(2, req.getInt("rid"));
			stmt.setString(3, req.getString("carNumber"));
			stmt.execute();
			
			stmt.close();
		}catch(SQLException | JSONException e){
			//e.printStackTrace();
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\nError: "+ e.getMessage());
			try {
				response.getWriter().print(Util.badRequest("Server Error"));
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}
		}
		try {
			Reservation.res.remove(req.getInt("rid"));
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Activate Reservation request "+request.getRemoteAddr());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Helper function to cancel an ordered ahead* reservation
	 * @param request
	 * @param response
	 * @param res - request info
	 */
	private void cancelReserve(HttpServletRequest request, HttpServletResponse response,JSONObject res) {
		
		Util.log_start("Cancel reservation request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		double refund = 0;
		
		try {
				
				Date now = new Date();
				Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				
				ResultSet uprs = stmt2.executeQuery(" select * from Reservations where rid = '"+Integer.parseInt(res.getString("rid")) +"'");
				if(uprs.next()){
					
					Date start = uprs.getTimestamp(5);
					
					
					double cost = uprs.getDouble(10);
					
					long diff = TimeUnit.MILLISECONDS.toHours((start.getTime()) - (now.getTime()));
					
					if(diff <= 0){
						refund = 0;
					}else if(diff < 3 && diff >= 1){
						refund = 0.5*cost;
					}else{
						refund = 0.9*cost;
					}
					
					//String rcpr = sdf.format(start);
					uprs.updateBoolean(7,false);
					uprs.updateString(9, "c");
					uprs.updateRow();
				}else{
					response.getWriter().print(new JSONObject(Util.badRequest("Reservation not found!")));
					stmt2.close();
					uprs.close();
					return;
					
				}
				uprs.close();
				stmt2.close();
				
				response.getWriter().print(new JSONObject(Util.success()).put("refund", refund));
				Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
				
		} catch (IOException    e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		return;

	}

	/**
	 * Helper function to reserve ahead.
	 * this function used by occasional and reservation orders.
	 * @param request
	 * @param response
	 * @param reserve
	 */
	private void reserveAhead(HttpServletRequest request, HttpServletResponse response,JSONObject reserve) {
		Util.log_start("Reserve ahead request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		String query = " insert into Reservations (carNumber, lotName, username, start, end, activated, rType,cost,dateActivated)"
		        + " values (?, ?, ?, ?, ?,?,?,?,?);";
		        		
		
		int rid = 0;
		PreparedStatement stmt;
		//System.out.println(reserve.toString());
		java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			stmt.setString(1, reserve.getString("carNumber"));
			stmt.setString(2, reserve.getString("lotName"));
			stmt.setString(3, reserve.getString("username"));
			stmt.setString(4, sdf.format(new Date(reserve.getLong("start"))));
			stmt.setString(5, sdf.format(new Date(reserve.getLong("end"))));
			stmt.setInt(6, reserve.getInt("activated"));
			stmt.setString(7, reserve.getString("type"));
			if(reserve.has("cost")){
				stmt.setDouble(8,(reserve.getDouble("cost")));
				stmt.setString(9, "1990-01-01 00:00:00");
			}else{
				stmt.setDouble(8,0);
				stmt.setString(9, Util.sdf.format(new Date()));
			}
			System.out.println(stmt.toString());
			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
		    if(rs.next())
				   rid = rs.getInt(1);
				
			stmt.close();
		}catch(SQLException | JSONException e){
			e.printStackTrace();
			//System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\nError: "+ e.getMessage());
			try {
				
				response.getWriter().print(Util.badRequest("Server Error"));
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				
			}
		}
		try {
			if(reserve.getString("type").equals("r")){
				long diff = TimeUnit.MILLISECONDS.toMillis(new Date(reserve.getLong("start")).getTime()) - TimeUnit.MILLISECONDS.toMillis(new Date().getTime());
				if(TimeUnit.MILLISECONDS.toDays(diff) == 0){
					if(rid != 0){
						Reservation.res.put(rid, new Reservation(rid, reserve.getString("carNumber"), reserve.getString("lotName"),
								 reserve.getString("username"), 
								 	(new Date(reserve.getLong("start"))), 
								 		(new Date(reserve.getLong("end"))), 
								 			reserve.getString("email")));
						
						RemindersManager.scheduleNewReservationReminder(Reservation.res.get(rid));
					}
					
				}
			}
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Reserve ahead request "+request.getRemoteAddr());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
