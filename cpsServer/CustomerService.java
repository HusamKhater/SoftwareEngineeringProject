package cpsServer;

/**
 * @author Baselscs
 */

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Servlet implementation class CustomerService
 * this servlet maps the requests received from the client. 
 * by reaching out to this servlet, the user/system user can perform these operations: 
 * 1) Make Complaint
 * 2) Get Complaints
 * 3) Handle Complaint
 */
@WebServlet("/CustomerService")
public class CustomerService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    //final JSONObject j = new JSONObject().put("result", false);
    
    public CustomerService() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String reqtext = Util.getRequestJson(request);
		
		JSONObject req;
		try {
			req = new JSONObject(reqtext);
			switch(req.getString("cmd")){
			
				case "makeComplaint": {
					//System.out.println("Status: OK");
					makeComplaint(request,response,req);
					return;
				}
				case "getComplaints":{
					getComplaints(request,response,req);
					return;
				}
				case "handleComplaint":{
					handleComplaint(request,response,req);
					return;
				}
			}
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage()+"\n");
		}
	}


	/**
	 * Function that perform handling by a customer service worker to a specific complaint
	 * @param request
	 * @param response
	 * @param req
	 */
	private void handleComplaint(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Handle Complaint request..." + request.getRemoteAddr());
		Connection con = Util.getConnection();
		try{
			
			String query = "Update Complaints set refund = "+req.getDouble("refund") +",dateHandled ='" + Util.sdf.format(new Date())  +"',handled = true where complaintID = " + req.getInt("complaintID");
			PreparedStatement stmt = con.prepareStatement(query);
			System.out.println(stmt.toString());
			stmt.execute();
			stmt.close();
			 query ="Update USERS "
					+ "SET balance = balance + ? WHERE username = ?";
			PreparedStatement updateBalance;
		
			updateBalance = con.prepareStatement(query);
			updateBalance.setDouble(1, req.getDouble("refund"));
			updateBalance.setString(2, req.getString("username"));
			updateBalance.executeUpdate();
			updateBalance.close();
			
			response.getWriter().print(Util.success());
			Util.log_end("...Success at Handle Complaint request" + request.getRemoteAddr());
			stmt.close();
			return;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	/**
	 * Function that returns the unhandled complaints
	 * @param request
	 * @param response
	 * @param req
	 */
	private void getComplaints(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Get Complaints request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();
		
		JSONArray complaints = new JSONArray();
		String query ="select * from Complaints "
				+ "where handled = false";
		PreparedStatement stmt;
		//complaintID, carNumber, username, orderID, lotName, handled, content, date
		try {
			stmt = con.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				//complaints.add(new JSONObject()));
						   
				complaints.put(new JSONObject().put("complaintID", rs.getInt(1))
						   .put("carNumber", rs.getString(2))
						   .put("username", rs.getString(3))
						   .put("orderID",rs.getString(4))
						   .put("lotName",rs.getString(5))
						   .put("content",rs.getString(7))
						   .put("date",rs.getString(8))
						   .put("email",rs.getString(9)));
			}
			if(complaints.length() == 0){
				response.getWriter().print(Util.badRequest("No complaints Found"));
				rs.close();
				stmt.close();
				return;
			}
			rs.close();
			stmt.close();
			//System.out.println(complaints);
		} catch (SQLException | JSONException | IOException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
		}
		try {
			response.getWriter().println(new JSONObject().put("result", true).put("complaints", complaints));
			//Util.printResponse(res);
			Util.log_end("...Success at Get Complaints request "+request.getRemoteAddr());
			
		} catch (IOException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
		}

		
	}


	/**
	 * Function that registers new complaint sent by the client
	 * @param request
	 * @param response
	 * @param comp
	 */
	private void makeComplaint(HttpServletRequest request, HttpServletResponse response, JSONObject comp) {
		Util.log_start("Make Complaint request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		try {
				if(comp == null){
					response.getWriter().print(Util.badRequest("couldn't get complaint info"));
				}
				String query = null;
				query = " insert into Complaints (carNumber, username, orderID, lotName,content,date,email)"
							+ " values (?, ?, ?, ?,?,?,?)";
				PreparedStatement stmt;
				java.text.SimpleDateFormat sdf = 
					     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				stmt = con.prepareStatement(query);
				stmt.setString(1, comp.getString("carNumber"));
				stmt.setString(2, comp.getString("username"));
				stmt.setInt(3,comp.getInt("orderID"));
				stmt.setString(4, comp.getString("lotName"));
				stmt.setString(5, comp.getString("content"));
				stmt.setString(6, sdf.format(new Date(comp.getLong("date"))));
				stmt.setString(7, comp.getString("email"));
				stmt.execute();
				stmt.close();
				
				response.getWriter().print(Util.success());
				Util.log_end("...Success at Make Complaint "+ request.getRemoteAddr());
				return;
				
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
		}finally{
			try {
				response.getWriter().print(Util._false());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

}
