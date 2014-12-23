/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import castro.base.plugin.CPlugin;
import castro.ctools.Plugin;
import castro.ctools.modules.CModule;
import castro.ctools.modules.purger.players.PlayerPurger;
import castro.ctools.modules.stats.DataSearch;
import castro.ctools.modules.stats.PlayerData;
import castro.ctools.modules.stats.Stats;


// TODO: purger
// - Dynmap deleting (for now it have to be done manually once 6 months or so)
public class Purger extends CModule implements Runnable
{
	public static Purger instance;
	public static PurgerSQL purgerSQL;
	public final Backup backup;
	
	private final BukkitScheduler scheduler;
	private final int taskId;
	private final Queue<String> toBurn;
	
	private int purged = 0;
	
	public Purger()
	{
		plugin.log("Initializing purger...");
		instance  = this;
		backup    = new Backup();
		purgerSQL = new PurgerSQL(plugin);
		toBurn    = purgerSQL.getPlayersToBurn();
		scheduler = plugin.getServer().getScheduler();
		taskId    = scheduler.scheduleSyncRepeatingTask(plugin, this, 1, 1);
		plugin.log("Initialized! Will burn " + toBurn.size() + " players");
	}
	
	
	@Override
	public void run()
	{
		for(int i = 0; i < 5; ++i) // Up to x players per tick
		{
    		String playerToBurn = toBurn.poll();
    		if(playerToBurn == null)
    		{
    			scheduler.cancelTask(taskId);
    			cleanOthers();
    			Plugin.dispatchConsoleCommand("pex reload");
    			plugin.log("<DONE> Purged: " + purged);
    			return;
    		}
    		
    		if(CPlugin.hasPermission(playerToBurn, "aliquam.builder")
    		|| CPlugin.hasPermission(playerToBurn, "purger.ignore"))
    		{
    			plugin.log("Ignoring " + playerToBurn);
    			return;
    		}
    		
    		plugin.log("Burning " + playerToBurn);
    		PlayerPurger pPurger = new PlayerPurger(playerToBurn);
    		if(pPurger.run())
    			purged++;
		}
	}
	
	private void cleanOthers()
	{
		try
		{
			// Should be redundant but in case it was not deleted by cWorlds...
    		Statement deleteLikes =  purgerSQL.getConn().createStatement();
    		deleteLikes.executeUpdate("DELETE FROM cworlds_likes WHERE plotId NOT IN (SELECT id FROM cworlds_plots)");
    		deleteLikes.close();
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		try
		{
			// Should be redundant but in case it was not deleted by cWorlds...
			Statement deleteMembers =  purgerSQL.getConn().createStatement();
			deleteMembers.executeUpdate("DELETE FROM cworlds_members WHERE plotId NOT IN (SELECT id FROM cworlds_plots)");
			deleteMembers.close();
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		// Clear stats. They are useless on Aliquam
		File statsDir = DataSearch.getStatsFilesDir();
		for (File file : statsDir.listFiles())
		    FileDeleteStrategy.FORCE.deleteQuietly(file);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!sender.hasPermission("aliquam.admin"))
			return false;
		if(!command.getName().equalsIgnoreCase("cpurger"))
			return false;
		if(args.length != 1)
			return false;
		
		if(args[0].equalsIgnoreCase("toburn"))
		{
			Queue<String> burnlist = purgerSQL.getPlayersToBurn();
			for(String player : burnlist)
			{
				if(CPlugin.hasPermission(player, "aliquam.builder")
				|| CPlugin.hasPermission(player, "purger.ignore"))
				{
					PlayerData pData = Stats.get(player);
					plugin.sendMessage(sender, "- " + player + "; Seen " + pData.seen);
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("burnplayer"))
		{
			PlayerPurger pPurger = new PlayerPurger(args[0]);
			pPurger.run();
			plugin.log("Burned!");
		}
		
		return false;
	}
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands()	{ return new String[] {"cpurger"}; }
}
