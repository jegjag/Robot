package robot.core;

import static java.lang.Math.*;

import static robot.core.Arduino.Motor.*;

import robot.core.Arduino.Direction;
import static robot.core.Arduino.Direction.*;

public class Motors
{
	private Arduino arduino;
	
	private int speed = 0;
	private Direction direction;
	private float turning = 0.0f;
	
	public Motors(Arduino arduino)
	{
		this.arduino = arduino;
	}
	
	public void turn(float axis)
	{
		turning = axis * 90;
	}
	
	public void forward(float axis)
	{
		if(axis > 0)
		{
			direction = FORWARD;
		}
		else if(axis < 0)
		{
			direction = BACKWARD;
		}
		else
		{
			direction = BRAKE;
			speed = 0;
			return;
		}
		speed = round(255 * abs(axis));
	}
	
	public void send()
	{
		if(turning > 0)
		{
			// Turn right
		}
		else if(turning < 0)
		{
			// Turn left
		}
		else
		{
			// Straight ahead
			arduino.send(A, direction, speed);
			arduino.send(B, direction, speed);
		}
	}
}
