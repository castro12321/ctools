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
import java.io.FilenameFilter;
import java.io.IOException;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;

import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;


public class CWorldsModule implements PurgeModule
{
	private class plotFilter implements FilenameFilter
	{
		private final String plotFilter;
		
		
		plotFilter(String player)
        {
	        plotFilter = "_"+player+"_";
        }
		
		
		@Override
		public boolean accept(File dir, String name)
		{
			return name.startsWith(plotFilter);
		}
	}
	
	
	private File[] getWorlds(String player)
	{
		File worldsDir = Bukkit.getWorldContainer();
		FilenameFilter filter = new plotFilter(player);
		return worldsDir.listFiles(filter);
	}
	
	
	public boolean purge (String player)
	{
		File[] playerWorlds = getWorlds(player);
		for(File world : playerWorlds)
		{
			CPlot plot = PlotsMgr.get(world.getName());
			PlotsMgr.deletePlot(plot, true);
		}
		return true;
	}
	
	
	public boolean backup(String player)
	{
		File[] playerWorlds = getWorlds(player);
		File   backupsDir   = new File(Bukkit.getWorldContainer(), "backups");
		for(File world : playerWorlds)
		{
			File backup = new File(backupsDir, world.getName());
	        try
            {
	            FileUtils.copyDirectory(world, backup);
            }
            catch(IOException e)
            {
	            e.printStackTrace();
	            return false;
            }
		}
		return true;
	}
}
