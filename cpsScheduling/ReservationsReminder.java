package cpsScheduling;


import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cpsServer.Mailer;

/**
 * 
 * @author Baselscs
 *	TimerTask class that is responsible for sending reminder emails to clients who are late for parking
 */
public class ReservationsReminder extends TimerTask{

	private int id;
	
	public ReservationsReminder(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		System.out.println("Reservations Latency Reminder Scheduled Task begins... "+ new Date() );
		if(Reservation.res.containsKey(id)){
			Mailer.sendLateEmail(Reservation.res.get(id));
			RemindersManager.scheduler.schedule(new ReservationRemover(this.id), 30, TimeUnit.MINUTES);
		}
		System.out.println("Reservations Latency Reminder Scheduled Task END");
		
		
	}

}
