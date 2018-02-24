package cpsScheduling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimerTask;

import cpsServer.Util;


/**
 * 
 * @author Baselscs
 * TimerTask class that is responsible for disabling subscriptions that are expired
 */
public class StatusUpdater extends TimerTask {

	@Override
	public void run() {
		try{
			
			System.out.println("Status Updater Scheduled Task begins...");
			Connection con = Util.getConnection();
			
			String query = "Update RoutineSubscription SET status='false' where end < ?";
			String query2 = "Update FullSubscription SET status='false' where end < ?";
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dd = new Date();
			String cpr = sdf.format(dd);
			PreparedStatement stmt;
			stmt = con.prepareStatement(query);
			stmt.setString(1, cpr);
			//System.out.println(stmt.toString());
			stmt.executeUpdate();
			stmt.close();
			
			stmt = con.prepareStatement(query2);
			stmt.setString(1, cpr);
			//System.out.println(stmt.toString());
			stmt.executeUpdate();
			stmt.close();
			
			System.out.println("Status Updater Scheduled Task END");
		} catch (SQLException /*| JSONException*/ e) {
			System.out.println("exception @: " + new Object(){}.getClass().getEnclosingMethod().getName() +"\n"+ e.getMessage());

		}
		
	}

}
