package robot.input;

import static robot.protocol.Command.*;

import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.*;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;

import robot.protocol.Command;

public class KeyboardListener implements KeyListener
{
	private ArrayDeque<Integer> keyStack = new ArrayDeque<Integer>(); 
	
	protected static final Command[] MOVE_BRAKE		= new Command[]
	{
		new Command(Motor.A, Direction.FORWARD, 0),
		new Command(Motor.B, Direction.FORWARD, 0)
	};
	
	protected static final Command[] MOVE_LEFT		= new Command[]
	{
		new Command(Motor.A, Direction.BACKWARD, 255),
		new Command(Motor.B, Direction.FORWARD, 255)
	};
	
	protected static final Command[] MOVE_RIGHT		= new Command[]
	{
		new Command(Motor.A, Direction.FORWARD, 255),
		new Command(Motor.B, Direction.BACKWARD, 255)
	};
	
	protected static final Command[] MOVE_FORWARD	= new Command[]
	{
		new Command(Motor.A, Direction.FORWARD, 255),
		new Command(Motor.B, Direction.FORWARD, 255)
	};
	
	protected static final Command[] MOVE_BACKWARD	= new Command[]
	{
		new Command(Motor.A, Direction.BACKWARD, 255),
		new Command(Motor.B, Direction.BACKWARD, 255)
	};
	
	// Update method
	/**
	 * @return 2 commands, one for motor A (index=0) and one for motor B (index=1)
	 */
	public Command[] update()
	{
		// Choose left, right or none
		int lrBias = 0;
		if(keyStack.contains(VK_A) || keyStack.contains(VK_LEFT))	lrBias--;
		if(keyStack.contains(VK_D) || keyStack.contains(VK_RIGHT))	lrBias++;
		
		// Choose forward, backwards or none
		int fbBias = 0;
		if(keyStack.contains(VK_W) || keyStack.contains(VK_UP))		fbBias++;
		if(keyStack.contains(VK_S) || keyStack.contains(VK_DOWN))	fbBias--;
		
		// Turn left or right first
		switch(lrBias)
		{
			case 1:		return MOVE_LEFT;
			case -1:	return MOVE_RIGHT;
			default:	break;
		}
		
		// If not, move forward
		switch(fbBias)
		{
			case 1:		return MOVE_FORWARD;
			case -1:	return MOVE_BACKWARD;
			default:	break;
		}
		
		// Brake
		return MOVE_BRAKE;
	}
	
	@Override public void keyPressed(KeyEvent e)	{ if(!keyStack.contains(e.getID()))	keyStack.push(e.getID()); }
	@Override public void keyReleased(KeyEvent e)	{ keyStack.remove(e.getID()); }
	@Override public void keyTyped(KeyEvent e)		{}
}
