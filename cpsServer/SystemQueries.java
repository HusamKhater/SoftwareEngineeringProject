package cpsServer;

import java.io.IOException;
import java.sql.Connection;
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
 * Servlet implementation class SystemQueries
 * This Servlet is responsible for answering the initializing process requests of the Parking lot,
 * it returns the info about the ParkingLots and the costs of the orders
 */
@WebServlet("/SystemQueries")
public class SystemQueries extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SystemQueries() {
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
		//System.out.println("System Queries request");
		Util.log_start("System Queries request..."+request.getRemoteAddr());

		//Util.log_end("...Success at Cancel reservation request "+request.getRemoteAddr());
		JSONArray lots = new JSONArray();
		JSONArray costs = new JSONArray();

		Connection con = Util.getConnection();

		
		
		
		String query = "Select * from Lots ";
		String query2 = "SELECT * FROM CpsCosts ORDER BY CpsCosts.index DESC LIMIT 1;";
		
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
			
			stmt = con.prepareStatement(query2);
			ResultSet rs2 = stmt.executeQuery();
			//double ahead = 1;
			if(rs2.next()){
				
			
				costs.put(new JSONObject().put("orderType", "occasional").put("cost", rs2.getDouble(2)));
				costs.put(new JSONObject().put("orderType", "reserve ahead").put("cost", rs2.getDouble(3)));
				costs.put(new JSONObject().put("orderType", "routine").put("cost", rs2.getDouble(4)*rs2.getDouble(3)));
				costs.put(new JSONObject().put("orderType", "business routine").put("cost", rs2.getDouble(6)*rs2.getDouble(3)));
				costs.put(new JSONObject().put("orderType", "full").put("cost", rs2.getDouble(5)*rs2.getDouble(3)));
				
					 
						
			}
			rs2.close();
			stmt.close();
		} catch (SQLException | JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			e.printStackTrace();
		}
		
		try {
			response.getWriter().println(new JSONObject().put("lots",lots).put("Costs", costs).put("result", true));
			//System.out.println("\nSuccess @ System Queries\n\n");
			//Util.log_start("Cancel reservation request..."+request.getRemoteAddr());

			Util.log_end("...Success at System Queries request "+request.getRemoteAddr());
			
		} catch (JSONException e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());
			e.printStackTrace();
		}
		
		

	}

	

}
