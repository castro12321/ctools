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

package castro.ctools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import castro.base.GenericCommandMgr;
import castro.commands.BaseCommand;
import castro.commands.ClearScreen;
import castro.commands.SetRank;
import castro.commands.Sudo;

public class CommandMgr implements GenericCommandMgr 
{	
	
	private BaseCommand getCommand(String command, String[] args)
	{
		switch(command)
		{
		case "clearscreen": return new ClearScreen();
		case "setrank":		return new SetRank();
		case "sudo":		return new Sudo();
		}
		
		return null;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String[] args)
	{	
		for(int i = 0; i < args.length; ++i)
			args[i] = args[i].toLowerCase();
		
		BaseCommand ccommand = getCommand(command.getName(), args);
		if(ccommand == null)
			return false;
		return ccommand.exec(sender, args);
	}
	
	
	public static boolean onCommand(BaseCommand command, CommandSender sender, String[] args)
	{
		return command.exec(sender, args);
	}
}
