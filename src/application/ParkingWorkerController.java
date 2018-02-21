/**
 * Sample Skeleton for 'ParkingWrokerView.fxml' Controller Class
 */

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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;

public class ParkingWorkerController {

	Alert informationAlert = new Alert(AlertType.INFORMATION);
	Alert errorAlert = new Alert(AlertType.ERROR);
	Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
	
    @FXML // fx:id="welcomeBanner"
    private Label welcomeBanner; // Value injected by FXMLLoader

    @FXML // fx:id="disabledParkingSpotButton"
    private Button disabledParkingSpotButton; // Value injected by FXMLLoader

    @FXML // fx:id="DisaParkSpotUntilHourComboBox"
    private ComboBox<String> DisaParkSpotUntilHourComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="ParkingReservationParkingLotWorkerBorderPane"
    private BorderPane ParkingReservationParkingLotWorkerBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="parkResLeavingMinuteComboBox"
    private ComboBox<String> parkResLeavingMinuteComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="parkResLeavingHourComboBox"
    private ComboBox<String> parkResLeavingHourComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="signOutButton"
    private Button signOutButton; // Value injected by FXMLLoader

    @FXML // fx:id="parkResLeavingDateDP"
    private DatePicker parkResLeavingDateDP; // Value injected by FXMLLoader

    @FXML // fx:id="parkingReservationButton"
    private Button parkingReservationButton; // Value injected by FXMLLoader

    @FXML // fx:id="DisaParkSpotUntilMinuteComboBox"
    private ComboBox<String> DisaParkSpotUntilMinuteComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="ParkingWorkerReserveParkingButton"
    private Button ParkingWorkerReserveParkingButton; // Value injected by FXMLLoader

    @FXML // fx:id="DiabledParkingSpotParkingBorderPane"
    private BorderPane DiabledParkingSpotParkingBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="textInTopOfLogIn"
    private Text textInTopOfLogIn; // Value injected by FXMLLoader

    @FXML // fx:id="DisaParkSpotSpotIdTF"
    private TextField DisaParkSpotSpotIdTF; // Value injected by FXMLLoader

    @FXML // fx:id="alternativeComboBox"
    private ComboBox<String> alternativeComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="parkResComboBox"
    private ComboBox<String> parkResComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="DisaParkSpotUntilDateDP"
    private DatePicker DisaParkSpotUntilDateDP; // Value injected by FXMLLoader

    @FXML // fx:id="IntitializationButton"
    private Button IntitializationButton; // Value injected by FXMLLoader

    @FXML // fx:id="alternativeParkingReToAlterParkButton"
    private Button alternativeParkingReToAlterParkButton; // Value injected by FXMLLoader

    @FXML // fx:id="parkResArrivingDateDP"
    private DatePicker parkResArrivingDateDP; // Value injected by FXMLLoader

    @FXML // fx:id="parkResArrivingMinuteComboBox"
    private ComboBox<String> parkResArrivingMinuteComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="AlternativeParkingParkingLotWorkerBorderPane"
    private BorderPane AlternativeParkingParkingLotWorkerBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="parkResReserveParkingButton"
    private Button parkResReserveParkingButton; // Value injected by FXMLLoader

    @FXML // fx:id="alternativeParkingReservationIdTF"
    private TextField alternativeParkingReservationIdTF; // Value injected by FXMLLoader

    @FXML // fx:id="parkResArrivingHourComboBox"
    private ComboBox<String> parkResArrivingHourComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="AlternativeParkingCarNumberTF"
    private TextField AlternativeParkingCarNumberTF; // Value injected by FXMLLoader

    @FXML // fx:id="IntitializationParkingLotWorkerBorderPane"
    private BorderPane IntitializationParkingLotWorkerBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="ParkingReservationCarNumberTF"
    private TextField ParkingReservationCarNumberTF; // Value injected by FXMLLoader

    @FXML // fx:id="alternativeParkButton"
    private Button alternativeParkButton; // Value injected by FXMLLoader
    
    @FXML // fx:id="ParkingWorkerActivateParkingSpotButton"
    private Button ParkingWorkerActivateParkingSpotButton; // Value injected by FXMLLoader
   
    @FXML // fx:id="ParkingWorkerDisabledParkingSpotButton"
    private Button ParkingWorkerDisabledParkingSpotButton; // Value injected by FXMLLoader

    @FXML // fx:id="ParkingReservationCreditCardIdTF"
    private TextField ParkingReservationCreditCardIdTF; // Value injected by FXMLLoader
 
    @FXML // fx:id="DepthComboBox"
    private ComboBox<String> DepthComboBox; // Value injected by FXMLLoader
   
    @FXML // fx:id="WidthComboBox"
    private ComboBox<String> WidthComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="HeightComboBox"
    private ComboBox<String> HeightComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="reserveHeightComboBox"
    private ComboBox<String> reserveHeightComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="reserveDepthComboBox"
    private ComboBox<String> reserveDepthComboBox; // Value injected by FXMLLoader
    
    @FXML // fx:id="reserveWidthComboBox"
    private ComboBox<String> reserveWidthComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="reservedByWorkerSpotsVB"
    private VBox reservedByWorkerSpotsVB; // Value injected by FXMLLoader

    @FXML // fx:id="disabledByWorkerSpotsVB"
    private VBox disabledByWorkerSpotsVB; // Value injected by FXMLLoader

    private ObservableList<String> myComboBoxParkResComboBox = FXCollections.observableArrayList();
    
    private ObservableList<String> myComboBoxWidth = FXCollections.observableArrayList();
    private ObservableList<String> myComboBoxDepth = FXCollections.observableArrayList();
    private ObservableList<String> myComboBoxHeight = FXCollections.observableArrayList();
    //private ObservableList<String> myComboBoxComplaintParkingData = FXCollections.observableArrayList();
    
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
     * sending Request to the server to disabled a specific Parking Spot
     * @param event
     */
    @FXML
    void disabledParkingSpot(ActionEvent event) {
    	
    	if((HeightComboBox.getValue() == null) || (WidthComboBox.getValue() == null) || (DepthComboBox.getValue() == null)) {

			informationAlert.setTitle("disable Spot warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please fill all the positions");
			informationAlert.showAndWait();
			return;
			
		} else {
			
			Integer _x = Integer.parseInt(HeightComboBox.getValue());
	    	Integer _y = Integer.parseInt(WidthComboBox.getValue());
	    	Integer _z = Integer.parseInt(DepthComboBox.getValue());
	    	
			
			String lotName = SharedData.getInstance().getCurrentParkingLot().get_name();
			
			int _high = _x - 1;
			int _width = _y - 1;
			int _depth = _z - 1;
			
			boolean canI = SharedData.getInstance().getCurrentParkingLot().CanDisapled();
			
			if(canI){
				
				if(SharedData.getInstance().getCurrentParkingLot().IsBusy(_high, _width, _depth)){
					
					informationAlert.setTitle("Disabling slot warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("There are a car parking in the wanted slot, please wait for the slot to be availabe");
					informationAlert.showAndWait();
					
				}else if(SharedData.getInstance().getCurrentParkingLot().IsDisapled(_high, _width, _depth)){
					
					informationAlert.setTitle("Disabling slot warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("Pay attention that this parking slot is already disabled.");
					informationAlert.showAndWait();
					
				}else if(SharedData.getInstance().getCurrentParkingLot().IsReserved(_high, _width, _depth)){
					
					informationAlert.setTitle("Disabling slot warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("Pay attention that this parking slot is already reserved.");
					informationAlert.showAndWait();
					
				}else if(SharedData.getInstance().getCurrentParkingLot().IsAvailable(_high, _width, _depth)){
					
					System.out.println("we have " + SharedData.getInstance().getCurrentParkingLot().getDisableSlots() + " disapled Slots");
					boolean temp = SharedData.getInstance().getCurrentParkingLot().disaplySlot(_high, _width, _depth);
					System.out.println("we have " + SharedData.getInstance().getCurrentParkingLot().getDisableSlots() + " disapled Slots");
					
					if(temp){
					
						
						//TODO: synchronize with server

						
						informationAlert.setTitle("Disabling slot Succeeded");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Disabling slot succeeded.");
						informationAlert.showAndWait();
						
						
					
					}else{
						
						informationAlert.setTitle("Disabling slot Error");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Something went wrong!!.");
						informationAlert.showAndWait();
						
					}
					
				}
				
			}else{
				
				informationAlert.setTitle("Disabling slot Error");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("No slots to disable them!!.");
				informationAlert.showAndWait();
				
			}
			
			JSONObject json = new JSONObject();
			try {
				
	
				
				json.put("lotName", lotName);
				json.put("cmd", "disableSpot");
	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			

			loadDisabledParkingSpot(null);
		}
    }
    
    /**
     * sending Request to the server to activate a Parking Spot that have been disabled 
     * @param event
     */
    void activateParkingSpot(ActionEvent event, int height, int width, int depth) {
    	

			
			Integer _x = height;
	    	Integer _y = width;
	    	Integer _z = depth;
	    	
			
			String lotName = SharedData.getInstance().getCurrentParkingLot().get_name();
			
			int _high = _x;
			int _width = _y;
			int _depth = _z;
			
			boolean canI = SharedData.getInstance().getCurrentParkingLot().CanUnDisapled();
			
			if(canI){
				
				if(SharedData.getInstance().getCurrentParkingLot().IsDisapled(_high, _width, _depth)){
					
					
					System.out.println("we have " + SharedData.getInstance().getCurrentParkingLot().getDisableSlots() + " disapled Slots");
					boolean temp = SharedData.getInstance().getCurrentParkingLot().undisaplySlot(_high, _width, _depth);
					System.out.println("we have " + SharedData.getInstance().getCurrentParkingLot().getDisableSlots() + " disapled Slots");
					
					if(temp){
					
						
						//TODO: synchronize with server
						
						informationAlert.setTitle("Activating disapled slot Succeeded");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Activating disapled slot Succeeded.");
						informationAlert.showAndWait();
						
						
					
					}else{
						
						informationAlert.setTitle("Activating disapled slot Error");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Something went wrong!!.");
						informationAlert.showAndWait();
						
					}
					
				}else{
					
						
					informationAlert.setTitle("Disapling slot Warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("The wanted slot is not disapled!!.");
					informationAlert.showAndWait();
					
					
					
				}
				
			}else{
				
				informationAlert.setTitle("Disapling slot Error");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("No disapled slots to available them!!.");
				informationAlert.showAndWait();
				
			}
			
			JSONObject json = new JSONObject();
			try {
				
				
	
				
				json.put("lotName", lotName);
				json.put("cmd", "disableSpot");
	
				// send to reservation servlet
	//			JSONObject ret = request(json, "CustomerServiceReservationController");
	//
	////			System.out.println(ret.getBoolean("result"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			loadDisabledParkingSpot(null);
    }

    // in each try to enter the parking lot we checked if it is full, so we didn't implement it.
    @FXML
    void ReferenceToAlternativeParking(ActionEvent event) {
		String _carNumber = AlternativeParkingCarNumberTF.getText();
		String _lotName = alternativeComboBox.getValue();
		String _orderId=alternativeParkingReservationIdTF.getText();
		if (_carNumber.equals("") || _orderId.equals("") || _lotName == null) {

			informationAlert.setTitle("Reference To Alternative Parking warrning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please fill all the fields to complete the methode");
			informationAlert.showAndWait();
			return;

		} else {
					JSONObject json = new JSONObject();
					try {
						
						//TODO: synchronize with server
						//TODO: is really needed !!
		
						json.put("carNumber", _carNumber);
						json.put("lotName", _lotName);
						json.put("orderID", _orderId);
						json.put("cmd", "ReferToAlternative");
						
						// send to reservation servlet
//						JSONObject ret = request(json, "CustomerServiceReservationController");
//
////						System.out.println(ret.getBoolean("result"));
//						if (ret.getBoolean("result")) {
//							System.out.println("Old balance is: " + SharedData.getInstance().getCurrentUser().getBalance());
//							
////							updateBalance((-1) * cost);
////							System.out.println("New balance is: " + SharedData.getInstance().getCurrentUser().getBalance());
//						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
    	
    	
    }

    /**
     * send request to the server to make a parking reservation 
     * @param event
     */
    /**
     * casting from object date to object calendar
     * @param event
     */
    @FXML
    void parkingWorkerReserveParking(ActionEvent event) {
		String _carNumber = ParkingReservationCarNumberTF.getText();
		String _creditCardNumber= ParkingReservationCreditCardIdTF.getText();
		String _arriveHour = parkResArrivingHourComboBox.getValue();
		String _arriveMinute = parkResArrivingMinuteComboBox.getValue();
		String _leaveHour = parkResLeavingHourComboBox.getValue();
		String _leaveMinute = parkResLeavingMinuteComboBox.getValue();
		String _lotName = parkResComboBox.getValue();
		LocalDate arriveLocalDate = parkResArrivingDateDP.getValue();
		LocalDate leaveLocalDate = parkResLeavingDateDP.getValue();
		Calendar arriveCal = Calendar.getInstance();
		Calendar leaveCal = Calendar.getInstance();
		Boolean flag = false;

		if (_leaveHour == null) {
			_leaveHour = "00";
			flag = true;
		}
		if (_leaveMinute == null) {
			_leaveMinute = "00";
		}

		if (_arriveHour == null || _arriveMinute == null) {

			informationAlert.setTitle("Reservation warrning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please fill arriving hour and minuts fields to complete the reservation");
			informationAlert.showAndWait();
			return;

		}

		if (arriveLocalDate != null) {
			Instant instant = Instant.from(arriveLocalDate.atStartOfDay(ZoneId.systemDefault()));
			Date date = Date.from(instant);
			arriveCal = toCalendar(date);
			arriveCal.set(Calendar.HOUR, Integer.parseInt(_arriveHour));
			arriveCal.set(Calendar.MINUTE, Integer.parseInt(_arriveMinute));
		} else {
			informationAlert.setTitle("Reservation warrning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText(
					"Please fill all the car number, arriving date and parking lot fields to complete the reservation");
			informationAlert.showAndWait();
			return;
		}

		if (leaveLocalDate != null) {
			Instant instant2 = Instant.from(leaveLocalDate.atStartOfDay(ZoneId.systemDefault()));
			if (_leaveHour.equals("00") && _leaveMinute.equals("00") && flag) {
				instant2 = Instant.from(leaveLocalDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1));
			}
			Date date2 = Date.from(instant2);
			leaveCal = toCalendar(date2);
			leaveCal.set(Calendar.HOUR, Integer.parseInt(_leaveHour));
			leaveCal.set(Calendar.MINUTE, Integer.parseInt(_leaveMinute));
			System.out.println(leaveCal.toString());
		} else {
			Instant instant2 = Instant.from(arriveLocalDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1));
			Date date2 = Date.from(instant2);
			leaveCal = toCalendar(date2);
			leaveCal.set(Calendar.HOUR, Integer.parseInt(_leaveHour));
			leaveCal.set(Calendar.MINUTE, Integer.parseInt(_leaveMinute));
			System.out.println(leaveCal.getTime().toString());

		}

		if (_carNumber.equals("") || _lotName == null||_creditCardNumber.equals("")) {

			informationAlert.setTitle("Reservation warrning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText(
					"Please fill all the car number,credit card id, arriving date and parking lot fields to complete the reservation");
			informationAlert.showAndWait();
			return;

		} else {
			long deff = TimeUnit.MILLISECONDS
					.toMinutes(Math.abs(leaveCal.getTimeInMillis() - arriveCal.getTimeInMillis()));
			double cost = Math.round(deff / 60.0) * SharedData.getInstance().getReservationCost();
			long _start = arriveCal.getTime().getTime();
			long _end = leaveCal.getTime().getTime();
			long _now = Calendar.getInstance().getTime().getTime();
			System.out.println(_now + " and the start is" + _start + " and the end is " + _end);
			if (_now > _start || _now > _end || _start >= _end) {
				informationAlert.setTitle("Reservation Warning");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("Please adjust dates and hours to convenient values");
				informationAlert.showAndWait();
			}else{
				String _name = SharedData.getInstance().getCurrentUser().getUsername();
	
				confirmAlert.setTitle("Confirmation Dialog");
				confirmAlert.setContentText("Would you like to reserve this parking for " + cost + "$ ?");
	
				Optional<ButtonType> result = confirmAlert.showAndWait();
				if (result.get() == ButtonType.OK) {
	
					if (SharedData.getInstance().getCurrentUser().getBalance() < cost) {
	
						informationAlert.setTitle("Reservation warrning");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText(
								"Insufficient fund, please make a deposit, you can do charge your wallet by clicking in Acount");
						informationAlert.showAndWait();
	
					} else {
						JSONObject json = new JSONObject();
						try {
							
							//TODO: synchronize with server
	
							json.put("carNumber", _carNumber);
							json.put("lotName", _lotName);
							json.put("username", _name);
							json.put("start", _start);
							json.put("end", _end);
							json.put("cost", cost);
							json.put("type", "r");
							json.put("activated", 0);
							json.put("cmd", "reserveAhead");
	
							// send to reservation servlet
	//						JSONObject ret = request(json, "CustomerServiceReservationController");
	//
	////						System.out.println(ret.getBoolean("result"));
	//						if (ret.getBoolean("result")) {
	//							System.out.println("Old balance is: " + SharedData.getInstance().getCurrentUser().getBalance());
	//							
	////							updateBalance((-1) * cost);
	////							System.out.println("New balance is: " + SharedData.getInstance().getCurrentUser().getBalance());
	//						}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
    }

    /**
     * casting form object date to object calendar
     * @param date
     * @return
     */
	public Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	/**
	 * View Reference To Alternative Parking Page
	 * @param event
	 */
	/*@FXML
    void loadReferenceToAlternativeParking(ActionEvent event) {
    	AlternativeParkingParkingLotWorkerBorderPane.setVisible(true);
    	IntitializationParkingLotWorkerBorderPane.setVisible(false);
    	ParkingReservationParkingLotWorkerBorderPane.setVisible(false);
    	DiabledParkingSpotParkingBorderPane.setVisible(false);
    	
    	
    	alternativeParkButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	alternativeParkButton.getStyleClass().add("pressedButton");
    	IntitializationButton.getStyleClass().removeAll("pressedButton", "focus");
    	IntitializationButton.getStyleClass().add("loginView-buttons");
    	parkingReservationButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingReservationButton.getStyleClass().add("loginView-buttons");
    	disabledParkingSpotButton.getStyleClass().removeAll("pressedButton", "focus");
    	disabledParkingSpotButton.getStyleClass().add("loginView-buttons");
    	
    	ArrayList<ParkingLot> parkingLotNames = SharedData.getInstance().getParkingLotsAL();

    	myComboBoxParkResComboBox.clear();
    	for(int i = 0; i < parkingLotNames.size(); i++){
    		myComboBoxParkResComboBox.add(parkingLotNames.get(i).get_name());
    	}
    	
    	alternativeComboBox.setItems(myComboBoxParkResComboBox);

    }*/

    @FXML
    void loadIntitialization(ActionEvent event) {
    	AlternativeParkingParkingLotWorkerBorderPane.setVisible(false);
    	IntitializationParkingLotWorkerBorderPane.setVisible(true);
    	ParkingReservationParkingLotWorkerBorderPane.setVisible(false);
    	DiabledParkingSpotParkingBorderPane.setVisible(false);
    	
    	
    	IntitializationButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	IntitializationButton.getStyleClass().add("pressedButton");
    	alternativeParkButton.getStyleClass().removeAll("pressedButton", "focus");
    	alternativeParkButton.getStyleClass().add("loginView-buttons");
    	parkingReservationButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingReservationButton.getStyleClass().add("loginView-buttons");
    	disabledParkingSpotButton.getStyleClass().removeAll("pressedButton", "focus");
    	disabledParkingSpotButton.getStyleClass().add("loginView-buttons");

    }
    /**
	 * View Parking Reservation Page
	 * @param event
	 */
    @FXML
    void loadParkingReservation(ActionEvent event) {
    	AlternativeParkingParkingLotWorkerBorderPane.setVisible(false);
    	IntitializationParkingLotWorkerBorderPane.setVisible(false);
    	ParkingReservationParkingLotWorkerBorderPane.setVisible(true);
    	DiabledParkingSpotParkingBorderPane.setVisible(false);
    	
    	
    	parkingReservationButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	parkingReservationButton.getStyleClass().add("pressedButton");
    	IntitializationButton.getStyleClass().removeAll("pressedButton", "focus");
    	IntitializationButton.getStyleClass().add("loginView-buttons");
    	alternativeParkButton.getStyleClass().removeAll("pressedButton", "focus");
    	alternativeParkButton.getStyleClass().add("loginView-buttons");
    	disabledParkingSpotButton.getStyleClass().removeAll("pressedButton", "focus");
    	disabledParkingSpotButton.getStyleClass().add("loginView-buttons");

    	myComboBoxWidth.clear();
    	for(Integer i = 1; i <SharedData.getInstance().getCurrentParkingLot().getWidth()+1 ; i++){
    			myComboBoxWidth.add(i.toString());
    	}
    	
    	myComboBoxHeight.clear();
    	for(Integer i = 1; i <SharedData.getInstance().getCurrentParkingLot().getHeight()+1 ; i++){
    			myComboBoxHeight.add(i.toString());
    	}
    	myComboBoxDepth.clear();
    	for(Integer i = 1; i < SharedData.getInstance().getCurrentParkingLot().getDepth()+1 ; i++){
    			myComboBoxDepth.add(i.toString());
    	}
    	reserveDepthComboBox.setItems(myComboBoxDepth);
    	reserveWidthComboBox.setItems(myComboBoxWidth);
    	reserveHeightComboBox.setItems(myComboBoxHeight);
        
    	int length2 = reservedByWorkerSpotsVB.getChildren().size();
		reservedByWorkerSpotsVB.getChildren().remove(0, length2);

    	
    	ArrayList<ParkingPosition> ps = SharedData.getInstance().getCurrentParkingLot().getSlotsByReserved();
    	for(int i = 0; i < ps.size(); i++){
    		int x = ps.get(i).x;
    		int y = ps.get(i).y;
    		int z = ps.get(i).z;
    		Label heightLabel = new Label(Integer.toString(ps.get(i).x + 1));
    		heightLabel.setStyle("-fx-pref-width: 80;");
			Label widthLabel = new Label(Integer.toString(ps.get(i).y + 1));
			widthLabel.setStyle("-fx-pref-width: 80;");
			Label depthLabel = new Label(Integer.toString(ps.get(i).z + 1));
			depthLabel.setStyle("-fx-pref-width: 80;");
			
			HBox hb = new HBox();
			hb.getChildren().add(heightLabel);
			hb.getChildren().add(widthLabel);
			hb.getChildren().add(depthLabel);
			hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
					+ "-fx-border-color: #d0e6f8; -fx-padding: 2 0 0 10;");
			reservedByWorkerSpotsVB.getChildren().add(hb);
	
			Button unReserveButton = new Button("Cancel Reservation");
			String css = getClass().getResource("application.css").toExternalForm();
			unReserveButton.getStylesheets().clear();
			unReserveButton.getStylesheets().add(css);
			unReserveButton.setOnAction(e -> unReservationCallBack(e, x, y, z));

			unReserveButton.getStyleClass().add("activate-button");
			hb.getChildren().add(unReserveButton);
    	}
    	

    	
    }
    
    private void unReservationCallBack(ActionEvent e, int height, int width, int depth) {
    	SharedData.getInstance().getCurrentParkingLot().unReserveSlot(height, width, depth);
    	loadParkingReservation(null);
    	return;
	}

    /**
     * reserve a specific parking spot by parking lot worker  
     * @param event
     */
	@FXML
    void reserveParkingByWorker(ActionEvent event) {
    	if((reserveHeightComboBox.getValue() == null) || (reserveWidthComboBox.getValue() == null) 
    			|| (reserveDepthComboBox.getValue() == null)) {

			informationAlert.setTitle("Reserve Spot Warning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please fill all the positions");
			informationAlert.showAndWait();
			return;
			
		} else {
			
			Integer _x = Integer.parseInt(reserveHeightComboBox.getValue());
	    	Integer _y = Integer.parseInt(reserveWidthComboBox.getValue());
	    	Integer _z = Integer.parseInt(reserveDepthComboBox.getValue());
	    	
			
			String lotName = SharedData.getInstance().getCurrentParkingLot().get_name();
			
			int _high = _x - 1;
			int _width = _y - 1;
			int _depth = _z - 1;
			
			boolean canI = SharedData.getInstance().getCurrentParkingLot().CanDisapled();
			
			if(canI){
				
				if(SharedData.getInstance().getCurrentParkingLot().IsBusy(_high, _width, _depth)){
					
					informationAlert.setTitle("Reserving slot Warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("There are a car parking in the wanted slot, please wait for the slot to be availabe");
					informationAlert.showAndWait();
					
				}else if(SharedData.getInstance().getCurrentParkingLot().IsDisapled(_high, _width, _depth)){
					
					informationAlert.setTitle("Reserving slot Warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("Pay attention that this parking slot is already disabled.");
					informationAlert.showAndWait();
					
				}else if(SharedData.getInstance().getCurrentParkingLot().IsReserved(_high, _width, _depth)){
					
					informationAlert.setTitle("Reserving slot Warning");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("Pay attention that this parking slot is already reserved.");
					informationAlert.showAndWait();
					
				}else if(SharedData.getInstance().getCurrentParkingLot().IsAvailable(_high, _width, _depth)){
					
					System.out.println("we have " + SharedData.getInstance().getCurrentParkingLot().getDisableSlots() + " disapled Slots");
					boolean temp = SharedData.getInstance().getCurrentParkingLot().reserveSlot(_high, _width, _depth);
					System.out.println("we have " + SharedData.getInstance().getCurrentParkingLot().getDisableSlots() + " disapled Slots");
					
					if(temp){
					
						
						//TODO: synchronize with server

						
						informationAlert.setTitle("Reserving slot Succeeded");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Reserving slot succeeded.");
						informationAlert.showAndWait();
						loadParkingReservation(null);
					}else{
						
						informationAlert.setTitle("Disapling slot Error");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Something went wrong!!.");
						informationAlert.showAndWait();
						
					}
					
				}
				
			}else{
				
				informationAlert.setTitle("Disapling slot Error");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("No slots to disable them!!.");
				informationAlert.showAndWait();
				
			}
			
			JSONObject json = new JSONObject();
			try {
				
	
				
				json.put("lotName", lotName);
				json.put("cmd", "disableSpot");
	
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			
		}
    }

	/**
	 * View Disabled Parking Spot Page
	 * @param event
	 */
    @FXML
    void loadDisabledParkingSpot(ActionEvent event) {
    	AlternativeParkingParkingLotWorkerBorderPane.setVisible(false);
    	IntitializationParkingLotWorkerBorderPane.setVisible(false);
    	ParkingReservationParkingLotWorkerBorderPane.setVisible(false);
    	DiabledParkingSpotParkingBorderPane.setVisible(true);
    	
    	
    	disabledParkingSpotButton.getStyleClass().removeAll("loginView-buttons", "focus");
    	disabledParkingSpotButton.getStyleClass().add("pressedButton");
    	IntitializationButton.getStyleClass().removeAll("pressedButton", "focus");
    	IntitializationButton.getStyleClass().add("loginView-buttons");
    	parkingReservationButton.getStyleClass().removeAll("pressedButton", "focus");
    	parkingReservationButton.getStyleClass().add("loginView-buttons");
    	alternativeParkButton.getStyleClass().removeAll("pressedButton", "focus");
    	alternativeParkButton.getStyleClass().add("loginView-buttons");
    	
    	myComboBoxWidth.clear();
    	for(Integer i = 1; i <SharedData.getInstance().getCurrentParkingLot().getWidth()+1 ; i++){
    			myComboBoxWidth.add(i.toString());
    	}
    	
    	myComboBoxHeight.clear();
    	for(Integer i = 1; i <SharedData.getInstance().getCurrentParkingLot().getHeight()+1 ; i++){
    			myComboBoxHeight.add(i.toString());
    	}
    	myComboBoxDepth.clear();
    	for(Integer i = 1; i < SharedData.getInstance().getCurrentParkingLot().getDepth()+1 ; i++){
    			myComboBoxDepth.add(i.toString());
    	}
    	DepthComboBox.setItems(myComboBoxDepth);
    	WidthComboBox.setItems(myComboBoxWidth);
    	HeightComboBox.setItems(myComboBoxHeight);
        
    	int length2 = disabledByWorkerSpotsVB.getChildren().size();
    	disabledByWorkerSpotsVB.getChildren().remove(0, length2);

    	
    	ArrayList<ParkingPosition> ps = SharedData.getInstance().getCurrentParkingLot().getSlotsByDisabled();
    	for(int i = 0; i < ps.size(); i++){
    		int x = ps.get(i).x;
    		int y = ps.get(i).y;
    		int z = ps.get(i).z;
    		Label heightLabel = new Label(Integer.toString(ps.get(i).x + 1));
    		heightLabel.setStyle("-fx-pref-width: 80;");
			Label widthLabel = new Label(Integer.toString(ps.get(i).y + 1));
			widthLabel.setStyle("-fx-pref-width: 80;");
			Label depthLabel = new Label(Integer.toString(ps.get(i).z + 1));
			depthLabel.setStyle("-fx-pref-width: 80;");
			
			HBox hb = new HBox();
			hb.getChildren().add(heightLabel);
			hb.getChildren().add(widthLabel);
			hb.getChildren().add(depthLabel);
			hb.setStyle("-fx-border-style: solid inside;-fx-pref-height: 30;-fx-border-width: 0 0 2 0;"
					+ "-fx-border-color: #d0e6f8; -fx-padding: 2 0 0 10;");
			disabledByWorkerSpotsVB.getChildren().add(hb);
	
			Button unReserveButton = new Button("Activate Spot");
			String css = getClass().getResource("application.css").toExternalForm();
			unReserveButton.getStylesheets().clear();
			unReserveButton.getStylesheets().add(css);
			unReserveButton.setOnAction(e -> activateParkingSpot(e, x, y, z));

			unReserveButton.getStyleClass().add("activate-button");
			hb.getChildren().add(unReserveButton);
			
    	}

    	
    }
    
    /**
     * clearing all the parking spots (Initialize Parking Lot)
     * @param event
     */
    @FXML
    void InitializeParkingLot(ActionEvent event) {
    	informationAlert.setTitle("Confirmation Dialog");
		confirmAlert.setContentText("Are you sure you want to initialize the parking lot?");

		Optional<ButtonType> result = confirmAlert.showAndWait();
		if (result.get() == ButtonType.OK) {

			SharedData.getInstance().getCurrentParkingLot().initialize();
			informationAlert.setTitle("Initialization Succeeded");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("You have initialized the parking lot successfully");
			informationAlert.showAndWait();
			
			// TODO: check if its neccassary to update the reserves
			
			loadDisabledParkingSpot(null);
		}

    }
	

    public void setWelcome(String welcome) {
		welcomeBanner.setText(welcome);
	}
	
	public void setTopOfParkingWorker(String _username) {
		textInTopOfLogIn.setText(_username);
	}
	
	/**
	 * this function block the parking lot, we change all the entries to disabled.
	 * @param event
	 */
	@FXML
	void BlockParkingLot(ActionEvent event){
		
		System.out.println("In the system worker block function");
		
		if(SharedData.getInstance().getCurrentParkingLot().CanIBlock()){
			if(SharedData.getInstance().getCurrentParkingLot().Block()){
				
				informationAlert.setTitle("Blocking parking lot Succeeded");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("Parking lot blocked successfully");
				informationAlert.showAndWait();
				
				loadDisabledParkingSpot(null);
			}else{
				informationAlert.setTitle("Disabling slot Error");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("Something went wrong!!.");
				informationAlert.showAndWait();
			}
		}else{
			informationAlert.setTitle("Initialization Worning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Sorry for now, you can't block the parking lot, some cars already parking.");
			informationAlert.showAndWait();
		}
		
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
    
    JSONObject request(JSONObject json, String servletName){
    	HttpURLConnection connection = null;
		try {
		    //Create connection
		    URL url = new URL("http://" + SharedData.getInstance().getIP() + ":" + SharedData.getInstance().getPORT() + "/server/" + servletName);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream sentData = new DataOutputStream (connection.getOutputStream());
		   
		    sentData.writeBytes(json.toString());
		    
		    sentData.close();
		    JSONObject ret;

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      //response.append('\r');
		    }
		    
		    rd.close();
//		    System.out.println(response.toString() + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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


}
