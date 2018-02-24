package cpsScheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import cpsServer.Mailer;
import cpsServer.Util;

/**
 * 
 * @author Baselscs
 * TimerTask class that is responsible for sending reminders for: newarly ending subscriptions, full subscription customers who exceeds parking limit (16 days).
 * also this class SCHEDULES reminders for today's reservation.
 */
public class RemindersManager extends TimerTask{
	
	
	public static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		Date todayStart = new Date(),todayEnd = new Date();;
		
		todayStart.setHours(0);
		todayStart.setMinutes(0);
		todayStart.setSeconds(0);
		
		todayEnd.setHours(23);
		todayEnd.setMinutes(59);
		todayEnd.setSeconds(59);
		JSONArray exceededSubs = Util.getExceededSubscriptions();
		for(int i = 0; i < exceededSubs.length(); i ++){
			try {
				Mailer.sendExcessionEmail(exceededSubs.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JSONArray subscriptions = Util.getNearlyEndingSubscriptions();
		//System.out.println(subscriptions.length());
		for(int i = 0 ; i < subscriptions.length(); i++){
			try {
				Mailer.sendSubscriptionRenewEmail(subscriptions.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JSONArray businessSubscription = Util.getNearlyEndingBusinessSubscriptions();
		//System.out.println(subscriptions.length());
		for(int i = 0 ; i < businessSubscription.length(); i++){
			try {
				Mailer.sendSubscriptionRenewEmailB(businessSubscription.getJSONObject(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<Reservation> res = Util.getReservationsBetween(todayStart, todayEnd);
		
    	if(res.size() == 0){
    		System.out.println("Nothing to show!");
    	}else{
    		System.out.println("Scheduled tasks for " + res.size());
    	}
    	for(Reservation r : res){
    		Reservation.res.put(r.getRid(), r);
    		scheduler.schedule(new ReservationsReminder(r.getRid()), getTimeWait(r.getStart()) ,TimeUnit.MINUTES);
    	}
    	
    	System.out.println("Scheduling Success!");
		
	}
	
	private static long getTimeWait(Date start) {
		long diffInMillies = TimeUnit.MILLISECONDS.toMillis(start.getTime()) - TimeUnit.MILLISECONDS.toMillis(new Date().getTime());
		long diff =TimeUnit.MILLISECONDS.toSeconds(diffInMillies);
		return (diff/60)+5;
	}
	
	public static void scheduleNewReservationReminder(Reservation r){
    	scheduler.schedule(new ReservationsReminder(r.getRid()), getTimeWait(r.getStart()) ,TimeUnit.MINUTES);
    	System.out.println("Scheduling New Reservation Success!");
	}
	
	public static void shutDown(){
		scheduler.shutdownNow();
	}

}
