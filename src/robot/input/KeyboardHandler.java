package robot.input;

import static robot.core.Init.*;

import static net.java.games.input.Component.Identifier.Key.*;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;

public class KeyboardHandler
{
	// Controller Object
	private Controller keyboard;
	
	// Constructor
	public KeyboardHandler(Controller keyboard)	{ this.keyboard = keyboard; }
	
	// Update method
	public void update()
	{
		keyboard.poll();
		
		final Component[] components = keyboard.getComponents();
		for(Component c : components)
		{
			final float val = c.getPollData();
			final Identifier id = c.getIdentifier();
			
			if(val == 1.0f)
			{
				if(id == W)
				{
					// Forward
				}
				if(id == S)
				{
					// Backward
				}
				
				if(id == A)
				{
					// Turn left
				}
				
				if(id == D)
				{
					// Turn right
				}
				
				if(id == ESCAPE)
				{
					// Exit
					exit();
				}
			}
			else if(val == 0.0f)
			{
				if(id == W)
				{
					// Stop W
				}
				if(id == S)
				{
					// Stop S
				}
			}
		}
	}
}
