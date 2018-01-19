package robot.core;

import static java.lang.Math.*;
import static net.java.games.input.Component.Identifier.Button.*;

import java.awt.Color;
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

import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Init implements Runnable
{
	public static final File cfgFile = new File("trashboi.cfg");
	public static final Properties SETTINGS = new Properties();
	public static final int UPDATE_FREQ = 60;
	
	public static Controller gamepad = null;
	public static Component steeringAnalog = null;
	public static Thread mainLoopThread;
	
	public static JFrame frame = new JFrame("Trashboi");
	public static JPanel panel = new JPanel();
	
	public static final Image loadingIcon = getImg("resources/ui/loading.png");
	
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
			SETTINGS.setProperty("socket_port", "27015");
			SETTINGS.setProperty("fullscreen", "true");
			SETTINGS.setProperty("undecorated", "true");
			SETTINGS.setProperty("width", "800");
			SETTINGS.setProperty("height", "600");
			SETTINGS.setProperty("force_load", "false");
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
			if(SETTINGS.getProperty("undecorated").equalsIgnoreCase("true"))	frame.setUndecorated(true);
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
				System.out.println("Set '" + gamepad.getName() + "' as controller.");
				continue;
			}
		}
		
		if(gamepad == null && !Boolean.parseBoolean(SETTINGS.getProperty("force_load")))
		{
			System.err.println("No usable controller found, exiting...");
			exit();
		}
		else if(Boolean.parseBoolean(SETTINGS.getProperty("force_load")))
		{
			System.out.println("Forcing load, DON'T EXPECT IT TO WORK.");
		}
		
		// Create main thread
		mainLoopThread = new Thread(new Init());
		mainLoopThread.setName("Main Loop Thread");
		mainLoopThread.start();
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
	
	public static float axis_X = 0F;
	public static float axis_Y = 0F;
	/** 
	 * <strong>LT & RT</strong><br> 
	 * Greater than zero means LT is pressed,<br>
	 * Less than zero means RT is pressed.
	 */
	public static float axis_Z = 0F;
	
	private float deadzone = 0.1F;
	
	public boolean a_pressed = false, b_pressed = false, x_pressed = false, y_pressed = false,
			lb_pressed = false, rb_pressed = false,
			ls_down = false, rs_down = false,
			start_pressed = false, back_pressed = false;
	
	private void update()
	{
		// Poll controller
		gamepad.poll();
		
		Component[] comps = gamepad.getComponents();
		for(Component c : comps)
		{
			char[] deadzoneChars = new String(c.getDeadZone() + "").toCharArray();
			deadzoneChars[deadzoneChars.length - 1] = '1';
			deadzone = Float.parseFloat(new String(deadzoneChars));
			
			Identifier id = c.getIdentifier();
			if(id.equals(Identifier.Axis.X))
			{
				float x = c.getPollData();
				if(!(
						(x > 0 && x < deadzone)
					||	(x < 0 && x > -deadzone)
					))
				{
					axis_X = x;
				}
				else axis_X = 0F;
			}
			else if(id.equals(Identifier.Axis.Y))
			{
				float y = c.getPollData();
				if(!(
						(y > 0 && y < deadzone)
					||	(y < 0 && y > -deadzone)
					))
				{
					axis_Y = y;
				}
				else axis_Y = 0F;
			}
			else if(id.equals(Identifier.Axis.Z))
			{	// The Left and Right trigger
				float z = c.getPollData();
				if(!(	// Deadzone on Z should be lower as they seem to be more accurate
						(z < 0 && z > deadzone / 8)
					||	(z > 0 && z < deadzone / 8)
				))
				{
					axis_Z = z;
				}
				else axis_Z = 0F;
			}
			else if(!c.isAnalog())
			{
				if(id.equals(_0))
				{
					if(c.getPollData() == 1.0F && !a_pressed)
					{
						a_pressed = true;
					}
					else if(c.getPollData() == 0.0F && a_pressed)
					{
						a_pressed = false;
					}
				}
				else if(id.equals(_1))
				{
					if(c.getPollData() == 1.0F && !b_pressed)
					{
						b_pressed = true;
					}
					else if(c.getPollData() == 0.0F && b_pressed)
					{
						b_pressed = false;
					}
				}
				else if(id.equals(_2))
				{
					if(c.getPollData() == 1.0F && !x_pressed)
					{
						x_pressed = true;
					}
					else if(c.getPollData() == 0.0F && x_pressed)
					{
						x_pressed = false;
					}
				}
				else if(id.equals(_3))
				{
					if(c.getPollData() == 1.0F && !y_pressed)
					{
						y_pressed = true;
					}
					else if(c.getPollData() == 0.0F && y_pressed)
					{
						// Toggle showGrid
						if(showGrid)	showGrid = false;
						else showGrid = true;
						
						y_pressed = false;
					}
				}
				else if(id.equals(_4))
				{
					if(c.getPollData() == 1.0F && !lb_pressed)
					{
						lb_pressed = true;
					}
					else if(c.getPollData() == 0.0F && lb_pressed)
					{
						lb_pressed = false;
					}
				}
				else if(id.equals(_5))
				{
					if(c.getPollData() == 1.0F && !rb_pressed)
					{
						rb_pressed = true;
					}
					else if(c.getPollData() == 0.0F && rb_pressed)
					{
						rb_pressed = false;
					}
				}
				else if(id.equals(_6))
				{
					if(c.getPollData() == 1.0F && !back_pressed)
					{
						back_pressed = true;
					}
					else if(c.getPollData() == 0.0F && back_pressed)
					{
						// EMERGENCY TERMINATE
						exit();
						
						back_pressed = false;
					}
				}
				else if(id.equals(_7))
				{
					if(c.getPollData() == 1.0F && !start_pressed)
					{
						start_pressed = true;
					}
					else if(c.getPollData() == 0.0F && start_pressed)
					{
						start_pressed = false;
					}
				}
				else if(id.equals(_8))
				{
					if(c.getPollData() == 1.0F && !ls_down)
					{
						ls_down = true;
					}
					else if(c.getPollData() == 0.0F && ls_down)
					{
						ls_down = false;
					}
				}
				else if(id.equals(_9))
				{
					if(c.getPollData() == 1.0F && !rs_down)
					{
						rs_down = true;
					}
					else if(c.getPollData() == 0.0F && rs_down)
					{
						rs_down = false;
					}
				}
			}
		}
	}
	
	private boolean showGrid = true;
	
	private BufferedImage canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
	
	//private double rotAmount = 0D;
	
	private void render(double delta)
	{
		// Init
		Graphics2D g2d = canvas.createGraphics();
		
		// Clear
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, frame.getWidth(), frame.getHeight());
		
		// Show analog trigger status
		float z = axis_Z;
		if(axis_Z < 0)
		{
			g2d.setColor(new Color(0, 160, 0));
			z *= -1;
		}
		else if(axis_Z > 0)
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
		int x = round(((frame.getWidth() / 2) + (axis_X * frame.getWidth() / 2)) - 5);
		int y = round((frame.getHeight() / 2) + (axis_Y * frame.getHeight() / 2) - 5);
		
		g2d.setColor(Color.RED);
		g2d.fillOval(x, y, 10, 10);
		
		// UI Overlay
		/*if(false)
		{
			g2d.setColor(Color.GRAY);
			x = (frame.getWidth() - 128);
			y = 128;
			
			AffineTransform at = AffineTransform.getTranslateInstance(x, y);
			at.translate(-64, -64);
			at.rotate(rotAmount, 64, 64);
			g2d.setTransform(at);
			g2d.drawImage(loadingIcon, 0, 0, null);
			rotAmount += 0.01D * delta;
		}*/
		
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
