/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger;

import java.util.Queue;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import castro.base.plugin.CPlugin;
import castro.ctools.Plugin;
import castro.ctools.modules.CModule;
import castro.ctools.modules.purger.players.PlayerPurger;
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
		for(int i = 0; i < 100; ++i) // Up to 100 players per tick
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
		/*
		try
		{
    		Statement deleteLikes =  purgerSQL.getConn().createStatement();
    		deleteLikes.executeUpdate(
    				  "DELETE FROM cworlds_likes "
    				+ "WHERE plotId NOT IN "
    				+ "(SELECT p.id FROM cworlds_plots)");
    		deleteLikes.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			Statement deleteMembers =  purgerSQL.getConn().createStatement();
			deleteMembers.executeUpdate(
    				  "DELETE FROM cworlds_members "
    				+ "WHERE plotId NOT IN "
    				+ "(SELECT p.id FROM cworlds_plots)");
			deleteMembers.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		*/
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("cpurger")
		&& args.length == 1
		&& args[0].equalsIgnoreCase("toburn"))
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
		return false;
	}
	
	@Override public boolean isListener()   { return false; }
	@Override public String[] getCommands()	{ return new String[] {"cpurger"}; }
}
