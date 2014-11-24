/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.ctools.Plugin;

public abstract class BaseCommand
{
	protected static Plugin plugin = Plugin.get();
	
	protected CommandSender sender;
	protected Player player;
	protected String playername;
	protected String[] args;
	
	protected abstract boolean prep();
	protected abstract boolean exec();
	
	protected abstract boolean onlyPlayer(); // Is console allowed to run this command?
	protected abstract int minArgs();
	protected abstract String getPermission(); // permissions are usually: aliquam.<rank name>
	
	
	public boolean exec(CommandSender sender, String[] args)
	{
		String permission = getPermission();
		if(permission != null)
			if(!sender.hasPermission(permission))
				return !plugin.sendMessage(sender, "Nie masz praw do wykonania tej komendy");
		
		if(onlyPlayer())
			if(sender instanceof Player)
				player = (Player)sender;
			else
				return !plugin.sendMessage(sender, "&cTa komende moze wykonac tylko gracz");
		
		if(minArgs() > args.length)
			return !plugin.sendMessage(sender, "&cPodales za malo argumentow");
		
		this.sender = sender;
		this.playername = sender.getName();
		this.args = args;
		
		if(prep())
			return exec();
		return false;
	}
}
