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

import org.bukkit.entity.Player;

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
		
		command = Plugin.joinArgs(args, 1);
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
