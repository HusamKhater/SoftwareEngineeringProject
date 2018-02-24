package cpsServer;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class Login
 * This servlet is responsible for log into the system, and maintain one sesseion per user
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * Default constructor. 
     */
    public Login() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
		//response.getWriter().append("Served at: ").append(request.getContextPath());
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
				case "Regular":{
					regularLogin(request,response,req);
					return;
					}
				case "System" : {
					systemLogin(request,response,req);
					return;
					}
				case "SignOut" :{
					updateLoggedOut(req);
					response.getWriter().print(Util.success());
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		
		/**
		 * helper function to grant access for system user
		 * 
		 * @param request
		 * @param response
		 * @param req
		 */
		private void systemLogin(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
			Util.log_start("[System User Login request...]"+request.getRemoteAddr());
			Connection con = Util.getConnection();
			JSONObject logged = null;
			try{
			
			JSONObject user = null;
			
			
			user = req;
			String query = "Select * from SystemUsers where username = ?";
			PreparedStatement stmt;
			
			
				stmt = con.prepareStatement(query);
				stmt.setString(1, user.getString("username"));
				
				ResultSet st = stmt.executeQuery();
				
				if(st.next()){
					//System.out.println(st.getString(3));
					if(!st.getString(4).equals(user.getString("password")) || (st.getInt(1) != user.getInt("workerID")) ){
						System.out.println("Failed to Login");
						response.getWriter().println(new JSONObject().put("result", false).put("info", "wrong username/password"));
						return;
					}else {
						if(st.getBoolean(9)){
							response.getWriter().println(new JSONObject().put("result", false).put("info", "User already signed in"));
							return;
						}
						logged =  Util.constructSystemUser(st.getInt(1),st.getString(2),
						st.getString(3),st.getString(4), st.getString(5), st.getString(6),st.getString(7),st.getString(8));
						updateLogged(st.getString(2),true);
					}
				}else{
					System.out.println("Failed to Login");
					response.getWriter().println(new JSONObject().put("result", false).put("info", "user doesn't exist"));
					return;
				}
				st.close();
				stmt.close();
				response.getWriter().println(new JSONObject().put("userInfo", logged).put("result", true));
				Util.log_end("[...Success at System User Login request] "+request.getRemoteAddr());
				
				return;
			}catch(SQLException  e){
				//e.printStackTrace();
				System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		/**
		 * helper function to grant access for regular user
		 * 
		 * @param request
		 * @param response
		 * @param req
		 */
		private void regularLogin(HttpServletRequest request, HttpServletResponse response, JSONObject req) {

			Util.log_start("Regular User Login request..."+request.getRemoteAddr());
			Connection con = Util.getConnection();
			JSONObject logged = null;
			try{
			
			JSONObject user = null;
			
			
			user = req;
			String query = "Select * from USERS where username = ?";
			PreparedStatement stmt;
			
			
				stmt = con.prepareStatement(query);
				stmt.setString(1, user.getString("username"));
				
				ResultSet st = stmt.executeQuery();
				
				if(st.next()){
					//System.out.println(st.getString(3));
					if(!st.getString(3).equals(user.getString("password"))){
						System.out.println("Failed to Login");
						response.getWriter().println(new JSONObject().put("result", false).put("info", "wrong username/password"));
						return;
					}else {
						if(st.getBoolean(9)){
							response.getWriter().println(new JSONObject().put("result", false).put("info", "User already signed in"));
							return;
						}
						logged =  Util.constructUser(st.getString(1),st.getString(2),
						st.getString(3),st.getString(4), st.getString(5), st.getString(6),st.getInt(7),st.getString(8));
						updateLogged(st.getString(1),false);
					}
				}else{
					System.out.println("Failed to Login");
					response.getWriter().println(new JSONObject().put("result", false).put("info", "user doesn't exist"));
					return;
				}
				st.close();
				stmt.close();
				response.getWriter().println(new JSONObject().put("userInfo", logged).put("result", true));
				Util.log_end("...Success at Regular User Login request "+request.getRemoteAddr());
				
				return;
			}catch(SQLException  e){
				//e.printStackTrace();
				System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

	}
		
		/**
		 * updating if the user is logged in the system
		 * @param string
		 * @param isSystem
		 */
		private void updateLogged(String string,boolean isSystem) {
			String updateLogged;
			if(isSystem){
				updateLogged = "Update SystemUsers set logged = true where username = '" + string + "'";
			}else{
				updateLogged = "Update USERS set logged = true where username = '" + string + "'";
			}
			Connection con = Util.getConnection();
			PreparedStatement stmt;
			try {
				stmt = con.prepareStatement(updateLogged);
				stmt.executeUpdate();
				System.out.println("User # " + string + " # is logged in!");
				stmt.close();
				return;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
		/**
		 * updating if the user is logged out of the system
		 * @param req
		 */
		private void updateLoggedOut(JSONObject req) {
			String updateLogged = "";
			
			Connection con = Util.getConnection();
			PreparedStatement stmt;
			try {
				if(req.has("systemUsername")){
					updateLogged = "Update SystemUsers set logged = false where username = '" + req.getString("systemUsername")+"'";

				}else{
					updateLogged = "Update USERS set logged = false where username = '" + req.getString("username")+"'";

				}
				stmt = con.prepareStatement(updateLogged);
				stmt.executeUpdate();
				stmt.close();
				System.out.println("User # " + req + " # logged out!");
				return;
			} catch (SQLException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}


		
		
		
	

}
