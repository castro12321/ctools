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
