/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;

import org.bukkit.entity.Player;

import castro.base.plugin.CUtils;
import castro.ctools.Plugin;

public class Sudo extends BaseCommand
{
	Player target;
	String command;
	
	@Override
	protected boolean prep()
	{		
		target = plugin.getServer().getPlayerExact(args[0]);
		if(target == null)
			return !plugin.sendMessage(sender, "&cPodany gracz jest offline");
		
		command = CUtils.joinArgs(args, 1);
		return true;
	}

	@Override
	protected boolean exec()
	{
		return Plugin.dispatchCommand(target, command);
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return false;
	}

	@Override
	protected int minArgs()
	{
		return 2;
	}
	
	
	@Override
	protected String getPermission()
	{
		return "aliquam.admin";
	}
}
