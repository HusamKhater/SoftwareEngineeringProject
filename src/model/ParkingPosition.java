package model;

public class ParkingPosition {
	public final int x;
	public final int y;
	public final int z;
	public final SpotStatus status;

	public ParkingPosition(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.status = SpotStatus.Available;
	}

}
