package robot.protocol;

public class Command
{
	// Enum definitions
	public static enum Motor
	{
		// Motors
		A((byte) 0x00),
		B((byte) 0x01);
		
		// Byte for protocol
		final char protocolByte;
		
		// Constructor (Enforcing byte length)
		Motor(byte protocolByte)				{ this.protocolByte = (char) protocolByte; }
	}
	
	public static enum Direction
	{
		// Directions
		FORWARD((byte) 0x01),
		BACKWARD((byte) 0x00);
		
		// Byte for protocol
		final char protocolByte;
		
		// Constructor (Enforcing byte length)
		Direction(byte protocolByte)			{ this.protocolByte = (char) protocolByte; }
	}
	
	// Variables
	public Motor motor;
	public Direction direction;
	public int speed;
	
	// Constructors
	public Command() {}
	public Command(Motor motor, Direction direction, int speed)
	{
		this.motor = motor;
		this.direction = direction;
		
		if(speed > 255 || speed < 0)	// Brake if invalid
		{
			System.err.println("Invalid speed \"" + speed + "\".");
			speed = 0;
		}
		
		this.speed = speed;
	}
}
