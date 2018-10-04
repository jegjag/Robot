package robot.protocol;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

/** Taken from: https://gist.github.com/tkojitu/1924274 */
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
		/* Unsure about this */ "/dev/tty.usbserial-A9007UX1"
	};
	
	private static final int TIMEOUT = 2000;	// ms
	private static final int DATA_RATE = 9600;
	
	private		SerialPort				serialPort;
	protected	ByteArrayOutputStream	baos;
	protected	OutputStream			output;
	protected	OutputStreamWriter		writer;
	
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
	
	public void sendCommand(Command cmd) throws IOException
	{
		char[] byteArray = { cmd.motor.protocolByte, cmd.direction.protocolByte, (char) cmd.speed, '\n' };
		writer.write(byteArray);
	}
	
	@Override
	public synchronized void close()
	{
		if(serialPort == null)	return;
		serialPort.close();
		try						{ writer.close(); output.close(); baos.close(); }
		catch(IOException e)	{ e.printStackTrace(); }
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
				if(currentPortID.getName().equals(portName))	{ id = currentPortID; break; }
			}
		}
		
		return id;
	}
}
