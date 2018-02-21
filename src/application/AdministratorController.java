package application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.BarChartSample;
import model.ParkingLot;
import model.SharedData;


public class AdministratorController {

    @FXML
    private Button currentSituationButton;

    @FXML
    private Label welcomeBanner;

    @FXML
    private BorderPane changePricesRequestsBorderPane;

    @FXML
    private ComboBox<String> parkLotNameComboBox;

    @FXML
    private ComboBox<String> parkLotNameComboBox1;

    @FXML
    private DatePicker toDateDP;

    @FXML
    private Button getReportButton;

    @FXML
    private Button getPerformanceReportButton;

    @FXML
    private Button signOutButton;

    @FXML
    private BorderPane reportsBorderPane;

    @FXML
    private DatePicker startDateWeekly;

    @FXML
    private Button reportsButton;

    @FXML
    private Text textInTopOfLogIn;

    @FXML
    private Button changePricesRequestsButton;
    
    @FXML // fx:id="changePricesRequestVbox"
    private VBox changePricesRequestVbox; // Value injected by FXMLLoader

    @FXML // fx:id="quarterReportsListVbox"
    private VBox quarterReportsListVbox; // Value injected by FXMLLoader
    
    @FXML // fx:id="quarterReportsButton"
    private Button quarterReportsButton; // Value injected by FXMLLoader

    @FXML // fx:id="quarterReportsBorderPane"
    private BorderPane quarterReportsBorderPane; // Value injected by FXMLLoader

    
    private ObservableList<String> myComboBoxData = FXCollections.observableArrayList();


	Alert informationAlert = new Alert(AlertType.INFORMATION);
	Alert errorAlert = new Alert(AlertType.ERROR);
	Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    
	/**
	 * sign out from system
	 * @param event
	 */
    @FXML
    void signOut(ActionEvent event) {
		JSONObject json = new JSONObject(), ret = new JSONObject();
		try {
			json.put("systemUsername", SharedData.getInstance().getCurrentSystemUser().get_username());
			json.put("cmd", "SignOut");
			ret = request(json, "Login");
			
			if(ret.getBoolean("result")){
				SharedData.getInstance().setCurrentSystemUser(null);
		
				Scene currentScene = signOutButton.getScene();
				Parent mainLayout = null;
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("MainView.fxml"));
				try {
					mainLayout = loader.load();
				} catch (IOException | NullPointerException e) {
		
					e.printStackTrace();
				}
		
				Scene scene = new Scene(mainLayout);
				Stage stage = (Stage) currentScene.getWindow();
				stage.setScene(scene);
			}else{
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

    }
    
    /**
     * approve to update costs that Suggested by Parking Lot Director 
     * @param e
     * @param reqID 
     */
    
    void aproveUpdateCost(ActionEvent e, int reqID){
    	
    	System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	JSONObject json = new JSONObject();
    	try {
			json.put("requestID", reqID);
			json.put("performChange", true);
			json.put("cmd","handleChangeRequest");
			
			JSONObject ret = request(json, "SystemUserServices");
			System.out.println(ret);
			if(ret.getBoolean("result")){
				
				System.out.println("Aproving the new Costs saved successfully.\nThe new Costs is");
				
				JSONObject upd = request(null, "SystemQueries");
				System.out.println(upd);
				System.out.println(upd.getJSONArray("Costs"));
				loadChangePricesRequestsBorderPane(null);
				
			}else{
				
				System.out.println("ERROR @ Aproving the new Costs.");
				
			}
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	
    }
   
    /**
     * decline to update costs that Suggested by Parking Lot Director 
     * @param e
     * @param reqID
     */
    void declineUpdtae(ActionEvent e, int reqID){
    
    	JSONObject json = new JSONObject();
    	try {
			json.put("requestID", reqID);
			json.put("performChange", false);
			json.put("cmd","handleChangeRequest");
			
			JSONObject ret = request(json, "SystemUserServices");
			System.out.println(ret);
			if(ret.getBoolean("result")){
				
				System.out.println("Decling the new Costs saved successfully.\nThe new Costs is");
				
				JSONObject upd = request(null, "SystemQueries");
				System.out.println(upd);
				System.out.println(upd.getJSONArray("Costs"));
				
				loadChangePricesRequestsBorderPane(null);
				
			}else{
				
				System.out.println("ERROR @ Declining the new Costs.");
				
			}
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
    	
    }

    /**
     * this method sends request to parking lot director and asks him  to send the lot's Current Situation.
     * The administrator will get a mail that contains  PDF of parking lot current Situation.
     * @param event
     */
    @FXML
    void getCurrentSituation(ActionEvent event) {
    	
    	String _lotName = parkLotNameComboBox.getValue();
    	if(_lotName == null){
    		informationAlert.setTitle("Warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("You must choose a parking lot");
			informationAlert.showAndWait();
			return;
    	}
    	
    	else{
    		JSONObject json = new JSONObject();
    		JSONObject ret = new JSONObject();

    		try {
    				json.put("lotName", _lotName);
    				json.put("requestType", "lotCurrentSituation");
    				json.put("cmd", "managementRequest");
    				ret = request(json, "SystemUserServices");
    				System.out.println(ret);
    				if (ret.getBoolean("result")) {
    					informationAlert.setTitle("Your request was sent successfully");
    					informationAlert.setHeaderText(null);
    					informationAlert.setContentText(
    							"Your request was sent successfully.\nPlease wait for the parking lot "
    							+ "manager to send response in a mail");
    					informationAlert.showAndWait();
    				}else{
    					if(ret.getString("info").equals("Request already exist")){
        					informationAlert.setTitle("Pending request");
        					informationAlert.setHeaderText(null);
        					informationAlert.setContentText(
        							"Previous request was send earlier.\nPlease wait for the parking lot "
        							+ "manager to send response in a mail");
        					informationAlert.showAndWait();
    					}
    				}
    			
    		} catch(JSONException e1) {
    			e1.printStackTrace();
    		}

    	}
		
    }
 
    
    /**
     * method that gives a report about the performance of all the parking lots, it counts the number 
     * of business subscriptions (multiple cars) and the number of the regular routine 
     * and full subscriptions(single car) 
     * @param event
     */
    @FXML
    void getPerformanceReport(ActionEvent event){
    	JSONObject json = new JSONObject();
		JSONObject ret = new JSONObject();
		try {
			json.put("cmd", "exclusiveInfo");
			ret = request(json, "ReportsGenerator");
			if(ret.getBoolean("result")){
				BarChartSample subscriptionsBarChart = new BarChartSample("Performance Report", ret.getJSONObject("info").getInt("business")
						,ret.getJSONObject("info").getInt("allSubscriptions"));
				VBox vB = new VBox();
    			vB.getChildren().add(subscriptionsBarChart.getBc());
    			Stage popupwindow=new Stage();
    			popupwindow.initModality(Modality.APPLICATION_MODAL);
    			popupwindow.setTitle("Performance Report");
    			
		        Scene scene  = new Scene(vB,300,400);
		        popupwindow.setScene(scene);
				popupwindow.showAndWait();

				System.out.println(ret.toString());
				
			}
		}catch(JSONException e) {
    			e.printStackTrace();
    		}

    }
    
    /**
     * method that makes a report which calculates the mean and the median and the deviation of: 
     * activated parking reservations, canceled parking reservations, late customers.
     * @param event
     */
    
    @FXML
    void getReport(ActionEvent event) {
    	String _lotName = parkLotNameComboBox1.getValue();
    	if(_lotName == null){
    		informationAlert.setTitle("Warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("You must choose a parking lot");
			informationAlert.showAndWait();
			return;
    	}
    	
    	LocalDate ld = startDateWeekly.getValue();
		Calendar cal = Calendar.getInstance();
    	if(ld == null){
    		informationAlert.setTitle("Warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("You must choose a date");
			informationAlert.showAndWait();
			return;
    	}else{
			Instant instant = Instant.from(ld.atStartOfDay(ZoneId.systemDefault()));
			Date date = Date.from(instant);
			cal = toCalendar(date);
    	}
    		long dateLong = cal.getTime().getTime();
    		JSONObject json = new JSONObject();
    		JSONObject ret = new JSONObject();
    		try {
    			json.put("cmd", "getWeeklyReport");
    			json.put("date", dateLong);
    			ret = request(json, "ReportsGenerator");
    			if(ret.getBoolean("result")){
    				System.out.println(ret.toString());
    			
    			int startDay = ret.getJSONObject("info").getJSONObject("content").getInt("startDay");
    			JSONObject parkingLotJSON = ret.getJSONObject("info").getJSONObject("content").getJSONObject(_lotName);
    			System.out.println(parkingLotJSON.toString());
    			BarChartSample activatedParkingReservationBC = new BarChartSample("Activated Parking Reservation",
    					parkingLotJSON, startDay);
    			BarChartSample canceledReservationsBC = new BarChartSample("Canceled Parking Reservation",
    					parkingLotJSON, startDay);
    			BarChartSample lateParkingBC = new BarChartSample("Late",
    					parkingLotJSON, startDay);
    			
    			VBox vB = new VBox();
    			ScrollPane sp = new ScrollPane();
    			

				HBox hb1 = new HBox();
				hb1.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;-fx-pref-width: 780;");

				HBox hb2 = new HBox();
				hb2.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;-fx-pref-width: 780;");
				
				HBox hb3 = new HBox();
				hb3.setStyle("-fx-padding: 10 0 0 40");
				HBox hb4 = new HBox();
				hb4.setStyle("-fx-padding: 10 0 0 40");
				HBox hb5 = new HBox();
				hb5.setStyle("-fx-padding: 10 0 0 40");
				
				Label activMedian = new Label("Median: " + 
						Integer.toString(parkingLotJSON.getInt("actualizedWeeklyMedian")) +"\t\tMean: ");
				Label activeMean = new Label(Double.toString(parkingLotJSON.getDouble("actualizedWeeklyMean")));
				activMedian.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				activMedian.setTextFill(Color.BLACK);
				activeMean.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				activeMean.setTextFill(Color.BLACK);
				hb3.getChildren().addAll(activMedian, activeMean);
				
				Label cancelMedian = new Label("Median: " +
						Integer.toString(parkingLotJSON.getInt("canceledWeeklyMedian"))+"\t\tMean: ");
				Label cancelMean = new Label(Double.toString(parkingLotJSON.getDouble("canceledWeeklyMean")));
				cancelMedian.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				cancelMedian.setTextFill(Color.BLACK);
				cancelMean.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				cancelMean.setTextFill(Color.BLACK);
				hb4.getChildren().addAll(cancelMedian, cancelMean);
				
				Label lateMedian = new Label("Median: " +
						Integer.toString(parkingLotJSON.getInt("lateWeeklyMedian"))+"\t\tMean: ");
				Label lateMean = new Label(Double.toString(parkingLotJSON.getDouble("lateWeeklyMean")));
				lateMedian.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				lateMedian.setTextFill(Color.BLACK);
				lateMean.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				lateMean.setTextFill(Color.BLACK);
				hb5.getChildren().addAll(lateMedian, lateMean);
				
    			vB.getChildren().add(activatedParkingReservationBC.getBc());
    			vB.getChildren().add(hb3);
    			vB.getChildren().add(hb1);
    			vB.getChildren().add(canceledReservationsBC.getBc());
    			vB.getChildren().add(hb4);
    			vB.getChildren().add(hb2);
    			vB.getChildren().add(lateParkingBC.getBc());
    			vB.getChildren().add(hb5);
    			sp.setStyle("-fx-pref-width:800;-fx-pref-height:600;");
    			sp.setContent(vB);
    			Stage popupwindow=new Stage();
    			popupwindow.initModality(Modality.APPLICATION_MODAL);
    			popupwindow.setTitle("Weekly Activities Report");
    			
		        Scene scene  = new Scene(sp,800,600);
		        popupwindow.setScene(scene);
				popupwindow.showAndWait();

    		}else {
        		informationAlert.setTitle("Report Not Found");
    			informationAlert.setHeaderText(null);
    			informationAlert.setContentText("Activity report for this date is not found");
    			informationAlert.showAndWait();
    			return;
    		}
    		}catch(JSONException e) {
    			e.printStackTrace();
    		}
    	
    	
    }
    
    /**
     * method that receives all the quarter reports that the parking lot director prepared, 3 types of reports:
     * reservations, complaints, disabled spots.
     * @param event
     */

    @FXML
    void loadQuarterReporsBorderPane(ActionEvent event) {
    	reportsBorderPane.setVisible(false);
    	changePricesRequestsBorderPane.setVisible(false); 
    	quarterReportsBorderPane.setVisible(true);

    	quarterReportsButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	quarterReportsButton.getStyleClass().add("pressedButton");
    	reportsButton.getStyleClass().removeAll("pressedButton", "focus");
    	reportsButton.getStyleClass().add("loginView-buttons");
    	changePricesRequestsButton.getStyleClass().removeAll("pressedButton", "focus");
    	changePricesRequestsButton.getStyleClass().add("loginView-buttons");
    
    	int length = quarterReportsListVbox.getChildren().size();
		quarterReportsListVbox.getChildren().remove(0, length);
		
		JSONObject json = new JSONObject();
		JSONObject ret = new JSONObject();
		try {
			json.put("cmd", "getQuarterReady");
			ret = request(json, "ReportsGenerator");
			System.out.println(ret.toString());
			if(ret.getBoolean("result")){
				System.out.println(ret.toString());
				JSONArray ja = ret.getJSONArray("info");
				for(int i = 0; i < ja.length(); i++){
					if(((JSONObject) ja.get(i)).getBoolean("jan")){
		        	    Label quarterName = new Label("Januray - March");
		        	    quarterName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        	    Label lotName = new Label(((JSONObject) ja.get(i)).getString("lotName"));
		        		lotName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        		
						HBox hb = new HBox();
						hb.getChildren().add(quarterName);
						hb.getChildren().add(lotName);
						hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
								+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
					
						Button resrvationReportButton = new Button("Reservation Report");
						String css = getClass().getResource("application.css").toExternalForm();
						resrvationReportButton.getStylesheets().clear();
						resrvationReportButton.getStylesheets().add(css);
						resrvationReportButton.setOnAction(e -> resrvationReportCallBack(e, lotName.getText(), 1));
						resrvationReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(resrvationReportButton);
						
						HBox tempo1 = new HBox();
						tempo1.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo1);
						
						Button complaintsReportButton = new Button("Complaints Report");
						complaintsReportButton.getStylesheets().clear();
						complaintsReportButton.getStylesheets().add(css);
						complaintsReportButton.setOnAction(e -> complaintsReportCallBack(e, lotName.getText(),1));
						complaintsReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(complaintsReportButton);
						
						HBox tempo2 = new HBox();
						tempo2.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo2);
						
						Button disabledSpotsReporttButton = new Button("Disabled Spots Report");
						disabledSpotsReporttButton.getStylesheets().clear();
						disabledSpotsReporttButton.getStylesheets().add(css);
						disabledSpotsReporttButton.setOnAction(e -> disabledSpotsReportsCallBack(e, lotName.getText(),1));
						disabledSpotsReporttButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(disabledSpotsReporttButton);
						quarterReportsListVbox.getChildren().add(hb);
					}
					if(((JSONObject) ja.get(i)).getBoolean("apr")){
		        	    Label quarterName = new Label("April - June");
		        	    quarterName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        	    Label lotName = new Label(((JSONObject) ja.get(i)).getString("lotName"));
		        		lotName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        		
						HBox hb = new HBox();
						hb.getChildren().add(quarterName);
						hb.getChildren().add(lotName);
						hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
								+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
					
						Button resrvationReportButton = new Button("Reservation Report");
						String css = getClass().getResource("application.css").toExternalForm();
						resrvationReportButton.getStylesheets().clear();
						resrvationReportButton.getStylesheets().add(css);
						resrvationReportButton.setOnAction(e -> resrvationReportCallBack(e, lotName.getText(),2));
						resrvationReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(resrvationReportButton);
						
						HBox tempo1 = new HBox();
						tempo1.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo1);
						
						Button complaintsReportButton = new Button("Complaints Report");
						complaintsReportButton.getStylesheets().clear();
						complaintsReportButton.getStylesheets().add(css);
						complaintsReportButton.setOnAction(e -> complaintsReportCallBack(e, lotName.getText(),2));
						complaintsReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(complaintsReportButton);
						
						HBox tempo2 = new HBox();
						tempo2.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo2);
						
						Button disabledSpotsReporttButton = new Button("Disabled Spots Report");
						disabledSpotsReporttButton.getStylesheets().clear();
						disabledSpotsReporttButton.getStylesheets().add(css);
						disabledSpotsReporttButton.setOnAction(e -> disabledSpotsReportsCallBack(e, lotName.getText(),2));
						disabledSpotsReporttButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(disabledSpotsReporttButton);
						quarterReportsListVbox.getChildren().add(hb);
					}
					if(((JSONObject) ja.get(i)).getBoolean("oct")){
		        	    Label quarterName = new Label("October - December");
		        	    quarterName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        	    Label lotName = new Label(((JSONObject) ja.get(i)).getString("lotName"));
		        		lotName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        		
						HBox hb = new HBox();
						hb.getChildren().add(quarterName);
						hb.getChildren().add(lotName);
						hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
								+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
					
						Button resrvationReportButton = new Button("Reservation Report");
						String css = getClass().getResource("application.css").toExternalForm();
						resrvationReportButton.getStylesheets().clear();
						resrvationReportButton.getStylesheets().add(css);
						resrvationReportButton.setOnAction(e -> resrvationReportCallBack(e, lotName.getText(),4));
						resrvationReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(resrvationReportButton);
						
						HBox tempo1 = new HBox();
						tempo1.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo1);
						
						Button complaintsReportButton = new Button("Complaints Report");
						complaintsReportButton.getStylesheets().clear();
						complaintsReportButton.getStylesheets().add(css);
						complaintsReportButton.setOnAction(e -> complaintsReportCallBack(e, lotName.getText(),4));
						complaintsReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(complaintsReportButton);
						
						HBox tempo2 = new HBox();
						tempo2.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo2);
						
						Button disabledSpotsReporttButton = new Button("Disabled Spots Report");
						disabledSpotsReporttButton.getStylesheets().clear();
						disabledSpotsReporttButton.getStylesheets().add(css);
						disabledSpotsReporttButton.setOnAction(e -> disabledSpotsReportsCallBack(e, lotName.getText(),4));
						disabledSpotsReporttButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(disabledSpotsReporttButton);
						quarterReportsListVbox.getChildren().add(hb);
					}
					if(((JSONObject) ja.get(i)).getBoolean("jul")){
		        	    Label quarterName = new Label("July - September");
		        	    quarterName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        	    Label lotName = new Label(((JSONObject) ja.get(i)).getString("lotName"));
		        		lotName.setStyle("-fx-pref-width: 120; -fx-padding: 3.5 0 0 0");
		        		
						HBox hb = new HBox();
						hb.getChildren().add(quarterName);
						hb.getChildren().add(lotName);
						hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
								+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
					
						Button resrvationReportButton = new Button("Reservation Report");
						String css = getClass().getResource("application.css").toExternalForm();
						resrvationReportButton.getStylesheets().clear();
						resrvationReportButton.getStylesheets().add(css);
						resrvationReportButton.setOnAction(e -> resrvationReportCallBack(e, lotName.getText(),3));
						resrvationReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(resrvationReportButton);
						
						HBox tempo1 = new HBox();
						tempo1.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo1);
						
						Button complaintsReportButton = new Button("Complaints Report");
						complaintsReportButton.getStylesheets().clear();
						complaintsReportButton.getStylesheets().add(css);
						complaintsReportButton.setOnAction(e -> complaintsReportCallBack(e, lotName.getText(),3));
						complaintsReportButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(complaintsReportButton);
						
						HBox tempo2 = new HBox();
						tempo2.setStyle("-fx-pref-width: 10;");
						hb.getChildren().add(tempo2);
						
						Button disabledSpotsReporttButton = new Button("Disabled Spots Report");
						disabledSpotsReporttButton.getStylesheets().clear();
						disabledSpotsReporttButton.getStylesheets().add(css);
						disabledSpotsReporttButton.setOnAction(e -> disabledSpotsReportsCallBack(e, lotName.getText(),3));
						disabledSpotsReporttButton.getStyleClass().add("loginView-buttons");
						hb.getChildren().add(disabledSpotsReporttButton);
						quarterReportsListVbox.getChildren().add(hb);
					}			
					
	    		} 
			}
			
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    }
    
    /**
     * get complaint quarter report by the relevant lot name. 
     * @param e
     * @param lotName
     * @param j 
     * @return
     */
   private Object complaintsReportCallBack(ActionEvent e, String lotName, int j) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		JSONObject ret = new JSONObject();
		try {
			json.put("cmd", "FiscalQuarter");
			json.put("lotName", lotName);
			json.put("quarter", j);
			ret = request(json, "ReportsGenerator");
			if(ret.getBoolean("result")){
				Stage popupwindow=new Stage();
				popupwindow.initModality(Modality.APPLICATION_MODAL);
				popupwindow.setTitle("total");
				
				HBox header = new HBox();
				header.setAlignment(Pos.CENTER);
				header.setStyle("-fx-pref-width:800;-fx-pref-height:50;-fx-background-color:#0a304e;"
						+ "-fx-padding:10;");
				
				VBox vB = new VBox();
				String s = "Total Complaints: ";
				s += Integer.toString(ret.getJSONObject("report").getJSONArray("complaints").length());
				
				/** Complaint Section **/
				
				Label totalComplaints = new Label(s);
				totalComplaints.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				totalComplaints.setTextFill(Color.WHITE);
				header.getChildren().add(totalComplaints);
				HBox complaints = new HBox();
				
				
				complaints.setStyle("-fx-pref-width:790;-fx-pref-height:20;-fx-padding:5;"
						+ "-fx-background-color:#7499b5");
				String complaintsHeader = "Complaints";
				Label complaintsHeaderLabel = new Label(complaintsHeader);
				complaintsHeaderLabel.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				complaintsHeaderLabel.setTextFill(Color.WHITE);
				complaints.getChildren().add(complaintsHeaderLabel);
				
				

				VBox everythingInReservation = new VBox();
				everythingInReservation.getChildren().add(complaints);
				
				int complaintsArraylength = ret.getJSONObject("report").getJSONArray("complaints").length();
		        for(int i = 0; i < complaintsArraylength; i++){
		        	
					HBox complaintsHbox = new HBox();
					complaintsHbox.setStyle("-fx-pref-width:790;-fx-pref-height:30;-fx-padding:5;"
							+ "-fx-border-style: solid inside;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; ");
					String comId = "Complaint Id: " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("complaints").get(i)).getInt("complaintID"));
					String start = "Date: " +((JSONObject) ret.getJSONObject("report").getJSONArray("complaints").get(i)).getString("date");
					String carNumber = "Car Number: " +((JSONObject) ret.getJSONObject("report").getJSONArray("complaints").get(i)).getString("carNumber");
					String refund = "Refund: " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("complaints").get(i)).getInt("refund"));
					String handled = "";
					if(((JSONObject) ret.getJSONObject("report").getJSONArray("complaints").get(i)).getBoolean("handled")){
						handled = "Status: Handled";
					}else{
						handled = "Status: Not Handled";
					}
					String content = comId + "        " + start +"        " + carNumber +"        " + handled+"        " + refund;
					complaintsHbox.getChildren().add(new Label(content));
					everythingInReservation.getChildren().add(complaintsHbox);
		        }

				
				vB.getChildren().add(header);
		        ScrollPane sp = new ScrollPane();
				sp.setStyle("-fx-pref-width:800; -fx-pref-height:550;");
				sp.setContent(everythingInReservation);
		        vB.getChildren().add(sp);

		        Scene scene  = new Scene(vB,800,600);
		        popupwindow.setScene(scene);
				popupwindow.showAndWait();

				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

   /**
    * get reservations quarter report by the relevant lot name. 
    * @param e
    * @param lotName
    * @param j 
    * @return
    */
private Object resrvationReportCallBack(ActionEvent e, String lotName, int j) {
		// TODO Auto-generated method stub
		System.out.println(lotName);
		JSONObject json = new JSONObject();
		JSONObject ret = new JSONObject();
		try {			
			json.put("cmd", "FiscalQuarter");
			json.put("lotName", lotName);
			json.put("quarter", j);
			ret = request(json, "ReportsGenerator");
			if(ret.getBoolean("result")){
				
				System.out.println(ret.toString());
				Stage popupwindow=new Stage();
				popupwindow.initModality(Modality.APPLICATION_MODAL);
				popupwindow.setTitle("total");
				
				HBox header = new HBox();
				header.setAlignment(Pos.CENTER);
				header.setStyle("-fx-pref-width:800;-fx-pref-height:50;-fx-background-color:#0a304e;"
						+ "-fx-padding:10;");
				
				VBox vB = new VBox();
				String s = "Total Parking Reservation: ";
				s += Integer.toString(ret.getJSONObject("report").getJSONArray("reservations").length());
				s += "\t\t";
				s += "Total Routinely Subscription: ";
				s += Integer.toString(ret.getJSONObject("report").getJSONArray("routineSubscriptions").length());
				s += "\t\t";
				s += "Total Full Subscription: ";
				s += Integer.toString(ret.getJSONObject("report").getJSONArray("fullSubscriptions").length());
				
				/** Parking Reservation Section **/
				
				Label totalReservation = new Label(s);
				totalReservation.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				totalReservation.setTextFill(Color.WHITE);
				header.getChildren().add(totalReservation);
				HBox parkingReservations = new HBox();
				
				
				parkingReservations.setStyle("-fx-pref-width:780;-fx-pref-height:20;-fx-padding:5;"
						+ "-fx-background-color:#7499b5");
				String parkingReservationsHeader = "Parking Reservations";
				Label parkingReservationsHeaderLabel = new Label(parkingReservationsHeader);
				parkingReservationsHeaderLabel.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				parkingReservationsHeaderLabel.setTextFill(Color.WHITE);
				parkingReservations.getChildren().add(parkingReservationsHeaderLabel);
				
				
				
				VBox everythingInReservation = new VBox();
				everythingInReservation.getChildren().add(parkingReservations);
				int reservationArraylength = ret.getJSONObject("report").getJSONArray("reservations").length();
		        for(int i = 0; i < reservationArraylength; i++){
		        	
					HBox parkingReservationsHbox = new HBox();
					parkingReservationsHbox.setStyle("-fx-pref-width:700;-fx-pref-height:30;-fx-padding:5;"
							+ "-fx-border-style: solid inside;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; ");
					String resId = "Red Id: " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("reservations").get(i)).getInt("rid"));
					String start = "Start: " +((JSONObject) ret.getJSONObject("report").getJSONArray("reservations").get(i)).getString("start");
					String end = "End: " +((JSONObject) ret.getJSONObject("report").getJSONArray("reservations").get(i)).getString("end");
					String carNumber = "Car Number: " +((JSONObject) ret.getJSONObject("report").getJSONArray("reservations").get(i)).getString("carNumber");
					String content = resId +"        " + start +"        " + end +"        " + carNumber;
					parkingReservationsHbox.getChildren().add(new Label(content));
					everythingInReservation.getChildren().add(parkingReservationsHbox);
		        }
		        
		        /** Parking Reservation Section **/

		        /** Routinely Section **/

				HBox routinelies = new HBox();
				
				
				routinelies.setStyle("-fx-pref-width:780;-fx-pref-height:20;-fx-padding:5;"
						+ "-fx-background-color:#7499b5");
				String routinelySubscriptionsHeader = "Routinely Subscriptions";
				Label routinelySubscriptionsHeaderLabel = new Label(routinelySubscriptionsHeader);
				routinelySubscriptionsHeaderLabel.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				routinelySubscriptionsHeaderLabel.setTextFill(Color.WHITE);
				routinelies.getChildren().add(routinelySubscriptionsHeaderLabel);
				
				everythingInReservation.getChildren().add(routinelies);
				int routineArraylength = ret.getJSONObject("report").getJSONArray("routineSubscriptions").length();
		        for(int i = 0; i < routineArraylength; i++){
		        	
					HBox routineSubscriptionsHbox = new HBox();
					routineSubscriptionsHbox.setStyle("-fx-pref-width:700;-fx-pref-height:30;-fx-padding:5;"
							+ "-fx-border-style: solid inside;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; ");
					String resId = "Sub Id: " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("routineSubscriptions").get(i)).getInt("rsid"));
					String start = "Start: " +((JSONObject) ret.getJSONObject("report").getJSONArray("routineSubscriptions").get(i)).getString("start");
					String end = "End: " +((JSONObject) ret.getJSONObject("report").getJSONArray("routineSubscriptions").get(i)).getString("end");
					String leaveHour = "Leaving Hour: " +((JSONObject) ret.getJSONObject("report").getJSONArray("routineSubscriptions").get(i)).getString("leaveHour");
					String carNumber = "Car Number: " +((JSONObject) ret.getJSONObject("report").getJSONArray("routineSubscriptions").get(i)).getString("carNumber");
					String type = "";
					if(((JSONObject) ret.getJSONObject("report").getJSONArray("routineSubscriptions").get(i)).getBoolean("business")){
						type = "Type: Business";
					}else{
						type = "Type: Regular";
					}
						
					String content = resId +"        " + start +"        " + end + "        " + leaveHour + "        " + carNumber +"        " + type;
					routineSubscriptionsHbox.getChildren().add(new Label(content));
					everythingInReservation.getChildren().add(routineSubscriptionsHbox);
		        }
		        
		        /** Routinely Section **/


		        /** Full Section **/

				HBox fullies = new HBox();
				
				
				fullies.setStyle("-fx-pref-width:780;-fx-pref-height:20;-fx-padding:5;"
						+ "-fx-background-color:#7499b5");
				String fullSubscriptionsHeader = "Full Subscriptions";
				Label fullSubscriptionsHeaderLabel = new Label(fullSubscriptionsHeader);
				fullSubscriptionsHeaderLabel.setFont(Font.font("Verdana",FontWeight.BOLD,10));
				fullSubscriptionsHeaderLabel.setTextFill(Color.WHITE);
				fullies.getChildren().add(fullSubscriptionsHeaderLabel);
				
				everythingInReservation.getChildren().add(fullies);
				int fullArraylength = ret.getJSONObject("report").getJSONArray("fullSubscriptions").length();
		        for(int i = 0; i < fullArraylength; i++){
		        	
					HBox fullSubscriptionsHbox = new HBox();
					fullSubscriptionsHbox.setStyle("-fx-pref-width:700;-fx-pref-height:30;-fx-padding:5;"
							+ "-fx-border-style: solid inside;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; ");
					String resId = "Sub Id: " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("fullSubscriptions").get(i)).getInt("fsid"));
					String start = "Start: " +((JSONObject) ret.getJSONObject("report").getJSONArray("fullSubscriptions").get(i)).getString("start");
					String end = "End: " +((JSONObject) ret.getJSONObject("report").getJSONArray("fullSubscriptions").get(i)).getString("end");
					String carNumber = "Car Number: " +((JSONObject) ret.getJSONObject("report").getJSONArray("fullSubscriptions").get(i)).getString("carNumber");
						
					String content = resId +"        " + start +"        " + end + "        " + carNumber;
					fullSubscriptionsHbox.getChildren().add(new Label(content));
					everythingInReservation.getChildren().add(fullSubscriptionsHbox);
		        }
		        
		        /** Routinely Section **/

		        
		        vB.getChildren().add(header);
		        ScrollPane sp = new ScrollPane();
				sp.setStyle("-fx-pref-width:800; -fx-pref-height:550;");
				sp.setContent(everythingInReservation);
		        vB.getChildren().add(sp);
				Scene scene  = new Scene(vB,800,600);
		        popupwindow.setScene(scene);
				popupwindow.showAndWait();
				
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

/**
 * get disabled spots quarter report by the relevant lot name. 
 * @param e
 * @param lotName
 * @param j 
 * @return
 */

private Object disabledSpotsReportsCallBack(ActionEvent e, String lotName, int j) {
		// TODO Auto-generated method stub
			JSONObject json = new JSONObject();
			JSONObject ret = new JSONObject();
			try {
				json.put("cmd", "FiscalQuarter");
				json.put("lotName", lotName);
				json.put("quarter", j);
				ret = request(json, "ReportsGenerator");
				if(ret.getBoolean("result")){
					Stage popupwindow=new Stage();
					popupwindow.initModality(Modality.APPLICATION_MODAL);
					popupwindow.setTitle("Disabled Spots Reports");
					
					HBox header = new HBox();
					header.setAlignment(Pos.CENTER);
					header.setStyle("-fx-pref-width:800;-fx-pref-height:50;-fx-background-color:#0a304e;"
							+ "-fx-padding:10;");
					
					VBox vB = new VBox();
					String s = "Total Disabled Spots: ";
					s += Integer.toString(ret.getJSONObject("report").getJSONArray("disabledSpots").length());
					
					/** Complaint Section **/
					
					Label totalDisableds = new Label(s);
					totalDisableds.setFont(Font.font("Verdana",FontWeight.BOLD,10));
					totalDisableds.setTextFill(Color.WHITE);
					header.getChildren().add(totalDisableds);
					HBox disableds = new HBox();
					
					
					disableds.setStyle("-fx-pref-width:790;-fx-pref-height:20;-fx-padding:5;"
							+ "-fx-background-color:#7499b5");
					String disabledsHeader = "Disabled Spots";
					Label disabledsHeaderLabel = new Label(disabledsHeader);
					disabledsHeaderLabel.setFont(Font.font("Verdana",FontWeight.BOLD,10));
					disabledsHeaderLabel.setTextFill(Color.WHITE);
					disableds.getChildren().add(disabledsHeaderLabel);
					
					

					VBox everythingInReservation = new VBox();
					everythingInReservation.getChildren().add(disableds);
					
					int disbaledsArraylength = ret.getJSONObject("report").getJSONArray("disabledSpots").length();
			        for(int i = 0; i < disbaledsArraylength; i++){
			        	
						HBox disabledsHbox = new HBox();
						disabledsHbox.setStyle("-fx-pref-width:790;-fx-pref-height:30;-fx-padding:5;"
								+ "-fx-border-style: solid inside;-fx-border-width: 0 0 2 0;"
							+ "-fx-border-color: #d0e6f8; ");
						String start = "Date: " +((JSONObject) ret.getJSONObject("report").getJSONArray("disabledSpots").get(i)).getString("issued");
						String position = "Position: <" + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("disabledSpots").get(i)).getInt("x"))
						+ ", " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("disabledSpots").get(i)).getInt("y"))
						+ ", " + Integer.toString(((JSONObject) ret.getJSONObject("report").getJSONArray("disabledSpots").get(i)).getInt("z")) +">";
		
						String content = start +"        " + position;
						disabledsHbox.getChildren().add(new Label(content));
						everythingInReservation.getChildren().add(disabledsHbox);
			        }

					
					vB.getChildren().add(header);
			        ScrollPane sp = new ScrollPane();
					sp.setStyle("-fx-pref-width:800; -fx-pref-height:550;");
					sp.setContent(everythingInReservation);
			        vB.getChildren().add(sp);

			        Scene scene  = new Scene(vB,800,600);
			        popupwindow.setScene(scene);
					popupwindow.showAndWait();

					
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;

	}

/**
    * view Reports page  
    * @param event
    */
    @FXML
    void loadReporsBorderPane(ActionEvent event) {
    	reportsBorderPane.setVisible(true);
    	changePricesRequestsBorderPane.setVisible(false); 
    	quarterReportsBorderPane.setVisible(false);

    	quarterReportsButton.getStyleClass().removeAll("pressedButton", "focus");
    	quarterReportsButton.getStyleClass().add("loginView-buttons");
    	reportsButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	reportsButton.getStyleClass().add("pressedButton");
    	changePricesRequestsButton.getStyleClass().removeAll("pressedButton", "focus");
    	changePricesRequestsButton.getStyleClass().add("loginView-buttons");
    	
    	ArrayList<ParkingLot> parkingLotNames = SharedData.getInstance().getParkingLotsAL();
        
    	myComboBoxData.clear();
    	for(int i = 0; i < parkingLotNames.size(); i++){
    		myComboBoxData.add(parkingLotNames.get(i).get_name());
    	}
    	
    	parkLotNameComboBox.setItems(myComboBoxData);
    	parkLotNameComboBox1.setItems(myComboBoxData);

    	
    }

    /**
     * view Change PricesREquests page  
     * @param event
     */  
    @FXML
    void loadChangePricesRequestsBorderPane(ActionEvent event) {
    	reportsBorderPane.setVisible(false);
    	changePricesRequestsBorderPane.setVisible(true); 
    	quarterReportsBorderPane.setVisible(false);

    	quarterReportsButton.getStyleClass().removeAll("pressedButton", "focus");
    	quarterReportsButton.getStyleClass().add("loginView-buttons");
    	changePricesRequestsButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	changePricesRequestsButton.getStyleClass().add("pressedButton");
    	reportsButton.getStyleClass().removeAll("pressedButton", "focus");
    	reportsButton.getStyleClass().add("loginView-buttons");
    	
    	int length = changePricesRequestVbox.getChildren().size();
    	changePricesRequestVbox.getChildren().remove(0, length);
		
    	
    	
    	//TODO: to get all the price change costs
    	
    	JSONObject json = new JSONObject();
    	
    	try {
			
    		json.put("cmd", "getCostChangeRequests");
			JSONObject ret = request(json, "SystemUserServices");
			System.out.println(ret);
			if(ret.getBoolean("result")){
				System.out.println("SUCCESS @ get cost change requests");
				

					
				JSONObject updateCost = request(null, "SystemQueries");
			
				if (updateCost.getBoolean("result")) {
					System.out.println(updateCost);

					JSONArray costs = updateCost.getJSONArray("Costs");
					
					SharedData.getInstance().setOccasionalCost(((JSONObject) costs.get(0)).getDouble("cost"));
					SharedData.getInstance().setReservationCost(((JSONObject) costs.get(1)).getDouble("cost"));
					SharedData.getInstance().setRoutineCost(((JSONObject) costs.get(2)).getDouble("cost"));
					SharedData.getInstance().setBusinessCost(((JSONObject) costs.get(3)).getDouble("cost"));
					SharedData.getInstance().setFullCost(((JSONObject) costs.get(4)).getDouble("cost"));
				}
				
				
				
				Label  occasionalReservationPrice= new Label(SharedData.getInstance().getOccasionalCost() + "");
		    	occasionalReservationPrice.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
		 		Label RegularReservationPrice = new Label(SharedData.getInstance().getReservationCost() + "");
		 		RegularReservationPrice.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
		 		Label  routinelySubscriptionHours = new Label(SharedData.getInstance().getRoutineCost() / SharedData.getInstance().getReservationCost() + "");
		 		routinelySubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
		 		Label   businessSubscriptionHours = new Label(SharedData.getInstance().getBusinessCost() / SharedData.getInstance().getReservationCost() + "");
		 		businessSubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
		 		Label   fullSubscriptionHours = new Label(SharedData.getInstance().getFullCost() / SharedData.getInstance().getReservationCost() + "");
		 		fullSubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
		 		Label lotName = new Label("Current Price");
		 		lotName.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
		 		
		 		HBox oldHbox = new HBox();
		    	oldHbox.getChildren().add(occasionalReservationPrice);
		    	oldHbox.getChildren().add(RegularReservationPrice);
		    	oldHbox.getChildren().add(routinelySubscriptionHours);
		    	oldHbox.getChildren().add(businessSubscriptionHours);
		    	oldHbox.getChildren().add(fullSubscriptionHours);
		    	oldHbox.getChildren().add(lotName);
		    	oldHbox.setStyle("-fx-background-color: red");
		    	oldHbox.getStyleClass().add("hbox");
		    	
				changePricesRequestVbox.getChildren().add(oldHbox);

				if(ret.getJSONArray("changeRequests").length() > 0){
					JSONArray loop = ret.getJSONArray("changeRequests");
					
					for(int i = 0; i < ret.getJSONArray("changeRequests").length(); i++){
						
						
				 		
				 		Label  NewOccasionalReservationPrice= new Label(((JSONObject)loop.get(i)).getInt("occasional") + "");
				 		NewOccasionalReservationPrice.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
						Label newRegularReservationPrice = new Label(((JSONObject)loop.get(i)).getInt("reserveAhead") + "");
						newRegularReservationPrice.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
						Label  newRoutinelySubscriptionHours = new Label(((JSONObject)loop.get(i)).getInt("routineHours") + "");
						newRoutinelySubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
						Label   newbusinessSubscriptionHours = new Label(((JSONObject)loop.get(i)).getInt("businessHours") + "");
						newbusinessSubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
						Label   newFullSubscriptionHours = new Label(((JSONObject)loop.get(i)).getInt("fullHours") + "");
						newFullSubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 3.5 0 0 0");
						Label   newLotName = new Label(((JSONObject)loop.get(i)).getString("lotName"));
						newFullSubscriptionHours.setStyle("-fx-pref-width: 90; -fx-padding: 0 3.5 0 0");
						
						Label   space = new Label("       ");
						newFullSubscriptionHours.setStyle("-fx-pref-width:90 ;");
						
				    
				    	Button approve=new Button("Approve");
						Button refuse=new Button("Decline");
								
						HBox newHbox = new HBox();
						newHbox.getChildren().add(NewOccasionalReservationPrice);
						newHbox.getChildren().add(newRegularReservationPrice);
						newHbox.getChildren().add(newRoutinelySubscriptionHours);
						newHbox.getChildren().add(newbusinessSubscriptionHours);
						newHbox.getChildren().add(newFullSubscriptionHours);
						newHbox.getChildren().add(newLotName);
						newHbox.getChildren().add(space);
						newHbox.getChildren().add(approve);
						newHbox.getChildren().add(refuse);
						oldHbox.setStyle("-fx-background-color: green");
						
						
						Label   id = new Label(((JSONObject)loop.get(i)).getInt("requestID") + "");
						
						
						approve.setId("approveButton" /*+ resId.getText()*/);
						String css = getClass().getResource("application.css").toExternalForm();
						approve.getStylesheets().clear();
						approve.getStylesheets().add(css);
						approve.setOnAction(e -> aproveUpdateCost(e, Integer.parseInt(id.getText())));
						approve.getStyleClass().add("approve-button");
						approve.setStyle("-fx-color: #d0e6f8;");
					
						refuse.setId("refuseButton" /*+ resId.getText()*/);
						refuse.getStylesheets().clear();
						refuse.getStylesheets().add(css);
						refuse.setOnAction(e -> declineUpdtae(e, Integer.parseInt(id.getText())));
						refuse.getStyleClass().add("approve-button");
						refuse.setStyle("-fx-color: #8d2626;");
					
						newHbox.setStyle("-fx-background-color:#98FB98;-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
								+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
						oldHbox.setStyle("-fx-background-color: #FF4500;-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
								+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
						changePricesRequestVbox.getChildren().add(newHbox);
						
					}
				
				
				}else{
					System.out.println("no requests to handle");
				}
			}else{
				System.out.println("ERROR while getting the cost change requests");
			}
			
			
		} catch (JSONException | NumberFormatException e) {
			e.printStackTrace();
		}
    	

    }
    

	public void setWelcome(String welcome) {
		welcomeBanner.setText(welcome);
	}

	public void setTopOfParkingWorker(String _fullname) {
		textInTopOfLogIn.setText(_fullname);
	}
	
	
	/**
	 * a method that talks with the server in servlet mechanism.
	 * Sending a request to the server by sending a json object that contains the data we want to send to the server,
	 * and the servlet name.
	 * 
	 * @param json 
	 * @param servletName 
	 * @return
	 */


	JSONObject request(JSONObject json, String servletName) {
		HttpURLConnection connection = null;
		try {
			// Create connection
			URL url = new URL("http://" + SharedData.getInstance().getIP() + ":" + SharedData.getInstance().getPORT()
					+ "/server/" + servletName);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			// Send request
			DataOutputStream sentData = new DataOutputStream(connection.getOutputStream());

		
			if (json != null) {
				sentData.writeBytes(json.toString());

				sentData.close();
			}
			JSONObject ret;

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if
															// Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				// response.append('\r');
			}

			rd.close();
			// System.out.println(response.toString() +
			// "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			ret = new JSONObject(response.toString());

			return ret;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (connection != null) {
				connection.disconnect();
			}

		}

		return null;

	}
	
	public Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}


}
