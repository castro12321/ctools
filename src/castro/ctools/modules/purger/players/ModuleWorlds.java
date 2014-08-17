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
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import castro.cBorder.BorderMgr;
import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


class ModuleWorlds extends PlayerPurgerModule
{
	private static MultiverseCore multiverse = getMV();
	
	public ModuleWorlds(String player)
    {
		super(player);
    }
	
	private static MultiverseCore getMV()
	{
		PluginManager PM = Bukkit.getServer().getPluginManager();
		return (MultiverseCore)PM.getPlugin("Multiverse-Core");
	}
	
	private static MVWorldManager getMVMgr()
	{
		return multiverse.getMVWorldManager();
	}
	
	private class plotFilter implements FilenameFilter
	{
		private final String plotFilter;
		
		
		plotFilter(String player)
        {
	        plotFilter = ("_"+player+"_").toLowerCase();
        }
		
		
		@Override
		public boolean accept(File dir, String name)
		{
			return name.toLowerCase().startsWith(plotFilter);
		}
	}
	
	
	private Collection<File> getPlots(String player)
	{
		FilenameFilter filter = new plotFilter(player);
		File[] worlds = backup.worlds().listFiles(filter);
		File[] wgWorlds = getWorldGuardWorlds().listFiles(filter);
		HashMap<String, File> plots = new HashMap<>();
		for(File file : worlds)
            plots.put(file.getAbsolutePath(), file);
		for(File file : wgWorlds)
            plots.put(file.getAbsolutePath(), file);
		return plots.values();
	}
	
	private File getWorldGuardWorlds()
	{
		return new File(backup.pluginConfig("WorldGuard"), "worlds");
	}
	
	private File getWorldGuardFile(String worldname)
	{
		return new File(getWorldGuardWorlds(), worldname);
	}
	
	@Override
	protected boolean purge()
	{
		Collection<File> playerWorlds = getPlots(player);
		for(File world : playerWorlds)
		{
			CPlot plot = PlotsMgr.get(world.getName());
			boolean foundPlot = plot != null;
			log("- " + world.getName() + " plot? " + foundPlot);
			try
            {
				// Delete cWorlds plot
				if(plot == null)
					log("Skipping! Plot not found");
				else
					PlotsMgr.deletePlot(plot, true);
				
				// Delete world
				if(world.exists())
					FileUtils.deleteDirectory(world);
				else
					log("Skipping! World file not found");
					
				// Delete WorldGuard config
				File wgConfig = getWorldGuardFile(world.getName());
				if(wgConfig.exists())
					FileUtils.deleteDirectory(wgConfig);
				else
					log("Skipping! No WG config found");
	            
	            // Delete MultiVerse record
	            MultiverseWorld mvWorld = getMVMgr().getMVWorld(world.getName());
	            if(mvWorld == null)
	            	log("Skipping! MV world not found");
	            else if(!getMV().deleteWorld(world.getName()))
	            	return !log("Cannot delete MV record");
	            
	            // Remove border
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
		Collection<File> playerWorlds = getPlots(player);
		for(File world : playerWorlds)
		{
			log("- " + world.getName());
			if(world.exists()) // kinda should if we found it in worlds directory xD
			{
				if(!backup.directory(world, player))
					return !log("- Cannot backup world");
			}
			else
				log("Skipping! Cannot find the world");
			
			File WgFile = getWorldGuardFile(world.getName());
			if(WgFile.exists())
			{
    			if(!backup.directory(WgFile, player))
    				return !log("- Cannot backup WG config");
			}
			else
				log("- Skipping! No WG config");
			
			// Multiverse backup is not needed. User can adjust settings in /plot settings easily...
			// cBorder backup is not needed. Will be recreated automatically when entering plot
			// Dynmap will be recreated automatically as well
		}
		return true;
	}
}
