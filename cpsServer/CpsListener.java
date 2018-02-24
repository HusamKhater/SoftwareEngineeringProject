package cpsServer;



import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionListener;


import cpsScheduling.RemindersManager;
import cpsScheduling.ReportsGeneratorScheduler;
import cpsScheduling.StatusUpdater;


/**
 * 
 * @author Baselscs
 * Web listener, responsible for scheduling and initializing the DB connection
 */
@WebListener
public class CpsListener implements ServletContextListener, ServletRequestListener, HttpSessionListener, ServletRequestAttributeListener, HttpSessionActivationListener, ServletContextAttributeListener, HttpSessionAttributeListener, HttpSessionBindingListener {

  
	

	

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    private ScheduledExecutorService scheduler;

	public void contextInitialized(ServletContextEvent sce)   { 
    	
		Util.initializeConnection();
		
		//Reminders
    	scheduler = Executors.newSingleThreadScheduledExecutor();
    	scheduler.scheduleAtFixedRate(new RemindersManager(), 0, 1, TimeUnit.DAYS);
		scheduler.scheduleAtFixedRate(new ReportsGeneratorScheduler(), 0, 7, TimeUnit.DAYS);
		scheduler.scheduleAtFixedRate(new StatusUpdater(), 0, 1, TimeUnit.DAYS);
    
    }

	


	
	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce)  { 
    	Util.closeConnection();
    	scheduler.shutdownNow();
    	RemindersManager.shutDown();
    }

	
	
	
}
