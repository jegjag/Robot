package robot.core;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import robot.input.ControllerHandler;
import robot.input.KeyboardHandler;
import robot.protocol.Arduino;
import robot.protocol.Command;

public class Init
{
	// Config
	private	static final File			configFile = new File("robot.properties");
	public	static Config				config;
	
	// Display
	public	static Window				frame;
	private	static BufferedImage		canvas;
	public	static final int			UPDATE_FREQ = 60;
	
	// Arduino connection
	public	static Arduino				arduino;
	
	// Input
	public	static ControllerHandler	cHandler = null;
	public	static KeyboardHandler		kHandler = null;
	
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		// System prep
		System.setProperty("sun.java2d.opengl", "True");
		
		// Load config
		try								{ config = new Config(configFile); }
		catch(IOException e)			{ e.printStackTrace(); }
		
		// Create window
		frame = new Window
		(
			config.get("fullscreen", "true").equalsIgnoreCase("true"),
			config.get("borderless", "false").equalsIgnoreCase("true"),
			Integer.parseInt(config.get("width", "1280")),
			Integer.parseInt(config.get("height", "1280"))
		);
		
		// Setup controllers
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(Controller c : controllers)
		{
			if(c.getType() == Controller.Type.GAMEPAD)		{ cHandler = new ControllerHandler(c); break; }
			if(c.getType() == Controller.Type.KEYBOARD)		{ kHandler = new KeyboardHandler(c); }
		}
		
		// Init arduino
		arduino = new Arduino();
		
		// Start game loop
		run();
	}
	
	public static void exit()
	{
		try										{ config.save(); }
		catch(IOException e)					{ e.printStackTrace(); }
		
		System.exit(0);
	}
	
	private static void update()
	{
		// Update controllers
		if(cHandler != null)
		{
			cHandler.update();
		}
		else if(kHandler != null)
		{
			Command[] operation = kHandler.update();
			try
			{
				arduino.sendCommand(operation[0]);
				arduino.sendCommand(operation[1]);
			}
			catch(IOException e)
			{
				System.err.println("Failed to communicate with the Arduino.");
				System.exit(-1);
			}
		}
	}
	
	// Grid view
	private static boolean showGrid = true;
	public static void toggleGrid()				{ showGrid = !showGrid; }
	
	private static void render(double delta)
	{
		// Init
		Graphics2D g2d = canvas.createGraphics();
		
		// Clear
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		// Show analog trigger status
		if(cHandler != null)
		{
			// Gamepad UI
			float z = cHandler.axis_z;
			if(cHandler.axis_z < 0)				{ g2d.setColor(new Color(0, 160, 0)); z *= -1; }
			else if(cHandler.axis_z > 0)		{ g2d.setColor(new Color(100, 0, 0)); }
			
			g2d.fillRect
			(
				0, frame.getHeight() - round(z * frame.getHeight()),
				frame.getWidth(), round(z * frame.getHeight())
			);
			
			if(showGrid)
			{
				// Draw grid for X=0 and Y=0
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawLine(frame.getWidth() / 2, 0, frame.getWidth() / 2, frame.getHeight());		// Y Plane
				g2d.drawLine(0, frame.getHeight() / 2, frame.getWidth(), frame.getHeight() / 2);	// X Plane
			}
			
			// Show position of left analog stick
			int x = round(((frame.getWidth() / 2) + (cHandler.axis_x * frame.getWidth() / 2)) - 5);
			int y = round((frame.getHeight() / 2) + (cHandler.axis_y * frame.getHeight() / 2) - 5);
			
			g2d.setColor(Color.RED);
			g2d.fillOval(x, y, 10, 10);
		}
		else
		{
			final String msg = "Keyboard Mode";
			g2d.setFont(new Font("Open Sans", Font.BOLD, 72));
			g2d.setColor(Color.WHITE);
			g2d.drawString
			(
				msg,
				(frame.getWidth() / 2) - (g2d.getFontMetrics().stringWidth(msg) / 2),
				(frame.getHeight() / 2) - (g2d.getFontMetrics().getHeight() / 2)
			);
		}
		// Dispose
		g2d.dispose();
		
		// Draw to screen
		g2d = frame.getPanelGraphics();
		g2d.drawImage(canvas, 0, 0, frame.getWidth(), frame.getHeight(), null);
		g2d.dispose();
	}
	
	private static void run()
	{
		//Start loop
		final long UPDATE_NANOS = 1000000000 / UPDATE_FREQ;
		long previous = System.nanoTime();
		long delay = 0L;
		
		canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		while(true)
		{
			long current = System.nanoTime();
			long elapsed = current - previous;
			previous = current;
			delay += elapsed;
			
			//Update loop until catchup to real time, or if certain amount of time passed.
			while(delay >= UPDATE_NANOS)
			{
				update();
				delay -= UPDATE_NANOS;
			}
			
			render(1.0D + (delay / UPDATE_NANOS));
		}
	}
}
