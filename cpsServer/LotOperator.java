package cpsServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * @author Baselscs
 * Servlet implementation class LotOperator
 */
@WebServlet("/LotOperator")
public class LotOperator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LotOperator() {
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
			switch(req.getString("cmd")){
				case "overlappingOrders":{
					//System.out.println("OK");
					overlappingOrders(request,response,req);
					return;
					}
				case "overlappingOrdersRF":{
					overlappingOrdersRF(request,response,req);
					return;
				}
				/*case "cancelReserve" : {
					cancelReserve(request,response,req);
					return;
					}*/
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Used upon triggering occasional/reservation order event
	 * @param request
	 * @param response
	 * @param req
	 */
	private void overlappingOrders(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		Util.log_start("Overlapping orders request..."+request.getRemoteAddr());

			int sum = getOverlappedOrders(req);
			try {
				response.getWriter().print(new JSONObject().put("overlapping", sum).put("result", true));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			Util.log_end("... Success at Overlapping orders request"+request.getRemoteAddr());

		
	}
	
	
	/** helper method
	 * Used upon triggering occasional/reservation order event
	 * @param req
	 * @return
	 */
	public static int getOverlappedOrders(JSONObject req){
		Connection con = Util.getConnection();
		int sum = 0;
		JSONArray routines = new JSONArray();
		try{
			
			java.text.SimpleDateFormat sdf = 
				     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String start = sdf.format(req.get("start"));
			String end = sdf.format(req.get("end"));
			String lotName = req.getString("lotName");
			//System.out.println(lotName);
			String condition = "(start <= '" + start
					+ "' and end > '" + start
					+ "' and end < '" + end
					+ "') OR (start <= '" + start
					+ "' and end >= '" + end
					+ "') OR (start > '" + start
					+ "' and end < '" + end
					+ "') OR (start > '" + start
					+ "' and start < '" + end
					+ "' and end >= '" + end
					+ "')";
			
			String includePL = "(lotName = '" + lotName + "') and(";
			String query = "SELECT((select count(distinct carNumber) from Reservations where "+includePL+ condition + ") and rType != 'c' and status = 1)"
					+"+(select count(distinct carNumber) from FullSubscription where "+condition + "and status = 1 ) )";
			PreparedStatement stmt;
			stmt = con.prepareStatement(query);
			

			//System.out.println("## DEBUG1 : " + stmt.toString());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				sum = rs.getInt(1);
			}
			stmt.close();
			rs.close();
			System.out.println("Reservations overlapping = " + sum);
			String query2 = "Select start,end,leaveHour from RoutineSubscription where " + includePL + condition + ") and status = 1 group by carNumber";
			stmt = con.prepareStatement(query2);
			
			//System.out.println("## DEBUG2 : " + stmt.toString());
			
			rs = stmt.executeQuery();
			//rsid, carNumber, lotName, username, start, end, leaveHour, status, used
			while(rs.next()){
				routines.put(
						new JSONObject()
						.put("start", rs.getDate(1))
						.put("end", rs.getDate(2))
						.put("leaveHour", rs.getString(3))
				);
			}
			if(routines.length() > 0){
				sum += getOverlappingRoutines(routines,sdf.format(req.get("start")),sdf.format(req.get("end")));
			}
			
			rs.close();
			stmt.close();
			
			
		}catch(JSONException | SQLException e){
		
			e.printStackTrace();
		}
		return sum;
	}

	
	@SuppressWarnings("deprecation")
	private static int getOverlappingRoutines(JSONArray routines, String start, String end) {
		java.text.SimpleDateFormat sdf = 
			     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int count = 0;
		try {
			
			Date order_start = sdf.parse(start);
			Date order_end = sdf.parse(end);
			
			long diffInMillies = TimeUnit.MILLISECONDS.toDays(order_end.getTime()) - TimeUnit.MILLISECONDS.toDays(order_start.getTime());
			
			if(diffInMillies > 0){
				System.out.println("## ROUTINES overlapping(ALL) = " + routines.length());
				return routines.length();
			}
			
			for(int i = 0; i < routines.length(); i++){
				JSONObject rout = new JSONObject(routines.get(i).toString());
				String[] time = rout.getString("leaveHour").split(":");
				Date routDate = new Date();
				routDate.setHours(Integer.parseInt(time[0]));
				routDate.setMinutes(Integer.parseInt(time[1]));
				
				Date ordDate = new Date();
				ordDate.setHours(order_start.getHours());
				ordDate.setMinutes(order_start.getMinutes());
				
				if(ordDate.before(routDate)){
					count++;
				}
			}
		} catch (ParseException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("## ROUTINES overlapping = " + count);
		return count;
	}
	
	
	private void overlappingOrdersRF(HttpServletRequest request, HttpServletResponse response, JSONObject req) {
		int sum = 0;
		JSONArray arr = Util.getLots();
		try{
			if(req.getBoolean("isFull")){
				for(int i = 0; i < arr.length(); i++){
					int result = getOverlappingOrdersByLotName(req, new JSONObject(arr.get(i).toString()).getString("lotName"));
					sum = result > sum ? result : sum;
					System.out.println("Done full");
				}
			}else{
				sum = getOverlappingOrdersByLotName(req, req.getString("lotName"));
			}
			
			response.getWriter().print(new JSONObject().put("overlapping", sum).put("result", true));
			
		}catch(JSONException | IOException e){
			try {
				response.getWriter().print(Util.badRequest("Server Error!"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			e.printStackTrace();
		}
		//return sum;

		
	}

	
	@SuppressWarnings("deprecation")
	public static int getOverlappingOrdersByLotName(JSONObject req, String LOTNAME){
		Connection con = Util.getConnection();
		int sum = 0;
		try{
			
			java.text.SimpleDateFormat sdf = 
				     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			String start = sdf.format(new Date());
			now.setMonth(now.getMonth() + 1);
			
			String end = sdf.format(now);
			String lotName = LOTNAME;
			//System.out.println(lotName);
			String condition = "(start <= '" + start
					+ "' and end > '" + start
					+ "' and end < '" + end
					+ "') OR (start <= '" + start
					+ "' and end >= '" + end
					+ "') OR (start > '" + start
					+ "' and end < '" + end
					+ "') OR (start > '" + start
					+ "' and start < '" + end
					+ "' and end >= '" + end
					+ "')";
			
			String includePL = "(lotName = '" + lotName + "') and(";
			String query = "SELECT((select count(distinct carNumber) from RoutineSubscription where "+includePL+ condition + ") and status = 1)"
					+"+(select count(distinct carNumber) from FullSubscription where "+condition + "and status = 1 ) )";
			PreparedStatement stmt;
			stmt = con.prepareStatement(query);
			System.out.println("######## DEBUG :distinct             " + stmt.toString());

			//System.out.println("## DEBUG1 : " + stmt.toString());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				sum = rs.getInt(1);
			}
			stmt.close();
			rs.close();
			
			System.out.println("Subscriptions overlapping = " + sum);
			String query2 = "Select start,end from Reservations where " + includePL + condition + ") and status = 1 and rType != 'c' group by carNumber";
			stmt = con.prepareStatement(query2);
			
			System.out.println("### DEBUG : " + stmt.toString());
			//System.out.println("## DEBUG2 : " + stmt.toString());

			rs = stmt.executeQuery();
			//rsid, carNumber, lotName, username, start, end, leaveHour, status, used
			ArrayList<Long> _start = new ArrayList<>();
			ArrayList<Long> _end = new ArrayList<>();
			while(rs.next()){
				
				_start.add(sdf.parse(rs.getString(1)).getTime());
				//System.out.println("& "+ sdf.format(rs.getDate(1).getTime()));
				_end.add(sdf.parse(rs.getString(2)).getTime());
				//System.out.println("@ " + sdf.format(rs.getDate(2).getTime()));
			}
			if(_start.size() > 0){
				sum += maxOverlapIntervalCount(_start,_end);
			}
			
			rs.close();
			stmt.close();
			
			//response.getWriter().print(new JSONObject().put("overlapping", sum).put("result", true));

		}catch(SQLException | ParseException e){
			

			e.printStackTrace();
		}
		return sum;

	}

	
	public static int maxOverlapIntervalCount(ArrayList<Long> start, ArrayList<Long> end){
		long[] _start = Util.LTol(start);
		long[] _end = Util.LTol(end); 
		int maxOverlap = 0;
		int currentOverlap = 0;
		
		Arrays.sort(_start);
		Arrays.sort(_end);
		
		int i = 0;
		int j = 0;
		int m=_start.length,n=_end.length;
		while(i< m && j < n){
			if(_start[i] < _end[j]){
				currentOverlap++;
				maxOverlap = Math.max(maxOverlap, currentOverlap);
				i++;
			}
			else{
				currentOverlap--;
				j++;
			}
		}
		System.out.println("max overlapping interval = " + maxOverlap);
		return maxOverlap;
	}

}
