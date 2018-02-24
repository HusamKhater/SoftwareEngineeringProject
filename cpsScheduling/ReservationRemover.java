package cpsScheduling;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.TimerTask;


import cpsServer.Util;

/**
 * 
 * @author Baselscs
 * TimerTask class that is responsible for updating reservations to canceled status after the client hasn't came to the Lot after 
 * he received a reminder email (30 mins).
 */
public class ReservationRemover extends TimerTask{

	private int id;
	
	public ReservationRemover(int id) {
		super();
		this.id = id;
	}

	@Override
	public void run() {
		Connection con = Util.getConnection();
		try {
				
				Statement stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
				
				ResultSet uprs = stmt2.executeQuery(" select * from Reservations where rid = '"+this.id +"'");
				if(uprs.next()){
					
					//String rcpr = sdf.format(start);
					uprs.updateBoolean(7,false);
					uprs.updateString(9, "c");
					uprs.updateRow();
				}
				uprs.close();
				stmt2.close();
				Reservation.res.remove(id);
				System.out.println("Reservation Canceled!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		return;

		
	}

}
