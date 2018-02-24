package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Signup
 * This servlet is responsible for Sign up operations
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
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
		//Util.log_end("...Success at Login request "+request.getRemoteAddr());
		Util.log_start("Signup request..."+request.getRemoteAddr());
		Connection con = Util.getConnection();
		
		String query = null;
		
		query = " insert into USERS (username, email, password, firstName, lastName, type,balance,company)"
		        + " values (?, ?, ?, ?, ?, ?,?,?)";
		PreparedStatement stmt;
		JSONObject userInfo;

		try {
			
			userInfo = new JSONObject(Util.getRequestJson(request));
			stmt = con.prepareStatement(query);
			stmt.setString(1, userInfo.getString("username"));
			stmt.setString(2, userInfo.getString("email"));
			stmt.setString(3, userInfo.getString("password"));
			stmt.setString(4, userInfo.getString("firstName"));
			stmt.setString(5, userInfo.getString("lastName"));
			stmt.setString(6, userInfo.getString("type"));
			stmt.setInt(7, userInfo.getInt("balance"));
			stmt.setString(8, userInfo.getString("company"));
			stmt.execute();
			stmt.close();
		} catch (SQLException | JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception at DB.signUp" + e.getMessage());;
			try {
				response.getWriter().println(new JSONObject().put("result", false).put("info", "signup failed - duplicate"));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}
		
		try {
			response.getWriter().println(new JSONObject().put("userInfo",userInfo).put("result", true));
			Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
			return;
		} catch (JSONException e) {
			// TODO Auto-generated catch blocku
			e.printStackTrace();
		}
		
	}

		
		

	}


