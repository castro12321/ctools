/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import castro.base.plugin.CUtils;
import castro.commands.CCommand;



public class ModBroadcast extends CModule
{
	String msg;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		CCommand cmd = new ModBroadcastCommand();
		return plugin.commandMgr.onCommand(cmd, sender, command, args);
	}
	
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		if (event.getMessage().startsWith("@"))
		{
			event.setCancelled(true);
			String msg = event.getMessage();
			msg = msg.substring(1, msg.length()); // Cut '@' at beggining
			
			plugin.modBroadcast(player, msg);
		}
	}
	

	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return new String[] {"modbroadcast"}; }
}


class ModBroadcastCommand extends CCommand
{
	String msg;
	
	@Override
	protected boolean prepare()
	{		
		msg = CUtils.joinArgs(args);
		return true;
	}

	@Override
	protected boolean execute()
	{
		return plugin.modBroadcast(sender, msg);
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return false;
	}

	@Override
	protected int minArgs()
	{
		return 1;
	}
	
	
	@Override
	public String[] neededPermissions()
	{
		return permissions("ctools.modbroadcast", "aliquam.mod");
	}
}