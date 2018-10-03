package robot.input;

import static net.java.games.input.Component.Identifier.Button.*;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;

import robot.core.Init;

public class ControllerHandler
{
	private Controller gamepad;
	
	public boolean	a_pressed = false, b_pressed = false,
					x_pressed = false, y_pressed = false,
					lb_pressed = false, rb_pressed = false,
					ls_down = false, rs_down = false,
					start_pressed = false, back_pressed = false;
	
	// Deadzone
	public float	deadzone = 0.1f;
	public float	trigger_deadzone = deadzone / 8;
	
	// Left stick
	public float	axis_x = 0f, axis_y = 0f;
	
	// LT/RT
	public float	axis_z = 0f;
	
	// Constructor
	public ControllerHandler(Controller gamepad)	{ this.gamepad = gamepad; }
	
	// Update
	public void update()
	{
		gamepad.poll();
		
		Component[] components = gamepad.getComponents();
		for(Component c : components)
		{
			// Handle deadzone
			char[] deadzoneChars = new String(c.getDeadZone() + "").toCharArray();
			deadzoneChars[deadzoneChars.length - 1] = '1';
			deadzone = Float.parseFloat(new String(deadzoneChars));
			
			// Handle input
			Identifier id = c.getIdentifier();
			
			/* I would love to be able to use a switch statement,
			 * but the library I'm using does not use enums for button IDs.
			 * This makes the whole process horribly inefficient as seen below. */
			
			if(c.isAnalog())
			{
				// Handle sticks and LT/RT
				float val = c.getPollData();
				if(id == Axis.Z)
				{
					if
					(
						!(	(val < 0 && val > trigger_deadzone)
						||	(val > 0 && val < trigger_deadzone))
					)		{ axis_z = val; }
					else	{ axis_z = 0.0f; }
				}
				else if
				(
					!(	(val > 0 && val < deadzone)
					|| 	(val < 0 && val > -deadzone))
				)
				{
					// Sticks
					if(id == Axis.X)	axis_x = val;
					if(id == Axis.Y)	axis_y = val;
				}
				else
				{
					if(id == Axis.X)	axis_x = 0.0f;
					if(id == Axis.Y)	axis_y = 0.0f;
				}
			}
			else
			{
				// Buttons
				if(id.equals(_0))
				{
					if(c.getPollData() == 1.0F && !a_pressed)			a_pressed = true;
					else if(c.getPollData() == 0.0F && a_pressed)		a_pressed = false;
				}
				if(id.equals(_1))
				{
					if(c.getPollData() == 1.0F && !b_pressed)			b_pressed = true;
					else if(c.getPollData() == 0.0F && b_pressed)		b_pressed = false;
				}
				if(id.equals(_2))
				{
					if(c.getPollData() == 1.0F && !x_pressed)			x_pressed = true;
					else if(c.getPollData() == 0.0F && x_pressed)		x_pressed = false;
				}
				if(id.equals(_3))
				{
					if(c.getPollData() == 1.0F && !y_pressed)			y_pressed = true;
					else if(c.getPollData() == 0.0F && y_pressed)
					{
						// Toggle showGrid
						Init.toggleGrid();
						y_pressed = false;
					}
				}
				if(id.equals(_4))
				{
					if(c.getPollData() == 1.0F && !lb_pressed)			lb_pressed = true;
					else if(c.getPollData() == 0.0F && lb_pressed)		lb_pressed = false;
				}
				if(id.equals(_5))
				{
					if(c.getPollData() == 1.0F && !rb_pressed)			rb_pressed = true;
					else if(c.getPollData() == 0.0F && rb_pressed)		rb_pressed = false;
				}
				if(id.equals(_6))
				{
					if(c.getPollData() == 1.0F && !back_pressed)		back_pressed = true;
					else if(c.getPollData() == 0.0F && back_pressed)
					{
						// Close program
						Init.exit();
						back_pressed = false;
					}
				}
				if(id.equals(_7))
				{
					if(c.getPollData() == 1.0F && !start_pressed)		start_pressed = true;
					else if(c.getPollData() == 0.0F && start_pressed)	start_pressed = false;
				}
				if(id.equals(_8))
				{
					if(c.getPollData() == 1.0F && !ls_down)				ls_down = true;
					else if(c.getPollData() == 0.0F && ls_down)			ls_down = false;
				}
				if(id.equals(_9))
				{
					if(c.getPollData() == 1.0F && !rs_down)				rs_down = true;
					else if(c.getPollData() == 0.0F && rs_down)			rs_down = false;
				}
			}
		}
	}
}
