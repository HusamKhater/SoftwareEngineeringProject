/**
 * Sample Skeleton for 'ParkingLotDirectorView.fxml' Controller Class
 */

package application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CpsMailBox;
import model.ParkingLot;
import model.ParkingSituation;
import model.SharedData;




public class ParkingLotDirectorController {
	
	Alert informationAlert = new Alert(AlertType.INFORMATION);
	Alert errorAlert = new Alert(AlertType.ERROR);
	Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
	
	

    @FXML // fx:id="fullSubscriptionHoursChangeButton"
    private Button fullSubscriptionHoursChangeButton; // Value injected by FXMLLoader

    @FXML // fx:id="welcomeBanner"
    private Label welcomeBanner; // Value injected by FXMLLoader

    @FXML // fx:id="routinelySubscriptionHoursTF"
    private TextField routinelySubscriptionHoursTF; // Value injected by FXMLLoader

    @FXML // fx:id="regularChangeButton"
    private Button regularChangeButton; // Value injected by FXMLLoader

    @FXML // fx:id="businessSubscriptionHoursChangeButton"
    private Button businessSubscriptionHoursChangeButton; // Value injected by FXMLLoader

    @FXML // fx:id="ChangePricesParkingLotDirectorBorderPane"
    private BorderPane ChangePricesParkingLotDirectorBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="fullSubscriptionHoursTF"
    private TextField fullSubscriptionHoursTF; // Value injected by FXMLLoader

    @FXML // fx:id="parkingLotDirectorReportsButton"
    private Button parkingLotDirectorReportsButton; // Value injected by FXMLLoader

    @FXML // fx:id="signOutButton"
    private Button signOutButton; // Value injected by FXMLLoader

    @FXML // fx:id="balanceOnTopOfLogIn"
    private Text balanceOnTopOfLogIn; // Value injected by FXMLLoader

    @FXML // fx:id="regularReservationPriceTF"
    private TextField regularReservationPriceTF; // Value injected by FXMLLoader

    @FXML // fx:id="businessSubscriptionHoursTF"
    private TextField businessSubscriptionHoursTF; // Value injected by FXMLLoader

    @FXML // fx:id="occasionalReservationPriceTF"
    private TextField occasionalReservationPriceTF; // Value injected by FXMLLoader

    @FXML // fx:id="parkingLotDirectorCahngePriceButton"
    private Button parkingLotDirectorCahngePriceButton; // Value injected by FXMLLoader

    @FXML // fx:id="CusSerparkResReserveParkingButton"
    private Button CusSerparkResReserveParkingButton; // Value injected by FXMLLoader

    @FXML // fx:id="routinelySubscriptionChangeButton"
    private Button routinelySubscriptionChangeButton; // Value injected by FXMLLoader

    @FXML // fx:id="ReportsParkingLotDirectorBorderPane"
    private BorderPane ReportsParkingLotDirectorBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="textInTopOfLogIn"
    private Text textInTopOfLogIn; // Value injected by FXMLLoader

    @FXML // fx:id="ParLotDirecReportComboBox"
    private ComboBox<String> ParLotDirecReportComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="occasionalChangeButton"
    private Button occasionalChangeButton; // Value injected by FXMLLoader
    
    @FXML // fx:id="administratorRequestsButton"
    private Button administratorRequestsButton; // Value injected by FXMLLoader

    @FXML // fx:id="viewAdministratorReqeustBorderPane"
    private BorderPane viewAdministratorReqeustBorderPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="administratorRequestsListVB"
    private VBox administratorRequestsListVB; // Value injected by FXMLLoader

    @FXML // fx:id="businessSubscriptionHoursT"
    private Label businessSubscriptionHoursT; // Value injected by FXMLLoader

    @FXML // fx:id="occasionalReservationPriceT"
    private Label occasionalReservationPriceT; // Value injected by FXMLLoader

    @FXML // fx:id="regularReservationPriceF"
    private Label regularReservationPriceT; // Value injected by FXMLLoader

    @FXML // fx:id="routinelySubscriptionHoursT"
    private Label routinelySubscriptionHoursT; // Value injected by FXMLLoader

    @FXML // fx:id="fullSubscriptionHoursT"
    private Label fullSubscriptionHoursT; // Value injected by FXMLLoader


    private ObservableList<String> myComboBoxParLotDirecReport= FXCollections.observableArrayList();
    
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
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

    }

    /**
     *sending  mail to Administrator that contain a specific report 
     * @param event
     */
    
    @FXML
    void sendReportToAdministrator(ActionEvent event) {
    	
    	String report = ParLotDirecReportComboBox.getValue();
    	
    	if (report == null) {

			informationAlert.setTitle("Report Warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please choose a duration");
			informationAlert.showAndWait();
			return;

		} else {
			String lotName = SharedData.getInstance().getCurrentParkingLot().get_name();
			
			JSONObject json = new JSONObject();
			try {
				Date date = new Date();
				LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				int month = localDate.getMonthValue();
				int val = -1;
				if(month == 1 || month == 2 || month == 3) val = 1;
				else if(month == 4 || month == 5 || month == 6) val = 2;
				else if(month == 7 || month == 8 || month == 9) val = 3;
				else val = 4;
				
				//TODO: synchronize with server
		    	int quarter = -1;
		    	
				if(report.equals("January - March")){
					quarter = 1;
				}else if(report.equals("April - June")){
					quarter = 2;
				}else if(report.equals("July - September")){
					quarter = 3;
				}else{
					quarter = 4;
				}
				System.out.println("$$$$$$$$$$$$$$$$$$$" + quarter + "%%%%%" + val);
				if(quarter > val){
		    		informationAlert.setTitle("Invalid Date");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("You're not allowed to get quarter report for this date");
					informationAlert.showAndWait();
					return;
				}
				json.put("quarter", quarter);
				json.put("lotName", lotName);
				json.put("cmd", "QuarterReady");
	
				// send to reservation servlet
				JSONObject ret = request(json, "ReportsGenerator");
	
				if (ret.getBoolean("result")) {
		    		informationAlert.setTitle("Report Was Sent Successfuly");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("your quarter report was sent");
					informationAlert.showAndWait();
				}
				else{
		    		informationAlert.setTitle("Report Already Sent");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("your quarter report was already sent");
					informationAlert.showAndWait();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    }
    
  
    /**
     * sending a new price suggestion to the Administrator
     * @param event
     */
    @FXML
    void occasionalChange(ActionEvent event) {
    	String costOccasional = occasionalReservationPriceTF.getText();
    	String costRegular = regularReservationPriceTF.getText();
    	String routinelySubHours = routinelySubscriptionHoursTF.getText();
    	String businessSubHours = businessSubscriptionHoursTF.getText();
    	String fullSubHours = fullSubscriptionHoursTF.getText();
    	
    	
    	if (costOccasional.equals("")||costRegular.equals("")||routinelySubHours.equals("")||businessSubHours.equals("")||fullSubHours.equals("")) {

    		informationAlert.setTitle("change warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please fill all the new prices");
			informationAlert.showAndWait();
			return;

		} else {
			
			JSONObject json = new JSONObject();
			try {
				
				
				String lotName = SharedData.getInstance().getCurrentParkingLot().get_name();
				int intRoutineHours=Integer.parseInt(routinelySubHours);
				int intBusinessHours=Integer.parseInt(businessSubHours);
				int intFullHours=Integer.parseInt(fullSubHours);
				//TODO: synchronize with server
	
				json.put("username", SharedData.getInstance().getCurrentSystemUser().get_username());
				json.put("lotName", lotName);
				
				json.put("occasional", Double.parseDouble(costOccasional));
				json.put("reserveAhead", Double.parseDouble(costRegular));
				json.put("routineHours", intRoutineHours);
				json.put("fullHours", intFullHours );
				json.put("businessHours",intBusinessHours);
				
				json.put("cmd", "requestCostChange");
	
				JSONObject ret = request(json, "SystemUserServices");
	
				System.out.println(ret);
				if(ret.getBoolean("result")){
					System.out.println("Changing occasional price SUCCEEDED!");
					
					informationAlert.setTitle("Request Success");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("Your request has been sent to the adminstrator");
					informationAlert.showAndWait();
				
				}else{
					System.out.println("ERROR @ occasional change price");
					
					informationAlert.setTitle("Request Failed");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("Something went wrong in sending the request.");
					informationAlert.showAndWait();
					
				}
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}catch(NumberFormatException e){
				informationAlert.setTitle("change warning");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("new cast ONLY number value.");
				informationAlert.showAndWait();
			}
		}
    }

    /**
     * view Reports Page
     * @param event
     */
    @FXML
    void loadReports(ActionEvent event) {
    	ReportsParkingLotDirectorBorderPane.setVisible(true);
    	ChangePricesParkingLotDirectorBorderPane.setVisible(false);
    	viewAdministratorReqeustBorderPane.setVisible(false);
    	
    	parkingLotDirectorReportsButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	parkingLotDirectorReportsButton.getStyleClass().add("pressedButton");
    	parkingLotDirectorCahngePriceButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingLotDirectorCahngePriceButton.getStyleClass().add("loginView-buttons");
    	administratorRequestsButton.getStyleClass().removeAll("pressedButton", "focus");
    	administratorRequestsButton.getStyleClass().add("loginView-buttons");
    	
    	ArrayList<String> Reports = new ArrayList<String>();
    	Reports.add("January - March");
    	Reports.add("April - June");
    	Reports.add("July - September");
    	Reports.add("October - December");
    	
    	myComboBoxParLotDirecReport.clear();     
    	for(int i = 0; i < Reports.size(); i++){
    		myComboBoxParLotDirecReport.add(Reports.get(i));
    	}
    	
    	ParLotDirecReportComboBox.setItems(myComboBoxParLotDirecReport);
    	
    	
    }

    /**
     * View Change Prices Page
     * @param event
     */

    @FXML
    void loadChangePrices(ActionEvent event) {
    	ReportsParkingLotDirectorBorderPane.setVisible(false);
    	ChangePricesParkingLotDirectorBorderPane.setVisible(true);
    	viewAdministratorReqeustBorderPane.setVisible(false);
    	
    	parkingLotDirectorCahngePriceButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	parkingLotDirectorCahngePriceButton.getStyleClass().add("pressedButton");
    	parkingLotDirectorReportsButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingLotDirectorReportsButton.getStyleClass().add("loginView-buttons");
    	administratorRequestsButton.getStyleClass().removeAll("pressedButton", "focus");
    	administratorRequestsButton.getStyleClass().add("loginView-buttons");
    	
    	


    	occasionalReservationPriceT.setText(SharedData.getInstance().getOccasionalCost()+"");
    	regularReservationPriceT.setText(SharedData.getInstance().getReservationCost()+"");
    	routinelySubscriptionHoursT.setText( ((int)SharedData.getInstance().getRoutineCost() / (int)SharedData.getInstance().getReservationCost() )+"");
    	businessSubscriptionHoursT.setText(((int)SharedData.getInstance().getBusinessCost() / (int)SharedData.getInstance().getReservationCost())+"");
    	fullSubscriptionHoursT.setText(((int)SharedData.getInstance().getFullCost()/ (int)SharedData.getInstance().getReservationCost() )+"");

    }
    

    /**
     * View Administrator Requests
     * @param event
     */

    @FXML
    void loadAdministratorRequests(ActionEvent event) {
    	ReportsParkingLotDirectorBorderPane.setVisible(false);
    	ChangePricesParkingLotDirectorBorderPane.setVisible(false);
    	viewAdministratorReqeustBorderPane.setVisible(true);
    	
    	parkingLotDirectorCahngePriceButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingLotDirectorCahngePriceButton.getStyleClass().add("loginView-buttons");
    	parkingLotDirectorReportsButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingLotDirectorReportsButton.getStyleClass().add("loginView-buttons");
    	administratorRequestsButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	administratorRequestsButton.getStyleClass().add("pressedButton");

    	JSONObject ret = getAdminRequest();

		try {

			JSONArray ja = ret.getJSONArray("managementRequests");

			System.out.println(ja);
			
			int length = administratorRequestsListVB.getChildren().size();
			administratorRequestsListVB.getChildren().remove(0, length);
			
			for(int i = 0; i < ja.length(); i++){
				String rt = ((JSONObject) ja.get(i)).getString("requestType");
				Label request = null;
				if(rt.equals("lotCurrentSituation")){
					request = new Label("Lot Current Situation");	
				}
        	    request.setStyle("-fx-pref-width: 130; -fx-padding: 3.5 0 0 0");
        		Label status = new Label("Pending");
        		status.setStyle("-fx-pref-width: 70; -fx-padding: 3.5 0 0 0");
        		//int requestID = ((JSONObject) ja.get(i)).getInt("reqeustID");
   
    		
				HBox hb = new HBox();
				hb.getChildren().add(request);
				hb.getChildren().add(status);
				hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
						+ "-fx-border-color: #d0e6f8; -fx-padding: 1.5 0 0 5;");
				administratorRequestsListVB.getChildren().add(hb);
				
				if(status.getText() == "Pending"){
					Button sendCurrentSituationToAdmin = new Button("Send Report");
					sendCurrentSituationToAdmin.setId("sendCurrentSituationToAdminButton");
					String css = getClass().getResource("application.css").toExternalForm();
					sendCurrentSituationToAdmin.getStylesheets().clear();
					sendCurrentSituationToAdmin.getStylesheets().add(css);
					JSONObject ret2 = (JSONObject) ja.get(i);
					System.out.println(ret2.toString());
					sendCurrentSituationToAdmin.setOnAction(e -> sendCurrentSituationToAdminCallBack(e, ret2));
					
					sendCurrentSituationToAdmin.getStyleClass().add("loginView-buttons");
					hb.getChildren().add(sendCurrentSituationToAdmin);
				}
				
    		}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

    }

	
    /**
     * sending a mail to administrator that contain's pdf Current Situation 
     * @param e
     * @param ret2
     */
    private void sendCurrentSituationToAdminCallBack(ActionEvent e, JSONObject ret2) {
		// TODO Auto-generated method stub
    	JSONObject json = new JSONObject();
		JSONObject ret = new JSONObject();

		try {
				json.put("requestID", ret2.getInt("requestID"));
				json.put("cmd", "handleManagementRequest");
				ret = request(json, "SystemUserServices");
				System.out.println(ret);
				if (ret.getBoolean("result")) {
					
			    	ParkingLot pl = SharedData.getInstance().getCurrentParkingLot();
					ParkingSituation ps = new ParkingSituation(pl.getHeight(), pl.getWidth());
					ps.getGridLayer(pl);
					
					String email = "cps.client4@gmail.com";
					CpsMailBox mail = new CpsMailBox(SharedData.getInstance().getCPSEmail(),
							 SharedData.getInstance().getCPSPassword(), email);
					mail.sendMailToAdministrator();
					
					
					informationAlert.setTitle("successful Respond");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText(
							"Your respond was sent successfully.\nCurrent Situation mail was send to CPS "
							+ "general manager");
					informationAlert.showAndWait();

					loadAdministratorRequests(null);
				}
			
		} catch(JSONException e1) {
			e1.printStackTrace();
		}


    	return;
	}

    /**
     * gets the administrator requests. 
     * @return
     */
	private JSONObject getAdminRequest() {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		JSONObject ret = new JSONObject();
		try {

			json.put("lotName", SharedData.getInstance().getCurrentParkingLot().get_name());
			json.put("cmd", "getManagementRequests");
			ret = request(json, "SystemUserServices");
			System.out.println(ret);
			if (ret.getBoolean("result")) {
				return ret;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
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

			sentData.writeBytes(json.toString());

			sentData.close();
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
	

	public void setWelcome(String welcome) {
		// TODO Auto-generated method stub
		welcomeBanner.setText(welcome);
	}

	public void setTopOfParkingWorker(String _fullname) {
		// TODO Auto-generated method stub
		textInTopOfLogIn.setText(_fullname);
	}


}
