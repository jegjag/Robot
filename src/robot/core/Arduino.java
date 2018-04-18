package robot.core;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

/**
 * Taken from: https://gist.github.com/tkojitu/1924274
 */
public class Arduino implements Closeable
{
	private static final String[] PORT_NAMES = new String[]
	{
		// Linux
		"/dev/ttyUSB0",
		
		// Windows
		"COM3",
		"COM4",
		
		// Mac
		"/dev/tty.usbserial-A9007UX1"
	};
	
	private static final int TIMEOUT = 2000;	// ms
	private static final int DATA_RATE = 9600;
	
	private SerialPort serialPort;
	private OutputStream output;
	private ByteArrayOutputStream baos;
	private OutputStreamWriter writer;
	
	public Arduino()
	{
		baos = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(baos);
		
		CommPortIdentifier id = findPortID();
		if(id == null)
		{
			System.err.println("Could not find Arduino on COM port (USB).");
			System.exit(-1);
		}
		
		try
		{
			serialPort = (SerialPort) id.open(this.getClass().getName(), TIMEOUT);
			serialPort.setSerialPortParams
			(
				DATA_RATE,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE
			);
			output = serialPort.getOutputStream();
			serialPort.notifyOnDataAvailable(true);
		}
		catch(Exception e)
		{
			System.err.println("Failed to bind to port.\n\nStacktrace:");
			e.printStackTrace();
			
			System.exit(-1);
		}
	}
	
	public static enum Motor
	{
		A("A"),
		B("B");
		
		final String symbol;
		
		Motor(String symbol)
		{
			this.symbol = symbol;
		}
	}
	
	public static enum Direction
	{
		FORWARD("U"),
		BACKWARD("D"),
		BRAKE("S");
		
		final String symbol;
		
		Direction(String symbol)
		{
			this.symbol = symbol;
		}
	}
	
	/**
	 * @param m - the motor to apply to
	 * @param d - the direction
	 * @param speed - must be between 1 and 255 (0 means brake)
	 */
	public void send(Motor m, Direction d, int speed)
	{
		if(speed >= 254)
		{
			speed = 255;
		}
		
		if(speed < 0)
		{
			speed = 0;
		}
		
		sendString(m.symbol + d.symbol + speed);
	}
	
	/**
	 * <strong>Format: MDS</strong><br>
	 * M - Motor<br>
	 * D - Direction<br>
	 * S - Speed<br>
	 * <br>
	 * <i>Example: <code>AF255</code></i>
	 */
	public void sendString(String command)
	{
		System.out.println(command);
		try
		{
			writer.write(command, 0, command.length());
			writer.flush();
			
			baos.writeTo(output);
			baos.reset();
		}
		catch(IOException e)
		{
			System.err.println("Failed to send command \"" + command + "\".\n\nStacktrace:");
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void close()
	{
		if(serialPort == null)	return;
		
		serialPort.close();
	}
	
	private final CommPortIdentifier findPortID()
	{
		CommPortIdentifier id = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		
		while(portEnum.hasMoreElements())
		{
			CommPortIdentifier currentPortID = (CommPortIdentifier) portEnum.nextElement();
			for(String portName : PORT_NAMES)
			{
				if(currentPortID.getName().equals(portName))
				{
					id = currentPortID;
					break;
				}
			}
		}
		
		return id;
	}
}
