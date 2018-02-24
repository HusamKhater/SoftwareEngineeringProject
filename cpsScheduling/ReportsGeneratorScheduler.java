package cpsScheduling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Date;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cpsServer.Util;

/**
 * 
 * @author Baselscs
 *TimerTask class responsible for generating statistical info weekly
 */
public class ReportsGeneratorScheduler extends TimerTask{

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		
		Date now = new Date();
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		now.setDate(now.getDate());
		
		
		Date lastWeek = new Date(now.getTime());
		lastWeek.setDate(now.getDate()-7);
		
		JSONObject report = new JSONObject();
		JSONArray lots = Util.getLots();
		
		try{
			
			for(int i = 0; i < lots.length(); i++){
				JSONObject lotReport = getWeeklyDataByLotName(lots.getJSONObject(i).getString("lotName"), now, lastWeek);
				report.put(lots.getJSONObject(i).getString("lotName"), lotReport);
			}
			report.put("startDay", now.getDay());
			//uordered data
			
			
			String reportQuery = "Insert into WeeklyReports (start,end,content) values(?,?,?)";
			Connection con = Util.getConnection();
			PreparedStatement ps = con.prepareStatement(reportQuery);
			ps.setString(1, Util.sdf.format(lastWeek));
			ps.setString(2, Util.sdf.format(now));
			ps.setString(3, report.toString());
			
			ps.execute();
			
			ps.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/*private void print(JSONArray[] arr) {
		for(int i = 0; i < arr.length; i++){
			System.out.println(arr[i].toString());
		}
		System.out.println("############\n\n");
		
	}*/
	
	@SuppressWarnings("deprecation")
	public JSONObject getWeeklyDataByLotName(String lotName,Date now,Date lastWeek){
		JSONObject repor = new JSONObject();
		
		
		JSONObject info = null; 
		try {
			info = Util.getAllReservationsBetweenByLotName(lastWeek, now,lotName);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*if(lotName.equals("Carmel")){
			System.out.println(info);
		}*/
		//index i contains objects of same day
		JSONArray[] ACTUALIZED = new JSONArray[7];
		JSONArray[] CANCELED = new JSONArray[7];
		JSONArray[] LATE = new JSONArray[7];
		
		initilize(ACTUALIZED);
		initilize(CANCELED);
		initilize(LATE);
		try{
			
			//uordered data
			JSONArray canceled = info.getJSONArray("canceled");
			JSONArray actualized = info.getJSONArray("actualized");
			JSONArray late = info.getJSONArray("late");
			
			for(int i = 0; i < canceled.length(); i++){
				
				CANCELED[Util.sdf.parse(canceled.getJSONObject(i).getString("start")).getDay()%7].put(canceled.getJSONObject(i));
				
			}
			//System.out.println(actualized.toString());
			for(int i = 0; i < actualized.length(); i++){
				JSONObject obj = actualized.getJSONObject(i);
				//System.out.println(Util.sdf.parse(obj.getString("start")).getDay()%7);
				ACTUALIZED[Util.sdf.parse(obj.getString("start")).getDay()%7].put(obj);
			
			}
			for(int i = 0; i < late.length(); i++){
				
				LATE[Util.sdf.parse(late.getJSONObject(i).getString("start")).getDay()%7].put(late.getJSONObject(i));
			
			}
			//System.out.println(ACTUALIZED.toString() + " \n\n" + LATE.toString()+" \n\n" + CANCELED.toString());
			//Mean for all
			
			//print(ACTUALIZED);
			//print(CANCELED);
			//print(LATE);
			double actualizedWeeklyMean = actualized.length()/7.0;
			double canceledWeeklyMean = canceled.length()/7.0;
			double lateWeeklyMean = late.length()/7.0;
			
			
			//DailyMeans
			double[] actualizedDailyMeans = new double[7];
			double[] canceledDailyMeans = new double[7];
			double[] lateDailyMeans = new double[7];
			
			for(int i = 0; i < 7; i++){
				//System.out.println("ACTUALIZED[i].length() " + ACTUALIZED[i].length() + " ____ actualized.length()" + actualized.length());
				actualizedDailyMeans[i] = ACTUALIZED[i].length()/(double)(actualized.length() == 0? 1 : actualized.length());
			}
			for(int i = 0; i < 7; i++){
				canceledDailyMeans[i] = CANCELED[i].length()/(double)(canceled.length() == 0? 1: canceled.length());
			}
			for(int i = 0; i < 7; i++){
				lateDailyMeans[i] = LATE[i].length()/(double)(late.length() == 0? 1 : late.length());
			}
			
			//Medians
			int actualizedWeeklyMedian = (getMedian(ACTUALIZED));
			int canceledWeeklyMedian = (getMedian(CANCELED));
			int lateWeeklyMedian = (getMedian(LATE));
			
			
			repor.put("actualizedWeeklyMean", actualizedWeeklyMean)
				 .put("canceledWeeklyMean", canceledWeeklyMean)
				 .put("lateWeeklyMean", lateWeeklyMean)
				 .put("actualizedDailyMeans",actualizedDailyMeans)
				 .put("canceledDailyMeans", canceledDailyMeans)
				 .put("lateDailyMeans", lateDailyMeans)
				 .put("actualizedWeeklyMedian", actualizedWeeklyMedian)
				 .put("canceledWeeklyMedian", canceledWeeklyMedian)
				 .put("lateWeeklyMedian", lateWeeklyMedian)
				 ;
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return repor;

	}

	private void initilize(JSONArray[] arr) {
		for(int i = 0; i < arr.length; i++ ){
			arr[i] = new JSONArray();
		}
		
	}


	private int getMedian(JSONArray[] arr) {
		int[] _arr = new int[7];
		for(int i = 0; i < 7; i++){
			_arr[i] = arr[i].length();
		}
		Arrays.sort(_arr);
		double median = _arr[_arr.length/2];
		
		for(int i = 0; i < 7; i++){
			if(median == arr[i].length()){
				return i;
			}
		}
		return 0;
	}

}
