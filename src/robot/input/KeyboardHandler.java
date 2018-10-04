package robot.input;

import static robot.core.Init.*;
import static robot.protocol.Command.*;

import static net.java.games.input.Component.Identifier.Key.*;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;

import robot.protocol.Command;

public class KeyboardHandler
{
	// Controller Object
	private Controller keyboard;
	
	// Constructor
	public KeyboardHandler(Controller keyboard)	{ this.keyboard = keyboard; }
	
	// Update method
	/**
	 * @return 2 commands, one for motor A (index=0) and one for motor B (index=1)
	 */
	public Command[] update()
	{
		keyboard.poll();
		
		final Component[] components = keyboard.getComponents();
		for(Component c : components)
		{
			final float val = c.getPollData();
			final Identifier id = c.getIdentifier();
			
			if(val == 1.0f)
			{
				if(id == A)
				{
					// Turn left
					Command motorA = new Command(Motor.A, Direction.BACKWARD, 255);
					Command motorB = new Command(Motor.B, Direction.FORWARD, 255);
					
					return new Command[] { motorA, motorB };
				}
				
				if(id == D)
				{
					// Turn right
					Command motorA = new Command(Motor.A, Direction.FORWARD, 255);
					Command motorB = new Command(Motor.B, Direction.BACKWARD, 255);
					
					return new Command[] { motorA, motorB };
				}
				
				if(id == W)
				{
					// Forward
					Command motorA = new Command(Motor.A, Direction.FORWARD, 255);
					Command motorB = new Command(Motor.B, Direction.FORWARD, 255);
					
					return new Command[] { motorA, motorB };
				}
				if(id == S)
				{
					// Backward
					Command motorA = new Command(Motor.A, Direction.BACKWARD, 255);
					Command motorB = new Command(Motor.B, Direction.BACKWARD, 255);
					
					return new Command[] { motorA, motorB };
				}
				
				if(id == ESCAPE)
				{
					// Exit
					exit();
					return null;
				}
			}
		}
		
		// Brake
		Command motorA = new Command(Motor.A, Direction.FORWARD, 0);
		Command motorB = new Command(Motor.B, Direction.BACKWARD, 0);
		return new Command[] { motorA, motorB };
	}
}
