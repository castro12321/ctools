/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import castro.base.GenericCommandMgr;
import castro.commands.BaseCommand;
import castro.commands.ClearScreen;
import castro.commands.Pay;
import castro.commands.SetRank;
import castro.commands.Spawner;
import castro.commands.Sudo;
import castro.commands.Who;

public class CommandMgr implements GenericCommandMgr 
{	
	
	private BaseCommand getCommand(String command, String[] args)
	{
		switch(command)
		{
		case "clearscreen": return new ClearScreen();
		case "spawner":     return new Spawner();
		case "setrank":     return new SetRank();
		case "sudo":        return new Sudo();
		case "list":        return new Who();
		case "pay":         return new Pay();
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
