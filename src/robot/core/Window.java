package robot.core;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JFrame
{
	private static final long serialVersionUID = 8261700996079018154L;
	
	private JPanel panel = new JPanel();
	
	public Window(boolean fullscreen, boolean borderless, int w, int h)
	{
		super("Robot Controller");
		if(fullscreen)
		{
			setSize(Toolkit.getDefaultToolkit().getScreenSize());
			setLocation(0, 0);
			add(panel);
			setUndecorated(true);
			setResizable(false);
			setVisible(true);
		}
		else
		{
			setSize(w, h);
			setLocationRelativeTo(null);
			add(panel);
			setUndecorated(borderless);
			setResizable(false);
			setVisible(true);
		}
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override public void windowClosing(WindowEvent e)	{ Init.exit(); }
		});
	}
	
	public Graphics2D getPanelGraphics()	{ return (Graphics2D) panel.getGraphics(); }
}
