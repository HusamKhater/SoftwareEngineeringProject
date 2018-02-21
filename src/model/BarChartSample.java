package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
 
public class BarChartSample {
    final CategoryAxis xAxis;
    final NumberAxis yAxis;
    final BarChart<String,Number> bc;
    /**
     * weekly activity Constructor  
     * @param title
     * @param parkingJO
     * @param startDay
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public BarChartSample(String title, JSONObject parkingJO, int startDay){
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        
        bc = new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle(title);
        JSONArray decilesJA = new JSONArray();
        
    	if(title.equals("Activated Parking Reservation")){
			try {
				decilesJA = parkingJO.getJSONArray("actualizedDailyMeans");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(title.equals("Canceled Parking Reservation")){
			try {
				decilesJA = parkingJO.getJSONArray("canceledDailyMeans");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				decilesJA = parkingJO.getJSONArray("lateDailyMeans");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
        xAxis.setLabel("Day");       
        yAxis.setLabel("Deciles");
    	XYChart.Series series1 = new XYChart.Series(); 
        for(int i = startDay; i < startDay + 7; i ++){      
            try {
				series1.getData().add(new XYChart.Data(getDayByInt(i%7), decilesJA.get(i%7)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        bc.getData().addAll(series1);  
        bc.setLegendVisible(false);
    }
    
	/**
	 * performance Constructor 
	 * @param title
	 * @param businessSub
	 * @param restSub
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public BarChartSample(String title, int businessSub, int restSub){
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        
        bc = new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle(title);
        xAxis.setLabel("Subscription Type");       
        yAxis.setLabel("Number Of Subs");

    	XYChart.Series series1 = new XYChart.Series(); 
    	series1.getData().add(new XYChart.Data("Business", businessSub));
    	series1.getData().add(new XYChart.Data("Full & Routine", restSub));
			

    	/*int maxBarWidth = 100;
    	int minCategoryGap = 5;
    	double barWidth=0;
        do{
            double catSpace = xAxis.getCategorySpacing();
            double avilableBarSpace = catSpace - (bc.getCategoryGap() + bc.getBarGap());
            barWidth = (avilableBarSpace / bc.getData().size()) - bc.getBarGap();
            if (barWidth >maxBarWidth){
                avilableBarSpace=(maxBarWidth + bc.getBarGap())* bc.getData().size();
                bc.setCategoryGap(catSpace-avilableBarSpace-bc.getBarGap());
            }
        } while(barWidth>maxBarWidth);

        do{
            double catSpace = xAxis.getCategorySpacing();
            double avilableBarSpace = catSpace - (minCategoryGap + bc.getBarGap());
            barWidth = Math.min(maxBarWidth, (avilableBarSpace / bc.getData().size()) - bc.getBarGap());
            avilableBarSpace=(barWidth + bc.getBarGap())* bc.getData().size();
            bc.setCategoryGap(catSpace-avilableBarSpace-bc.getBarGap());
        } while(barWidth < maxBarWidth && bc.getCategoryGap()>minCategoryGap);
*/    	
    	
    	
        bc.getData().addAll(series1);  
        bc.setLegendVisible(false);
    }
    
    

    
    public BarChart<String, Number> getBc() {
    	return bc;
	}




	public String getMedian(){
    	return null;
    }
	
	public String getDayByInt(int number){
		if(number == 0) return "Sunday";
		if(number == 1) return "Monday";
		if(number == 2) return "Tuesday";
		if(number == 3) return "Wednesday";
		if(number == 4) return "Thursday";
		if(number == 5) return "Friday";
		if(number == 6) return "Saturday";
		return "";
	}
}