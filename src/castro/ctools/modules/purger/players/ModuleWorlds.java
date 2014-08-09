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

package castro.ctools.modules.purger.players;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import castro.cBorder.BorderMgr;
import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;


class ModuleWorlds extends PlayerPurgerModule
{
	private static MultiverseCore multiverse = getMV();
	
	private static MultiverseCore getMV()
	{
		PluginManager PM = Bukkit.getServer().getPluginManager();
		return (MultiverseCore)PM.getPlugin("Multiverse-Core");
	}
	
	private static MVWorldManager getMVMgr()
	{
		return multiverse.getMVWorldManager();
	}
	
	public ModuleWorlds(String player)
    {
		super(player);
    }
	
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
		return backup.worlds().listFiles(filter);
	}
	
	
	private File getWorldGuardFile(String worldname)
	{
		File worlds = new File(backup.pluginConfig("WorldGuard"), "worlds");
		return new File(worlds, worldname);
	}
	
	@Override
	protected boolean purge()
	{
		log("- Deleting player worlds...");
		File[] playerWorlds = getPlots(player);
		for(File world : playerWorlds)
		{
			CPlot plot = PlotsMgr.get(world.getName());
			log("    -  " + world.getName() + " plot? " + plot);
			try
            {
				log("        - World");
				if(plot != null)
					PlotsMgr.deletePlot(plot, true);
				if(world.exists())
					FileUtils.deleteDirectory(world);
				
				log("        - WorldGuard");
	            FileUtils.deleteDirectory(getWorldGuardFile(world.getName()));
	            
	            log("        - MultiVerse");
	            if(getMVMgr().getMVWorld(world.getName()) != null)
	            	getMV().deleteWorld(world.getName());
	            
	            log("        - cBorder");
	            BorderMgr.removeBorder(world.getName());
            }
            catch(IOException e)
            {
	            e.printStackTrace();
	            return false;
            }
		}
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		log("- Backing up player worlds...");
		File[] playerWorlds = getPlots(player);
		for(File world : playerWorlds)
		{
			log("    - " + world.getName());
			
			log("        - World");
			if(world.exists()) // kinda should if we found it in worlds directory xD
				if(!backup.directory(world, player))
					return false;
			
			log("        - WorldGuard");
			File WgFile = getWorldGuardFile(world.getName());
			if(WgFile.exists())
    			if(!backup.directory(WgFile, player))
    				return false;
			
			// Multiverse backup is not needed. User can adjust settings in /plot settings easily...
			// cBorder backup is not needed. Will be recreated automatically when entering plot
			// Dynmap will be recreated automatically as well
		}
		return true;
	}
}
