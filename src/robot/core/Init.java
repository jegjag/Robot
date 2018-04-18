package robot.core;

import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

import robot.input.ControllerHandler;
import robot.input.KeyboardHandler;

public class Init
{
	public static final File cfgFile = new File("trashboi.cfg");
	public static final Properties SETTINGS = new Properties();
	public static Arduino arduino;
	
	public static final int UPDATE_FREQ = 60;
	
	public static Thread mainLoopThread;
	
	public static Controller gamepad = null;
	public static ControllerHandler cHandler = null;
	
	public static Controller keyboard = null;
	public static KeyboardHandler kHandler = null;
	
	public static JFrame frame = new JFrame("Trashboi");
	public static JPanel panel = new JPanel();
	
	public static Image getImg(String path)
	{
		return Toolkit.getDefaultToolkit().createImage(path);
	}
	
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		// System prep
		System.setProperty("sun.java2d.opengl", "True");
		
		// Load config
		if(!cfgFile.exists())
		{
			cfgFile.createNewFile();
			
			SETTINGS.setProperty("socket_port", "29914");
			SETTINGS.setProperty("fullscreen", "true");
			SETTINGS.setProperty("undecorated", "true");
			SETTINGS.setProperty("width", "800");
			SETTINGS.setProperty("height", "600");
			
			FileOutputStream fos = new FileOutputStream(cfgFile);
			SETTINGS.store(fos, "Config, you should know what you're doing if you're messing with this.");
			fos.close();
		}
		
		FileInputStream fis = new FileInputStream(cfgFile);
		SETTINGS.load(fis);
		fis.close();
		
		// Create Window stuff
		WindowAdapter callExitOnClose = new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				Init.exit();
			}
		};
		
		if(SETTINGS.getProperty("fullscreen").equalsIgnoreCase("true"))
		{
			frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			frame.setLocation(0, 0);
			frame.add(panel);
			frame.setUndecorated(true);
			frame.setResizable(false);
			frame.setVisible(true);
		}
		else
		{
			frame.setSize(Integer.parseInt(SETTINGS.getProperty("width", "800")), Integer.parseInt(SETTINGS.getProperty("height", "600")));
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(panel);
			if(SETTINGS.getProperty("undecorated").equalsIgnoreCase("true"))
				frame.setUndecorated(true);
			frame.setResizable(false);
			frame.setVisible(true);
		}
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(callExitOnClose);
		
		// JInput stuff (For XBAAWKS controller so I don't have to chase after the laptop)
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(Controller c : controllers)
		{
			if(c.getType() == Controller.Type.GAMEPAD)
			{
				gamepad = c;
				cHandler = new ControllerHandler(gamepad);
				System.out.println("Set '" + gamepad.getName() + "' as controller.");
				break;
			}
			
			if(c.getType() == Controller.Type.KEYBOARD)
			{
				keyboard = c;
				kHandler = new KeyboardHandler(keyboard);
			}
		}
		
		arduino = new Arduino();
		
		// Create main thread
		run();
	}
	
	public static void exit()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(cfgFile);
			SETTINGS.store(fos, "The Config, you should know what you're doing if you're messing with this.");
			fos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
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
			kHandler.update();
		}
	}
	
	public static boolean showGrid = true;
	
	private static BufferedImage canvas;
	
	//private double rotAmount = 0D;
	
	private static void render(double delta)
	{
		// Init
		Graphics2D g2d = canvas.createGraphics();
		
		// Clear
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		// Show analog trigger status
		if(gamepad != null)
		{
			// Gamepad UI
			float z = cHandler.axis_z;
			if(cHandler.axis_z < 0)
			{
				g2d.setColor(new Color(0, 160, 0));
				z *= -1;
			}
			else if(cHandler.axis_z > 0)
			{
				g2d.setColor(new Color(100, 0, 0));
			}
			g2d.fillRect(0, frame.getHeight() - round(z * frame.getHeight()), frame.getWidth(), round(z * frame.getHeight()));
			
			if(showGrid)
			{
				// Draw grid for X=0 and Y=0
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawLine(frame.getWidth() / 2, 0, frame.getWidth() / 2, frame.getHeight());	// Y Plane
				g2d.drawLine(0, frame.getHeight() / 2, frame.getWidth(), frame.getHeight() / 2);// X Plane
			}
			
			// Show position of left analog stick
			int x = round(((frame.getWidth() / 2) + (cHandler.axis_x * frame.getWidth() / 2)) - 5);
			int y = round((frame.getHeight() / 2) + (cHandler.axis_y * frame.getHeight() / 2) - 5);
			
			g2d.setColor(Color.RED);
			g2d.fillOval(x, y, 10, 10);
		}
		else
		{
			// Keyboard / Network UI
			
			final String msg = "Keyboard Mode";
			g2d.setFont(new Font("Open Sans", Font.BOLD, 72));
			g2d.setColor(Color.WHITE);
			g2d.drawString(msg, (frame.getWidth() / 2) - (g2d.getFontMetrics().stringWidth(msg) / 2),
					(frame.getHeight() / 2) - (g2d.getFontMetrics().getHeight() / 2));
		}
		// Dispose
		g2d.dispose();
		
		// Draw to screen
		g2d = (Graphics2D) panel.getGraphics();
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
			
			//Pre-update stuff goes here
			
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
