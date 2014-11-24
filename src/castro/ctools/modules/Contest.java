/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.cWorlds.mapGenerator.CallbackPlotCreated;
import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;
import castro.ctools.Plugin;


public class Contest extends CModule
{
	Player player;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length == 1 && sender.hasPermission("contest.admin"))
		{
			if(args[0].equalsIgnoreCase("enable"))
				plugin.con.set("contest", true);
			if(args[0].equalsIgnoreCase("disable"))
				plugin.con.set("contest", false);
		}
		
		if(sender instanceof Player)
		{
			// Check if there is a contest running right now
			if(plugin.con.getBoolean("contest") != true)
			{
				// If not, block regular players from entering the contest plot
				plugin.sendMessage(sender, "There is no contest available now");
				if(!sender.hasPermission("contest.admin"))
					return true;
			}
			
			player = (Player)sender;
			String playername = player.getName().toLowerCase();
			
			// Enter the contest plot
			CPlot plot = PlotsMgr.get(playername, 100);
			if(plot != null)
				return Plugin.dispatchCommand(player, "plot 100");
			
			// Or create if it doesn't exist
			try
            {
				plugin.sendMessage(sender, "The contest plot is being prepared for you. Please wait...");
                PlotsMgr.createPlot(playername, 100, onCreate);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
			return true;
		}
		return false;
	}
	
	private CallbackPlotCreated onCreate = new CallbackPlotCreated()
	{
		@Override
		public void callback(CPlot plot)
		{
			Plugin.dispatchCommand(player, "plot 100");
		}
	};
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return new String[] {"contest"}; }
}