package robot.input;

import static robot.core.Init.*;

import static net.java.games.input.Component.Identifier.Key.*;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;

public class KeyboardHandler
{
	private Controller keyboard;
	
	public KeyboardHandler(Controller keyboard)
	{
		this.keyboard = keyboard;
	}
	
	public void update()
	{
		keyboard.poll();
		
		Component[] components = keyboard.getComponents();
		for(Component c : components)
		{
			float val = c.getPollData();
			Identifier id = c.getIdentifier();
			
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
		}
	}
}
