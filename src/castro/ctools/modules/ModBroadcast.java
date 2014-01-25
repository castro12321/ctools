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

package castro.ctools.modules;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import castro.base.plugin.CUtils;
import castro.commands.BaseCommand;
import castro.ctools.CommandMgr;



public class ModBroadcast extends CModule
{
	String msg;
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		BaseCommand cmd = new ModBroadcastCommand(); 
		return CommandMgr.onCommand(cmd, sender, args);
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


class ModBroadcastCommand extends BaseCommand
{
	String msg;
	
	@Override
	protected boolean prep()
	{		
		msg = CUtils.joinArgs(args);
		return true;
	}

	@Override
	protected boolean exec()
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
	protected String getPermission()
	{
		return "aliquam.mod";
	}
}