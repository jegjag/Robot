package robot.core;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Init
{
	public static void main(String[] args)
	{
		// JInput stuff (For XBAAWKS controller so I don't have to chase after the laptop)
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(int i = 0; i < controllers.length; i++)
		{
			// Get controller name
			System.out.println("Controller '" + controllers[i].getName() + "' detected.");
		}
	}
}
