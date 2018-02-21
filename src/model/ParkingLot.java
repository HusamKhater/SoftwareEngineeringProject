package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import com.itextpdf.text.pdf.ArabicLigaturizer;
import com.sun.javafx.scene.text.HitInfo;


public class ParkingLot {
	
	private String _name;
	private int _depth;
	private int _height;
	private int _width;
	private final int _capacity;
	private ParkingSlot[][][] _lot;
	private int _emptySlots;
	private int _reservedSlots;
	private int _usedSlots;
	private int _disabledSlots;

	private HashMap<String, ParkingPosition> _hash; // list of parked cars
	private HashMap<String, Calendar> _isParkedToday;

	/**
	 * 
	 * the constructor.
	 * 
	 * @param _name the name of the lot
	 * @param _depth the depth of the lot
	 * @param _height the height of the lot
	 * @param _width the width of the lot
	 * 
	 * 
	 */
	public ParkingLot(String _name, int _depth, int _height, int _width) {

		super();
		this._name = _name;
		this._depth = _depth;
		this._height = _height;
		this._width = _width;

		_lot = new ParkingSlot[_height][_width][_depth];

		for (int i = 0; i < this._depth; i++) {
			for (int j = 0; j < this._width; j++) {
				for (int k = 0; k < this._height; k++) {
					_lot[k][j][i] = new ParkingSlot();
				}
			}
		}

		_emptySlots = _depth * _width * _height;
		_capacity = _emptySlots;
		_reservedSlots = 0;
		_usedSlots = 0;
		_disabledSlots = 0;

		_hash = new HashMap<>();
		_isParkedToday = new HashMap<>();

	}
	

	/**
	 * void function that add the given number to the empty slots indicator
	 * @param change the number of the add/removed amount
	 * 
	 */
	public void changeEmptySlots(int change) {
		_emptySlots = _emptySlots + change;
	}

	/**
	 * void function that add the given number to the reserved slots indicator
	 * @param change the number of the add/removed amount
	 * 
	 */
	public void changeReservedSlots(int change) {
		_reservedSlots = _reservedSlots + change;
	}

	/**
	 * void function that add the given number to the used slots indicator
	 * @param change the number of the add/removed amount
	 * 
	 */
	public void changeUsedSlots(int change) {
		_usedSlots = _usedSlots + change;
	}
	
	/**
	 * void function that add the given number to the disapled slots indicator
	 * @param change the number of the add/removed amount
	 * 
	 */
	public void changeDisableSlots(int change) {
		_disabledSlots = _disabledSlots + change;
	}

	/**
	 * 
	 * @return the number of empty slots in the lot
	 */
	public int getEmptySlots() {
		return _emptySlots;
	}

	/**
	 * 
	 * @return the number of reserved slots in the lot
	 */
	public int getReservedSlots() {
		return _reservedSlots;
	}

	/**
	 * 
	 * @return the number of used slots in the lot
	 */
	public int getUsedSlots() {
		return _usedSlots;
	}

	/**
	 * 
	 * @return the number of disabled slots in the lot
	 */
	public int getDisableSlots() {
		return _disabledSlots;
	}

	/**
	 * 
	 * @return the depth of the lot
	 */
	public int getDepth() {
		return _depth;
	}


	/**
	 * 
	 * @param _depth the new depth of the lot
	 */
	public void setDepth(int _depth) {
		this._depth = _depth;
	}

	/**
	 * 
	 * @return the height of the lot
	 */
	public int getHeight() {
		return _height;
	}

	/**
	 * 
	 * @param _height the new height of the lot
	 */
	public void setHeight(int _height) {
		this._height = _height;
	}

	/**
	 * 
	 * @return the width of the lot
	 */
	public int getWidth() {
		return _width;
	}

	/**
	 * 
	 * @param _width the new depth of the lot
	 */
	public void setWidth(int _width) {
		this._width = _width;
	}

	/**
	 * 
	 * @return the lot matrix witch is 3D matrix of parking slots
	 */
	public ParkingSlot[][][] get_lot() {
		return _lot;
	}
	
	/**
	 * 
	 * @param _lot the new 3D matrix to change
	 */
	public void setLot(ParkingSlot[][][] _lot) {
		this._lot = _lot;
	}

	/**
	 * 
	 * @return the name of the lot
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * 
	 * @param _name the new name of the lot
	 */
	public void set_name(String _name) {
		this._name = _name;
	}

	/**
	 * function that search the 3D matrix and return the closest empty slot
	 * 
	 * @return a ParkingPosition object, that hold the coordinates of the empty slot to insert a car to it.
	 */
	public ParkingPosition getEmptyParkingPosition() {
		for (int i = 0; i < _depth; i++) {
			for (int j = 0; j < this._width; j++) {
				for (int k = 0; k < this._height; k++) {
					// System.out.println(_lot[k][j][i] + " "+ i + j + k);
					if (_lot[k][j][i].getStatus() == SpotStatus.Available) {
						return new ParkingPosition(k, j, i);
					}
				}
			}
		}
		
		
		
		return null;
	}

	public void rePark(){
		
		ArrayList<ParkingSlot> temp = new ArrayList<ParkingSlot>();
		ArrayList<Long> time = new ArrayList<Long>();
		
		_hash.clear();
		for (int i = 0; i < _depth; i++) {
			for (int j = 0; j < this._width; j++) {
				for (int k = 0; k < this._height; k++) {
					// System.out.println(_lot[k][j][i] + " "+ i + j + k);
					if (_lot[k][j][i].getStatus() == SpotStatus.Busy) {
//						System.out.println(_lot[k][j][i].getCarNumber() + " in " + k + " " + j + " " 
//								+ i + " with: " + _lot[k][j][i].getLeave().getTime().toString());
//						System.out.println(_lot[k][j][i].getCarNumber());
						
						ParkingSlot n = new ParkingSlot();
						n.setCarNumber(_lot[k][j][i].getCarNumber());
						n.setStatus(_lot[k][j][i].getStatus());
						n.setArrive(_lot[k][j][i].getArrive());
						n.setLeave(_lot[k][j][i].getLeave());
						
						temp.add(n);
						time.add(n.getLeave().getTimeInMillis());
//						System.out.println(time.size());
						_lot[k][j][i].setCarNumber("");
						_lot[k][j][i].setStatus(SpotStatus.Available);
						_lot[k][j][i].setArrive(null);
						_lot[k][j][i].setLeave(null);
						
						_usedSlots--;
						_emptySlots++;
						
					}
				}
			}
		}
		
		ArrayList<ParkingSlot> ret = new ArrayList<ParkingSlot>();
		
		while(time.size() > 0){
			long min = Collections.min(time);
			int index = time.indexOf(min);
			
			ParkingSlot n = new ParkingSlot();
			n.setCarNumber(temp.get(index).getCarNumber());
			n.setStatus(temp.get(index).getStatus());
			n.setArrive(temp.get(index).getArrive());
			n.setLeave(temp.get(index).getLeave());
			ret.add(n);
			
			temp.remove(index);
			time.remove(index);
		}
		
		ParkingPosition pos;
		for(int i = 0; i < ret.size(); i++){
//			InsertCar(ret.get(i).getCarNumber(), ret.get(i).getArrive(), ret.get(i).getLeave());
			pos = getEmptyParkingPosition();
			int x = pos.x;
			int y = pos.y;
			int z = pos.z;

			_lot[x][y][z].setCarNumber(ret.get(i).getCarNumber());

			if (_lot[x][y][z].getStatus() == SpotStatus.Reserved) {
				_reservedSlots--;
			} else if (_lot[x][y][z].getStatus() == SpotStatus.Available) {
				_emptySlots--;
			}
			_lot[x][y][z].setStatus(SpotStatus.Busy);

			_lot[x][y][z].setArrive(ret.get(i).getArrive());
			_lot[x][y][z].setLeave(ret.get(i).getLeave());

			_usedSlots++;
			

			_hash.put(ret.get(i).getCarNumber(), new ParkingPosition(x, y, z));
		}
					
	}
	
	/**
	 * 
	 * @param carNumber the new car number
	 * @param arrive the expected arriving time
	 * @param leave the expected leaving time
	 * @return true if the function succeeded, false if the car is already parked.
	 */
	public boolean InsertCar(String carNumber, Calendar arrive, Calendar leave) {

		ParkingPosition pos;
		if (!this.isFull()) {
			if (_hash.containsKey(carNumber)) {
//				System.out.println("@@@@@@:> your car is already in !!");
				return false;
			} else {
				pos = getEmptyParkingPosition();
				int x = pos.x;
				int y = pos.y;
				int z = pos.z;

				_lot[x][y][z].setCarNumber(carNumber);

				if (_lot[x][y][z].getStatus() == SpotStatus.Reserved) {
					_reservedSlots--;
				} else if (_lot[x][y][z].getStatus() == SpotStatus.Available) {
					_emptySlots--;
				}
				_lot[x][y][z].setStatus(SpotStatus.Busy);

				_lot[x][y][z].setArrive(arrive);
				_lot[x][y][z].setLeave(leave);

				_usedSlots++;
				

				_hash.put(carNumber, new ParkingPosition(x, y, z));

				rePark();
				
				return true;
			}
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @return true if the parking lot is full, false otherwise
	 */
	public boolean isFull() {
		return (_disabledSlots + _reservedSlots + _usedSlots == _capacity);
	}

	/**
	 * 
	 * @param carNumber the car number to extract
	 * @return true if extracting the car succeeded, false otherwise
	 */
	public boolean ExtractCar(String carNumber) {
		if (_hash.containsKey(carNumber)) {
			ParkingPosition pos = _hash.get(carNumber);
			_lot[pos.x][pos.y][pos.z].setCarNumber("");
			_lot[pos.x][pos.y][pos.z].setStatus(SpotStatus.Available);
			_lot[pos.x][pos.y][pos.z].setArrive(null);
			_lot[pos.x][pos.y][pos.z].setLeave(null);

			_usedSlots--;
			_emptySlots++;

			_hash.remove(carNumber);

			rePark();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * function that initialize the array, that means that we have to return the parking lot to the started view,
	 * no parked cars, no disabled/reserved slots.
	 */
	public void initialize() {
		_hash.clear();
		_isParkedToday.clear();
		
		for (int i = 0; i < this._depth; i++) {
			for (int j = 0; j < this._width; j++) {
				for (int k = 0; k < this._height; k++) {
					_lot[k][j][i].setCarNumber("");
					_lot[k][j][i].setStatus(SpotStatus.Available);
					_lot[k][j][i].setArrive(null);
					_lot[k][j][i].setLeave(null);
				}
			}	
		}
		_usedSlots=0;
		_reservedSlots = 0;
		_disabledSlots = 0;
		
		_emptySlots=_capacity;
		
		
	}
	
	/**
	 * 
	 * @param carNumber the car number to track, 
	 * @return a string witch indicate the details of the car in the lot, when it arrived, when its expected to leave.
	 */
	public String carExists(String carNumber) {
		if (_hash.containsKey(carNumber)) {
			ParkingPosition pos = _hash.get(carNumber);
			Calendar arrive = _lot[pos.x][pos.y][pos.z].getArrive();
			Calendar leave = _lot[pos.x][pos.y][pos.z].getLeave();
			return("Your car has been entered to the parking lot in " + arrive.getTime().toString()
					+"\nIt is in safe hands."
					+"\nyou are expected to arrive here in " + leave.getTime().toString() + " and take it."
					+"\nPlease try to be in time.");
		} else {
			return("Car doesn't exists in the parking lot.");
		}
	}
	
	/**
	 * 
	 * @param carNumber
	 * @return the position of the given car number in the parking lot.
	 */
	public ParkingPosition getPosition(String carNumber) {
		return _hash.get(carNumber);

	}

	/**
	 * function that get a number of expected reserved number, check if the given number can parked together in the lot in the current time.
	 * used to check for occissional park 
	 * 
	 * @param numOfReserves the number of reserves to check if they can enter together
	 * @return true if there are place to the given number of reserved to park in the lot.
	 */
	public boolean CanPark(int numOfReserves) {

		int parkingLen = _hash.size();
		int total = parkingLen + numOfReserves + _disabledSlots + _reservedSlots;
		return total < _capacity;

	}

	/**
	 * function that get a number of expected reserved number, check if the given number can parked together in the lot.
	 * 
	 * @param numOfReserves the number of reserves to check if they can enter together
	 * @return true if there are place to the given number of reserved to park in the lot.
	 */
	public boolean CanReserve(int numOfReserves) {

		int total = numOfReserves + _disabledSlots + _reservedSlots;
		return total < _capacity;

	}
	
	/**
	 * 
	 * @return if there are a free slot to disable
	 */
	public boolean CanDisapled(){
		return (_usedSlots + _disabledSlots + _reservedSlots) < _capacity;
	}
	
	/**
	 * 
	 * @return if there are at least one disapled slot
	 */
	public boolean CanUnDisapled(){
		return (_disabledSlots) > 0;
	}
	
	/**
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true if the giver position is busy
	 */
	public boolean IsBusy(int hight, int width, int depth){
		return _lot[hight][width][depth].getStatus() == SpotStatus.Busy;
	}
	
	/**
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true if the giver position is available
	 */
	public boolean IsAvailable(int hight, int width, int depth){
		return _lot[hight][width][depth].getStatus() == SpotStatus.Available;
	}
	
	/**
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true if the giver position is disabled
	 */
	public boolean IsDisapled(int hight, int width, int depth){
		return _lot[hight][width][depth].getStatus() == SpotStatus.Unavailable;
	}
	
	/**
	 * the function indicate a given position as disabled
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true if disabling succeeded, false otherwise
	 */
	public boolean disaplySlot(int hight, int width, int depth) {
		
		_lot[hight][width][depth].setCarNumber("");
		_lot[hight][width][depth].setStatus(SpotStatus.Unavailable);
		_lot[hight][width][depth].setArrive(null);
		_lot[hight][width][depth].setLeave(null);
		
		_disabledSlots++;
		_emptySlots--;
		
		return true;
		
	}
	
	/**
	 * 
	 * the function indicate a given position as available
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true if availabling succeeded, false otherwise
	 */
	public boolean undisaplySlot(int hight, int width, int depth) {
		
		_lot[hight][width][depth].setCarNumber("");
		_lot[hight][width][depth].setStatus(SpotStatus.Available);
		_lot[hight][width][depth].setArrive(null);
		_lot[hight][width][depth].setLeave(null);
		
		_disabledSlots--;
		_emptySlots++;
		
		return true;
		
	}
	
	
	/**
	 * this function check if the given car number (occisionl) already parked today
	 * 
	 * @param carNumber
	 * @param arrive
	 * @param leave
	 * @return true if the car inserted to the parking lot. false if the car parked today
	 */
	public boolean checkParkForRoutineSub(String carNumber, Calendar arrive, Calendar leave){
		
		if(_isParkedToday.get(carNumber) == null || 
				(_isParkedToday.get(carNumber).get(Calendar.DAY_OF_MONTH) < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) ||
				(_isParkedToday.get(carNumber).get(Calendar.MONTH) < Calendar.getInstance().get(Calendar.MONTH))){
			return parkThisRoutine(carNumber, arrive, leave);
		}
		return false;
	}
	
	/**
	 * function that park the routine subscripe if the car didnt parked today
	 * @param carNumber
	 * @param arrive
	 * @param leave
	 * @return true if the insertion succeeded, false otherwise
	 */
	public boolean parkThisRoutine(String carNumber, Calendar arrive, Calendar leave){
		if(_isParkedToday.get(carNumber) != null){
			_isParkedToday.get(carNumber).setTime(arrive.getTime());
		}else{
			_isParkedToday.put(carNumber, arrive);
		}
		
		return InsertCar(carNumber, arrive, leave);
	}
	
	/**
	 * function that reserve a given slot as reserved
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true :)
	 */
	public boolean reserveSlot(int hight, int width, int depth) {
		
		_lot[hight][width][depth].setCarNumber("");
		_lot[hight][width][depth].setStatus(SpotStatus.Reserved);
		_lot[hight][width][depth].setArrive(null);
		_lot[hight][width][depth].setLeave(null);
		
		_reservedSlots++;
		_emptySlots--;
		
		return true;
		
	}
	
	/**
	 * funtion that reserve a given slot as available
	 * 
	 * @param hight
	 * @param width
	 * @param depth
	 * @return true :)
	 */
	public boolean unReserveSlot(int hight, int width, int depth) {
		
		_lot[hight][width][depth].setCarNumber("");
		_lot[hight][width][depth].setStatus(SpotStatus.Available);
		_lot[hight][width][depth].setArrive(null);
		_lot[hight][width][depth].setLeave(null);
		
		_reservedSlots--;
		_emptySlots++;
		
		return true;
		
	}
	
	/**
	 * 
	 * @return arraylist of the reserved positions, to show the worker which slots is reserved
	 */
    public ArrayList<ParkingPosition> getSlotsByReserved(){
		ArrayList <ParkingPosition> slotsAL = new ArrayList<ParkingPosition>();
		for(int x = 0; x < this._height; x++){
			for(int y = 0; y < this._width; y++){
				for(int z = 0; z < this._depth; z++){
					if(this._lot[x][y][z].getStatus().equals(SpotStatus.Reserved)){
						slotsAL.add(new ParkingPosition(x,y,z));
					}
				}
			}
		}
    	return slotsAL;
    }
    
    /**
	 * 
	 * @return arraylist of the reserved positions, to show the worker which slots is disabled
	 */
    public ArrayList<ParkingPosition> getSlotsByDisabled(){
		ArrayList <ParkingPosition> slotsAL = new ArrayList<ParkingPosition>();
		for(int x = 0; x < this._height; x++){
			for(int y = 0; y < this._width; y++){
				for(int z = 0; z < this._depth; z++){
					if(this._lot[x][y][z].getStatus().equals(SpotStatus.Unavailable)){
						slotsAL.add(new ParkingPosition(x,y,z));
					}
				}
			}
		}
    	return slotsAL;
    }

    /**
     * 
     * @param hight
     * @param width
     * @param depth
     * @return true if the given position is reserved, else otherwise 
     */
	public boolean IsReserved(int hight, int width, int depth){
		return _lot[hight][width][depth].getStatus() == SpotStatus.Reserved;
	}
	
	/**
	 * 
	 * @return true if there are no cars in the lot, so we can block the parking lot, if there are a parked car, we cannot block
	 */
	public boolean CanIBlock(){
		return _usedSlots == 0;
	}

	/**
	 * 
	 * the function block the parking lot, change all the empty slots to disapled.
	 * we use this function in case we have a full case.
	 * 
	 * @return true
	 */
	public boolean Block() {
		
		for (int i = 0; i < this._depth; i++) {
			for (int j = 0; j < this._width; j++) {
				for (int k = 0; k < this._height; k++) {
					
					_lot[k][j][i].setCarNumber("");
					_lot[k][j][i].setStatus(SpotStatus.Unavailable);
					_lot[k][j][i].setArrive(null);
					_lot[k][j][i].setLeave(null);
				
					_disabledSlots++;
				}
			}	
		}
		_usedSlots = 0;
		_reservedSlots = 0;
		_emptySlots = 0;
	
		return true;
	}
    

}
