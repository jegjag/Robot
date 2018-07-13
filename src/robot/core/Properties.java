package robot.core;

public class Properties
{
	public Direction direction;
	public int speed;
	
	public static enum MotorID
	{
		A("A"),
		B("B");
		
		final String symbol;
		
		MotorID(String symbol)
		{
			this.symbol = symbol;
		}
	}
	
	public static enum Direction
	{
		FORWARD("U"),
		BACKWARD("D"),
		BRAKE("S");
		
		final String symbol;
		
		Direction(String symbol)
		{
			this.symbol = symbol;
		}
	}
}