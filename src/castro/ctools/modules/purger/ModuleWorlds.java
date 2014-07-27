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
import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;


class ModuleWorlds extends Module
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
	
	
	private File[] getPlots(String player)
	{
		FilenameFilter filter = new plotFilter(player);
		return getWorldsDir().listFiles(filter);
	}
	
	
	private File getWorldGuardFile(String worldname)
	{
		File worlds = new File(getPluginDir("WorldGuard"), "worlds");
		return new File(worlds, worldname);
	}
	
	
	boolean purge (String player)
	{
		log("- Deleting player worlds...");
		File[] playerWorlds = getPlots(player);
		for(File world : playerWorlds)
		{
			CPlot plot = PlotsMgr.get(world.getName());
			log("    -  " + world.getName() + " plot? " + plot);
			if(plot == null)
				return false;
			try
            {
	            PlotsMgr.deletePlot(plot, true);
	            FileUtils.deleteDirectory(getWorldGuardFile(world.getName()));
            }
            catch(IOException e)
            {
	            e.printStackTrace();
	            return false;
            }
		}
		return true;
	}
	
	
	boolean backup(String player)
	{
		log("- Backing up player worlds...");
		File[] playerWorlds = getPlots(player);
		for(File world : playerWorlds)
		{
			log("    - " + world.getName());
			
			log("    - World");
			if(world.exists()) // kinda should if we found it in worlds directory xD
				if(!backupDir(world, player))
					return false;
			
			log("    - WorldGuard");
			File WgFile = getWorldGuardFile(world.getName());
			if(WgFile.exists())
    			if(!backupDir(WgFile, player))
    				return false;
		}
		return true;
	}
}
