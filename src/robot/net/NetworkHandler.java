package robot.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkHandler
{
	protected ServerSocket robotService;
	protected Socket serviceSocket;
	private boolean accepted = false;
	
	public boolean isConnected()
	{
		return accepted;
	}
	
	public NetworkHandler(String port) throws NumberFormatException, IOException
	{
		robotService = new ServerSocket(Integer.parseInt(port));
		//serviceSocket = robotService.accept();
	}
}
