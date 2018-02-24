package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class SupscriptionController
 * This servlet is responsible for all the operations regarding subscriptions
 * by reaching out to this servlet, the user can perform these operations: 
 * 1) Register Regular Routine Subscription
 * 2) Register Full Subscription
 * 3) Register Business Subscription
 * 4) Activate Business Subscription
 * 5) Get Business Info
 */
@WebServlet("/SubscriptionController")
public class SubscriptionController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubscriptionController() {
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
		System.out.println("SupscriptionController request");
		
		String reqtext = Util.getRequestJson(request);
		
		JSONObject req;
		try {
			req = new JSONObject(reqtext);
			switch(req.getString("cmd")){
				case "RegularRoutineSubscription": {
					regularRoutineSubscription(request,response,req);
					return;
				}
				case "FullSubscription":{
					fullSubscription(request,response,req);
					return;
				}
				case "BusinessSubscription":{
					businessSubscription(request,response,req);
					return;
				}
				case "ActivateBusinessSubscription":{
					activateBusinessSubscription(request,response,req);
					return;
				}
				case "BusinessInfo" :{
					getBusinessInfo(request,response,req);
					return;
				}
				
			}
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage()+"\n");
		}
	}

	/**
	 * Helper function to return BusinessSubscription info: code,cars,start,end
	 * @param request
	 * @param response
	 * @param req  - request info
	 */
	private void getBusinessInfo(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Business Info request... "+request.getRemoteAddr());

		Connection con = Util.getConnection();
		
		JSONArray businessInfo = new JSONArray();
		
		
		PreparedStatement stmt;
		try {
			String query = "Select * from BusinessSubscription where username = '" + req.getString("username") +"'";

			stmt = con.prepareStatement(query);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				//JSONObject res = 
				businessInfo.put(
						new JSONObject()
						.put("subCode", rs.getString(1))
						.put("count", rs.getString(5).split(";").length)
						.put("start", rs.getString(6))
						.put("end", rs.getString(7))
						.put("cars", rs.getString(5))
						
				);
			}
			rs.close();
			stmt.close();
			response.getWriter().println(new JSONObject().put("businessInfo",businessInfo).put("result", true));
			Util.log_end("... Success at Business Info request "+request.getRemoteAddr());

		}catch(Exception e){
			try {
				response.getWriter().print(Util.badRequest("Server Error!"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
		
		
	}

	/**
	 * Helper function to activate business subscription for each car
	 * @param request
	 * @param response
	 * @param activateSub  - request info
	 */
	private void activateBusinessSubscription(HttpServletRequest request, HttpServletResponse response,JSONObject activateSub) {
		Util.log_start("Activate Business Subscription request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();
		//System.out.println("IN");
		
		try {
				PreparedStatement stmt;
				String _start = null;
				String _end = null;
				String _leaveHour = null;
				String _lotName = null;
				String _registred = null;
				if(activateSub == null){
					response.getWriter().print(Util.badRequest("couldn't get subscription info"));
				}
				
				String preQuery = "select cars,status,start,end,leaveHour,lotName,registered from BusinessSubscription where subCode = ?";
				stmt = con.prepareStatement(preQuery);
				stmt.setString(1, activateSub.getString("code"));

				ResultSet rss = stmt.executeQuery();
				String cars = null;
				//int add = 0;
				if(rss.next()){
					
					 if(!rss.getBoolean(2)){
						 response.getWriter().print(Util.badRequest("Expired subscription"));
						 return;
					 }else if(rss.getString(7) != null){
						 if(rss.getString(7).toLowerCase().contains(activateSub.getString("carNumber"))){
							 response.getWriter().print(Util.badRequest("Car already registred"));
							 return;
						 }
					 }
					 cars = rss.getString(1);
					 _start = rss.getString(3);
					 _end = rss.getString(4);
					 _leaveHour = rss.getString(5);
					 _lotName = rss.getString(6);
					 _registred = rss.getString(7) == null? "":rss.getString(7);
					 
				}else{
					stmt.close();
					rss.close();
					response.getWriter().print(Util.badRequest("Code not Found"));
					return;
				}
				rss.close();
				
				String _carNumber = activateSub.getString("carNumber");
				System.out.println(cars + "@@@ " +_carNumber );
				if((!cars.toLowerCase().contains(_carNumber)) || _carNumber.equals("")){
					response.getWriter().print(Util.badRequest("Car is not registered"));
					return;
				}
				stmt.close();
				String query = " insert into RoutineSubscription (carNumber, lotName, username, start, end,leaveHour,business)"
						+ " values (?, ?, ?, ?, ?, ?,?)";
				stmt = con.prepareStatement(query);
				stmt.setString(1, _carNumber);
				stmt.setString(2, _lotName);
				stmt.setString(3, activateSub.getString("username"));
				stmt.setString(4, _start);
				stmt.setString(5, _end);
				stmt.setString(6, _leaveHour);
				stmt.setBoolean(7, true);
				stmt.execute();
				stmt.close();
				
				String update = "Update BusinessSubscription set registered =? where subCode = ?";
				stmt = con.prepareStatement(update);
				stmt.setString(1,_registred + ";" + _carNumber);
				stmt.setString(2, activateSub.getString("code"));
				stmt.execute();
				stmt.close();
				System.out.println("Sucess Activate Business Subscription");
				response.getWriter().print(new JSONObject(Util.success()));
				
				//Util.log_start("Cancel reservation request..."+request.getRemoteAddr());

				Util.log_end("...Success at Activate Business Subscription request "+request.getRemoteAddr());
		} catch (IOException    e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			return;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			return;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			return;
		}
		return;

		
	}

	/**
	 * Helper function for registering business subscription
	 * @param request
	 * @param response
	 * @param bSub  - request info
	 */
	private void businessSubscription(HttpServletRequest request, HttpServletResponse response, JSONObject bSub) {
		Util.log_start("Business Subscription request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();
		try {
				PreparedStatement stmt;
				String md5Code = null;
				if(bSub == null){
					response.getWriter().print(Util.badRequest("couldn't get subscription info"));
				}
				
				String preQuery = "select max(count) from BusinessSubscription where company=?";
				stmt = con.prepareStatement(preQuery);
				stmt.setString(1, bSub.getString("company"));
				ResultSet rss = stmt.executeQuery();
				int add = 0;
				if(rss.next()){
					 add = rss.getInt(1) + 1;
					 
				}else{
					add = 1;
				}
				rss.close();
				stmt.close();
				
				md5Code = Util.getMD5(bSub.getString("company")+add).substring(0, 4)+add;

				String query = null;
				query = " insert into BusinessSubscription (subCode, company,username,lotName,cars, start, end, leaveHour,count)"
							+ " values ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				java.text.SimpleDateFormat sdf = 
					     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				stmt = con.prepareStatement(query);
				//md5Code = Util.getMD5(bSub.getString("company"));
				stmt.setString(1, md5Code);
				stmt.setString(2, bSub.getString("company"));
				stmt.setString(3, bSub.getString("username"));
				stmt.setString(4, bSub.getString("lotName"));
				stmt.setString(5, bSub.getString("cars"));				
				stmt.setString(6, sdf.format(new Date(bSub.getLong("start"))));
				stmt.setString(7, sdf.format(new Date(bSub.getLong("end"))));
				stmt.setString(8, bSub.getString("leaveHour"));
				stmt.setInt(9, add);
				//System.out.println(md5Code);
				stmt.execute();
				stmt.close();
				response.getWriter().print(new JSONObject(Util.success()).put("code", md5Code));
				
				//Util.log_start("Cancel reservation request..."+request.getRemoteAddr());

				Util.log_end("...Success at Business Subscription request "+request.getRemoteAddr());
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
	 * Helper function for registering full subscription
	 * @param request
	 * @param response
	 * @param fSub - request info
	 */
	private void fullSubscription(HttpServletRequest request, HttpServletResponse response, JSONObject fSub) {
		Util.log_start("Full Subscription request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();
		try {
				
				if(fSub == null){
					response.getWriter().print(Util.badRequest("couldn't get subscription info"));
				}
				String query = null;
				query = " insert into FullSubscription (carNumber, username, start, end,lastEntry)"
							+ " values (?, ?, ?, ?,?)";
				PreparedStatement stmt;
				java.text.SimpleDateFormat sdf = 
					     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				stmt = con.prepareStatement(query);
				stmt.setString(1, fSub.getString("carNumber"));
				stmt.setString(2, fSub.getString("username"));
				stmt.setString(3, sdf.format(new Date(fSub.getLong("start"))));
				stmt.setString(4, sdf.format(new Date(fSub.getLong("end"))));
				stmt.setString(5,  Util.sdf.format(new java.util.Date()));
				
				stmt.execute();
				stmt.close();
				response.getWriter().print(Util.success());
				//Util.log_start("Cancel reservation request..."+request.getRemoteAddr());

				Util.log_end("...Success at Full Subscription request "+request.getRemoteAddr());
				
		} catch (IOException    e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

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
	 * Helper function for registering routine subscription
	 * @param request
	 * @param response
	 * @param subs - request info
	 */
	private void regularRoutineSubscription(HttpServletRequest request, HttpServletResponse response, JSONObject subs) {
		Util.log_start("Routine Subscription request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();

		
		try {
			
				if(subs == null){
					response.getWriter().print(Util.badRequest("couldn't get subscription info"));
				}
				String query = null;
				if(subs.has("leave")){
				query = " insert into RoutineSubscription (carNumber, lotName, username, start, end, leaveHour)"
						+ " values (?, ?, ?, ?, ?,?)";	
				}
				else {
					query = " insert into RoutineSubscription (carNumber, lotName, username, start, end)"
							+ " values (?, ?, ?, ?, ?)";
				}
				        
				PreparedStatement stmt;
				java.text.SimpleDateFormat sdf = 
					     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				stmt = con.prepareStatement(query);
				stmt.setString(1, subs.getString("carNumber"));
				stmt.setString(2, subs.getString("lotName"));
				stmt.setString(3, subs.getString("username"));
				stmt.setString(4, sdf.format(new Date(subs.getLong("start"))));
				stmt.setString(5, sdf.format(new Date(subs.getLong("end"))));
				if(subs.has("leave")){
					stmt.setString(6, subs.getString("leave"));
				}
				stmt.execute();
				stmt.close();
				response.getWriter().print(Util.success());
				//Util.log_start("Cancel reservation request..."+request.getRemoteAddr());

				Util.log_end("...Success at Routine Subscription request "+request.getRemoteAddr());
				
		} catch (IOException    e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
	}

}
