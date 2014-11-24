/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger;

import java.io.File;
import java.io.IOException;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;


public class Backup
{
	public boolean text(String file, String player, String text)
	{
		File backup = new File(getBackupDir(player), file);
		try
		{
			FileUtils.writeStringToFile(backup, text);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean directory(File dir, String player)
	{
		File backup = new File(getBackupDir(player), dir.getName());
		try
        {
            FileUtils.copyDirectory(dir, backup);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
		return true;
	}
	
	
	public boolean file(File file, String player)
	{
		File backup = new File(getBackupDir(player), file.getName());
		try
		{
			FileUtils.copyFile(file, backup);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	private File getBackupsDir()
	{
		return new File("./../backup_long/purger");
	}
	
	
	private static long time = System.currentTimeMillis() / (1000 * 60);
	private File getBackupDir(String player)
	{
		return new File(getBackupsDir(), player + time);
	}
	
	
	private File getPluginsDir()
	{
		return new File("plugins");
	}
	
	
	public File pluginConfig(String pluginname)
	{
		return new File(getPluginsDir(), pluginname);
	}
	
	
	public File worlds()
	{
		return Bukkit.getWorldContainer();
	}
	
	/*
	private File getWorldDir(String worldname)
	{
		return new File(worlds(), worldname);
	}
	*/
}
