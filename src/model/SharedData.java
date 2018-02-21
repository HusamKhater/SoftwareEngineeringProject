package model;

import java.util.ArrayList;

public class SharedData {
		private User currentUser;
		private SystemUser currentSystemUser;
		private ParkingLot currentParkingLot;
		private double reservationCost;
		private double occasionalCost;
		private double routineCost;
		private double businessCost;
		private double fullCost;
		private String IP;
		private String PORT;
		private String CPSEmail;
		private String CPSPassword;
		private ArrayList<ParkingLot> parkingLotsAL;
		private static SharedData instance = null;
	   	private SharedData() {
	   		currentUser = null;
	   		currentSystemUser = null;
	   		currentParkingLot = null;
	   		reservationCost = -1;
	   		occasionalCost = -1;
	   		routineCost = -1;
	   		businessCost = -1;
	   		fullCost = -1;
	   		IP = null;
	   		PORT = null;
	   		parkingLotsAL = null;
	   		CPSEmail = null;
	   		CPSPassword = null;
	   	}
	   	
		public static SharedData getInstance() {
	   		if(instance == null) {
	   			instance = new SharedData();
	   		}
	   		return instance;
	   	}

	   	public ParkingLot getCurrentParkingLot() {
			return currentParkingLot;
		}

		public void setCurrentParkingLot(ParkingLot currentParkingLot) {
			this.currentParkingLot = currentParkingLot;
		}

		public double getReservationCost() {
			return reservationCost;
		}

		public void setReservationCost(double reservationCost) {
			this.reservationCost = reservationCost;
		}

		public double getOccasionalCost() {
			return occasionalCost;
		}

		public void setOccasionalCost(double occasionalCost) {
			this.occasionalCost = occasionalCost;
		}

		public double getRoutineCost() {
			return routineCost;
		}

		public void setRoutineCost(double routineCost) {
			this.routineCost = routineCost;
		}

		public double getBusinessCost() {
			return businessCost;
		}

		public void setBusinessCost(double businessCost) {
			this.businessCost = businessCost;
		}

		public double getFullCost() {
			return fullCost;
		}

		public void setFullCost(double fullCost) {
			this.fullCost = fullCost;
		}

		public String getIP() {
			return IP;
		}

		public void setIP(String iP) {
			IP = iP;
		}

		public String getPORT() {
			return PORT;
		}

		public void setPORT(String pORT) {
			PORT = pORT;
		}

		public User getCurrentUser() {
			return currentUser;
		}

		public void setCurrentUser(User currentUser) {
			this.currentUser = currentUser;
		}

		public SystemUser getCurrentSystemUser() {
			return currentSystemUser;
		}

		public void setCurrentSystemUser(SystemUser currentSystemUser) {
			this.currentSystemUser = currentSystemUser;
		}

		public ArrayList<ParkingLot> getParkingLotsAL() {
			return parkingLotsAL;
		}

		public void setParkingLotsAL(ArrayList<ParkingLot> parkingLotsAL) {
			this.parkingLotsAL = parkingLotsAL;
		}

		public String getCPSEmail() {
			return CPSEmail;
		}

		public void setCPSEmail(String cPSEmail) {
			CPSEmail = cPSEmail;
		}

		public String getCPSPassword() {
			return CPSPassword;
		}

		public void setCPSPassword(String cPSPassword) {
			CPSPassword = cPSPassword;
		}
		
	   	
}