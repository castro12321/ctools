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

import castro.ctools.Plugin;


public class SetRank extends BaseCommand
{
	String target;
	String rank;
	
	@Override
	protected boolean prep()
	{
		if(args.length > 1)
		{
			target = args[0];
			rank   = args[1];
			
			if (!sender.hasPermission("castro.setrank." + rank))
				return !plugin.sendMessage(sender, "Nie masz praw do nadawania rangi " + rank);
		}
		return true;
	}

	@Override
	protected boolean exec()
	{
		if(rank == null)
			Plugin.dispatchCommand(sender, "pex user " + args[0] + " group list");
		else
		{			
			Plugin.dispatchConsoleCommand("pex user " + target + " group set " + rank);
			plugin.broadcast(target + " otrzymal range " + rank);
			plugin.reloadWELimit(target);
			plugin.checkPlayerBankAccount(plugin.getServer().getPlayerExact(target));
		}
		return true;
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
