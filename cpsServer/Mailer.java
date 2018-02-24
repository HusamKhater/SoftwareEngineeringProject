package cpsServer;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.json.JSONException;
import org.json.JSONObject;

import cpsScheduling.Reservation;

/**
 * 
 * @author Baselscs
 *A class that is responsible for sending messages to clients,system users
 */
public class Mailer {
	
	public static java.text.SimpleDateFormat sdf = 
			new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void send(String to, String subject, String msg) {

		final String user = "cps.reminders.18@gmail.com";// change accordingly
		final String pass = "cps123456789";
		
		

		// 1st step) Get the session object
		Properties props = new Properties();
		 props.setProperty("mail.transport.protocol", "smtp");     
		    props.setProperty("mail.host", "smtp.gmail.com");  
		    props.put("mail.smtp.auth", "true");  
		    props.put("mail.smtp.port", "465");  
		    props.put("mail.debug", "true");  
		    props.put("mail.smtp.socketFactory.port", "465");  
		    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
		    props.put("mail.smtp.socketFactory.fallback", "false");  


		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		});
		// 2nd step)compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(msg);

			// 3rd step)send message
			Transport.send(message);

			//System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}
	
	public static void sendLateEmail(Reservation res){
		
		
		String subject = "Late for parking";
		String msg = "Hello " + res.getUsername()  + ",\n"
				+ "According to the information we have, You have reserved a parking on " + res.getLotName() + " Parking Lot "
						+ "at " + sdf.format(res.getStart()).split(" ")[1] + " today.\n"
								+ "You got this email as a reminder for being late.\nYou are asked to head to the lot within 30 minutes before the reservation being canceled.\nThank you,\nCPS team.";
		Mailer.send(res.getEmail(), subject, msg);
		return;
	}
	
	public static void sendSubscriptionRenewEmail(JSONObject subscription){
		String subject = "Subscription Renewal";
		String lotName;
		try {
			lotName = subscription.getString("type").equals("R") ? 
									"your Routine subscription at " +	subscription.getString("lotName") + " Parking Lot "
														: "your Full subscription";
		
		
			String msg = "Hello " + subscription.getString("username")  + ",\n"
					+ "According to the information we have, " + lotName
							+" will expire in " + subscription.getString("end")+ ".\n"
							+ "If you wish to continue using our services, please renew your subscription.\n\n"
							+ "Thank you,\nCPS team.";
			
			Mailer.send(subscription.getString("email"), subject, msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public static void sendExcessionEmail(JSONObject subscription) {
		try {
		String subject = "Parking Excession Alert";
		String msg = "Hello " + subscription.getString("username")  + ",\n"
				+ "According to the information we have, You have exceeded your parking limit.\n"
						+ "Your Last entry was at " + subscription.getString("lastEntry") +", which is before 14 days (Or more).\n"
								+ "You are asked to come and collect your car as fast as possible.\nThank you,\nCPS team.";
		
			Mailer.send(subscription.getString("email"), subject, msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void sendSubscriptionRenewEmailB(JSONObject subscription) {
		String subject = "Subscription Renewal";
		
		try {
			String msg = "Hello " + subscription.getString("username")  + ",\n"
					+ "According to the information we have, " + "your Business Subscription for company " + subscription.getString("company")
							+" will expire in " + subscription.getString("end")+ ".\n"
							+ "If you wish to continue using our services, please renew your subscription.\n\n"
							+ "Thank you,\nCPS team.";
			
			Mailer.send(subscription.getString("email"), subject, msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
}