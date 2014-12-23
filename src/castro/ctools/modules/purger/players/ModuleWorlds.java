/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import castro.cWorlds.CPlot;
import castro.cWorlds.PlotsMgr;
import castro.ctools.Plugin;
import castro.ctools.modules.purger.Backup;
import castro.ctools.modules.stats.PlayerData;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;


class ModuleWorlds extends PlayerPurgerModule
{
	private static MultiverseCore multiverse = getMV();
	private static MVWorldManager MVMgr = multiverse.getMVWorldManager();
	private static HashMap<String, List<String>> playerWorldsByNick = new HashMap<>(); static { fillWorlds(); }
	
	private static Collection<String> getPlots(String playername)
	{
		Collection<String> plots = playerWorldsByNick.get(playername);
		if(plots == null)
			return new ArrayList<String>();
		return plots;
	}
	
	private static void fillWorlds()
	{
		File[] worlds = Backup.worlds().listFiles();
		for(File worldFile : worlds)
		{
			try
			{
				String worldname = worldFile.getName();
				String playername = CPlot.getPlayerName(worldname);
				
				List<String> playerWorlds = playerWorldsByNick.get(playername);
				if(playerWorlds == null)
				{
					playerWorlds = new ArrayList<>();
					playerWorldsByNick.put(playername, playerWorlds);
				}
				if(!playerWorlds.contains(worldname))
					playerWorlds.add(worldname);
			}
			catch(Exception e)
			{
				Plugin.get().log("Cannot addPlayerWorld() for world: " + worldFile.getName());
			}
		}
	}
	
	public ModuleWorlds(PlayerData pData)
    {
		super(pData);
    }
	
	private static MultiverseCore getMV()
	{
		PluginManager PM = Bukkit.getServer().getPluginManager();
		return (MultiverseCore)PM.getPlugin("Multiverse-Core");
	}
	
	private static File getWorldFile(String worldname)
	{
		return new File(Bukkit.getWorldContainer(), worldname);
	}
	
	@Override
	protected boolean purge()
	{
		Collection<String> playerWorlds = getPlots(playername);
		for(String worldname : playerWorlds)
		{
			CPlot plot = PlotsMgr.get(worldname);
			log("- " + worldname + " plot? " + (plot != null));
			try
            {
				// Delete cWorlds plot
				if(plot != null)
					PlotsMgr.deletePlot(plot);
				
	            // Delete MultiVerse record if somehow cWorlds didn't (if possible)
	            MultiverseWorld mvWorld = MVMgr.getMVWorld(worldname);
	            if(mvWorld != null)
	            	if(!MVMgr.removeWorldFromConfig(worldname))
	            		return !log("Cannot delete MV record");
	            
	            // Delete world manually
	            File worldFile = getWorldFile(worldname); 
				if(worldFile.exists())
					FileUtils.deleteDirectory(worldFile);
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
		Collection<String> playerWorlds = getPlots(playername);
		for(String worldname : playerWorlds)
		{
			log("- " + worldname);
			
			File worldFile = getWorldFile(worldname);
			if(worldFile.exists())
			{
				if(!Backup.directory(worldFile, playername))
					return !log("- Cannot backup world");
			}
			
			// WorldGuard backup is not needed. User can add members in /plot settings easily...
			// Multiverse backup is not needed. User can adjust settings in /plot settings easily...
			// cBorder backup is not needed. Will be recreated automatically when entering plot
			// Dynmap will be recreated automatically as well
		}
		return true;
	}
}
