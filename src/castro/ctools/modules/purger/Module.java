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

import castro.ctools.Plugin;


abstract class Module
{
	abstract boolean purge (String player);
	abstract boolean backup(String player);
	
	protected boolean backupText(String file, String player, String text)
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
	
	protected boolean backupDir(File dir, String player)
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
	
	
	protected boolean backupFile(File file, String player)
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
	
	
	protected File getBackupsDir()
	{
		return new File("./../backup_long/purger");
	}
	
	
	private static long time = System.currentTimeMillis() / (1000 * 60);
	protected File getBackupDir(String player)
	{
		return new File(getBackupsDir(), player + time);
	}
	
	
	protected File getPluginsDir()
	{
		return new File("plugins");
	}
	
	
	protected File getPluginDir(String pluginname)
	{
		return new File(getPluginsDir(), pluginname);
	}
	
	
	protected File getWorldsDir()
	{
		return Bukkit.getWorldContainer();
	}
	
	
	protected File getWorldDir(String worldname)
	{
		return new File(getWorldsDir(), worldname);
	}
	
	@Override
	public String toString()
	{
		String modulename = super.toString().replace("castro.ctools.modules.purger.Module", "");
		return modulename.substring(0, modulename.indexOf("@"));
	}
	
	protected void log(String msg)
	{
		Plugin.get().log("    " + msg);
	}
}
