package model;


import java.util.ArrayList;
import model.ParkingSlot;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ParkingSituation {
	
	private ArrayList<GridPane> gpAL;
	private int length;
	private int width;
	
    public ParkingSituation(int length, int width) {
		super();
		this.gpAL = new ArrayList<GridPane>();
		this.length = length;
		this.width = width;
	}

	public void getGridLayer(ParkingLot parkingLot) {
			Matrix mat = new Matrix(parkingLot.getHeight(), parkingLot.getWidth(), 1, parkingLot.get_lot());
			mat.makeMatrix(parkingLot);
	   }
        
    }