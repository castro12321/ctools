/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

import org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
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
	private final PermissionsEx pex;
	private final PermissionManager pexMgr;
	
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
		pex       = (PermissionsEx)Bukkit.getPluginManager().getPlugin("PermissionsEx");
		pexMgr    = pex.getPermissionsManager();
		plugin.log("Initialized! Will burn " + toBurn.size() + " players");
	}
	
	private static long t = 0;
	private static long lagCount = 0;
	void reset()
	{
		t = System.currentTimeMillis();
	}
	long timeStep(String msg)
	{
		long time = System.currentTimeMillis() - t;
		while(time > 2000)
		{
			lagCount++;
			time -= 2000;
		}
		plugin.log("DEBUG " + time + "ms " + msg);
		reset();
		return time;
	}
	
	@Override
	public void run()
	{
		for(int i = 0; i < 1; ++i) // Up to 1 players per tick
		{
    		String playerToBurn = toBurn.poll();
    		if(playerToBurn == null || lagCount > 30)
    		{
    			scheduler.cancelTask(taskId);
    			cleanOthers();
    			Plugin.dispatchConsoleCommand("pex reload");
    			plugin.log("<DONE> Purged: " + purged);
    			return;
    		}
    		
    		reset();
    		PlayerData pData = Stats.sql.getPlayer(playerToBurn);
    		timeStep("[Purger] get pData UUID? " + (pData.uuid != null));
    		
    		if(pData.uuid == null)
    		{
	    		if(pexMgr.has(pData.playername, "aliquam.builder", null))
	    		{
	    			plugin.log("Ignoring " + playerToBurn);
	    			return;
	    		}
    		}
    		else
    		{
    			if(pexMgr.has(pData.uuid, "aliquam.builder", null))
	    		{
	    			plugin.log("Ignoring " + playerToBurn);
	    			return;
	    		}
    		}
    		timeStep("[Purger] perm check");
    		
    		plugin.log("Burning " + playerToBurn);
    		PlayerPurger pPurger = new PlayerPurger(pData);
    		timeStep("[Purger] player ctor");
    		if(pPurger.run())
    			purged++;
    		timeStep("[Purger] player run " + purged);
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
			PlayerData pData = Stats.sql.getPlayer(args[0]);
			if(pData == null)
				return false;
			PlayerPurger pPurger = new PlayerPurger(pData);
			pPurger.run();
			plugin.log("Burned!");
		}
		
		return false;
	}
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands()	{ return new String[] {"cpurger"}; }
}
