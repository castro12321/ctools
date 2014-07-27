/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
