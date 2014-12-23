/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import castro.cWorlds.PlotsMgr;
import castro.ctools.modules.stats.PlayerData;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


class ModuleWorlds extends PlayerPurgerModule
{
	private static MultiverseCore multiverse = getMV();
	
	public ModuleWorlds(PlayerData pData)
    {
		super(pData);
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
		Collection<File> playerWorlds = getPlots(playername);
		for(File world : playerWorlds)
		{
			castro.cWorlds.
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
		Collection<File> playerWorlds = getPlots(playername);
		for(File world : playerWorlds)
		{
			log("- " + world.getName());
			if(world.exists()) // kinda should if we found it in worlds directory xD
			{
				if(!backup.directory(world, playername))
					return !log("- Cannot backup world");
			}
			else
				log("Skipping! Cannot find the world");
			
			File WgFile = getWorldGuardFile(world.getName());
			if(WgFile.exists())
			{
    			if(!backup.directory(WgFile, playername))
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
