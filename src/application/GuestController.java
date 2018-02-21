/**
 * Sample Skeleton for 'GuestView.fxml' Controller Class
 */

package application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.SharedData;

public class GuestController {

	Alert informationAlert = new Alert(AlertType.INFORMATION);
	Alert errorAlert = new Alert(AlertType.ERROR);
	Alert confirmAlert = new Alert(AlertType.CONFIRMATION);

	@FXML // fx:id="GuestLeavingHourComboBox"
	private ComboBox<String> GuestLeavingHourComboBox; // Value injected by
														// FXMLLoader
	@FXML // fx:id="welcomeBanner"
	private Label welcomeBanner; // Value injected by FXMLLoader

	@FXML // fx:id="GuestUserIdTF"
	private TextField GuestUserIdTF; // Value injected by FXMLLoader

	@FXML // fx:id="GuestLeavingMinuteComboBox"
	private ComboBox<String> GuestLeavingMinuteComboBox; // Value injected by
															// FXMLLoader
	@FXML // fx:id="GuestLeavingDateDP"
	private DatePicker GuestLeavingDateDP; // Value injected by FXMLLoader

	@FXML // fx:id="GuestUserEmailTF"
	private TextField GuestUserEmailTF; // Value injected by FXMLLoader

	@FXML // fx:id="textInTopOfLogIn"
	private Text textInTopOfLogIn; // Value injected by FXMLLoader

	@FXML // fx:id="GuestReserveParkingButton"
	private Button GuestReserveParkingButton; // Value injected by FXMLLoader

	@FXML // fx:id="GuestCarNumberTF"
	private TextField GuestCarNumberTF; // Value injected by FXMLLoader

	@FXML // fx:id="signOutButton"
	private Button signOutButton; // Value injected by FXMLLoader

	private ObservableList<String> myComboBoxHoursData = FXCollections.observableArrayList();

	private ObservableList<String> myComboBoxMinutesData = FXCollections.observableArrayList();

	@FXML // fx:id="GuestCarNumberExit"
	private TextField GuestCarNumberExit; // Value injected by FXMLLoader

	@FXML
	private TextField GuestCriditCardNumber;

	@FXML // fx:id="GuestExitButton"
	private Button GuestExitButton; // Value injected by FXMLLoader

	/**
	 * sign out from system
	 * @param event
	 */
	@FXML
	void signOut(ActionEvent event) {
		SharedData.getInstance().setCurrentUser(null);

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
	
	/**
	 * sending request to the server to make parking reservation as a guest and after that enter the car to the parking lot
	 * @param event
	 */
	@FXML
	void reserveParking(ActionEvent event) {

		String _carNumber = GuestCarNumberTF.getText();
		String _lotName = SharedData.getInstance().getCurrentParkingLot().get_name();
		String _ocLeaveHour = GuestLeavingHourComboBox.getValue();
		String _ocLeaveMinute = GuestLeavingMinuteComboBox.getValue();
		LocalDate leaveLocalDate = GuestLeavingDateDP.getValue();
		Calendar arriveCal = Calendar.getInstance();
		Calendar leaveCal = Calendar.getInstance();
		boolean flag = false;

		if (_ocLeaveHour == null) {
			_ocLeaveHour = "00";
			flag = true;
		}

		if (_ocLeaveMinute == null) {
			_ocLeaveMinute = "00";
		}

		if (leaveLocalDate != null) {
			Instant leaveInstant = Instant.from(leaveLocalDate.atStartOfDay(ZoneId.systemDefault()));
			if (_ocLeaveHour.equals("00") && _ocLeaveMinute.equals("00") && flag) {
				leaveInstant = Instant.from(leaveLocalDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1));
			}
			Date leaveDate = Date.from(leaveInstant);
			leaveCal = toCalendar(leaveDate);
			leaveCal.set(Calendar.HOUR, Integer.parseInt(_ocLeaveHour));
			leaveCal.set(Calendar.MINUTE, Integer.parseInt(_ocLeaveMinute));
			// System.out.println(leaveCal.toString());
		}

		if (_carNumber.equals("") || (leaveLocalDate == null)) {
			informationAlert.setTitle("Reservation warrning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("Please fill all the above field to complete the reservation");
			informationAlert.showAndWait();

		} else {

			long _start = arriveCal.getTime().getTime();
			long _end = leaveCal.getTime().getTime();
			long deff = TimeUnit.MILLISECONDS
					.toMinutes(Math.abs(leaveCal.getTimeInMillis() - arriveCal.getTimeInMillis()));
			long cost = (long) (Math.ceil(deff / 60.0) * SharedData.getInstance().getOccasionalCost());
			int ocLeaveHourInt = Integer.parseInt(_ocLeaveHour);
			int ocLeaveMinuteInt = Integer.parseInt(_ocLeaveMinute);
			long _now = Calendar.getInstance().getTime().getTime();
			System.out.println(_now + " and the start is" + _start + " and the end is " + _end);
			if (_now > _end || _start >= _end) {
				informationAlert.setTitle("Reservation Warning");
				informationAlert.setHeaderText(null);
				informationAlert.setContentText("Please adjust dates and hours to convenient values");
				informationAlert.showAndWait();
				} else{
				String leaveHour = "";
				if (ocLeaveMinuteInt < 10) {
					leaveHour = (ocLeaveHourInt) + ":0" + (ocLeaveMinuteInt);
				} else {
					leaveHour = (ocLeaveHourInt) + ":" + (ocLeaveMinuteInt);
				}
	
				if (ocLeaveHourInt < 10) {
					leaveHour = "0" + leaveHour;
				}
	
				JSONObject json = new JSONObject();
				try {
					confirmAlert.setTitle("Confirmation Dialog");
					confirmAlert.setContentText("Would you like to reserve this parking for " + cost + "\u20AA ?");
	
					Optional<ButtonType> result = confirmAlert.showAndWait();
					if (result.get() == ButtonType.OK) {
	
						json.put("carNumber", _carNumber);
						json.put("lotName", _lotName);
						json.put("username", "GUEST");
						json.put("leave", leaveHour);
						json.put("start", _start);
						json.put("end", _end);
						json.put("type", "o");
						json.put("activated", 1);
						json.put("cmd", "reserveAhead");
	
						JSONObject check = request(new JSONObject().put("start", _start).put("end", _end)
								.put("lotName", _lotName).put("cmd", "overlappingOrders"), "LotOperator");
						boolean canI = SharedData.getInstance().getCurrentParkingLot().CanPark(check.getInt("overlapping"));
	
						if (canI) {
	
							JSONObject ret = request(json, "ReservationController");
	
							System.out.println(ret.getBoolean("result"));
							if (ret.getBoolean("result")) {
	
								informationAlert.setTitle("Parking succeeded");
								informationAlert.setHeaderText(null);
								informationAlert
										.setContentText("Please pay attention that the payment is after exiting the car.");
								informationAlert.showAndWait();
	
								boolean res = SharedData.getInstance().getCurrentParkingLot().InsertCar(_carNumber,
										arriveCal, leaveCal);
	
								if (res) {
									informationAlert.setTitle("Parking Succeeded");
									informationAlert.setHeaderText(null);
									informationAlert.setContentText("Your car in a safe hands, have a nice day");
									informationAlert.showAndWait();
								} else {
									informationAlert.setTitle("Parking error");
									informationAlert.setHeaderText(null);
									informationAlert.setContentText("Error while parking the car !!");
									informationAlert.showAndWait();
								}
	
								signOut(null);
							}
	
						} else {
	
							informationAlert.setTitle("Reservation warning");
							informationAlert.setHeaderText(null);
							informationAlert.setContentText(
									"Sorry for the inconvenience, for now, there are no enough place in this parking lot.");
							informationAlert.showAndWait();
	
						}
	
					} else {
	
						informationAlert.setTitle("Reservation warning");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("Okay, we didn't charge you");
						informationAlert.showAndWait();
					}
	
				} catch (JSONException e) {
					e.printStackTrace();
				}
	
			}
		}
	}

	
	/**
	 * exiting the guest car from the parking lot  
	 * @param event
	 */
	@FXML
	void GuestExit(ActionEvent event) {

		String carNumber = GuestCarNumberExit.getText();
		String criditNumber = GuestCriditCardNumber.getText();

		if (carNumber.equals("") || criditNumber.equals("")) {
			informationAlert.setTitle("Extracting warrning");
			informationAlert.setHeaderText(null);
			informationAlert.setContentText("please fill both car number and cridit card number");
			informationAlert.showAndWait();
		} else {

			JSONObject json = new JSONObject();
			try {

				json.put("carNumber", carNumber);
				json.put("username", "GUEST");
				json.put("lotName", SharedData.getInstance().getCurrentParkingLot().get_name());
				System.out.println(SharedData.getInstance().getCurrentParkingLot().get_name());
				// TODO: send reserve id to basel that send in the mail
				json.put("cmd", "orderByCarNumber");
				JSONObject ret = request(json, "ReservationController");

				System.out.println(ret);
				if (ret.getBoolean("result")) {

					JSONObject order = new JSONObject();
					order = ret.getJSONObject("order");

					String arriveDate = order.getString("start");
					String leaveDate = order.getString("end");

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					Calendar arrivingCal = Calendar.getInstance();
					Calendar leavingCal = Calendar.getInstance();

					try {

						arrivingCal.setTime(sdf.parse(arriveDate));
						leavingCal.setTime(sdf.parse(leaveDate));

						long _end = leavingCal.getTime().getTime();
						long deff = TimeUnit.MILLISECONDS
								.toMinutes(Math.abs(leavingCal.getTimeInMillis() - arrivingCal.getTimeInMillis()));
						System.out.println(
								"$$ -> " + (Math.ceil(deff / 60.0) * SharedData.getInstance().getOccasionalCost()));
						System.out.println("$$ -> " + Math.max(0, Calendar.getInstance().getTime().getTime() - _end) * 2
								* SharedData.getInstance().getOccasionalCost());
						long cost = (long) ((Math.ceil(deff / 60.0) * SharedData.getInstance().getOccasionalCost())
								+ (Math.max(0, Calendar.getInstance().getTime().getTime() - _end) * 2
										* SharedData.getInstance().getOccasionalCost()));

						informationAlert.setTitle("bill confirmation");
						informationAlert.setHeaderText(null);
						informationAlert.setContentText("you have been charged " + cost + "\u20AA from your credit card");
						informationAlert.showAndWait();

						JSONObject res = request(new JSONObject().put("cmd", "exit").put("rid", order.getInt("rid"))
								.put("carNumber", carNumber), "ReservationController");
						System.out.println(res);
						if (res.getBoolean("result")) {

							// TODO: exit the car from the lot

							System.out.println(SharedData.getInstance().getCurrentParkingLot().getEmptySlots());
							boolean resu = SharedData.getInstance().getCurrentParkingLot().ExtractCar(carNumber);
							System.out.println(SharedData.getInstance().getCurrentParkingLot().getEmptySlots());

							if (resu) {

								informationAlert.setTitle("Extracting Succeeded !!");
								informationAlert.setHeaderText(null);
								informationAlert.setContentText(
										"Get your car and have a NICE day. \n Glad doing business with you, see you soon");
								informationAlert.showAndWait();

							} else {

								informationAlert.setTitle("Parking Error");
								informationAlert.setHeaderText(null);
								informationAlert.setContentText("Failed park the car!");
								informationAlert.showAndWait();

							}

						} else {

						}

					} catch (ParseException e) {
						e.printStackTrace();
					}

				} else {
					informationAlert.setTitle("Reservation Error");
					informationAlert.setHeaderText(null);
					informationAlert.setContentText("You dont have a car in the parking lot, no reserve for this car");
					informationAlert.showAndWait();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * casting from object date to object calendar
	 * @param date
	 * @return
	 */
	public Calendar toCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public ObservableList<String> getMyComboBoxHoursData() {
		return myComboBoxHoursData;
	}

	public void setMyComboBoxHoursData(ObservableList<String> myComboBoxHoursData) {
		this.myComboBoxHoursData = myComboBoxHoursData;
		this.GuestLeavingHourComboBox.setItems(this.myComboBoxHoursData);
	}

	public ObservableList<String> getMyComboBoxMinutesData() {
		return myComboBoxMinutesData;
	}

	public void setMyComboBoxMinutesData(ObservableList<String> myComboBoxMinutesData) {
		this.myComboBoxMinutesData = myComboBoxMinutesData;
		this.GuestLeavingMinuteComboBox.setItems(this.myComboBoxMinutesData);
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

}
