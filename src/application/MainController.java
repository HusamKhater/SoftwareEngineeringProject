/**
 * Sample Skeleton for 'MainView.fxml' Controller Class
 */

package application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ParkingLot;
import model.SharedData;
import model.SystemUser;
import model.User;

//package com.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainController {
	
	Alert informationAlert = new Alert(AlertType.INFORMATION);
	Alert errorAlert = new Alert(AlertType.ERROR);
	Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
	
	/**
	 * saved the ip and the port of the server in shared data object 
	 * @param h
	 * @param p
	 */
	static void initialize(String h, String p){
		System.out.println("in the initializeer");
		//client = new Client(h, p);
		SharedData.getInstance().setIP(h);
		SharedData.getInstance().setPORT(p);
		System.out.println("after the declareatio");
	}
    @FXML
    private CheckBox BusinessCheckBox;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private  Button signiInButton;

    @FXML
    private TextField emailTextField;
    
    @FXML
    private TextField usernameRegisterationTextField;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField companyNameTextField;

    @FXML
    private CheckBox systemWorkerCheckBox;

    @FXML
    private TextField workerIdTextField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField passwordRegisterationPasswordField;

    @FXML
    private Button guestButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label workedIdLabel;
    @FXML // fx:id="TrackReservationButton"
    private Button TrackReservationButton; // Value injected by FXMLLoader

    public static int TrakCheck=0;
  
    /**
     * function that load the view of the continue as a guest, we show the user the option to do a occasional park.
     * @param event
     */
    @FXML
    void continueasAGuest(ActionEvent event) {

    	ObservableList<String> setted = FXCollections.observableArrayList();
    	ObservableList<String> setted2 = FXCollections.observableArrayList();
    	
    	setted.clear();
    	for(Integer i = 0; i < 24; i++){
    		if(i < 10 ){
    			setted.add("0" + i.toString());
    		}
    		else
    			setted.add(i.toString());
    	}
    	
    	setted2.clear();
    	for(Integer i = 0; i < 60; i++){
    		if(i < 10 ){
    			setted2.add("0" + i.toString());
    		}
    		else
    			setted2.add(i.toString());
    	}
    	
    	// updating the costs and lot names
		JSONObject upd = request(null, "SystemQueries");
		
		try {
			if (upd.getBoolean("result")) {
				System.out.println(upd);

				JSONArray costs = upd.getJSONArray("Costs");

				SharedData.getInstance().setReservationCost(((JSONObject) costs.get(1)).getDouble("cost"));
				SharedData.getInstance().setOccasionalCost(((JSONObject) costs.get(0)).getDouble("cost"));
				SharedData.getInstance().setRoutineCost(((JSONObject) costs.get(2)).getDouble("cost"));
				SharedData.getInstance().setBusinessCost(((JSONObject) costs.get(3)).getDouble("cost"));
				SharedData.getInstance().setFullCost(((JSONObject) costs.get(4)).getDouble("cost"));
				
				JSONArray parkingLotsJA = upd.getJSONArray("lots");
				ArrayList<ParkingLot> parkingLotsAL= new ArrayList<ParkingLot>();
				for (int i = 0; i < parkingLotsJA.length(); i++){
					parkingLotsAL.add(new ParkingLot(parkingLotsJA.getJSONObject(i).getString("lotName")
							, 3, 3, parkingLotsJA.getJSONObject(i).getInt("width")));
				}
				
				SharedData.getInstance().setParkingLotsAL(parkingLotsAL);
				
			}
		}catch(JSONException e){
			System.out.println("Something ERROR while updating once log in");
			e.printStackTrace();
		}
    	
    	
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GuestView.fxml"));
        Parent root;
		try {
			root = loader.load();
	        guestButton.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
        GuestController gu = loader.getController();
        gu.setMyComboBoxHoursData(setted);
        gu.setMyComboBoxMinutesData(setted2);
    }
   
    /**
     * this callback function get the user data from the GUI.
     * check if the username already exist or not,
     * if its Ok, it create a new user and update the,
     * a message will shown to the user for the results 
     * @param event
     */
    @FXML
    void registeration(ActionEvent event) { 
//    	System.out.println("HELLOOOOO FROM Registeration");
//    	System.out.println("We have new client, you are welcome");
    	
    	String _firstname = firstNameTextField.getText();
    	String _lastname = lastNameTextField.getText();
    	String _email = emailTextField.getText();
    	String _username = usernameRegisterationTextField.getText();
    	String _password = passwordRegisterationPasswordField.getText();
    	
    	
    	if(_firstname.equals("") || _lastname.equals("") || _email.equals("") ||
    			_username.equals("") || _password.equals("")){
    		
    		informationAlert.setTitle("Sign up warrning");
    		informationAlert.setHeaderText(null);
    		informationAlert.setContentText("Please fill all the above field to complete the registeration");
    		informationAlert.showAndWait();
    		
    	}else{
    		
    		try {
    			JSONObject toSend = new JSONObject();
    			toSend.put("firstName", _firstname);
    			toSend.put("lastName", _lastname);
    			toSend.put("email", _email);
    			toSend.put("username", _username);
    			toSend.put("password", _password);
    			toSend.put("balance", 0);
    			
    	    	if(BusinessCheckBox.isSelected()){
    	    		toSend.put("type", "b");
    	    		toSend.put("company", companyNameTextField.getText());
    	    	}
    	    	else{
    	    		toSend.put("type", "r");
    	    		toSend.put("company", "None");
    	    	}
    	    	
    	    	JSONObject ret = request(toSend, "Signup");
//        		System.out.println(ret.toString());
        		
        		if(ret.getBoolean("result")){
        			confirmAlert.setTitle("Sign up successeded");
        			confirmAlert.setHeaderText(null);
        			confirmAlert.setContentText("Registeration success, thanks for choosing us!!");
        			confirmAlert.showAndWait();
        		}else{
        			registerationFailed();
        		}
    			
    		} catch (JSONException | NullPointerException e1) {
    			
    			e1.printStackTrace();
    		}
    		
    	}
    	
    }
    
    /**
     * a callback function called when the registration failed
     */
    void registerationFailed(){
    	informationAlert.setTitle("Sign up ");
    	informationAlert.setHeaderText(null);
    	informationAlert.setContentText("Username and Email must ne unique");
    	informationAlert.showAndWait();
    }
    
    /**
     * a success callback registration function
     */
    void registerationSucceded(){	
    }
    
    /**
     * function that get the username and password, and the system user id if exist
     * check if the user already signed in
     * allow user to sign in if the username and password is available and the user is not signed in from another
     * else block the login flow 
     * @param event
     * @throws JSONException
     */
    @FXML
    void signInButton(ActionEvent event) throws JSONException {
//    	client.request();

//    	if(client == null){
//    		System.out.println("No Connection to the Server");
//    	}
    	
//    	System.out.println("TEST@@@@@@@@@@@@@@@");
    	String _username = usernameTextField.getText();
    	String _password = passwordPasswordField.getText();
    	
    	
    	if(_username.equals("") || _password.equals("")){
    		System.out.println("missing fields");
    		
    		informationAlert.setTitle("Sign in warrning");
    		informationAlert.setHeaderText(null);
    		informationAlert.setContentText("Please fill both the username and password.");
    		informationAlert.showAndWait();
    		
  
    	}else{
	    		if(systemWorkerCheckBox.isSelected() == false)
	    		{
	        		JSONObject json = new JSONObject().put("username", _username).put("password", _password).put("cmd", "Regular");
	        		JSONObject ret = request(json, "Login");
	        		System.out.println(ret.toString());
	        		
	        		if(ret.getBoolean("result")){
	        			
	        			JSONObject temp = ret.getJSONObject("userInfo");
	        			String _firstname = temp.getString("firstName");
	        			String _lastname = temp.getString("lastName");
	        			String _passwrd = temp.getString("password");
	        			String _type = temp.getString("type");
	        			String _email = temp.getString("email");
	        			String _usernm = temp.getString("username");
	        			int _balance = temp.getInt("balance");
	        			String _company = temp.getString("company");
	        			
	        			
	        			SharedData.getInstance().setCurrentUser(new User(_usernm, _email, _passwrd, _firstname,
	        					_lastname, _type, _balance, _company));
	        			
	        			
	        			// updating the costs and lot names
	        			JSONObject upd = request(null, "SystemQueries");
	        			try {
	        				if (upd.getBoolean("result")) {
	        					System.out.println(upd);

	        					JSONArray costs = upd.getJSONArray("Costs");

	        					SharedData.getInstance().setReservationCost(((JSONObject) costs.get(1)).getDouble("cost"));
	        					SharedData.getInstance().setOccasionalCost(((JSONObject) costs.get(0)).getDouble("cost"));
	        					SharedData.getInstance().setRoutineCost(((JSONObject) costs.get(2)).getDouble("cost"));
	        					SharedData.getInstance().setBusinessCost(((JSONObject) costs.get(3)).getDouble("cost"));
	        					SharedData.getInstance().setFullCost(((JSONObject) costs.get(4)).getDouble("cost"));
	        					
	        					JSONArray parkingLotsJA = upd.getJSONArray("lots");
	        					ArrayList<ParkingLot> parkingLotsAL= new ArrayList<ParkingLot>();
	        					for (int i = 0; i < parkingLotsJA.length(); i++){
	        						parkingLotsAL.add(new ParkingLot(parkingLotsJA.getJSONObject(i).getString("lotName")
	        								, 3, 3, parkingLotsJA.getJSONObject(i).getInt("width")));
	        					}
	        					
	        					SharedData.getInstance().setParkingLotsAL(parkingLotsAL);
	        					
	        				}
	        			}catch(JSONException e){
	        				System.out.println("Something ERROR while updating once log in");
	        				e.printStackTrace();
	        			}
	        			
	        			//TODO: change the costs and parking lots name
	        			
	        			SignInCallBack();
	        		}else{
	        			if(ret.getString("info").equals("User already signed in")){
	        				informationAlert.setTitle("Sign in warrning");
		    	    		informationAlert.setHeaderText(null);
		    	    		informationAlert.setContentText("User already signed in.");
		    	    		informationAlert.showAndWait();
	        			}else{
	        				SignInFailed();
	        			}
	        		}
	    		}else{
	    	    	String _workerID = workerIdTextField.getText();
	    	    	System.out.println("print the worker ID" + _workerID);
	    			if(_workerID.equals("")){
	    	    		System.out.println("missing fields");
	    	    		
	    	    		informationAlert.setTitle("Sign in warrning");
	    	    		informationAlert.setHeaderText(null);
	    	    		informationAlert.setContentText("Please fill the worker Id field");
	    	    		informationAlert.showAndWait();
	    			}
	    	    	else{
	    	    		try{
	    	    			Integer _workerIDint = Integer.parseInt(_workerID);
			        		JSONObject json = new JSONObject().put("username", _username)
					        .put("password", _password).put("workerID", _workerIDint).put("cmd", "System");
					        JSONObject ret = request(json, "Login");
					        System.out.println(ret.toString());	
					        if(ret.getBoolean("result")){
			        			JSONObject temp = ret.getJSONObject("userInfo");
			        			String _firstname = temp.getString("firstName");
			        			String _lastname = temp.getString("lastName");
			        			String _passwrd = temp.getString("password");
			        			String _rank = temp.getString("rank");
			        			String _email = temp.getString("email");
			        			String _usernm = temp.getString("username");
			        			int _workerIDtemp = temp.getInt("workerID");
			        			String _affiliation = temp.getString("affiliation");
			        			
			        			System.out.println(temp);
			        			
			        			SharedData.getInstance().setCurrentSystemUser(
			        					new SystemUser(_usernm, _email, _workerIDtemp, _affiliation,_passwrd
			        							,_firstname, _lastname,_rank));
			        			
			        			SignInCallBack();
			        		}else{
			        			if(ret.getString("info").equals("User already signed in")){
			        				informationAlert.setTitle("Sign in warrning");
				    	    		informationAlert.setHeaderText(null);
				    	    		informationAlert.setContentText("User already signed in.");
				    	    		informationAlert.showAndWait();
			        			}else{
			        				SignInFailed();
			        			}
			        		}
	    	    		}catch( NumberFormatException e){
		    	    		informationAlert.setTitle("Sign in warrning");
		    	    		informationAlert.setHeaderText(null);
		    	    		informationAlert.setContentText("Worker ID field isn't supposed to contain letters");
		    	    		informationAlert.showAndWait();
	    	    		}

		        		
	    	    	}
	    		}
    	}
    	
        		   
//        		client.request("login",new JSONObject().put("username", _username).put("password", _password).toString());
    			

//    	System.out.println(_username);
//    	System.out.println(_password);
//    	
    }
    
    /**
     * Signing out from the system
     */
     
    void SignInCallBack(){
		if(systemWorkerCheckBox.isSelected() == false)
		{
	    	String _username = usernameTextField.getText();
	    	Scene currentScene = signiInButton.getScene();
	    	
	    	Parent mainLayout = null;
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("LogInView.fxml"));
			try {
				mainLayout = loader.load();
			} catch (IOException | NullPointerException e) {
				
				e.printStackTrace();
			}
			LogInController logInController = loader.getController();
			logInController.setWelcome("Welcome to CPS");
			logInController.setTopOfLogInView(_username, Double.toString(SharedData.getInstance().getCurrentUser().getBalance()));
			
			if(SharedData.getInstance().getCurrentUser().getType().equals("b")){
				Button businessButton = new Button("Business Routinely Subscription");
				logInController.setBusinessButton(businessButton);
				String css = getClass().getResource("application.css").toExternalForm();
				businessButton.getStylesheets().clear();
				businessButton.getStylesheets().add(css);
				businessButton.getStyleClass().add("loginView-buttons");
				businessButton.setStyle("-fx-pref-width:200px; -fx-pref-height:40px;");
				businessButton.setId("businessRoutinelySubscriptionButton");
				businessButton.setOnAction(e-> {
					logInController.loadBusinessRoutinelySubscription(null);
				});
			}
			
			Scene scene = new Scene(mainLayout);
	    	
	    	Stage stage = (Stage) currentScene.getWindow();
			stage.setScene(scene);
		}else{
			String workerRank = SharedData.getInstance().getCurrentSystemUser().get_rank();
			if(workerRank.equals("D")){
		    	String _fullname = SharedData.getInstance().getCurrentSystemUser().get_firstName()
		    			+ " " + SharedData.getInstance().getCurrentSystemUser().get_lastName();
		    	Scene currentScene = signiInButton.getScene();
		    	
		    	Parent mainLayout = null;
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("ParkingWorkerView.fxml"));
				try {
					mainLayout = loader.load();
				} catch (IOException | NullPointerException e) {
					
					e.printStackTrace();
				}
				ParkingWorkerController parkingWorkerController = loader.getController();
				parkingWorkerController.setWelcome("Welcome to Workers System");
				parkingWorkerController.setTopOfParkingWorker(_fullname);
				Scene scene = new Scene(mainLayout);
		    	
		    	Stage stage = (Stage) currentScene.getWindow();
				stage.setScene(scene);
			}else if(workerRank.equals("C")){
				String _fullname = SharedData.getInstance().getCurrentSystemUser().get_firstName()
		    			+ " " + SharedData.getInstance().getCurrentSystemUser().get_lastName();
		    	Scene currentScene = signiInButton.getScene();
		    	
		    	Parent mainLayout = null;
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("CustomerServiceView.fxml"));
				try {
					mainLayout = loader.load();
				} catch (IOException | NullPointerException e) {
					
					e.printStackTrace();
				}
				CustomerServiceController customerServiceController = loader.getController();
				customerServiceController.setWelcome("Welcome to Workers System");
				customerServiceController.setTopOfParkingWorker(_fullname);
				Scene scene = new Scene(mainLayout);
		    	
		    	Stage stage = (Stage) currentScene.getWindow();
				stage.setScene(scene);
			}else if(workerRank.equals("B")){
				String _fullname = SharedData.getInstance().getCurrentSystemUser().get_firstName()
		    			+ " " + SharedData.getInstance().getCurrentSystemUser().get_lastName();
		    	Scene currentScene = signiInButton.getScene();
		    	
		    	Parent mainLayout = null;
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("ParkingLotDirectorView.fxml"));
				try {
					mainLayout = loader.load();
				} catch (IOException | NullPointerException e) {
					
					e.printStackTrace();
				}
				ParkingLotDirectorController parkingLotDirectorController = loader.getController();
				parkingLotDirectorController.setWelcome("Welcome to Workers System");
				parkingLotDirectorController.setTopOfParkingWorker(_fullname);
				Scene scene = new Scene(mainLayout);
		    	
		    	Stage stage = (Stage) currentScene.getWindow();
				stage.setScene(scene);

			}
			else{
				String _fullname = SharedData.getInstance().getCurrentSystemUser().get_firstName()
		    			+ " " + SharedData.getInstance().getCurrentSystemUser().get_lastName();
		    	Scene currentScene = signiInButton.getScene();
		    	
		    	Parent mainLayout = null;
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("AdministratorView.fxml"));
				try {
					mainLayout = loader.load();
				} catch (IOException | NullPointerException e) {
					
					e.printStackTrace();
				}
				AdministratorController administratorController = loader.getController();
				administratorController.setWelcome("Welcome to Workers System");
				administratorController.setTopOfParkingWorker(_fullname);
				Scene scene = new Scene(mainLayout);
		    	
		    	Stage stage = (Stage) currentScene.getWindow();
				stage.setScene(scene);
			}

		}		
    }

    void SignInFailed(){
    	
    	informationAlert.setTitle("Sign in Error");
    	informationAlert.setHeaderText(null);
    	informationAlert.setContentText("Username or Password uncorrect");
    	informationAlert.showAndWait();
    }
    
    /**
     * callback function once clicking on the business check box once registration
     * @param event
     */
    @FXML
    void BusinessCheckBoxEventHandler(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
		Scene currentScene = cb.getScene();
		TextField businessNameTf = (TextField) currentScene.lookup("#companyNameTextField");
    	if(cb.isSelected()){
    		businessNameTf.setVisible(true);
    	}
    	else{
    		businessNameTf.setVisible(false);
    	}
    }
    
    /**
     * show the user the track reservation window to click 
     * @param event
     * @throws IOException
     */
    @FXML
    void TrackReservation(ActionEvent event) throws IOException 
    {
    	   
    	 	TrakCheck=0;
    		Stage popupwindow=new Stage();
    		      
    		popupwindow.initModality(Modality.APPLICATION_MODAL);
    		popupwindow.setTitle("Track Reservation");
   
    		Label reservationLabel= new Label("Reservation Id:");
    		reservationLabel.setStyle("-fx-pref-width: 80px");

    		Label carNumberLabel = new Label  ("Car Number:");
    		carNumberLabel.setStyle("-fx-pref-width: 80px");
    		TextField reservationTF= new TextField();     
    		TextField carNumberTF= new TextField();    
    		    
    		
    		Button trackbutton= new Button("Track");    
    	
    		HBox layout= new HBox(10);
    		HBox layout2= new HBox(10);
    		VBox vB=new VBox();
    		
    		vB.setPadding(new Insets(10, 10, 10, 10));   
    		
    		layout.getChildren().addAll(reservationLabel,reservationTF);
    		layout2.getChildren().addAll(carNumberLabel,carNumberTF);
    		  
    		vB.getChildren().addAll(layout,layout2,trackbutton);
    		//layout.setAlignment(Pos.CENTER);
    		
    		trackbutton.setOnAction(e -> {
				try {
					trackbutton(vB, carNumberTF.getText());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			});
    		
    		Scene scene1= new Scene(vB, 300, 250);
    		      
    		popupwindow.setScene(scene1);
    		      
    		popupwindow.showAndWait();
    }
    
    /**
     * track popup to insert a car number to track
     * @param vb
     * @param carNumber
     * @throws IOException
     */
    void trackbutton(VBox vb, String carNumber) throws IOException 
    {
    	if(TrakCheck==0) {
    		String trackingResponse = SharedData.getInstance().getCurrentParkingLot().carExists(carNumber);
    		TextArea  trakTA=new TextArea(trackingResponse);
    		trakTA.setWrapText(true);
    		vb.getChildren().add(trakTA);
    		TrakCheck=1;
    		trakTA.setEditable(false);
    	}
    }
    	
    /**
     * callback function once clicking the worker id box to show the worker id text field
     * 
     * @param event
     */
    @FXML
    void WorkerIDCheckBoxEventHandler(ActionEvent event) {
    	CheckBox cb = (CheckBox) event.getSource();
		Scene currentScene = cb.getScene();
		Label workerIdl = (Label) currentScene.lookup("#workedIdLabel");
		TextField workerIdTf = (TextField) currentScene.lookup("#workerIdTextField");
    	if(cb.isSelected()){
    		workerIdTf.setVisible(true);
    		workerIdl.setVisible(true);
    	}
    	else{
    		workerIdTf.setVisible(false);
    		workerIdl.setVisible(false);
    	}
    }
    
    
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
		   
		    if(json != null){
		    	sentData.writeBytes(json.toString());
			    
			    sentData.close();
		    }
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


