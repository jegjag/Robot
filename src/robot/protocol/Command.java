package robot.protocol;

public class Command
{
	public static enum Motor
	{
		// Motors
		A((byte) 0x00),
		B((byte) 0x01);
		
		// Byte for protocol
		final byte protocolByte;
		
		// Constructor
		Motor(byte protocolByte)			{ this.protocolByte = protocolByte; }
	}
	
	public static enum Direction
	{
		// Directions
		FORWARD((byte) 0x01),
		BACKWARD((byte) 0x00);
		
		// Byte for protocol
		final byte protocolByte;
		
		// Constructor
		Direction(byte protocolByte)		{ this.protocolByte = protocolByte; }
	}
	
	public Motor motor;
	public Direction direction;
	public int speed;
}
