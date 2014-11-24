/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import castro.ctools.Plugin;


public abstract class CModule implements Listener, CommandExecutor
{
	protected final Plugin plugin;
	protected final Permission permission;
	protected final Economy economy;
	
	
	public CModule()
	{
		plugin     = Plugin.get();
		economy    = Plugin.economy;
		permission = Plugin.permission;
	}
	
	
	public abstract boolean isListener();
	public abstract String[] getCommands();
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		return true;
	}
}