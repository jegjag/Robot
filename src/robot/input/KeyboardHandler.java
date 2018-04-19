package robot.input;

import static robot.core.Init.*;

import static net.java.games.input.Component.Identifier.Key.*;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;

public class KeyboardHandler
{
	private Controller keyboard;
	
	private boolean w_pressed = false, s_pressed = false;
	
	public float accelAxis = 0.0f;
	
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
					w_pressed = true;
				}
				if(id == S)
				{
					// Backward
					s_pressed = true;
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
					w_pressed = false;
				}
				if(id == S)
				{
					s_pressed = false;
				}
			}
		}
		
		if(w_pressed && s_pressed)
		{
			accelAxis = 0.0f;
		}
		else if(w_pressed)
		{
			accelAxis = 1.0f;
		}
		else if(s_pressed)
		{
			accelAxis = -1.0f;
		}
		else
		{
			accelAxis = 0.0f;
		}
	}
}
