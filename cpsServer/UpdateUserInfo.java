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
 * Servlet implementation class UpdateUserInfo
 */
@WebServlet("/UpdateUserInfo")
public class UpdateUserInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateUserInfo() {
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
	 * doPost of this servlet maps the requests received from the client, for now the only operation achevied 
	 * by reaching out to this servlet is updating the user's balance.
	 * further operations can be implemented in the future regarding updating user's info.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String reqtext = Util.getRequestJson(request);
		
		JSONObject req;
		try {
			req = new JSONObject(reqtext);
			switch(req.getString("cmd")){
				case "balance": {
					updateBalance(request,response,req);
					return;
				}
			}
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage()+"\n");
		}
		
	}

	/**
	 * Helper function that Updates user's balance - Accesses DB. every user who connect to this servlet with request to update balance, mapped to this function
	 * @param request - http request 
	 * @param response - http response used for returning response to the client
	 * @param req - Json object that represent the user's request
	 */
	public static void updateBalance(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Update Balance request..."+request.getRemoteAddr());
		System.out.println(req.toString());
		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		Connection con = Util.getConnection();
		
		
		String query ="Update USERS "
				+ "SET balance = balance + ? WHERE username = ?";
		PreparedStatement updateBalance;
		
		try {
			updateBalance = con.prepareStatement(query);
			updateBalance.setLong(1, req.getLong("change"));
			updateBalance.setString(2, req.getString("username"));
			updateBalance.executeUpdate();
			
			updateBalance.close();
		} catch (SQLException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
		}
		try {
			response.getWriter().println(new JSONObject().put("result", true));
			
			Util.log_end("...Success at Update Balance request "+request.getRemoteAddr());
			
		} catch (IOException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
		}
		
		
	}
		
}


