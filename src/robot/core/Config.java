package robot.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config
{
	private File cfgFile;
	private Properties properties;
	
	public Config(File propertiesFile) throws IOException
	{
		cfgFile = propertiesFile;
		if(!cfgFile.exists())
		{
			cfgFile.createNewFile();
			
			properties.setProperty("fullscreen", "true");
			properties.setProperty("borderless", "false");
			properties.setProperty("width", "1280");
			properties.setProperty("height", "720");
			
			FileOutputStream fos = new FileOutputStream(cfgFile);
			properties.store(fos, "Config, you should know what you're doing if you're messing with this.");
			fos.close();
		}
		
		FileInputStream fis = new FileInputStream(cfgFile);
		properties.load(fis);
		fis.close();
	}
	
	public void save() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(cfgFile);
		properties.store(fos, "The Config, you should know what you're doing if you're messing with this.");
		fos.close();
	}
	
	public String get(String key)						{ return properties.getProperty(key); }
	public String get(String key, String defaultValue)	{ return properties.getProperty(key, defaultValue); }
}
