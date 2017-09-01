package robot.core;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Init implements Runnable
{
	public static final int UPDATE_FREQ = 60;
	
	public static Controller gamepad = null;
	public static Component steeringAnalog = null;
	public static Thread pollThread;
	
	public static JFrame frame = new JFrame("KillDroid 420 Controller");
	public static JPanel panel = new JPanel();
	
	public static void main(String[] args)
	{
		//System prep
		System.setProperty("sun.java2d.opengl", "True");
		
		// Create Window stuff
		frame.setSize(800, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setVisible(true);
		
		// JInput stuff (For XBAAWKS controller so I don't have to chase after the laptop)
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(Controller c : controllers)
		{
			// Get controller name
			System.out.println("Controller '" + c.getName() + "' detected.");
			if(c.getType() == Controller.Type.GAMEPAD)
			{
				gamepad = c;
				System.out.println("Set '" + gamepad.getName() + "' as controller.");
				continue;
			}
		}
		
		if(gamepad == null)
		{
			System.err.println("No usable controller found, exiting...");
			System.exit(0);
		}
		
		pollThread = new Thread(new Init());
		pollThread.start();
	}
	
	private static float axis_X = 0F;
	private static float axis_Y = 0F;
	
	private void update()
	{
		// Poll controller
		gamepad.poll();
		
		Component[] comps = gamepad.getComponents();
		for(Component c : comps)
		{
			Identifier id = c.getIdentifier();
			if(id.equals(Identifier.Axis.X))
			{
				float x = c.getPollData();
				if(!(x > 0 && x < 0.1))	// Use 0.1 as deadzone
				{
					axis_X = x;
				}else axis_X = 0F;
			}
			else if(id.equals(Identifier.Axis.Y))
			{
				float y = c.getPollData();
				if(!(y < 0 && y > -0.02))
				{
					axis_Y = c.getPollData();
				}else axis_Y = 0F;
			}
		}
	}
	
	private BufferedImage canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
	
	private void render(double delta)
	{
		// Init
		Graphics2D g2d = canvas.createGraphics();
		
		// Clear
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		// Show position of left analog stick
		int x = round(((frame.getWidth() / 2) + (axis_X * frame.getWidth() / 2)) - 5);
		int y = round((frame.getHeight() / 2) + (axis_Y * frame.getHeight() / 2) - 5);
		
		g2d.setColor(Color.RED);
		g2d.fillOval(x, y, 10, 10);
		
		// Dispose
		g2d.dispose();
		
		// Draw to screen
		g2d = (Graphics2D) panel.getGraphics();
		g2d.drawImage(canvas, 0, 0, frame.getWidth(), frame.getHeight(), null);
		g2d.dispose();
	}
	
	@Override
	public void run()
	{
		//Start loop
		final long UPDATE_NANOS = 1000000000 / UPDATE_FREQ;
		long previous = System.nanoTime();
		long delay = 0L;
		
		while(true){
			long current = System.nanoTime();
			long elapsed = current - previous;
			previous = current;
			delay += elapsed;
			
			//Pre-update stuff goes here
			
			//Update loop until catchup to real time, or if certain amount of time passed.
			while(delay >= UPDATE_NANOS){
				update();
				delay -= UPDATE_NANOS;
			}
			
			render(1.0D + (delay / UPDATE_NANOS));
		}
	}
}
